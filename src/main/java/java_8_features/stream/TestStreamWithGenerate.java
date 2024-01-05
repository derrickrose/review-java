package java_8_features.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TestStreamWithGenerate {

  private static int Limit = 10;

  private static int Start = 0;

  private static Stream<String> stream;

  private static Supplier<String> supplier;

  public static Stream<String> generate(Supplier<String> supplierCode) {
    supplier = supplierCode;
    List<String> strings = new ArrayList<String>();
    for (int i = Start; i < Limit; i++) {
      strings.add(supplier.get());
    }
    stream = strings.stream();
    return stream;
  }

}
