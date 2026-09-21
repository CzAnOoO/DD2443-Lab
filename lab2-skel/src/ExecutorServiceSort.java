
/**
 * Sort using Java's ExecutorService.
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceSort implements Sorter {
        public final int threads;

        public ExecutorServiceSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                // TODO Implement your sorting algorithm.
                if (arr == null || arr.length <= 1) {
                        return;
                }
                // This will initiate the sort
                ExecutorService pool = Executors.newFixedThreadPool(threads);

                // https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
                int deepT = 31 - Integer.numberOfLeadingZeros(threads);

                Future<?> start = pool.submit(new Worker(arr, 0, arr.length - 1, pool, deepT));

                try {
                        start.get();
                } catch (Exception e) {
                        // TODO: handle exception
                }

                pool.shutdown();

        }

        public int getThreads() {
                return threads;
        }

        private static class Worker implements Runnable {
                int[] array;
                int low;
                int high;
                ExecutorService pool;
                int deepT;

                Worker(int[] array, int low, int high, ExecutorService pool, int deepT) {
                        this.array = array;
                        this.low = low;
                        this.high = high;
                        this.pool = pool;
                        this.deepT = deepT;
                }

                public void run() {
                        quicksort(array, low, high, deepT);
                }

                // https://www.youtube.com/watch?v=Hoixgm4-P4M
                private void swap(int[] array, int a, int b) {
                        int temp = array[a];
                        array[a] = array[b];
                        array[b] = temp;
                }

                public int partition(int[] array, int low, int high) {
                        // Pick the last element as pivot (or median of three)
                        int mid = low + (high - low) / 2;
                        swap(array, mid, high);
                        int pivot = array[high];

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

                public void quicksort(int[] array, int low, int high, int deepT) {

                        if (low >= high) {
                                return;
                        }
                        // Create two treads 
                        // ExecutorService executor = Executors.newFixedThreadPool(2);

                        // Step 1: Pick a pivot and rearrange elements around it in-place
                        int pivot_index = partition(array, low, high);

                        if (deepT > 0) {
                                deepT--;
                                // Step 2: Recurse on the left sub-array (elements before pivot)
                                // quicksort(array, low, pivot_index - 1, deepT);
                                Future<?> left = pool.submit(new Worker(array, low, pivot_index - 1, pool, deepT));

                                // Step 3: Recurse on the right sub-array (elements after pivot)
                                //Future<?> right = pool.submit(new Worker(array, pivot_index + 1, high, pool, deepT));
                                quicksort(array, pivot_index + 1, high, deepT);

                                try {
                                        left.get();
                                        // right.get();
                                } catch (Exception e) {
                                        e.printStackTrace();
                                        // TODO: handle exception
                                }
                                return;
                        }

                        quicksort(array, low, pivot_index - 1, deepT);
                        quicksort(array, pivot_index + 1, high, deepT);
                }
        }
}
