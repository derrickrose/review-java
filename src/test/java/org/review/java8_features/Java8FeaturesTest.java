package org.review.java8_features;

import org.junit.Ignore;
import org.junit.Test;
import java_8_features.function.Car;
import java_8_features.function.UseOfFunction;
import java_8_features.interface_revolution.default_method.Alien;
import java_8_features.interface_revolution.functional_interface.WorkFromHome;
import java_8_features.interface_revolution.static_method.SuperReproduction;
import java_8_features.lambda.UseOfLambda;
import java_8_features.reference_to_a_method.UseReferenceToAMethod;
import java_8_features.stream.Moto;
import java_8_features.stream.TestStreamWithGenerate;
import java_8_features.stream.UseOfStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8FeaturesTest {
  @Ignore
  @Test
  public void java8FeaturesInterfaceRevolution_Test() {
    Alien a = new Alien();
    a.reproduce();
    a.redef();

    SuperReproduction.description();
  }

  @Ignore
  @Test
  public void functionalInterface_Test() {
    WorkFromHome workFromHome = new WorkFromHome();
    workFromHome.locationType();
  }

  @Ignore
  @Test
  public void lambda_Test() {
    UseOfLambda.transportation.calculatePrice(2);
    UseOfLambda.transportationUsingLambda.calculatePrice(2);
    System.out.println("to sum values : " + UseOfLambda.sum.sum(2, 6));
  }

  @Ignore
  @Test
  public void useOfFunction_Test() {
    boolean testNotNull = UseOfFunction.isNotNull.test("");
    System.out.println("value of testNotNull " + testNotNull);
  }

  @Test
  @Ignore
  public void useOfConsumer_Test() {
    List<Car> cars = Arrays.asList(new Car(), new Car());
    // using lambda
    cars.forEach((car) -> UseOfFunction.initializeBrand.accept(car));

    // using reference to a method
    // cars.forEach(UseOfFunction.initializeBrand::accept);

    cars.forEach(
        (car) -> {
          System.out.println("brand set " + car.getBrand());
        });

    Car car1 = cars.get(0);
    car1.BrandConsumer.accept("kia");

    System.out.println("new brand set " + car1.BrandSupplier.get());
  }

  @Ignore
  @Test
  public void useOfReferenceToMethod_Test() {
    Function<String, UseReferenceToAMethod> create = UseReferenceToAMethod::new;
    UseReferenceToAMethod instance = create.apply("test");
    Consumer<String> print = System.out::println;
    print.accept("value " + instance.nameHandling.get());
  }

  @Ignore
  @Test
  public void functionComposition_Test() {
    Function<String, String> replace = (str) -> str.replace("s", "r");
    Function<String, String> cleanSpace = (str) -> str.replace(" ", "");
    String str = replace.andThen(cleanSpace).apply("s s");
    System.out.println("after using compose function " + str);
  }

  @Test
  public void useOfStream_Test() {
    Stream<String> stringStream = UseOfStream.strings.stream();
    stringStream.forEach(System.out::println);
    Stream<Moto> motoStream = UseOfStream.motos.stream();
    System.out.println("--------------- all motos ---------------");
    motoStream.forEach(System.out::println);
    // note that stream is directly closed once it is used
    motoStream = UseOfStream.motos.stream();
    System.out.println("--------------- filtering on yamaha ---------------");
    motoStream
        .filter((moto) -> moto.getBrand().equalsIgnoreCase("Yamaha"))
        .forEach(System.out::println);
    // iterate, a method produced by Stream class itself is an example of endless
    System.out.println("--------------- using iterate ---------------");
    Stream.iterate(1, (x) -> x + 1).limit(100).filter((x) -> x < 5).forEach(System.out::println);
    System.out.println("--------------- using generate ---------------");
    Stream.generate(Moto::new).limit(2).forEach(System.out::println);
    System.out.println("--------------- my own generate test ---------------");
    TestStreamWithGenerate.generate(() -> 1 + "x").limit(2).forEach(System.out::println);
    System.out.println("--------------- use of map ---------------");
    UseOfStream.motos.stream()
        .map(Moto::getBrand)
        .filter((x) -> x.equalsIgnoreCase("yamaha"))
        .forEach(System.out::println);
    System.out.println("--------------- use of collect ---------------");
    Set<String> notYamahaBrands =
        UseOfStream.motos.stream()
            .map(Moto::getBrand)
            .filter((x) -> !x.equalsIgnoreCase("yamaha"))
            .collect(Collectors.toSet());
    System.out.println("count " + notYamahaBrands.stream().count());
    notYamahaBrands.forEach(System.out::println);
    System.out.println("--------------- use of stream with nio ---------------");
    String fileName = "my_fic.txt";
    try (Stream<String> sf = Files.lines(Paths.get(fileName))) {
      sf.forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
