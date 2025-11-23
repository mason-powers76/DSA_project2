import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Implements the HeapSort algorithm using a Min-Heap structure to sort
 * FlightPlan objects by cost or time.
 */
public class PathSorter {

    // Internal array to manage the heap elements
    private PathResult[] heap;
    private int size;

    /**
     * Sorts the list of paths using the HeapSort algorithm (Min-Heap).
     * @param paths The list of PathResult objects to be sorted.
     * @return A List of PathResult objects sorted from most efficient to least efficient.
     */
    public List<PathResult> heapSort(List<PathResult> paths) {
        // Handle edge case of an empty list
        if (paths == null || paths.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Initialize Heap Array
        // Convert List to Array for efficient heap indexing
        this.heap = paths.toArray(new PathResult[0]);
        this.size = paths.size();

        // 2. Build the Heap (Min-Heapify Phase)
        // Start from the last non-leaf node (size/2 - 1)
        for (int i = size / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }

        // 3. Sorting Down Phase (Extracting the minimum elements)
        // Move the smallest element (root) to the end of the array one by one
        // Note: The loop condition ensures the heap portion shrinks
        for (int i = size - 1; i > 0; i--) {
            // Swap root (smallest) with the last element of the unsorted portion
            swap(0, i);

            // The largest index (i) is now sorted, so reduce the heap size
            size--;

            // Restore the Min-Heap property on the reduced heap
            minHeapify(0);
        }

        // The heap array is now fully sorted. Return it as a List.
        return new ArrayList<>(Arrays.asList(this.heap));
    }

    /**
     * Restores the Min-Heap property starting at a given index.
     */
    private void minHeapify(int i) {
        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // Check if the left child exists and is smaller than the current smallest
        // Comparison relies on PathResult.compareTo() which uses the sortBy flag ('T' or 'C')
        if (left < size && heap[left].compareTo(heap[smallest]) < 0) {
            smallest = left;
        }

        // Check if the right child exists and is smaller than the current smallest
        if (right < size && heap[right].compareTo(heap[smallest]) < 0) {
            smallest = right;
        }

        // If the smallest is not the root, swap and continue heapifying down
        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    /**
     * Helper method to swap two elements in the array.
     */
    private void swap(int i, int j) {
        PathResult temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}