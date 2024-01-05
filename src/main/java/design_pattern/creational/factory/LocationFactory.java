package design_pattern.creational.factory;

import lombok.ToString;
/**
 * This class is used to instantiate derived object from Location.
 *
 * @author frils
 * @version 1.0
 * @since 2020-07-17
 */
@ToString
public class LocationFactory {
  /**
   * This method is used to get accurate instance of Location.
   *
   * @param name This is the first parameter to getInstance method
   * @param numberInhabitants This is the second parameter to getInstance method
   * @param capitalName This is the third parameter to getInstance method
   * @return This returns an instance of Location.
   */
  public static Location getInstance(String name, long numberInhabitants, String capitalName) {
    Location location;
    if (capitalName == null) {
      location = setValues(new City(), name, numberInhabitants);
    } else {
      location = setValues(new Country(), name, numberInhabitants);
      ((Country) (location)).setCapital(capitalName);
    }
    return location;
  }

  private static Location setValues(Location location, String name, long numberInhabitants) {
    location.setName(name);
    location.setNumberInhabitants(numberInhabitants);

    return location;
  }
}
