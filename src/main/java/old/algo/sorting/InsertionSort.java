package old.algo.sorting;
/*
 * This class is used to order array with insertion approach.
 * @version 1.0
 * @since 1.O on 2020/07/18
 */
public class InsertionSort {
  /*
   * This is used to ascendant order a given array with insertion way.
   *
   * @param array given array to order
   * @return it returns an ordered array
   * @ref https://www.geeksforgeeks.org/insertion-sort/
   */
  public static int[] orderAscendant(int[] array) {
    // TODO validate param array
    int temp;
    for (int loop = 0; loop < array.length; loop++) {
      for (int nestedLoop = 0; nestedLoop < array.length; nestedLoop++) {
        if (array[loop] < array[nestedLoop]) {
          temp = array[loop];
          array[loop] = array[nestedLoop];
          array[nestedLoop] = temp;
        }
      }
    }
    return array;
  }
}
