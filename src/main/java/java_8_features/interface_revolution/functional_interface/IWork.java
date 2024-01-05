package java_8_features.interface_revolution.functional_interface;
/*
 * This class is used to highlight the use of functional interface in java
 * Basically it refers to an interface that have only one abstract method which is called function
 * @version 1.0
 * @since 1.O on 2020/11/09
 */
@FunctionalInterface
public interface IWork {
  void locationType();
  // If ever we add another abstract method here, it will present a compilation error
  // because of the annotation @FunctionalInterface
}
