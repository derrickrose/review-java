package old.java_8_features.function;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
public class Car {
  private String brand;

  public Consumer<String> BrandConsumer = (value) -> this.brand = value;
  public Supplier<String> BrandSupplier = () -> this.brand;
}
