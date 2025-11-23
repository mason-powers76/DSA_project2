import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Manages user interaction, file loading, and coordination between
 * the data structure (FlightGraph) and the algorithms (PathFinder, PathSorter).
 */
public class Driver {

    private final Scanner scanner = new Scanner(System.in);
    private final FlightGraph graph = new FlightGraph();
    private final PathFinder finder = new PathFinder();
    private final PathSorter sorter = new PathSorter();

    // ----------------------------------------------------
    // Public Entry Method
    // ----------------------------------------------------

    /**
     * Executes the main application loop.
     */
    public void run() {
        System.out.println("--- CS 3345 Flight Planner ---");

        // 1. Build the Graph from the Flight Data File
        if (!promptAndBuildGraph()) {
            System.out.println("Fatal error: Could not load flight data. Exiting.");
            return;
        }

        // 2. Main Interaction Loop (File or Manual Input)
        boolean running = true;
        while (running) {
            char choice = promptForFlightPlanSource();

            switch (choice) {
                case 'F':
                    processRequestedFlightsFile();
                    break;
                case 'M':
                    processManualFlightPlan();
                    break;
                case 'E':
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter F, M, or E.");
            }
        }

        System.out.println("\n--- Program Exited. Thank you. ---");
        scanner.close();
    }

    // ----------------------------------------------------
    // Phase 3: File Reading Logic
    // ----------------------------------------------------

    /**
     * Prompts user for the flight data file and populates the FlightGraph.
     * @return true if the graph was built successfully, false otherwise.
     */
    private boolean promptAndBuildGraph() {
        System.out.print("Enter the path to the Flight Data file (e.g., flight_data.txt): ");
        String fileName = scanner.nextLine().trim();

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            // Read the first line which indicates the total number of records [cite: 80, 86]
            int numRecords = fileScanner.nextInt();
            fileScanner.nextLine(); // Consume the rest of the line

            System.out.println("Building graph with " + numRecords + " records...");

            for (int i = 0; i < numRecords; i++) {
                String line = fileScanner.nextLine();
                // Data is separated by a pipe '|' [cite: 88]
                String[] parts = line.split("\\|");

                if (parts.length < 3) continue; // Skip malformed lines

                // Part 1: Origin and Destination (space separated)
                String[] cityData = parts[0].trim().split("\\s+");
                String origin = cityData[0];
                String destination = cityData[1];

                // Part 2 & 3: Cost and Time (separated by pipe)
                int cost = Integer.parseInt(parts[1].trim());
                int time = Integer.parseInt(parts[2].trim());

                // Add the path (FlightGraph handles bi-directionality)
                graph.addFlightPath(origin, destination, cost, time);
            }
            System.out.println("Graph built successfully.");
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found at the specified path.");
            return false;
        } catch (InputMismatchException | NumberFormatException e) {
            System.err.println("Error: Input file format is incorrect. Check the integer count or cost/time values.");
            return false;
        }
    }

    /**
     * Processes a file containing multiple flight requests.
     */
    private void processRequestedFlightsFile() {
        System.out.print("Enter the path to the Requested Flight Plans file: ");
        String fileName = scanner.nextLine().trim();

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            int numRequests = fileScanner.nextInt();
            fileScanner.nextLine();

            for (int i = 1; i <= numRequests; i++) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|"); // File is pipe-delimited [cite: 92]

                if (parts.length != 3) {
                    System.err.println("Skipping Request " + i + ": Incorrect format.");
                    continue;
                }

                String origin = parts[0].trim();
                String destination = parts[1].trim();
                char sortBy = parts[2].trim().toUpperCase().charAt(0); // 'T' or 'C' [cite: 92]

                findAndOutputPlans(i, origin, destination, sortBy);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Requested Flights file not found.");
        }
    }

    /**
     * Processes a single flight request entered manually by the user.
     */
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

        // Use 999 as a placeholder flight number for manual requests
        findAndOutputPlans(999, origin, destination, sortBy);
    }

    // ----------------------------------------------------
    // Phase 4: Core Logic Coordination & Output
    // ----------------------------------------------------

    /**
     * The core method that runs the search, sorts the results, and prints the output.
     * This minimizes code in the other interaction methods.
     */
    private void findAndOutputPlans(int flightNumber, String origin, String destination, char sortBy) {

        // 1. Search (Phase 2: Iterative Backtracking)
        List<PathResult> allPaths = finder.findFlights(graph, origin, destination, sortBy);

        // Output Header
        String sortCriteria = (sortBy == 'T' ? "Time" : "Cost");
        System.out.println("\nFlight " + flightNumber + ": " + origin + ", " + destination + " (" + sortCriteria + ")");

        // 2. Handle No Path Found
        if (allPaths.isEmpty()) {
            System.out.println("    No flight plan can be created between " + origin + " and " + destination + "."); [cite: 99]
            return;
        }

        // 3. Sort (Phase 3: HeapSort)
        // Since we want the most efficient (smallest cost/time), we use the Min-Heap.
        List<PathResult> sortedPaths = sorter.heapSort(allPaths);

        // 4. Output Top 3 Plans [cite: 97, 98]
        int numToOutput = Math.min(3, sortedPaths.size());

        for (int i = 0; i < numToOutput; i++) {
            PathResult plan = sortedPaths.get(i);

            // Format the path string (e.g., Dallas -> Austin -> Houston)
            String pathStr = formatPath(plan.getPath());

            // Output format adherence is critical [cite: 101]
            System.out.printf("    Path %d: %s. Time: %d Cost: %.2f\n",
                    i + 1,
                    pathStr,
                    plan.getTotalTime(),
                    (double)plan.getTotalCost()
            );
        }
    }

    /**
     * Helper to format the path list into the required string format.
     * @param path A list of city names.
     * @return A string like "CityA -> CityB -> CityC".
     */
    private String formatPath(List<String> path) {
        return String.join(" -> ", path);
    }

    /**
     * Prompts the user for the source of flight plans.
     */
    private char promptForFlightPlanSource() {
        System.out.println("\n--- Flight Plan Source ---");
        System.out.println("Select how to input flight requests:");
        System.out.println("  [F] Process a Requested Flights File");
        System.out.println("  [M] Enter a Manual Flight Request");
        System.out.println("  [E] Exit Program");
        System.out.print("Enter choice (F/M/E): ");

        String input = scanner.nextLine().trim().toUpperCase();
        if (input.isEmpty()) return 'X';
        return input.charAt(0);
    }
}