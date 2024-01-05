package algo.zero_of_function;

import org.junit.Test;

import java.util.function.Function;

import static algo.zero_of_function.ZeroOfFunction.doSearchRange;

public class ZeroOfFunctionTest {

  @Test
  public void searchRange() {

    // Function<Double, Double> function = (x) -> Math.pow(x, 2) - 4;
    Function<Double, Double> function = (x) -> x - 2d;

    double[] range = doSearchRange(function, -10d, 10d, 0.01);

    System.out.println(range[0] + " - " + range[1]);

    int a = 0;
  }
}
