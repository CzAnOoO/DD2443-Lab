
/**
 * Sort using Java's ForkJoinPool.
 */

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
        public final int threads;

        public ForkJoinPoolSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                if (arr == null || arr.length <= 1) {
                        return;
                }

                ForkJoinPool pool = new ForkJoinPool(threads);

                try {
                        pool.invoke(new Worker(arr, 0, arr.length - 1));
                } finally {
                        pool.shutdown();
                }

                // TODO: sort arr.
        }

        public int getThreads() {
                return threads;
        }

        // worker ----------------

        private static class Worker extends RecursiveAction {

                private final int[] array;
                private final int low;
                private final int high;

                Worker(int[] array, int low, int high) {
                        this.array = array;
                        this.high = high;
                        this.low = low;
                }

                protected void compute() {

                        if (low >= high) {
                                return;
                        }

                        int pivot_index = partition(array, low, high);

                        if (high - low < 4096) {
                                quicksort(array, low, pivot_index - 1);

                                quicksort(array, pivot_index + 1, high);
                                return;
                        }

                        Worker leftTask = new Worker(array, low, pivot_index - 1);
                        Worker rightTask = new Worker(array, pivot_index + 1, high);

                        // Fork the left task into the work-stealing queue
                        leftTask.fork();

                        // Run the right task directly on the current worker thread
                        rightTask.compute();

                        // Wait for the left task to finish
                        leftTask.join();
                }

                public void quicksort(int[] arr, int low, int high) {

                        if (low >= high) {
                                return;
                        }

                        int partition = partition(arr, low, high);
                        quicksort(arr, low, partition - 1);
                        quicksort(arr, partition + 1, high);

                }

                public int partition(int[] array, int low, int high) {
                        // Pick the last element as pivot (or median of three)
                        // int middle = low + (high - low) / 2;
                        int mid = low + (high - low) / 2;
                        swap(array, mid, high);
                        int pivot = array[high];
                        // swap(array,middle,high);

                        // Boundary pointer for elements <= pivot
                        int i = low - 1;

                        // Scan all elements up to high - 1
                        for (int j = low; j < high; j++) {
                                if (array[j] <= pivot) {
                                        i++;
                                        swap(array, i, j); // Move smaller element to the left region
                                }
                        }

                        // Put the pivot in its permanent sorted spot right after the left region
                        swap(array, i + 1, high);

                        // Return pivot's final index
                        return i + 1;
                }

                private void swap(int[] array, int a, int b) {
                        int temp = array[a];
                        array[a] = array[b];
                        array[b] = temp;
                }

        }
}
