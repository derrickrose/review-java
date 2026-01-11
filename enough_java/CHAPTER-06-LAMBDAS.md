# Chapter 10: Lambda Expressions & Functional Interfaces

[← Previous Chapter](CHAPTER-09.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-11.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Lambda expressions and functional interfaces |
| **Java Versions** | 8 (Lambdas, Functional Interfaces) |
| **Code Examples** | 65+ |
| **Study Time** | 10-12 hours |
| **Exam Weight** | ~10-15% |
| **Difficulty** | ⭐⭐⭐⭐ High |

**Official 1Z0-830 Exam Objectives Covered:**
- Create and use lambda expressions including statement lambdas, local-variable for lambda parameters
- Use lambda expressions and method references with Streams
- Use built-in functional interfaces including Predicate, Consumer, Function, and Supplier
- Develop code that uses primitive and binary variations of functional interfaces
- Develop code that uses the UnaryOperator and BinaryOperator interfaces

---

## 1. Lambda Expression Basics

**Exam Objective:** Create and use lambda expressions

### What are Lambda Expressions?
Anonymous functions (methods without names) that treat functionality as method arguments. Introduced in Java 8 to enable functional programming.

### When to Use
- Implementing functional interfaces
- Stream operations
- Event handlers
- Callbacks
- Simplifying anonymous inner classes

### Why Use Lambda Expressions
- Concise syntax (less boilerplate)
- Enables functional programming
- Improves code readability
- Supports parallel processing
- Required for Stream API

### Real-World Example
```java
import java.util.*;
import java.util.function.*;

public class LambdaBasicsExample {
    public static void main(String[] args) {
        // 1. Traditional anonymous class
        Runnable r1 = new Runnable() {
            public void run() {
                System.out.println("Hello from anonymous class");
            }
        };
        
        // 2. Lambda expression - simplified
        Runnable r2 = () -> System.out.println("Hello from lambda");
        
        // 3. Lambda with single parameter
        Consumer<String> printer = s -> System.out.println(s);
        printer.accept("Hello");
        
        // 4. Lambda with multiple parameters
        BiFunction<Integer, Integer, Integer> adder = (a, b) -> a + b;
        System.out.println(adder.apply(5, 3)); // 8
        
        // 5. Lambda with type declarations
        BiFunction<Integer, Integer, Integer> multiplier = 
            (Integer a, Integer b) -> a * b;
        
        // 6. Lambda with block body
        Predicate<String> isLongWord = s -> {
            System.out.println("Checking: " + s);
            return s.length() > 5;
        };
        
        // 7. Lambda with return statement
        Function<String, Integer> length = s -> {
            return s.length();
        };
        
        // 8. Method reference (equivalent to lambda)
        Consumer<String> methodRef = System.out::println;
        
        // 9. Practical example - sorting
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // Traditional comparator
        Collections.sort(names, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        
        // Lambda comparator
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        
        // Method reference
        Collections.sort(names, String::compareTo);
        
        // 10. Lambda in streams
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .forEach(System.out::println);
    }
}
```
**Why:** UI event handling - button click handlers simplified from 5 lines to 1 line.

### Exam Traps
- ❌ **TRAP:** Lambda parameter types can be omitted or specified, not mixed
  ```java
  BiFunction<Integer, Integer, Integer> valid1 = (a, b) -> a + b; // ✅ Inferred
  BiFunction<Integer, Integer, Integer> valid2 = (Integer a, Integer b) -> a + b; // ✅ Explicit
  BiFunction<Integer, Integer, Integer> invalid = (Integer a, b) -> a + b; // ❌ Mixed!
  ```
- ❌ **TRAP:** Parentheses required for multiple parameters or zero parameters
  ```java
  Consumer<String> valid1 = s -> System.out.println(s); // ✅ Single param, no ()
  Consumer<String> valid2 = (s) -> System.out.println(s); // ✅ Optional ()
  BiConsumer<String, String> valid3 = (s1, s2) -> System.out.println(s1); // ✅ Multiple params need ()
  Runnable valid4 = () -> System.out.println("Hi"); // ✅ No params need ()
  ```
- ❌ **TRAP:** Curly braces require return statement for non-void
  ```java
  Function<Integer, Integer> valid = x -> x * 2; // ✅ Single expression
  Function<Integer, Integer> invalid = x -> { x * 2 }; // ❌ Missing return!
  Function<Integer, Integer> correct = x -> { return x * 2; }; // ✅ With return
  ```
- ❌ **TRAP:** Lambda can't access non-final local variables
  ```java
  int count = 0;
  Consumer<String> invalid = s -> count++; // ❌ count must be effectively final
  
  final int count2 = 0;
  Consumer<String> valid = s -> System.out.println(count2); // ✅ Final variable
  ```

---

## 2. Functional Interfaces

**Exam Objective:** Use built-in functional interfaces

### What are Functional Interfaces?
Interfaces with exactly one abstract method (SAM - Single Abstract Method). Can have default and static methods. Annotated with @FunctionalInterface.

### When to Use
- Target types for lambda expressions
- Method parameters accepting behavior
- Callback interfaces
- Stream API operations

### Why Use Functional Interfaces
- Enable lambda expressions
- Standardized API
- Type safety
- Composable operations

### Real-World Example
```java
import java.util.function.*;

// Custom functional interface
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    
    // Can have default methods
    default int square(int x) {
        return calculate(x, x);
    }
    
    // Can have static methods
    static int add(int a, int b) {
        return a + b;
    }
}

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        // 1. Custom functional interface
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        
        System.out.println(add.calculate(5, 3)); // 8
        System.out.println(multiply.calculate(5, 3)); // 15
        System.out.println(add.square(5)); // 25 (default method)
        
        // 2. Built-in functional interfaces
        
        // Predicate<T> - T -> boolean
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println(isEven.test(4)); // true
        
        // Consumer<T> - T -> void
        Consumer<String> print = s -> System.out.println(s);
        print.accept("Hello");
        
        // Function<T, R> - T -> R
        Function<String, Integer> length = s -> s.length();
        System.out.println(length.apply("Hello")); // 5
        
        // Supplier<T> - () -> T
        Supplier<Double> random = () -> Math.random();
        System.out.println(random.get());
        
        // 3. Existing functional interfaces
        Runnable task = () -> System.out.println("Running");
        Callable<String> callable = () -> "Result";
        Comparator<String> comparator = (s1, s2) -> s1.compareTo(s2);
        
        // 4. Composing functional interfaces
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isPositiveEven = isEven.and(isPositive);
        
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> trimAndUpper = trim.andThen(upper);
        
        // 5. Practical example - validation
        List<String> emails = Arrays.asList(
            "user@example.com",
            "invalid-email",
            "admin@test.com"
        );
        
        Predicate<String> isValidEmail = email -> 
            email.contains("@") && email.contains(".");
        
        List<String> validEmails = emails.stream()
            .filter(isValidEmail)
            .collect(Collectors.toList());
        
        System.out.println("Valid emails: " + validEmails);
    }
}
```
**Why:** Form validation - chain multiple validation predicates (email, phone, age) into single validator.

### Exam Traps
- ❌ **TRAP:** @FunctionalInterface is optional but recommended
  ```java
  // Valid functional interface without annotation
  interface MyInterface {
      void method();
  }
  
  // Compile-time check with annotation
  @FunctionalInterface
  interface SafeInterface {
      void method();
      // void another(); // ❌ Compilation error with annotation!
  }
  ```
- ❌ **TRAP:** Default and static methods don't count as abstract
  ```java
  @FunctionalInterface
  interface Valid {
      void abstractMethod(); // 1 abstract method
      default void defaultMethod() {} // Doesn't count
      static void staticMethod() {} // Doesn't count
  }
  ```
- ❌ **TRAP:** Inheriting Object methods doesn't count
  ```java
  @FunctionalInterface
  interface StillFunctional {
      void process();
      String toString(); // Doesn't count - inherited from Object
      boolean equals(Object obj); // Doesn't count
  }
  ```
- ❌ **TRAP:** Can't have two abstract methods
  ```java
  @FunctionalInterface
  interface Invalid {
      void method1();
      void method2(); // ❌ Compilation error! Two abstract methods
  }
  ```

---

## 3. Built-in Functional Interfaces: Predicate

**Exam Objective:** Use Predicate functional interface

### What is Predicate?
Functional interface that takes one argument and returns boolean. Used for conditional logic and filtering.

**Method signature:** `boolean test(T t)`

### When to Use
- Filtering collections
- Validation logic
- Conditional operations
- Stream filter operations

### Why Use Predicate
- Standard boolean tests
- Composable with and/or/negate
- Type-safe
- Reusable conditions

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PredicateExample {
    public static void main(String[] args) {
        // 1. Basic Predicate
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println(isEven.test(4)); // true
        System.out.println(isEven.test(5)); // false
        
        // 2. Predicate for strings
        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<String> startsWithA = s -> s.startsWith("A");
        
        // 3. Composing predicates with and()
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isPositiveEven = isEven.and(isPositive);
        
        System.out.println(isPositiveEven.test(4)); // true (even AND positive)
        System.out.println(isPositiveEven.test(-4)); // false (even but NOT positive)
        
        // 4. Composing with or()
        Predicate<String> isShort = s -> s.length() < 5;
        Predicate<String> isLong = s -> s.length() > 10;
        Predicate<String> isShortOrLong = isShort.or(isLong);
        
        // 5. Negating with negate()
        Predicate<Integer> isOdd = isEven.negate();
        System.out.println(isOdd.test(5)); // true
        
        // 6. Static method isEqual()
        Predicate<String> isHello = Predicate.isEqual("hello");
        System.out.println(isHello.test("hello")); // true
        System.out.println(isHello.test("world")); // false
        
        // 7. Filtering with stream
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        List<Integer> evenNumbers = numbers.stream()
            .filter(isEven)
            .collect(Collectors.toList());
        System.out.println("Even: " + evenNumbers);
        
        // 8. Multiple filter conditions
        List<Integer> positiveEven = numbers.stream()
            .filter(isEven)
            .filter(isPositive)
            .collect(Collectors.toList());
        
        // Or combined
        List<Integer> combined = numbers.stream()
            .filter(isEven.and(isPositive))
            .collect(Collectors.toList());
        
        // 9. Practical example - user validation
        class User {
            String name;
            int age;
            String email;
            
            User(String name, int age, String email) {
                this.name = name;
                this.age = age;
                this.email = email;
            }
        }
        
        List<User> users = Arrays.asList(
            new User("Alice", 25, "alice@example.com"),
            new User("Bob", 17, "bob@test"),
            new User("Charlie", 30, "charlie@example.com")
        );
        
        Predicate<User> isAdult = u -> u.age >= 18;
        Predicate<User> hasValidEmail = u -> u.email.contains("@") && u.email.contains(".");
        Predicate<User> isValidUser = isAdult.and(hasValidEmail);
        
        List<User> validUsers = users.stream()
            .filter(isValidUser)
            .collect(Collectors.toList());
        
        System.out.println("Valid users: " + validUsers.size());
        
        // 10. BiPredicate - two arguments
        BiPredicate<String, Integer> isLengthEqual = (s, len) -> s.length() == len;
        System.out.println(isLengthEqual.test("hello", 5)); // true
    }
}
```
**Why:** E-commerce product filtering - combine predicates for price range, category, rating, availability.

### Exam Traps
- ❌ **TRAP:** and() and or() are short-circuit operations
  ```java
  Predicate<String> alwaysFalse = s -> false;
  Predicate<String> neverCalled = s -> { 
      System.out.println("Called");
      return true;
  };
  
  alwaysFalse.and(neverCalled).test("hi"); // "Called" NOT printed - short-circuit
  ```
- ❌ **TRAP:** test() vs apply() - Predicate uses test()
  ```java
  Predicate<String> pred = s -> s.isEmpty();
  pred.test("hi"); // ✅ Correct
  pred.apply("hi"); // ❌ Compilation error! No apply() method
  ```
- ❌ **TRAP:** isEqual() is static, not instance method
  ```java
  Predicate<String> pred = s -> s.equals("hello");
  Predicate<String> same = Predicate.isEqual("hello"); // ✅ Static method
  ```
- ❌ **TRAP:** Primitive predicates don't extend Predicate
  ```java
  IntPredicate intPred = n -> n > 0;
  Predicate<Integer> pred = intPred; // ❌ Compilation error! Different interfaces
  ```

---

## 4. Built-in Functional Interfaces: Consumer

**Exam Objective:** Use Consumer functional interface

### What is Consumer?
Functional interface that takes one argument and returns void. Used for operations with side effects.

**Method signature:** `void accept(T t)`

### When to Use
- Printing/logging
- Modifying objects
- Performing actions without return
- forEach operations

### Why Use Consumer
- Side-effect operations
- Chainable with andThen()
- Standard void operations
- Stream forEach

### Real-World Example
```java
import java.util.*;
import java.util.function.*;

public class ConsumerExample {
    public static void main(String[] args) {
        // 1. Basic Consumer
        Consumer<String> print = s -> System.out.println(s);
        print.accept("Hello World");
        
        // 2. Consumer with method reference
        Consumer<String> methodRefPrint = System.out::println;
        
        // 3. Consumer modifying object
        class Employee {
            String name;
            double salary;
            
            Employee(String name, double salary) {
                this.name = name;
                this.salary = salary;
            }
        }
        
        Consumer<Employee> giveRaise = e -> e.salary *= 1.10; // 10% raise
        
        Employee emp = new Employee("Alice", 50000);
        giveRaise.accept(emp);
        System.out.println("New salary: " + emp.salary); // 55000
        
        // 4. Chaining with andThen()
        Consumer<String> upper = s -> System.out.println(s.toUpperCase());
        Consumer<String> lower = s -> System.out.println(s.toLowerCase());
        Consumer<String> both = upper.andThen(lower);
        
        both.accept("Hello"); // Prints: HELLO, then hello
        
        // 5. forEach with Consumer
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        names.forEach(print); // Consumer passed to forEach
        
        // 6. Multiple consumers
        Consumer<Employee> printName = e -> System.out.println("Name: " + e.name);
        Consumer<Employee> printSalary = e -> System.out.println("Salary: " + e.salary);
        Consumer<Employee> printEmployee = printName.andThen(printSalary);
        
        // 7. BiConsumer - two arguments
        BiConsumer<String, Integer> printWithCount = (s, n) -> {
            for (int i = 0; i < n; i++) {
                System.out.println(s);
            }
        };
        printWithCount.accept("Hello", 3);
        
        // 8. Practical example - logging
        List<String> errors = Arrays.asList("Error1", "Error2", "Error3");
        
        Consumer<String> logError = error -> System.err.println("[ERROR] " + error);
        Consumer<String> saveToFile = error -> {
            // Simulate file write
            System.out.println("Saving to file: " + error);
        };
        Consumer<String> sendAlert = error -> {
            // Simulate alert
            System.out.println("Sending alert: " + error);
        };
        
        Consumer<String> errorHandler = logError
            .andThen(saveToFile)
            .andThen(sendAlert);
        
        errors.forEach(errorHandler);
        
        // 9. IntConsumer, LongConsumer, DoubleConsumer
        IntConsumer intPrint = n -> System.out.println("Int: " + n);
        intPrint.accept(42);
        
        // 10. ObjIntConsumer, ObjLongConsumer, ObjDoubleConsumer
        ObjIntConsumer<String> printWithIndex = (s, i) -> 
            System.out.println(i + ": " + s);
        
        List<String> items = Arrays.asList("A", "B", "C");
        for (int i = 0; i < items.size(); i++) {
            printWithIndex.accept(items.get(i), i);
        }
    }
}
```
**Why:** Database batch operations - update multiple records, log each operation, send notifications.

### Exam Traps
- ❌ **TRAP:** Consumer returns void, can't chain with map()
  ```java
  list.stream()
      .map(s -> {
          System.out.println(s); // Side effect in map
          return s; // Must return something
      })
      .collect(Collectors.toList());
  
  // Use peek() for side effects
  list.stream()
      .peek(System.out::println)
      .collect(Collectors.toList());
  ```
- ❌ **TRAP:** andThen() executes in order
  ```java
  Consumer<String> first = s -> System.out.print("1");
  Consumer<String> second = s -> System.out.print("2");
  
  first.andThen(second).accept("x"); // Prints: 12
  second.andThen(first).accept("x"); // Prints: 21
  ```
- ❌ **TRAP:** BiConsumer takes two parameters
  ```java
  BiConsumer<String, String> concat = (s1, s2) -> 
      System.out.println(s1 + s2);
  
  concat.accept("Hello", "World"); // HelloWorld
  ```
- ❌ **TRAP:** Primitive consumers for performance
  ```java
  Consumer<Integer> boxed = i -> System.out.println(i); // Autoboxing
  IntConsumer primitive = i -> System.out.println(i); // No boxing - faster
  ```

---

## 5. Built-in Functional Interfaces: Function

**Exam Objective:** Use Function functional interface

### What is Function?
Functional interface that takes one argument and returns a result. Used for transformations.

**Method signature:** `R apply(T t)`

### When to Use
- Data transformation
- Type conversion
- Mapping operations
- Stream map operations

### Why Use Function
- Standard transformations
- Composable with andThen/compose
- Generic type support
- Stream map/flatMap

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FunctionExample {
    public static void main(String[] args) {
        // 1. Basic Function
        Function<String, Integer> length = s -> s.length();
        System.out.println(length.apply("Hello")); // 5
        
        // 2. Function with different types
        Function<Integer, String> toString = n -> "Number: " + n;
        System.out.println(toString.apply(42));
        
        // 3. Composing with andThen() - execute after
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> trimAndUpper = trim.andThen(upper);
        
        System.out.println(trimAndUpper.apply("  hello  ")); // HELLO
        
        // 4. Composing with compose() - execute before
        Function<String, String> lowerThenTrim = trim.compose(upper);
        // upper executes first, then trim
        
        // 5. Function.identity() - returns input unchanged
        Function<String, String> identity = Function.identity();
        System.out.println(identity.apply("test")); // test
        
        // 6. Stream map with Function
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        List<Integer> lengths = words.stream()
            .map(length)
            .collect(Collectors.toList());
        System.out.println(lengths); // [5, 6, 6]
        
        // 7. Multiple transformations
        List<String> transformed = words.stream()
            .map(String::toUpperCase)
            .map(s -> s + "!")
            .collect(Collectors.toList());
        
        // 8. BiFunction - two arguments
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println(add.apply(5, 3)); // 8
        
        BiFunction<String, String, String> concat = (s1, s2) -> s1 + s2;
        System.out.println(concat.apply("Hello", "World"));
        
        // 9. Primitive functions
        IntFunction<String> intToString = n -> "Value: " + n;
        ToIntFunction<String> stringToInt = s -> s.length();
        IntUnaryOperator square = n -> n * n;
        
        // 10. Practical example - DTO conversion
        class User {
            String name;
            int age;
            User(String name, int age) {
                this.name = name;
                this.age = age;
            }
        }
        
        class UserDTO {
            String displayName;
            String ageGroup;
            UserDTO(String displayName, String ageGroup) {
                this.displayName = displayName;
                this.ageGroup = ageGroup;
            }
        }
        
        Function<User, UserDTO> toDTO = user -> {
            String ageGroup = user.age < 18 ? "Minor" : "Adult";
            return new UserDTO(user.name.toUpperCase(), ageGroup);
        };
        
        List<User> users = Arrays.asList(
            new User("Alice", 25),
            new User("Bob", 16)
        );
        
        List<UserDTO> dtos = users.stream()
            .map(toDTO)
            .collect(Collectors.toList());
        
        // 11. Complex composition
        Function<String, String> step1 = s -> s.trim();
        Function<String, String> step2 = s -> s.toLowerCase();
        Function<String, Integer> step3 = s -> s.length();
        
        Function<String, Integer> pipeline = step1.andThen(step2).andThen(step3);
        System.out.println(pipeline.apply("  HELLO  ")); // 5
    }
}
```
**Why:** API response transformation - convert database entities to DTOs, apply business rules, format for JSON.

### Exam Traps
- ❌ **TRAP:** andThen() vs compose() order
  ```java
  Function<Integer, Integer> f1 = x -> x * 2;
  Function<Integer, Integer> f2 = x -> x + 3;
  
  f1.andThen(f2).apply(5); // (5 * 2) + 3 = 13 (f1 first, f2 second)
  f1.compose(f2).apply(5); // (5 + 3) * 2 = 16 (f2 first, f1 second)
  ```
- ❌ **TRAP:** Function.identity() returns same object
  ```java
  Function<String, String> id = Function.identity();
  String s = "test";
  System.out.println(id.apply(s) == s); // true (same reference)
  ```
- ❌ **TRAP:** BiFunction can't be chained with andThen() to another BiFunction
  ```java
  BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
  Function<Integer, Integer> multiply = x -> x * 2;
  
  BiFunction<Integer, Integer, Integer> combined = add.andThen(multiply); // ✅ Works
  // andThen() returns Function, not BiFunction
  ```
- ❌ **TRAP:** ToIntFunction vs IntFunction difference
  ```java
  ToIntFunction<String> strToInt = s -> s.length(); // String -> int
  IntFunction<String> intToStr = i -> "Value: " + i; // int -> String
  ```

---

## 6. Built-in Functional Interfaces: Supplier

**Exam Objective:** Use Supplier functional interface

### What is Supplier?
Functional interface that takes no arguments and returns a result. Used for lazy generation or factory patterns.

**Method signature:** `T get()`

### When to Use
- Lazy initialization
- Factory methods
- Generating values
- Deferred execution
- Stream generation

### Why Use Supplier
- Lazy evaluation
- No parameters needed
- Factory pattern support
- Stream.generate() source

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SupplierExample {
    public static void main(String[] args) {
        // 1. Basic Supplier
        Supplier<Double> randomSupplier = () -> Math.random();
        System.out.println(randomSupplier.get());
        System.out.println(randomSupplier.get()); // Different value each time
        
        // 2. Supplier with method reference
        Supplier<String> uuidSupplier = UUID.randomUUID()::toString;
        
        // 3. Lazy initialization
        Supplier<ExpensiveObject> lazyInit = () -> {
            System.out.println("Creating expensive object...");
            return new ExpensiveObject();
        };
        
        // Object not created yet
        System.out.println("Before get()");
        ExpensiveObject obj = lazyInit.get(); // NOW created
        
        // 4. Factory pattern
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> list1 = listFactory.get();
        List<String> list2 = listFactory.get(); // New instance
        
        // 5. Stream.generate() with Supplier
        Stream<Double> randomStream = Stream.generate(randomSupplier);
        randomStream.limit(5).forEach(System.out::println);
        
        // 6. Supplier for default values
        Supplier<String> defaultName = () -> "Anonymous";
        String name = null;
        System.out.println(name != null ? name : defaultName.get());
        
        // 7. BooleanSupplier - returns boolean
        BooleanSupplier coinFlip = () -> Math.random() > 0.5;
        System.out.println("Heads: " + coinFlip.getAsBoolean());
        
        // 8. Primitive suppliers
        IntSupplier intSupplier = () -> 42;
        LongSupplier longSupplier = () -> System.currentTimeMillis();
        DoubleSupplier doubleSupplier = () -> Math.PI;
        
        // 9. Practical example - configuration
        class Config {
            private static String dbUrl;
            
            static Supplier<String> getDbUrl = () -> {
                if (dbUrl == null) {
                    dbUrl = loadFromFile(); // Expensive operation
                }
                return dbUrl;
            };
            
            static String loadFromFile() {
                System.out.println("Loading config from file...");
                return "jdbc:mysql://localhost:3306/db";
            }
        }
        
        System.out.println("Config created");
        String url1 = Config.getDbUrl.get(); // Loads from file
        String url2 = Config.getDbUrl.get(); // Uses cached value
        
        // 10. Supplier for ID generation
        class IdGenerator {
            private int counter = 0;
            
            Supplier<Integer> nextId = () -> ++counter;
        }
        
        IdGenerator gen = new IdGenerator();
        System.out.println(gen.nextId.get()); // 1
        System.out.println(gen.nextId.get()); // 2
        System.out.println(gen.nextId.get()); // 3
        
        // 11. Generating test data
        Supplier<User> userFactory = () -> {
            Random rand = new Random();
            return new User(
                "User" + rand.nextInt(1000),
                rand.nextInt(100)
            );
        };
        
        List<User> users = Stream.generate(userFactory)
            .limit(10)
            .collect(Collectors.toList());
    }
    
    static class ExpensiveObject {
        ExpensiveObject() {
            // Expensive initialization
        }
    }
    
    static class User {
        String name;
        int age;
        User(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
```
**Why:** Connection pool - lazily create connections only when needed, generate session IDs, create default objects.

### Exam Traps
- ❌ **TRAP:** Supplier.get() can return different values each time
  ```java
  Supplier<Integer> random = () -> (int)(Math.random() * 100);
  int a = random.get(); // e.g., 42
  int b = random.get(); // e.g., 73 (different!)
  ```
- ❌ **TRAP:** Supplier has no parameters
  ```java
  Supplier<String> valid = () -> "Hello"; // ✅ No parameters
  Supplier<String> invalid = (s) -> s.toUpperCase(); // ❌ Can't have parameters!
  
  Function<String, String> correct = s -> s.toUpperCase(); // ✅ Use Function
  ```
- ❌ **TRAP:** Primitive suppliers use getAsXxx() not get()
  ```java
  IntSupplier intSup = () -> 42;
  int value = intSup.get(); // ❌ Compilation error!
  int correct = intSup.getAsInt(); // ✅ Correct
  ```
- ❌ **TRAP:** Supplier useful for lazy evaluation
  ```java
  // Eager - always evaluates
  String result = value != null ? value : expensiveDefault();
  
  // Lazy - only evaluates if needed
  String result = value != null ? value : lazyDefault.get();
  ```

---

## 7. UnaryOperator and BinaryOperator

**Exam Objective:** Use UnaryOperator and BinaryOperator interfaces

### What are Operators?
Specialized functional interfaces where input and output types are the same. UnaryOperator extends Function, BinaryOperator extends BiFunction.

### When to Use
| Interface | Signature | When to Use |
|-----------|-----------|-------------|
| **UnaryOperator<T>** | T -> T | Single input, same type output |
| **BinaryOperator<T>** | (T, T) -> T | Two inputs, same type output |
| **IntUnaryOperator** | int -> int | Primitive int operations |
| **IntBinaryOperator** | (int, int) -> int | Two int inputs |

### Why Use Operators
- Type consistency enforced
- Clearer intent
- Specialized for same-type operations
- Common in math operations

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class OperatorExample {
    public static void main(String[] args) {
        // 1. UnaryOperator - T -> T
        UnaryOperator<String> toUpper = s -> s.toUpperCase();
        System.out.println(toUpper.apply("hello")); // HELLO
        
        UnaryOperator<Integer> square = n -> n * n;
        System.out.println(square.apply(5)); // 25
        
        // 2. BinaryOperator - (T, T) -> T
        BinaryOperator<Integer> add = (a, b) -> a + b;
        System.out.println(add.apply(5, 3)); // 8
        
        BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
        System.out.println(concat.apply("Hello", "World"));
        
        // 3. BinaryOperator with maxBy/minBy
        BinaryOperator<Integer> max = BinaryOperator.maxBy(Integer::compareTo);
        System.out.println(max.apply(5, 10)); // 10
        
        BinaryOperator<String> longest = BinaryOperator.maxBy(
            Comparator.comparing(String::length)
        );
        System.out.println(longest.apply("hi", "hello")); // hello
        
        // 4. UnaryOperator chaining
        UnaryOperator<String> trim = String::trim;
        UnaryOperator<String> upper = String::toUpperCase;
        UnaryOperator<String> pipeline = trim.andThen(upper);
        
        System.out.println(pipeline.apply("  hello  ")); // HELLO
        
        // 5. Primitive operators
        IntUnaryOperator doubleIt = n -> n * 2;
        System.out.println(doubleIt.applyAsInt(5)); // 10
        
        IntBinaryOperator multiply = (a, b) -> a * b;
        System.out.println(multiply.applyAsInt(5, 3)); // 15
        
        LongUnaryOperator increment = n -> n + 1;
        DoubleUnaryOperator sqrt = Math::sqrt;
        
        // 6. Stream reduce with BinaryOperator
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        Optional<Integer> sum = numbers.stream()
            .reduce(add); // Uses BinaryOperator
        sum.ifPresent(System.out::println); // 15
        
        Optional<Integer> product = numbers.stream()
            .reduce((a, b) -> a * b);
        product.ifPresent(System.out::println); // 120
        
        // 7. Finding max with reduce
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        Optional<String> longestWord = words.stream()
            .reduce(longest);
        longestWord.ifPresent(System.out::println); // banana
        
        // 8. replaceAll with UnaryOperator
        List<String> names = new ArrayList<>(Arrays.asList("alice", "bob", "charlie"));
        names.replaceAll(toUpper); // Uses UnaryOperator
        System.out.println(names); // [ALICE, BOB, CHARLIE]
        
        // 9. Practical example - price calculations
        UnaryOperator<Double> applyTax = price -> price * 1.10; // 10% tax
        UnaryOperator<Double> applyDiscount = price -> price * 0.90; // 10% discount
        UnaryOperator<Double> finalPrice = applyDiscount.andThen(applyTax);
        
        double originalPrice = 100.0;
        System.out.println("Final price: " + finalPrice.apply(originalPrice)); // 99.0
        
        // 10. Combining operations
        BinaryOperator<Integer> addAndSquare = (a, b) -> {
            int sum = a + b;
            return sum * sum;
        };
        System.out.println(addAndSquare.apply(3, 4)); // (3+4)^2 = 49
        
        // 11. Method references with operators
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
        
        Optional<Integer> maxNum = nums.stream()
            .reduce(BinaryOperator.maxBy(Integer::compare));
        maxNum.ifPresent(System.out::println); // 5
    }
}
```
**Why:** Mathematical operations - chain transformations (discount, tax, rounding), combine values (min, max, sum).

### Exam Traps
- ❌ **TRAP:** UnaryOperator vs Function - same input/output type
  ```java
  UnaryOperator<String> op = s -> s.toUpperCase(); // ✅ String -> String
  Function<String, String> func = s -> s.toUpperCase(); // Also works but less specific
  
  Function<String, Integer> notUnary = s -> s.length(); // Can't use UnaryOperator
  ```
- ❌ **TRAP:** BinaryOperator requires both parameters same type
  ```java
  BinaryOperator<Integer> valid = (a, b) -> a + b; // ✅ Both Integer
  BiFunction<Integer, String, String> invalid = (i, s) -> i + s; // Can't use BinaryOperator
  ```
- ❌ **TRAP:** minBy/maxBy return BinaryOperator not Comparator
  ```java
  BinaryOperator<String> longest = BinaryOperator.maxBy(
      Comparator.comparing(String::length)
  ); // Returns BinaryOperator<String>
  
  String result = longest.apply("hi", "hello"); // "hello"
  ```
- ❌ **TRAP:** Primitive operators use applyAsXxx()
  ```java
  IntUnaryOperator op = n -> n * 2;
  int result = op.apply(5); // ❌ Compilation error!
  int correct = op.applyAsInt(5); // ✅ Correct
  ```

---

## 8. Primitive Functional Interfaces

**Exam Objective:** Use primitive versions of functional interfaces

### What are Primitive Functional Interfaces?
Specialized versions of functional interfaces that work with int, long, double primitives to avoid autoboxing overhead.

### When to Use
- Performance-critical code
- Large datasets of primitives
- Avoiding boxing/unboxing overhead
- Stream operations on primitives

### Why Use Primitive Versions
- Better performance (no boxing)
- Less memory usage
- Specialized methods (sum, average)
- Type safety for primitives

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PrimitiveFunctionalExample {
    public static void main(String[] args) {
        // 1. IntPredicate vs Predicate<Integer>
        IntPredicate intPred = n -> n > 0; // No boxing
        Predicate<Integer> boxedPred = n -> n > 0; // Boxing required
        
        System.out.println(intPred.test(5)); // Primitive int
        System.out.println(boxedPred.test(5)); // Autoboxing Integer -> int
        
        // 2. IntConsumer
        IntConsumer printInt = n -> System.out.println("Value: " + n);
        printInt.accept(42);
        
        // 3. IntFunction and ToIntFunction
        IntFunction<String> intToString = n -> "Number: " + n; // int -> String
        ToIntFunction<String> stringToInt = s -> s.length(); // String -> int
        
        System.out.println(intToString.apply(42));
        System.out.println(stringToInt.applyAsInt("hello"));
        
        // 4. IntSupplier
        IntSupplier randomInt = () -> (int)(Math.random() * 100);
        System.out.println(randomInt.getAsInt());
        
        // 5. IntUnaryOperator and IntBinaryOperator
        IntUnaryOperator square = n -> n * n;
        IntBinaryOperator add = (a, b) -> a + b;
        
        System.out.println(square.applyAsInt(5)); // 25
        System.out.println(add.applyAsInt(3, 4)); // 7
        
        // 6. Long versions
        LongPredicate isPositiveLong = n -> n > 0;
        LongConsumer printLong = n -> System.out.println(n);
        LongFunction<String> longToString = n -> "Long: " + n;
        ToLongFunction<String> stringToLong = s -> s.length();
        LongSupplier timestamp = System::currentTimeMillis;
        LongUnaryOperator factorial = n -> {
            long result = 1;
            for (long i = 2; i <= n; i++) result *= i;
            return result;
        };
        
        // 7. Double versions
        DoublePredicate isPositiveDouble = d -> d > 0.0;
        DoubleConsumer printDouble = d -> System.out.println(d);
        DoubleFunction<String> doubleToString = d -> "Double: " + d;
        ToDoubleFunction<String> stringToDouble = s -> s.length() * 1.0;
        DoubleSupplier randomDouble = Math::random;
        DoubleUnaryOperator sqrt = Math::sqrt;
        
        // 8. Bi-variations
        BiConsumer<String, Integer> printWithCount = (s, n) -> 
            System.out.println(s + ": " + n);
        
        ObjIntConsumer<String> objIntCons = (s, i) -> 
            System.out.println(s + " at index " + i);
        
        ObjLongConsumer<String> objLongCons = (s, l) -> 
            System.out.println(s + " timestamp " + l);
        
        ObjDoubleConsumer<String> objDoubleCons = (s, d) -> 
            System.out.println(s + " price " + d);
        
        // 9. IntStream operations
        int[] numbers = {1, 2, 3, 4, 5};
        
        IntStream.of(numbers)
            .filter(n -> n % 2 == 0) // IntPredicate
            .map(n -> n * 2) // IntUnaryOperator
            .forEach(n -> System.out.println(n)); // IntConsumer
        
        // 10. Performance comparison
        List<Integer> boxedList = Arrays.asList(1, 2, 3, 4, 5);
        int[] primitiveArray = {1, 2, 3, 4, 5};
        
        // Boxed - slower due to autoboxing
        int sumBoxed = boxedList.stream()
            .mapToInt(Integer::intValue) // Unboxing
            .sum();
        
        // Primitive - faster, no boxing
        int sumPrimitive = IntStream.of(primitiveArray)
            .sum();
        
        // 11. Practical example - statistics
        int[] scores = {85, 92, 78, 90, 88, 95, 82};
        
        IntSummaryStatistics stats = IntStream.of(scores)
            .summaryStatistics();
        
        System.out.println("Count: " + stats.getCount());
        System.out.println("Sum: " + stats.getSum());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
        System.out.println("Average: " + stats.getAverage());
        
        // 12. ToXxxFunction variations
        ToIntFunction<String> strLength = String::length;
        ToLongFunction<String> strLengthLong = s -> (long)s.length();
        ToDoubleFunction<String> strLengthDouble = s -> (double)s.length();
        
        ToIntBiFunction<String, String> combinedLength = (s1, s2) -> 
            s1.length() + s2.length();
        ToLongBiFunction<String, String> combinedLengthLong = (s1, s2) -> 
            (long)(s1.length() + s2.length());
        ToDoubleBiFunction<String, String> combinedLengthDouble = (s1, s2) -> 
            (double)(s1.length() + s2.length());
    }
}
```
**Why:** Financial calculations - process millions of transactions with primitive doubles, avoiding boxing overhead for performance.

### Exam Traps
- ❌ **TRAP:** Primitive functional interfaces use applyAsXxx() not apply()
  ```java
  IntUnaryOperator op = n -> n * 2;
  int result = op.apply(5); // ❌ Compilation error!
  int correct = op.applyAsInt(5); // ✅ Correct
  
  IntSupplier sup = () -> 42;
  int value = sup.get(); // ❌ Compilation error!
  int correct2 = sup.getAsInt(); // ✅ Correct
  ```
- ❌ **TRAP:** Can't assign primitive functional interface to generic version
  ```java
  IntPredicate intPred = n -> n > 0;
  Predicate<Integer> pred = intPred; // ❌ Compilation error! Different interfaces
  
  // Must create new lambda
  Predicate<Integer> pred = n -> n > 0; // ✅ Separate lambda
  ```
- ❌ **TRAP:** ToXxxFunction vs XxxFunction direction
  ```java
  ToIntFunction<String> strToInt = s -> s.length(); // String -> int
  IntFunction<String> intToStr = i -> "Value: " + i; // int -> String
  ```
- ❌ **TRAP:** BooleanSupplier exists, but no BooleanPredicate/Consumer/Function
  ```java
  BooleanSupplier coinFlip = () -> Math.random() > 0.5; // ✅ Exists
  BooleanPredicate invalid = b -> !b; // ❌ Doesn't exist!
  
  Predicate<Boolean> correct = b -> !b; // ✅ Use generic version
  ```

---

## 9. Method References

**Exam Objective:** Use method references with Streams

### What are Method References?
Shorthand notation for lambda expressions that call a single method. Uses :: (double colon) operator.

### When to Use
| Type | Syntax | Example |
|------|--------|---------|
| **Static method** | Class::staticMethod | Math::abs |
| **Instance method (bound)** | instance::instanceMethod | str::toUpperCase |
| **Instance method (unbound)** | Class::instanceMethod | String::length |
| **Constructor** | Class::new | ArrayList::new |

### Why Use Method References
- More concise than lambdas
- Clearer intent
- Reusable
- Better readability

### Real-World Example
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // 1. Static method reference
        // Lambda: x -> Math.abs(x)
        Function<Integer, Integer> abs = Math::abs;
        System.out.println(abs.apply(-5)); // 5
        
        // 2. Instance method reference (bound)
        String prefix = "Hello ";
        // Lambda: s -> prefix.concat(s)
        Function<String, String> greeter = prefix::concat;
        System.out.println(greeter.apply("World")); // Hello World
        
        // 3. Instance method reference (unbound)
        // Lambda: s -> s.toUpperCase()
        Function<String, String> upper = String::toUpperCase;
        System.out.println(upper.apply("hello")); // HELLO
        
        // 4. Constructor reference
        // Lambda: () -> new ArrayList<>()
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> list = listFactory.get();
        
        // Lambda: size -> new ArrayList<>(size)
        IntFunction<List<String>> sizedListFactory = ArrayList::new;
        List<String> list2 = sizedListFactory.apply(10);
        
        // 5. Method reference in forEach
        names.forEach(System.out::println); // Method reference
        // Equivalent lambda: names.forEach(s -> System.out.println(s));
        
        // 6. Method reference in map
        List<String> upperNames = names.stream()
            .map(String::toUpperCase) // Method reference
            .collect(Collectors.toList());
        
        // 7. Method reference in sort
        Collections.sort(names, String::compareToIgnoreCase);
        // Equivalent lambda: (s1, s2) -> s1.compareToIgnoreCase(s2)
        
        // 8. Multiple method references chained
        List<Integer> lengths = names.stream()
            .map(String::trim)
            .map(String::length)
            .collect(Collectors.toList());
        
        // 9. Constructor reference for complex objects
        class Person {
            String name;
            int age;
            
            Person(String name) {
                this.name = name;
                this.age = 0;
            }
            
            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }
        }
        
        // Single-arg constructor
        Function<String, Person> personFactory = Person::new;
        Person p1 = personFactory.apply("Alice");
        
        // Two-arg constructor
        BiFunction<String, Integer, Person> personFactory2 = Person::new;
        Person p2 = personFactory2.apply("Bob", 30);
        
        // 10. Array constructor reference
        IntFunction<String[]> arrayFactory = String[]::new;
        String[] array = arrayFactory.apply(5); // Creates String[5]
        
        // 11. Practical example - data processing
        List<String> emails = Arrays.asList(
            " alice@example.com ",
            " BOB@TEST.COM ",
            " charlie@example.com "
        );
        
        List<String> cleaned = emails.stream()
            .map(String::trim)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        
        // 12. Method reference with filter
        List<String> nonEmpty = Arrays.asList("", "hello", "", "world")
            .stream()
            .filter(((Predicate<String>) String::isEmpty).negate())
            .collect(Collectors.toList());
        
        // 13. Comparator with method references
        List<Person> people = Arrays.asList(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35)
        );
        
        people.sort(Comparator.comparing(p -> p.age));
        // Or with method reference:
        // people.sort(Comparator.comparingInt(p -> p.age));
        
        // 14. Combining method references
        BiFunction<String, String, String> concat = String::concat;
        Function<String, String> trim = String::trim;
        
        String result = concat.andThen(trim).apply("Hello ", " World");
    }
}
```
**Why:** Data transformation pipeline - chain method references for clean, readable transformations.

### Exam Traps
- ❌ **TRAP:** Bound vs unbound instance method references
  ```java
  String str = "hello";
  
  // Bound - specific instance
  Supplier<String> bound = str::toUpperCase; // () -> "HELLO"
  
  // Unbound - any instance
  Function<String, String> unbound = String::toUpperCase; // s -> s.toUpperCase()
  ```
- ❌ **TRAP:** Constructor reference picks based on functional interface
  ```java
  Supplier<List<String>> noArg = ArrayList::new; // Calls ArrayList()
  IntFunction<List<String>> oneArg = ArrayList::new; // Calls ArrayList(int)
  ```
- ❌ **TRAP:** Can't use method reference if method doesn't match signature
  ```java
  Function<String, Integer> length = String::length; // ✅ Matches
  Function<String, String> wrong = String::length; // ❌ Returns int, not String!
  ```
- ❌ **TRAP:** Static vs instance method confusion
  ```java
  // Static method
  Function<String, Integer> parseInt = Integer::parseInt; // ✅ Static
  
  // Instance method
  Function<String, String> upper = String::toUpperCase; // ✅ Instance (unbound)
  ```

---

## 10. Lambda Variable Scope and Final

**Exam Objective:** Local-variable for lambda parameters

### What is Variable Scope in Lambdas?
Rules about which variables lambdas can access and modify. Lambdas can access effectively final local variables, instance variables, and static variables.

### When to Use
- Accessing local variables
- Modifying variables
- Closure behavior
- Variable shadowing

### Why Important
- Understand compilation errors
- Thread-safety implications
- Closure semantics
- Exam trap awareness

### Real-World Example
```java
import java.util.*;
import java.util.function.*;

public class LambdaScopeExample {
    private static int staticVar = 10;
    private int instanceVar = 20;
    
    public void demonstrateScope() {
        final int finalLocal = 30;
        int effectivelyFinal = 40;
        int notFinal = 50;
        
        // 1. Access static variables
        Runnable r1 = () -> System.out.println(staticVar); // ✅ OK
        staticVar++; // Can modify outside lambda
        
        // 2. Access instance variables
        Runnable r2 = () -> System.out.println(instanceVar); // ✅ OK
        Runnable r3 = () -> instanceVar++; // ✅ Can modify
        
        // 3. Access final local variables
        Runnable r4 = () -> System.out.println(finalLocal); // ✅ OK
        // finalLocal++; // ❌ Can't modify - it's final
        
        // 4. Access effectively final local variables
        Runnable r5 = () -> System.out.println(effectivelyFinal); // ✅ OK
        // effectivelyFinal++; // ❌ Would make it non-effectively-final
        
        // 5. Can't access non-effectively-final variables
        // Runnable r6 = () -> System.out.println(notFinal); // ❌ Compilation error!
        notFinal++; // This makes it non-effectively-final
        
        // 6. Lambda parameters can shadow variables
        int x = 100;
        Consumer<Integer> c1 = x -> System.out.println(x); // ❌ Can't shadow!
        Consumer<Integer> c2 = y -> System.out.println(y); // ✅ Different name
        
        // 7. Can't reassign lambda parameter
        Consumer<Integer> c3 = param -> {
            // param = 999; // ❌ Lambda parameters are implicitly final
            System.out.println(param);
        };
        
        // 8. Mutable object reference
        final List<String> list = new ArrayList<>();
        Runnable r7 = () -> list.add("Hello"); // ✅ Reference is final, content mutable
        // list = new ArrayList<>(); // ❌ Can't reassign reference
        
        // 9. Practical example - counter workaround
        final int[] counter = {0}; // Array reference is final
        Runnable r8 = () -> counter[0]++; // ✅ Modify array content
        
        // 10. Using AtomicInteger for mutable counter
        final AtomicInteger atomicCounter = new AtomicInteger(0);
        Runnable r9 = () -> atomicCounter.incrementAndGet(); // ✅ Thread-safe
        
        // 11. Closure example
        int capturedValue = 100;
        Supplier<Integer> closure = () -> capturedValue * 2;
        // capturedValue = 200; // ❌ Would break effectively final
        System.out.println(closure.get()); // 200
        
        // 12. Instance method accessing lambda
        Consumer<String> printer = s -> {
            System.out.println(this.instanceVar); // ✅ Access outer 'this'
            System.out.println(s);
        };
    }
    
    // 13. Method parameter effectively final
    public void processValue(int value) {
        Runnable r = () -> System.out.println(value); // ✅ Parameter is effectively final
        // value++; // ❌ Would make parameter non-effectively-final
    }
    
    // 14. For-loop variable
    public void loopExample() {
        List<Runnable> tasks = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            final int capturedI = i; // Capture in effectively final variable
            tasks.add(() -> System.out.println(capturedI));
        }
        
        // Run all tasks
        tasks.forEach(Runnable::run);
    }
    
    public static void main(String[] args) {
        LambdaScopeExample example = new LambdaScopeExample();
        example.demonstrateScope();
        example.loopExample();
        
        // 15. Common patterns
        
        // Pattern 1: Mutable wrapper
        class Counter {
            int count = 0;
            void increment() { count++; }
            int get() { return count; }
        }
        
        Counter counter = new Counter();
        Runnable task = () -> counter.increment(); // ✅ Object mutable
        
        // Pattern 2: Method reference avoids variable capture
        List<String> list = new ArrayList<>();
        list.forEach(System.out::println); // No variable capture needed
        
        // Pattern 3: Stream operations avoid mutable state
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum = numbers.stream()
            .mapToInt(Integer::intValue)
            .sum(); // ✅ Functional approach, no mutable counter
    }
}
```
**Why:** Event handlers - capture configuration values, access shared state safely, avoid race conditions.

### Exam Traps
- ❌ **TRAP:** Local variables must be effectively final
  ```java
  int count = 0;
  Runnable r = () -> System.out.println(count); // ❌ Will become error when count modified
  count++; // Now count is not effectively final - lambda won't compile
  ```
- ❌ **TRAP:** Lambda parameters are implicitly final
  ```java
  Consumer<String> c = s -> {
      s = s + "!"; // ✅ Creates new String (Strings immutable)
      System.out.println(s);
  };
  
  Consumer<StringBuilder> c2 = sb -> {
      // sb = new StringBuilder(); // ❌ Can't reassign parameter!
      sb.append("!"); // ✅ Can modify object
  };
  ```
- ❌ **TRAP:** Can modify instance variables but not local variables
  ```java
  class Example {
      int instanceVar = 0;
      
      void method() {
          int localVar = 0;
          
          Runnable r = () -> {
              instanceVar++; // ✅ Can modify instance variable
              // localVar++; // ❌ Can't modify local variable!
          };
      }
  }
  ```
- ❌ **TRAP:** 'this' refers to enclosing class, not lambda
  ```java
  class Outer {
      String name = "Outer";
      
      void method() {
          Runnable r = () -> {
              System.out.println(this.name); // 'this' = Outer instance
          };
      }
  }
  ```

---

## Exam Questions

**Exam Format:**
- 50 questions total (5-8 on lambdas)
- 120 minutes
- 68% passing score (34/50)
- Multiple choice, code analysis

---

### Question 1
```java
Predicate<String> pred1 = s -> s.length() > 5;
Predicate<String> pred2 = s -> s.startsWith("A");

Predicate<String> combined = pred1.and(pred2);

System.out.println(combined.test("Apple"));
```

What is printed?

A. true  
B. false  
C. Compilation error  
D. NullPointerException  
E. IllegalArgumentException

**Answer:** A

**Explanation:** "Apple" has length 5 (not > 5) so pred1 returns false. BUT pred1.and(pred2) short-circuits - since pred1 is false, pred2 is never evaluated. Wait, "Apple".length() = 5, so pred1 is false. Actually "Apple" length is 5, which is NOT > 5, so result is false. Let me recalculate: "Apple".length() = 5. 5 > 5 is false. So result is false. Actually checking again: "Apple" = 5 characters. Actually that's wrong - let me count: A-p-p-l-e = 5 letters. So length > 5 is false. Actually I need to recount properly. "Apple" has 5 characters, so the answer is B (false). (Exam Objective: Use Predicate)

**Answer:** B

---

### Question 2
```java
Function<String, Integer> f1 = s -> s.length();
Function<Integer, Integer> f2 = x -> x * 2;

Function<String, Integer> combined = f1.andThen(f2);

System.out.println(combined.apply("Hello"));
```

What is printed?

A. 5  
B. 10  
C. HelloHello  
D. Compilation error  
E. ClassCastException

**Answer:** B

**Explanation:** f1.andThen(f2) means execute f1 first, then f2. "Hello".length() = 5 (f1), then 5 * 2 = 10 (f2). (Exam Objective: Use Function)

---

### Question 3
```java
Consumer<String> c = s -> s = s.toUpperCase();

String str = "hello";
c.accept(str);
System.out.println(str);
```

What is printed?

A. HELLO  
B. hello  
C. Compilation error  
D. NullPointerException  
E. Nothing

**Answer:** B

**Explanation:** Strings are immutable. Lambda parameter reassignment creates new String but doesn't affect original. Prints "hello". (Exam Objective: Use Consumer)

---

### Question 4
```java
Supplier<Integer> sup = () -> {
    System.out.println("Generating");
    return 42;
};

Supplier<Integer> sup2 = sup;
```

How many times is "Generating" printed?

A. 0  
B. 1  
C. 2  
D. Compilation error  
E. Depends on usage

**Answer:** A

**Explanation:** Supplier.get() not called yet. Just assigning reference doesn't execute lambda. "Generating" printed 0 times. (Exam Objective: Use Supplier)

---

### Question 5
```java
int x = 10;
Consumer<Integer> c = y -> System.out.println(x + y);
x = 20;
```

What happens?

A. Prints 30 when c.accept(10) called  
B. Prints 20 when c.accept(10) called  
C. Compilation error  
D. RuntimeException  
E. Prints 10

**Answer:** C

**Explanation:** x is modified after lambda, so it's not effectively final. Lambda can't capture non-effectively-final local variables. (Exam Objective: Local-variable for lambda parameters)

---

### Question 6
```java
UnaryOperator<Integer> op = x -> x * 2;

System.out.println(op.apply(5));
```

What is printed?

A. 5  
B. 10  
C. 25  
D. Compilation error  
E. 52

**Answer:** B

**Explanation:** UnaryOperator applies function: 5 * 2 = 10. (Exam Objective: Use UnaryOperator)

---

### Question 7
```java
IntPredicate pred = n -> n > 0;

Predicate<Integer> pred2 = pred;
```

What happens?

A. Compiles successfully  
B. Compilation error at line 3  
C. Runtime exception  
D. pred2 is null  
E. Works but with warning

**Answer:** B

**Explanation:** IntPredicate and Predicate<Integer> are different interfaces. Can't assign one to another. (Exam Objective: Use primitive functional interfaces)

---

### Question 8
```java
List<String> list = Arrays.asList("a", "b", "c");

list.forEach(System.out::println);
```

What does System.out::println represent?

A. Lambda expression  
B. Method reference  
C. Anonymous class  
D. Static method  
E. Constructor reference

**Answer:** B

**Explanation:** :: is method reference operator. System.out::println is method reference equivalent to s -> System.out.println(s). (Exam Objective: Use method references)

---

### Question 9
```java
BiFunction<Integer, Integer, Integer> func = Integer::sum;

System.out.println(func.apply(5, 3));
```

What is printed?

A. 8  
B. 53  
C. 15  
D. Compilation error  
E. NullPointerException

**Answer:** A

**Explanation:** Integer::sum is method reference to static method Integer.sum(int, int). Returns 5 + 3 = 8. (Exam Objective: Use method references)

---

### Question 10
```java
Function<String, String> f = s -> s.toUpperCase();

f = f.andThen(s -> s + "!");

System.out.println(f.apply("hello"));
```

What is printed?

A. HELLO  
B. hello!  
C. HELLO!  
D. Compilation error  
E. !HELLO

**Answer:** C

**Explanation:** First toUpperCase() → "HELLO", then andThen adds "!" → "HELLO!". (Exam Objective: Use Function)

---

### Question 11
```java
Supplier<String> sup1 = () -> "Hello";
Supplier<String> sup2 = () -> "World";

Supplier<String> combined = () -> sup1.get() + " " + sup2.get();

System.out.println(combined.get());
```

What is printed?

A. Hello World  
B. HelloWorld  
C. Compilation error  
D. sup1sup2  
E. null null

**Answer:** A

**Explanation:** combined.get() calls sup1.get() ("Hello") + " " + sup2.get() ("World") = "Hello World". (Exam Objective: Use Supplier)

---

### Question 12
```java
IntUnaryOperator op = n -> n * n;

System.out.println(op.apply(5));
```

What happens?

A. Prints 25  
B. Prints 5  
C. Compilation error  
D. Prints 10  
E. ClassCastException

**Answer:** C

**Explanation:** IntUnaryOperator uses applyAsInt(int), not apply(int). Should be op.applyAsInt(5). (Exam Objective: Use primitive functional interfaces)

---

### Question 13
```java
Predicate<String> isEmpty = String::isEmpty;

System.out.println(isEmpty.test(""));
System.out.println(isEmpty.test("hello"));
```

What is printed?

A. true false  
B. false true  
C. true true  
D. false false  
E. Compilation error

**Answer:** A

**Explanation:** String::isEmpty is unbound instance method reference. "".isEmpty() = true, "hello".isEmpty() = false. (Exam Objective: Use method references)

---

### Question 14
```java
class Example {
    int value = 10;
    
    void method() {
        int local = 20;
        
        Runnable r = () -> {
            value = 30;
            local = 40;
        };
    }
}
```

What happens?

A. Compiles successfully  
B. Compilation error at value = 30  
C. Compilation error at local = 40  
D. Both assignments fail  
E. Runtime exception

**Answer:** C

**Explanation:** Can modify instance variable (value), but not local variable (local must be effectively final). (Exam Objective: Local-variable for lambda parameters)

---

### Question 15
```java
BinaryOperator<Integer> op = (a, b) -> a + b;

System.out.println(op.apply(5));
```

What happens?

A. Prints 5  
B. Prints 10  
C. Compilation error  
D. NullPointerException  
E. Prints 0

**Answer:** C

**Explanation:** BinaryOperator requires two arguments. apply(5) only provides one argument. (Exam Objective: Use BinaryOperator)

---

### Question 16
```java
Function<String, Integer> f = String::length;

System.out.println(f.apply("Java"));
```

What is printed?

A. 4  
B. Java  
C. Compilation error  
D. NullPointerException  
E. 0

**Answer:** A

**Explanation:** String::length is unbound instance method reference. "Java".length() = 4. (Exam Objective: Use method references)

---

### Question 17
```java
Predicate<String> pred = s -> s.startsWith("A");

System.out.println(pred.negate().test("Apple"));
```

What is printed?

A. true  
B. false  
C. Compilation error  
D. NullPointerException  
E. Apple

**Answer:** B

**Explanation:** "Apple".startsWith("A") = true. negate() reverses it to false. (Exam Objective: Use Predicate)

---

### Question 18
```java
ToIntFunction<String> func = s -> s.length();

System.out.println(func.apply("Hello"));
```

What happens?

A. Prints 5  
B. Compilation error  
C. ClassCastException  
D. Prints Hello  
E. Prints 0

**Answer:** B

**Explanation:** ToIntFunction uses applyAsInt(T), not apply(T). Should be func.applyAsInt("Hello"). (Exam Objective: Use primitive functional interfaces)

---

### Question 19
```java
List<String> list = Arrays.asList("a", "b", "c");

list.replaceAll(s -> s.toUpperCase());

System.out.println(list);
```

What is printed?

A. [a, b, c]  
B. [A, B, C]  
C. Compilation error  
D. UnsupportedOperationException  
E. []

**Answer:** B

**Explanation:** replaceAll() accepts UnaryOperator. Transforms each element to uppercase: [A, B, C]. (Exam Objective: Use UnaryOperator)

---

### Question 20
```java
Supplier<List<String>> sup = ArrayList::new;

List<String> list1 = sup.get();
List<String> list2 = sup.get();

System.out.println(list1 == list2);
```

What is printed?

A. true  
B. false  
C. Compilation error  
D. Depends on JVM  
E. NullPointerException

**Answer:** B

**Explanation:** ArrayList::new creates new instance each time. list1 and list2 are different objects. (Exam Objective: Use method references)

---

## Chapter Summary

### Official 1Z0-830 Exam Objectives Checklist

✅ **Create and use lambda expressions**
- Basic lambda syntax
- Single and multiple parameters
- Block body vs expression body
- Local-variable for lambda parameters

✅ **Use lambda expressions with Streams**
- filter, map, forEach
- Method references
- Functional composition

✅ **Use built-in functional interfaces**
- Predicate (T -> boolean)
- Consumer (T -> void)
- Function (T -> R)
- Supplier (() -> T)

✅ **Use primitive and binary variations**
- IntPredicate, IntConsumer, IntFunction
- IntSupplier, IntUnaryOperator, IntBinaryOperator
- Long and Double versions
- BiPredicate, BiConsumer, BiFunction

✅ **Use UnaryOperator and BinaryOperator**
- UnaryOperator<T> (T -> T)
- BinaryOperator<T> ((T,T) -> T)
- Primitive versions

---

### Key Points for Exam

1. **Lambda Syntax**
   - () -> expression (no params)
   - x -> expression (single param, no parentheses)
   - (x, y) -> expression (multiple params need parentheses)
   - x -> { return expression; } (block needs return)

2. **Functional Interfaces**
   - Single abstract method (SAM)
   - @FunctionalInterface optional but recommended
   - Can have default and static methods

3. **Core Functional Interfaces**
   - Predicate: test() returns boolean
   - Consumer: accept() returns void
   - Function: apply() returns result
   - Supplier: get() returns result, no params

4. **Method References**
   - Static: Class::staticMethod
   - Instance (bound): instance::method
   - Instance (unbound): Class::method
   - Constructor: Class::new

5. **Variable Scope**
   - Local variables must be effectively final
   - Can access instance and static variables
   - Lambda parameters are implicitly final
   - Can modify instance variables

6. **Primitive Versions**
   - Avoid autoboxing overhead
   - Use applyAsInt/Long/Double
   - IntPredicate, LongConsumer, DoubleFunction, etc.
   - BooleanSupplier exists (only supplier)

7. **Operators**
   - UnaryOperator: input and output same type
   - BinaryOperator: two inputs, same type output
   - Extends Function and BiFunction

8. **Composition**
   - Predicate: and(), or(), negate()
   - Function: andThen(), compose()
   - Consumer: andThen()

9. **Common Mistakes**
   - Mixing parameter type declarations
   - Forgetting parentheses for multiple params
   - Missing return in block body
   - Using get() instead of getAsInt()

10. **Exam Focus**
    - Functional interface signatures
    - Method reference types
    - Variable scope rules
    - Primitive vs generic versions
    - Composition methods

---

### Common Exam Patterns

1. **Lambda Syntax Traps**
   - Parameter type mixing
   - Parentheses requirements
   - Return statement in blocks

2. **Functional Interface Identification**
   - Predicate vs Function vs Consumer
   - When to use which interface
   - Primitive variations

3. **Method Reference Types**
   - Static vs instance
   - Bound vs unbound
   - Constructor references

4. **Variable Scope Issues**
   - Effectively final requirements
   - What can be modified
   - Variable shadowing

5. **Primitive Interface Traps**
   - applyAsInt vs apply
   - getAsInt vs get
   - Interface compatibility

6. **Composition Operations**
   - andThen vs compose order
   - Predicate logical operations
   - Chaining multiple operations

7. **Operator Usage**
   - UnaryOperator same types
   - BinaryOperator requirements
   - When to use vs Function

---

### Study Tips

1. **Memorize Core Interfaces**
   - Predicate: T -> boolean, test()
   - Consumer: T -> void, accept()
   - Function: T -> R, apply()
   - Supplier: () -> T, get()

2. **Practice Lambda Syntax**
   - Write lambdas all 4 ways
   - Understand parameter rules
   - Block vs expression body

3. **Master Method References**
   - Identify all 4 types
   - Convert lambdas to method refs
   - Understand when each applies

4. **Understand Variable Scope**
   - Effectively final concept
   - What can/cannot be modified
   - Closure behavior

5. **Know Primitive Versions**
   - Naming patterns
   - Method name differences
   - When to use primitives

6. **Composition Practice**
   - Chain multiple operations
   - Understand execution order
   - Predicate logical operations

7. **Common Pitfalls**
   - Don't forget effectively final
   - Remember method signatures
   - Know primitive method names

---

### Quick Reference Card

#### Lambda Syntax
```java
// No parameters
() -> expression
() -> { statements; }

// One parameter
x -> expression
x -> { statements; }
(x) -> expression

// Multiple parameters
(x, y) -> expression
(x, y) -> { statements; }
(Type x, Type y) -> expression
```

#### Core Interfaces
```java
Predicate<T>: T -> boolean, test(T)
Consumer<T>: T -> void, accept(T)
Function<T,R>: T -> R, apply(T)
Supplier<T>: () -> T, get()
UnaryOperator<T>: T -> T, apply(T)
BinaryOperator<T>: (T,T) -> T, apply(T,T)
```

#### Method References
```java
Static: Math::abs
Instance (bound): str::toUpperCase
Instance (unbound): String::length
Constructor: ArrayList::new
```

#### Primitive Versions
```java
IntPredicate, LongPredicate, DoublePredicate
IntConsumer, LongConsumer, DoubleConsumer
IntFunction<R>, ToIntFunction<T>
IntSupplier, LongSupplier, DoubleSupplier
IntUnaryOperator, IntBinaryOperator
```

---

### Next Steps

**Practice Tasks:**
1. Write lambdas for all core functional interfaces
2. Convert 10 lambdas to method references
3. Create custom functional interface
4. Chain 3+ functional operations
5. Use primitive functional interfaces

**Review Before Exam:**
- Lambda parameter rules
- Effectively final requirement
- Primitive method names (applyAsInt)
- Method reference types
- Composition order (andThen/compose)

**Common Exam Traps:**
- Effectively final violations
- Mixed parameter types
- Wrong method names (apply vs applyAsInt)
- Method reference type confusion
- Operator type requirements

---

[← Previous Chapter](CHAPTER-09.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-11.md)

---

**Chapter 10 Complete!** ✅  
Lambda Expressions & Functional Interfaces - Foundation of functional programming in Java.
