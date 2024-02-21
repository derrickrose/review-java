package old.java_8_features.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * This class is used to highlight the use of some java functions
 * @ref java.util.function
 * @version 1.0
 * @since 1.O on 2020/11/09
 */
public class UseOfFunction {

  public static Function<String, Void> printString =
      new Function<String, Void>() {
        @Override
        public Void apply(String s) {
          System.out.println("the value sent in parameter is " + s);
          return null;
        }
      };

  public static Function<String, Void> printStringUsingLambda =
      (value) -> {
        System.out.println("{using lambda expressions} the value sent in parameter is " + value);
        return null;
      };

  public static Function<Integer, Integer> multiplyByTwo =
      (arg1) -> Integer.valueOf(arg1.intValue() * 2);

  public static Predicate<String> isNotNull = (value) -> value != null;

  public static Consumer<Car> initializeBrand = (car) -> car.setBrand("unknown");





}
