public class SequentialSort implements Sorter {

        public SequentialSort() {
        }

        public void sort(int[] arr) {
                // TODO Implement your sorting algorithm.
                if (arr == null || arr.length <= 1) {
                        return;
                 }

                quicksort(arr,0,arr.length-1);

        }


        public void quicksort(int[] arr,int low,int high){

                if (low >= high) {
                         return;
                }   

                int partition =partition(arr, low, high);
                quicksort(arr, low, partition-1);
                quicksort(arr, partition+1, high);

        }

        public int partition(int[] array,int low, int high) {
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

        public int getThreads() {
                return 1;
        }
}
