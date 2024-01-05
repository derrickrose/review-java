package java_8_features.interface_revolution.static_method;
/**
 * This class is used to show off JAVA static method inside an interface. This is only valid
 * starting from java8
 *
 * @author frils
 * @version 1.0
 * @since 1.0 on 2020/10/26
 * @ref
 *     https://openclassrooms.com/fr/courses/26832-apprenez-a-programmer-en-java/5012356-decouvrez-java-8-et-la-revolution-des-interfaces
 */
public interface Reproduction {

  public static void description() {
    System.out.println("Static method inside an interface");
  }
}
