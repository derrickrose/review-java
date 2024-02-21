package old.java_8_features.reference_to_a_method;

import java.util.function.Consumer;
import java.util.function.Supplier;

/*
 * This class is used to highlight the use of reference to a method :: (feature available from java8)
 * Basically it is a way to minimize codes of lambda expressions
 * @version 1.0
 * @since 1.O on 2020/11/10
 */
public class UseReferenceToAMethod {

  private String name;

  public UseReferenceToAMethod(String s) {
    this.nameSetting.accept(s);
  }

  public Consumer<String> nameSetting = (name) -> this.name = name;
  public Supplier<String> nameHandling = () -> this.name;
  public IRef ref1 = (str) -> str;
  public IRef ref3 = IRef::getValue2;
  public IRef ref2 = ref1::getValue;
}
