package creational.abstract_factory;

import old.creational.abstract_factory.IRelationalDB;
import creational.abstract_factory.InfrastructureFactory;
import org.junit.Test;

public class AbstractFactoryTest {

  @Test
  public void abstractFactory_Test() {
    InfrastructureFactory infrastructureFactory =
        InfrastructureFactory.getFactoryInstance("serverless");
    IRelationalDB db = infrastructureFactory.getRelationalDB(null);
    System.out.println("db " + db.getName());
  }
}
