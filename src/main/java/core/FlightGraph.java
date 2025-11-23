package core;

import java.util.LinkedList;

//creates a graph from the city nodes and flight edges
public class FlightGraph {

    private final LinkedList<CityNode> adjacencyList;

    public FlightGraph() {
        this.adjacencyList = new LinkedList<>();
    }


    private CityNode findOrCreateCity(String name) {
        for (CityNode city : adjacencyList) {
            if (city.getName().equalsIgnoreCase(name)) {
                return city;
            }
        }
        CityNode newCity = new CityNode(name);
        adjacencyList.add(newCity);
        return newCity;
    }

    //detemines the paths for the graph
    public void addFlightPath(String origin, String destination, int cost, int time) {
        // 1. Add Origin -> Destination
        CityNode originCity = findOrCreateCity(origin);
        originCity.addFlight(destination, cost, time);

        // 2. Add Destination -> Origin
        CityNode destCity = findOrCreateCity(destination);
        destCity.addFlight(origin, cost, time);
    }

    //references the adjacency list to get the neighbor of a city
    public CityNode getCity(String name) {
        for (CityNode city : adjacencyList) {
            if (city.getName().equalsIgnoreCase(name)) {
                return city;
            }
        }
        return null; // City not in the network
    }
}