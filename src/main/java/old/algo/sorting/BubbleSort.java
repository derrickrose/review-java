package old.algo.sorting;
/**
 * This class is used to master JAVA Array for the amazon recruitment assessment test.
 *
 * @author frils
 * @version 1.0
 * @since 1.0 on 2020/07/18
 * @ref https://www.geeksforgeeks.org/bubble-sort/?ref=lbp
 */
public class BubbleSort {

  /**
   * This method is used to ascendant order an array of primitive int.
   *
   * @param array This is the unique param of orderAscendant method
   * @return This returns the given array now ordered ascendant
   */
  public static int[] orderAscendant(int[] array) {
    // TODO optimize
    int updateTimes;
    int nbr = 0;
    do {
      nbr++;
      updateTimes = 0;
      for (int index = 0; index < array.length - 1; index++) {
        if (array[index] > array[index + 1]) {
          updateTimes++;
          int currentValue = array[index];
          array[index] = array[index + 1];
          array[index + 1] = currentValue;
        }
      }
    } while (updateTimes != 0);
    System.out.println("\nnbr " + nbr);
    return array;
  }

  /**
   * This method is used to descendant order an array of primitive int.
   *
   * @param array This is the unique param of orderDescendant method
   * @return This returns the given array now ordered descendant
   */
  public static int[] orderDescendant(int[] array) {
    // TODO optimize
    int updateTimes;
    int nbr = 0;
    do {
      nbr++;
      updateTimes = 0;
      for (int index = 0; index < array.length - 1; index++) {
        if (array[index] < array[index + 1]) {
          updateTimes++;
          int currentValue = array[index];
          array[index] = array[index + 1];
          array[index + 1] = currentValue;
        }
      }
      if (updateTimes == 0) {
        break;
      }
    } while (updateTimes != 0);
    System.out.println("\nnbr " + nbr);
    return array;
  }
}
