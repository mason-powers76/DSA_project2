import java.util.LinkedList;

/**
 * Represents a complete, found flight plan (path) with its total metrics.
 * Implements Comparable for later use with HeapSort.
 */
public class PathResult implements Comparable<PathResult> {
    private final LinkedList<String> path;
    private final int totalCost;
    private final int totalTime;

    // Flag set during the search/sorting phase to determine comparison criteria
    private char sortBy;

    public PathResult(LinkedList<String> path, int cost, int time, char sortBy) {
        // Use a deep copy to ensure the path history is immutable
        this.path = new LinkedList<>(path);
        this.totalCost = cost;
        this.totalTime = time;
        this.sortBy = sortBy;
    }

    // Getters for cost, time, and path...
    public int getTotalCost() { return totalCost; }
    public int getTotalTime() { return totalTime; }
    public LinkedList<String> getPath() { return path; }
    public void setSortBy(char sortBy) { this.sortBy = sortBy; }

    /**
     * Compares this path to another based on the 'sortBy' flag (T for Time, C for Cost)[cite: 60].
     * Required for HeapSort implementation.
     */
    @Override
    public int compareTo(PathResult other) {
        if (sortBy == 'T') {
            return Integer.compare(this.totalTime, other.totalTime); // Sort by Time
        } else if (sortBy == 'C') {
            return Integer.compare(this.totalCost, other.totalCost); // Sort by Cost
        }
        // Default to Time if flag is invalid
        return Integer.compare(this.totalTime, other.totalTime);
    }

    // We will add a toString method later to handle the output format [cite: 68, 69, 70]
}