package algo.sorting;

import algo.sorting.QuickSort;
import org.junit.Test;

public class ArraysQuickSortTest {

  static void printArray(int arr[]) {
    int n = arr.length;
    for (int i = 0; i < n; ++i) System.out.print(arr[i] + " ");
    System.out.println();
  }

  @Test
  public void main() {
    int arr[] = {10, 7, 10, 9, 5, 5};
    int n = arr.length;

    QuickSort.sort(arr, 0, n - 1);

    System.out.println("sorted array");
    printArray(arr);
  }
}
