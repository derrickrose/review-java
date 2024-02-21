package old.java_8_features.interface_revolution.default_method;

import old.java_8_features.interface_revolution.static_method.Reproduction;

public interface Mitosis extends Reproduction {

  public static void description() {
    Reproduction.description();
    System.out.println("redefined inside Mitosis");
  }

  public default void reproduce() {
    System.out.println("I replicate");
  }

  public default void redef() {
    System.out.println("redef inside Mitosis");
  }
}
