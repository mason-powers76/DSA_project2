import java.util.LinkedList;

public class CityNode {
    private final String name;
    // The linked list of outgoing flights from this city
    private final LinkedList<FlightEdge> flights;

    public CityNode(String name) {
        this.name = name;
        this.flights = new LinkedList<>();
    }

    public void addFlight(String destination, int cost, int time) {
        FlightEdge newFlight = new FlightEdge(destination, cost, time);
        this.flights.add(newFlight);
    }

    public String getName() {
        return name;
    }

    public LinkedList<FlightEdge> getFlights() {
        return flights;
    }
}