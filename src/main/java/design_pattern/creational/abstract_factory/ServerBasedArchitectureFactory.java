package design_pattern.creational.abstract_factory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerBasedArchitectureFactory implements InfrastructureFactory {
  @Override
  public IRelationalDB getRelationalDB(String dBName) {
    return new MariaDB();
  }

  @Override
  public INonRelationalDB getNonRelationalDB(String dBName) {
    return new MongoDB();
  }
}
