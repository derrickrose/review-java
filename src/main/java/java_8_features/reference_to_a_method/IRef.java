package java_8_features.reference_to_a_method;

@FunctionalInterface
public interface IRef {
  String getValue(String str);

  static String getValue2(String str) {
    return str;
  }
}
