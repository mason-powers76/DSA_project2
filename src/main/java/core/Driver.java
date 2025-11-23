package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

//Provides coordination between the classes as well as file loading
public class Driver {

    private final Scanner scanner = new Scanner(System.in);
    private final FlightGraph graph = new FlightGraph();
    private final PathFinder finder = new PathFinder();
    private final PathSorter sorter = new PathSorter();

    public void run() {
        System.out.println("CS/CE 3345 Flight Planner");

        // 1. Build the graph
        if (!promptAndBuildGraph()) {
            System.out.println("Error: Could not load flight data. Exiting.");
            return;
        }

        // 2. Process the Requested Flights
        System.out.println("Processing File Requests...");
        processRequestedFlightsFile();

        // 3. Interaction Loop (Manual Input / Exit)
        boolean running = true;
        while (running) {

            // Display secondary menu (only M and E options now)
            System.out.println("\nFlight Plan Source");
            System.out.println("Select next action:");
            System.out.println("  [M] Enter a Manual Flight Request");
            System.out.println("  [E] Exit Program");
            System.out.print("Enter choice (M/E): ");

            String input = scanner.nextLine().trim().toUpperCase();
            char choice = input.isEmpty() ? 'X' : input.charAt(0);

            switch (choice) {
                case 'M':
                    processManualFlightPlan();
                    break;
                case 'E':
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter M or E.");
            }
        }

        System.out.println("\nProgram Exited. Thank you.");
        scanner.close();
    }

    //Prompts use for flight data files
    private boolean promptAndBuildGraph() {
        System.out.print("Enter the path to the Flight Data file (e.g., src/main/resources/OriginationDestinationData.txt): ");
        String fileName = scanner.nextLine().trim();

        int recordsAdded = 0;

        try (Scanner fileScanner = new Scanner(new File(fileName))) {

            // records the integer to know input file length
            if (!fileScanner.hasNextLine()) {
                System.err.println("Error: Input file is empty.");
                return false;
            }
            String firstLine = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(firstLine);
            if (!lineScanner.hasNextInt()) {
                System.err.println("Error: Could not find the integer record count in the first line: " + firstLine);
                return false;
            }
            int numRecords = lineScanner.nextInt();

            System.out.println("Adding " + numRecords + " records to the graph from " + fileName + "...");

            for (int i = 0; i < numRecords; i++) {
                if (!fileScanner.hasNextLine()) {
                    System.err.println("Warning: File ended prematurely. Expected " + numRecords + " records.");
                    break;
                }

                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");

                if (parts.length < 4) continue;

                String origin = parts[0].trim();
                String destination = parts[1].trim();

                try {
                    int cost = Integer.parseInt(parts[2].trim());
                    int time = Integer.parseInt(parts[3].trim());

                    graph.addFlightPath(origin, destination, cost, time);
                    recordsAdded++;
                } catch (NumberFormatException e) {

                }
            }
            System.out.println("Successfully added " + recordsAdded + " flight paths.");
            return true;

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found at the specified path. Check your path.");
            return false;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during file parsing.");
            e.printStackTrace();
            return false;
        }
    }

    private void processRequestedFlightsFile() {
        System.out.print("Enter the path to the Requested Flight Plans file (e.g., src/main/resources/RequestedFlights.txt): ");
        String fileName = scanner.nextLine().trim();

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            // Safely parse the number of requests from the first line
            String firstLine = fileScanner.nextLine();
            Scanner lineScanner = new Scanner(firstLine);
            if (!lineScanner.hasNextInt()) {
                System.err.println("Error: Could not find integer request count in the file.");
                return;
            }
            int numRequests = lineScanner.nextInt();

            for (int i = 1; i <= numRequests; i++) {
                if (!fileScanner.hasNextLine()) {
                    System.err.println("Warning: Request file ended early.");
                    break;
                }

                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");

                if (parts.length != 3) {
                    System.err.println("Skipping Request " + i + ": Incorrect format: " + line);
                    continue;
                }

                String origin = parts[0].trim();
                String destination = parts[1].trim();
                char sortBy = parts[2].trim().toUpperCase().charAt(0); // 'T' or 'C'

                findAndOutputPlans(i, origin, destination, sortBy);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Requested Flights file not found. Check your path.");
        }
    }

    //process for a manual flight
    private void processManualFlightPlan() {
        System.out.print("\nEnter request (Format: Origin|Destination|SortBy - e.g., Chicago|Dallas|C): ");
        String line = scanner.nextLine().trim();

        String[] parts = line.split("\\|");

        if (parts.length != 3) {
            System.err.println("Invalid manual format. Must use Origin|Destination|SortBy.");
            return;
        }

        String origin = parts[0].trim();
        String destination = parts[1].trim();
        char sortBy = parts[2].trim().toUpperCase().charAt(0);

        if (sortBy != 'T' && sortBy != 'C') {
            System.err.println("Invalid sort key. Must be 'T' (Time) or 'C' (Cost).");
            return;
        }

        findAndOutputPlans(999, origin, destination, sortBy);
    }

    //process for the search and path finder
    private void findAndOutputPlans(int flightNumber, String origin, String destination, char sortBy) {

        // 1. Search
        List<PathResult> allPaths = finder.findFlights(graph, origin, destination, sortBy);

        // Output Header
        String sortCriteria = (sortBy == 'T' ? "Time" : "Cost");
        System.out.println("\nFlight " + flightNumber + ": " + origin + ", " + destination + " (" + sortCriteria + ")");

        // 2. Handle No Path Found
        if (allPaths.isEmpty()) {
            System.out.println("    No flight plan can be created between " + origin + " and " + destination + ".");
            return;
        }

        // 3. Sort
        List<PathResult> sortedPaths = sorter.heapSort(allPaths);

        // 4. Output Top 3 Plans
        int numToOutput = Math.min(3, sortedPaths.size());

        for (int i = 0; i < numToOutput; i++) {
            PathResult plan = sortedPaths.get(i);

            // Format the path string (e.g., Dallas -> Austin -> Houston)
            String pathStr = formatPath(plan.getPath());

            // Output format adherence (Path X: ... Cost: X.XX)
            System.out.printf("    Path %d: %s. Time: %d Cost: %.2f\n",
                    i + 1,
                    pathStr,
                    plan.getTotalTime(),
                    (double)plan.getTotalCost()
            );
        }
    }

    private String formatPath(List<String> path) {
        return String.join(" -> ", path);
    }
}