public class FlightEdge {
    private final String destination;
    private final int cost; // Dollar cost
    private final int time; // Time in minutes

    public FlightEdge(String destination, int cost, int time) {
        this.destination = destination;
        this.cost = cost;
        this.time = time;
    }

    public String getDestination() {
        return destination;
    }

    public int getCost() {
        return cost;
    }

    public int getTime() {
        return time;
    }
}