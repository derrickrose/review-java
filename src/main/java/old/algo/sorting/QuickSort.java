package old.algo.sorting;
/*
 * This class is used to master quicksort for amz assessment.
 *
 * @author randofrils
 * @version 1.0.0
 * @since version 1.0.0 on 2020/07/20
 * @reference geeksforgeeks quicksort
 */
public class QuickSort {
  /* This function takes last element as pivot,
  places the pivot element at its correct
  position in sorted array, and places all
  smaller (smaller than pivot) to left of
  pivot and all greater elements to right
  of pivot */
  public static int partition(int array[], int low, int high) {
    // TODO validate parameter
    int pivot = array[high];
    int smallerElementIndex = (low - 1); // index of smaller element
    for (int index = low; index < high; index++) {
      // If current element is smaller than the pivot
      if (array[index] < pivot) {
        smallerElementIndex++;

        // swap arr[smallerElementIndex] and arr[index]
        int temp = array[smallerElementIndex];
        array[smallerElementIndex] = array[index];
        array[index] = temp;
      }
    }

    // swap arr[smallerElementIndex+1] and arr[high] (or pivot)
    int temp = array[smallerElementIndex + 1];
    array[smallerElementIndex + 1] = array[high];
    array[high] = temp;

    return smallerElementIndex + 1;
  }

  /*
   * Quicksort main function
   *
   * @param array[] --> Array to be sorted,
   * @param low  --> Starting index,
   * @param high  --> Ending index
   */
  public static void sort(int array[], int low, int high) {
    // TODO validate parameter
    if (low < high) {
      /* partitionIndex is partitioning index, array[partitionIndex] is
      now at right place */
      int partitionIndex = partition(array, low, high);

      // Recursively sort elements before
      // partition and after partition
      sort(array, low, partitionIndex - 1);
      sort(array, partitionIndex + 1, high);
    }
  }
}
