import java.util.Stack;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;

public class PathFinder {

    // Internal class to hold the search state on the stack
    private static class SearchState {
        final String currentCityName;
        final int accumulatedCost;
        final int accumulatedTime;
        final LinkedList<String> pathHistory;
        final HashSet<String> visitedCities;
        // ... (Constructor and Getters will be needed)
    }

    /**
     * Executes an iterative backtracking search to find all paths.
     * @param graph The FlightGraph data container.
     * @param startCity The origin city name.
     * @param endCity The destination city name.
     * @param sortBy 'T' for time, 'C' for cost (used for PathResult initialization).
     * @return A list of all valid FlightPlan objects found.
     */
    public List<PathResult> findFlights(FlightGraph graph, String startCity, String endCity, char sortBy) {

        // Final list to store all successful paths
        List<PathResult> allPaths = new LinkedList<>();

        // Stack for the iterative search (simulates recursion)
        Stack<SearchState> stack = new Stack<>();

        // 1. Initial State Setup
        // Create the history objects for the starting city
        LinkedList<String> initialPath = new LinkedList<>();
        initialPath.add(startCity);
        HashSet<String> initialVisited = new HashSet<>();
        initialVisited.add(startCity);

        // Push the starting state onto the stack
        SearchState initialState = new SearchState(startCity, 0, 0, initialPath, initialVisited);
        stack.push(initialState);

        // 2. The Main Iterative Backtracking Loop
        while (!stack.isEmpty()) {
            SearchState current = stack.pop();

            // Success Condition Check
            if (current.currentCityName.equalsIgnoreCase(endCity)) {
                // If we hit the destination, record the result and continue the search
                PathResult pathFound = new PathResult(
                        current.pathHistory,
                        current.accumulatedCost,
                        current.accumulatedTime,
                        sortBy
                );
                allPaths.add(pathFound);
                continue; // Found a path, but must keep searching for others
            }

            // Get the CityNode to access its neighbors (outgoing flights)
            CityNode city = graph.getCity(current.currentCityName);
            if (city == null) continue; // Should not happen if graph is built correctly

            // 3. Explore Neighbors (Try all outgoing edges)
            for (FlightEdge flight : city.getFlights()) {
                String nextCity = flight.getDestination();

                // Cycle Detection: Check if the next city is already in the path
                if (!current.visitedCities.contains(nextCity)) {

                    // Crucial Step: Deep Copying State for the next step!
                    // ----------------------------------------------------

                    // New Visited Set (deep copy)
                    HashSet<String> nextVisited = new HashSet<>(current.visitedCities);
                    nextVisited.add(nextCity);

                    // New Path History (deep copy)
                    LinkedList<String> nextPath = new LinkedList<>(current.pathHistory);
                    nextPath.add(nextCity);

                    // New Accumulated Metrics
                    int newCost = current.accumulatedCost + flight.getCost();
                    int newTime = current.accumulatedTime + flight.getTime();

                    // Create and Push New State
                    SearchState nextState = new SearchState(
                            nextCity,
                            newCost,
                            newTime,
                            nextPath,
                            nextVisited
                    );
                    stack.push(nextState);
                }
            } // End of FlightEdge exploration
        } // End of while loop

        return allPaths;
    }
}