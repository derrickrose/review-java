package design_pattern.creational.abstract_factory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MariaDB implements IRelationalDB {
  @Override
  public String getName() {
    return "I am MariadDB - I am a server based relational db";
  }
}
