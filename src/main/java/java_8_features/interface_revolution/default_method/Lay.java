package java_8_features.interface_revolution.default_method;

import java_8_features.interface_revolution.static_method.Reproduction;

public interface Lay extends Reproduction {

  public static void description() {
    Reproduction.description();
    System.out.println("redefined inside Lay");
  }

  public default void reproduce() {
    System.out.println("I lay eggs");
  }

}
