package java_8_features.interface_revolution.default_method;
/**
 * This class is used to show off redefining JAVA default method inside a class that extends from 2
 * interfaces with the same method name. The class will fail to compile since it doesn't know what
 * implementation to use. We are forced to redefine the method here.
 *
 * @author frils
 * @version 1.0
 * @since 1.0 on 2020/10/26
 * @ref
 *     https://openclassrooms.com/fr/courses/26832-apprenez-a-programmer-en-java/5012356-decouvrez-java-8-et-la-revolution-des-interfaces
 */
public class Alien implements Lay, Mitosis {

  @Override
  public void reproduce() {
    Lay.super.reproduce();
    Mitosis.super.reproduce();
  }

}
