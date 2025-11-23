import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Implements the HeapSort algorithm using a Min-Heap structure to sort
 * PathResult objects by cost or time (determined by PathResult.compareTo).
 */
public class PathSorter {

    // Internal array to manage the heap elements
    private PathResult[] heap;
    private int size; // Tracks the current size of the heap portion of the array

    /**
     * Sorts the list of paths using the HeapSort algorithm (Min-Heap).
     * @param paths The list of PathResult objects to be sorted.
     * @return A List of PathResult objects sorted from most efficient (lowest cost/time)
     * to least efficient (highest cost/time).
     */
    public List<PathResult> heapSort(List<PathResult> paths) {
        if (paths == null || paths.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Initialize Heap Array
        this.heap = paths.toArray(new PathResult[0]);
        this.size = paths.size();

        // 2. Build the Heap (Min-Heapify Phase)
        // Start from the last non-leaf node
        for (int i = size / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }

        // 3. Sorting Down Phase (Extracting the minimum elements)
        // After this loop, the array is sorted DESCENDINGLY (largest to smallest).
        for (int i = size - 1; i > 0; i--) {
            // Swap root (smallest) with the last unsorted element
            swap(0, i);

            // Reduce the size of the heap portion
            size--;

            // Restore the Min-Heap property on the reduced heap
            minHeapify(0);
        }

        // --- CRITICAL FIX: REVERSE THE ORDER ---
        // The array is now sorted from largest to smallest. We need smallest to largest.
        List<PathResult> sortedList = new ArrayList<>(Arrays.asList(this.heap));
        Collections.reverse(sortedList);

        // Reset size back to full array length for proper object state (cleanup)
        this.size = this.heap.length;

        // Return the list, now correctly ordered from most efficient (min) to least efficient (max).
        return sortedList;
    }

    /**
     * Restores the Min-Heap property starting at a given index.
     */
    private void minHeapify(int i) {
        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // Check if the left child exists and is smaller than the current smallest
        if (left < size && heap[left].compareTo(heap[smallest]) < 0) {
            smallest = left;
        }

        // Check if the right child exists and is smaller than the current smallest
        if (right < size && heap[right].compareTo(heap[smallest]) < 0) {
            smallest = right;
        }

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