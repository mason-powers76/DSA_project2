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
    private int size;

    /**
     * Sorts the list of paths using the HeapSort algorithm (Min-Heap).
     * @param paths The list of PathResult objects to be sorted.
     * @return A List of PathResult objects sorted from most efficient to least efficient.
     */
    public List<PathResult> heapSort(List<PathResult> paths) {
        if (paths == null || paths.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Initialize Heap Array
        this.heap = paths.toArray(new PathResult[0]);
        this.size = paths.size();

        // 2. Build the Heap (Min-Heapify Phase)
        for (int i = size / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }

        // 3. Sorting Down Phase (Extracting the minimum elements)
        for (int i = size - 1; i > 0; i--) {
            // Swap root (smallest) with the last unsorted element
            swap(0, i);
            size--;
            minHeapify(0);
        }

        // The array is now fully sorted.
        return new ArrayList<>(Arrays.asList(this.heap));
    }

    /**
     * Restores the Min-Heap property starting at a given index.
     */
    private void minHeapify(int i) {
        int smallest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        // Check left child
        if (left < size && heap[left].compareTo(heap[smallest]) < 0) {
            smallest = left;
        }

        // Check right child
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