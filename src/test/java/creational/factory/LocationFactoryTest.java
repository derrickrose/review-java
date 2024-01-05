package creational.factory;

import design_pattern.creational.factory.Country;
import design_pattern.creational.factory.Location;
import design_pattern.creational.factory.LocationFactory;
import org.junit.Test;

public class LocationFactoryTest {

    @Test
    public void getInstanceFactory_Test() {
        Location location = LocationFactory.getInstance("Madagascar", 22_000_000l, "Antananarivo");
        System.out.println(location.toString());
        System.out.println(location instanceof Country);

        location = LocationFactory.getInstance("Antananarivo", 5_000_000l, null);
        System.out.println(location.toString());
        System.out.println(location instanceof Country);
    }
}
