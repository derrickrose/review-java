package old.java_8_features.stream;

import java.util.Arrays;
import java.util.List;

/*
 * This class is used to highlight the use of stream available from java8
 * Basically it is the replacement of the Iterator pattern
 * @version 1.0
 * @since 1.O on 2020/11/11
 */
public class UseOfStream {

  public static final List<String> strings = Arrays.asList("one", "two", "three", "four", "five");

  public static final List<Moto> motos =
      Arrays.asList(
          new Moto("yamaha", "fz1", "2011"),
          new Moto("yamaha", "f6", "2018"),
          new Moto("honda", "mz10", "2015"),
          new Moto("suzuki", "s600", "2020"),
          new Moto("kawazaki", "k8", "2008"),
          new Moto("bmw", "b400", "2012"),
          new Moto("honda", "mZ9", "2017"));
}
