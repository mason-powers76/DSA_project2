import java.util.LinkedList;

/**
 * The Adjacency List representation of the flight network.
 * It is a LinkedList of CityNode objects (a linked list of linked lists).
 */
public class FlightGraph {
    // The master list of all CityNode objects (the Adjacency List)
    private final LinkedList<CityNode> adjacencyList;

    public FlightGraph() {
        this.adjacencyList = new LinkedList<>();
    }

    /**
     * Helper to find a CityNode by name, creating it if it doesn't exist.
     */
    private CityNode findOrCreateCity(String name) {
        for (CityNode city : adjacencyList) {
            if (city.getName().equalsIgnoreCase(name)) {
                return city;
            }
        }
        // If not found, create a new city and add it to the graph
        CityNode newCity = new CityNode(name);
        adjacencyList.add(newCity);
        return newCity;
    }

    /**
     * Adds a flight and its cost/time to the graph. 
     * Handles bi-directional assumption.
     */
    public void addFlightPath(String origin, String destination, int cost, int time) {
        // 1. Add Origin -> Destination
        CityNode originCity = findOrCreateCity(origin);
        originCity.addFlight(destination, cost, time);

        // 2. Add Destination -> Origin (Assumption: flights are bi-directional )
        CityNode destCity = findOrCreateCity(destination);
        destCity.addFlight(origin, cost, time);
    }

    /**
     * Used by the PathFinder to get a city's neighbors.
     */
    public CityNode getCity(String name) {
        for (CityNode city : adjacencyList) {
            if (city.getName().equalsIgnoreCase(name)) {
                return city;
            }
        }
        return null; // City not in the network
    }
}