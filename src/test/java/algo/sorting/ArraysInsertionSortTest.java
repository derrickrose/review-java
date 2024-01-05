package algo.sorting;

import algo.sorting.InsertionSort;
import org.junit.Test;

public class ArraysInsertionSortTest {
  @Test
  public void arraysInsertionSortAscendant_Test() {
    int[] array = {23, 20, 3, 0, 5, 5, 8, 4, 2, 0, 20};
    System.out.println("\nbefore the sort ascendant");
    for (int value : array) {
      System.out.print(" " + value + "");
    }
    array = InsertionSort.orderAscendant(array);
    System.out.println("\nafter the sort ascendant");
    for (int value : array) {
      System.out.print(" " + value + "");
    }
  }
}
