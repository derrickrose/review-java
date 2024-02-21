package old.algo.sorting;

/*
 * This class is used to order array with selection approach.
 * @version 1.0
 * @since 1.O on 2020/07/18
 */
public class SelectionSort {
  /*
   * This is used to ascendant order a given array with selection way.
   *
   * @param array given array to order
   * @return it returns an ordered array
   * @ref https://www.geeksforgeeks.org/selection-sort/
   */
  public static int[] orderAscendant(int[] array) {
    // TODO validate parameter array
    // TODO optimize sorting
    int[] sortedArray = new int[array.length];
    int loopMinValue;
    int loopMinPosition = 0;
    for (int iteration = 0; iteration < sortedArray.length; iteration++) {
      loopMinValue = Integer.MAX_VALUE;
      for (int loop = 0; loop < array.length; loop++) {
        if (array[loop] < loopMinValue) {
          loopMinValue = array[loop];
          loopMinPosition = loop;
        }
      }
      array[loopMinPosition] = Integer.MAX_VALUE;
      sortedArray[iteration] = loopMinValue;
    }
    return sortedArray;
  }

  /*
   * This is used to descendant order a given array with selection way.
   *
   * @param given array to order
   * @return it returns an ordered array
   * @ref https://www.geeksforgeeks.org/selection-sort/
   */
  public static int[] orderDescendant(int[] array) {
    // TODO validate parameter array
    // TODO optimize sorting
    int[] sortedArray = new int[array.length];
    int loopMaxValue;
    int loopMaxPosition = 0;
    for (int iteration = 0; iteration < sortedArray.length; iteration++) {
      loopMaxValue = Integer.MIN_VALUE;
      for (int loop = 0; loop < array.length; loop++) {
        if (array[loop] > loopMaxValue) {
          loopMaxValue = array[loop];
          loopMaxPosition = loop;
        }
      }
      array[loopMaxPosition] = Integer.MIN_VALUE;
      sortedArray[iteration] = loopMaxValue;
    }
    return sortedArray;
  }
}
