package algo.sorting;

import algo.sorting.BubbleSort;
import org.junit.Test;

public class BubbleSortTest {

  @Test
  public void arraysBubbleSort_Test() {
    // initialisation simple
    int[] array = new int[5];
    array[0] = 1;
    array[1] = 2;
    array[2] = 3;
    array[3] = 4;
    array[4] = 5;
    // initialisation with values
    int[] array1 = {64, 34, 25, 12, 22, 11, 90};
    // go through an array with index
    for (int indexArray = 0; indexArray < array.length; indexArray++) {
      System.out.println(array[indexArray]);
    }
    // go through an array without index
    for (int value : array) {
      System.out.println(value);
    }
    System.out.println("\nbefore the sort " + array1);
    for (int value : array1) {
      System.out.print(" " + value + "");
    }
    // sort an array ascendant
    array1 = BubbleSort.orderAscendant(array1);
    System.out.println("\nafter the sort " + array1);
    for (int value : array1) {
      System.out.print(" " + value + "");
    }
    int previous = Integer.MIN_VALUE;
    // the idea here is to iterate the array and compare each value with its predecessor
    for (int value : array1) {
      assert previous <= value;
      previous = value;
    }

    // sort the array in descendant
    array1 = BubbleSort.orderDescendant(array1);
    System.out.println("\nafter the sort descendant");
    for (int value : array1) {
      System.out.print(" " + value + "");
    }

    // the idea here is to iterate the array and compare each value with its predecessor
    for (int value : array1) {
      assert previous >= value;
      previous = value;
    }
  }
}
