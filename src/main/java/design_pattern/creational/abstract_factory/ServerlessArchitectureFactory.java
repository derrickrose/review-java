package design_pattern.creational.abstract_factory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerlessArchitectureFactory implements InfrastructureFactory {
  @Override
  public IRelationalDB getRelationalDB(String dBName) {
    return new RdsDB();
  }

  @Override
  public INonRelationalDB getNonRelationalDB(String dBName) {
    return new DynamoDB();
  }
}
