import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Sort using Java's ParallelStreams and Lambda functions.
 *
 * Hints:
 * - Do not take advice from StackOverflow.
 * - Think outside the box.
 *      - Stream of threads?
 *      - Stream of function invocations?
 *
 * By default, the number of threads in parallel stream is limited by the
 * number of cores in the system. You can limit the number of threads used by
 * parallel streams by wrapping it in a ForkJoinPool.
 *      ForkJoinPool myPool = new ForkJoinPool(threads);
 *      myPool.submit(() -> "my parallel stream method / function");
 */

public class ParallelStreamSort implements Sorter {
        public final int threads;

        public ParallelStreamSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                ForkJoinPool myPool = new ForkJoinPool(threads);

                // TODO: sort arr.
                Future<?> start = myPool.submit(() -> quicksort(arr, 0, arr.length - 1));
                try {
                        start.get();
                } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                myPool.shutdown();
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

        public void quicksort(int[] array, int low, int high) {

                if (low >= high) {
                        return;
                }

                // Step 1: Pick a pivot and rearrange elements around it in-place
                int pivot_index = partition(array, low, high);

                if (high - low < 4096) {
                        // Step 2: Recurse on the left sub-array (elements before pivot)
                        quicksort(array, low, pivot_index - 1);

                        // Step 3: Recurse on the right sub-array (elements after pivot)
                        quicksort(array, pivot_index + 1, high);
                        return;
                }

                Runnable[] tasks = {
                                () -> quicksort(array, low, pivot_index - 1),
                                () -> quicksort(array, pivot_index + 1, high)
                };

                Stream.of(tasks).parallel().forEach(task -> task.run());
        }

        public int getThreads() {
                return threads;
        }
}
