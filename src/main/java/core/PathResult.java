package core;

import java.util.LinkedList;

//represents the paths that are found
public class PathResult implements Comparable<PathResult> {
    private final LinkedList<String> path;
    private final int totalCost;
    private final int totalTime;

    // Flag set during the search/sorting phase to determine comparison criteria
    private char sortBy;

    public PathResult(LinkedList<String> path, int cost, int time, char sortBy) {

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

    //comparator for the paths to determine the cost and time for each
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

}