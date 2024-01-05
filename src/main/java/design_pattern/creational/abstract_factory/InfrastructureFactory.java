package design_pattern.creational.abstract_factory;
/*
 * This is used to create DBConnectionObject according to existing infrastructure.
 *
 * @author frils
 * @version 1.0
 * @since 1.0 on 2020/07/21
 * @reference GeeksForGeeks
 */
interface InfrastructureFactory {

  public IRelationalDB getRelationalDB(String dBName);

  public INonRelationalDB getNonRelationalDB(String dBName);

  public static InfrastructureFactory getFactoryInstance(String infra) {
    if (infra.equalsIgnoreCase("serverless")) {
      return new ServerlessArchitectureFactory();
    } else {
      return new ServerBasedArchitectureFactory();
    }
  }
}
