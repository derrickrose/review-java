package design_pattern.creational.abstract_factory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RdsDB implements IRelationalDB {
  @Override
  public String getName() {
    return "I am AWSRDSDB - I am a cloud relational db";
  }
}
