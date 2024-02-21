package old.creational.abstract_factory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DynamoDB implements INonRelationalDB {
  @Override
  public String getName() {
    return "I am DynamoDB - I am a non relational cloud db";
  }
}
