package core;

import java.util.Stack;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;

//performs the exhaustive search and iterative backtracking
public class PathFinder {

    // Internal class to hold the necessary state for the stack
    private static class SearchState {
        final String currentCityName;
        final int accumulatedCost;
        final int accumulatedTime;
        final LinkedList<String> pathHistory;
        final HashSet<String> visitedCities;

        // constructor
        public SearchState(String currentCityName, int accumulatedCost, int accumulatedTime,
                           LinkedList<String> pathHistory, HashSet<String> visitedCities) {
            this.currentCityName = currentCityName;
            this.accumulatedCost = accumulatedCost;
            this.accumulatedTime = accumulatedTime;
            this.pathHistory = pathHistory;
            this.visitedCities = visitedCities;
        }
    }


    public List<PathResult> findFlights(FlightGraph graph, String startCity, String endCity, char sortBy) {

        List<PathResult> allPaths = new LinkedList<>();
        Stack<SearchState> stack = new Stack<>();

        // 1. Initial State Setup
        LinkedList<String> initialPath = new LinkedList<>();
        initialPath.add(startCity);
        HashSet<String> initialVisited = new HashSet<>();
        initialVisited.add(startCity);

        SearchState initialState = new SearchState(startCity, 0, 0, initialPath, initialVisited);
        stack.push(initialState);

        // 2. The Main Iterative Backtracking Loop
        while (!stack.isEmpty()) {
            SearchState current = stack.pop();

            // Success Condition Check
            if (current.currentCityName.equalsIgnoreCase(endCity)) {
                // Record the path found and continue searching for others
                PathResult pathFound = new PathResult(
                        current.pathHistory,
                        current.accumulatedCost,
                        current.accumulatedTime,
                        sortBy
                );
                allPaths.add(pathFound);
                continue;
            }

            CityNode city = graph.getCity(current.currentCityName);
            if (city == null) continue;

            // 3. Explore Neighbors
            for (FlightEdge flight : city.getFlights()) {
                String nextCity = flight.getDestination();

                // Cycle Detection: Check if the next city is already in the path
                if (!current.visitedCities.contains(nextCity)) {


                    // Create new history objects for the next step
                    HashSet<String> nextVisited = new HashSet<>(current.visitedCities);
                    nextVisited.add(nextCity);

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
            }
        }

        return allPaths;
    }
}