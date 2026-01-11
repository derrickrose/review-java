# Chapter 6: Lambda Expressions and Stream API - PART 1

[Part 2: Stream API →](CHAPTER-06-PART2-STREAMS.md) | [Back to Main Guide](README.md)

---

## Chapter Overview

Lambda expressions and the Stream API represent one of the most significant additions to Java (introduced in Java 8, enhanced in subsequent versions). They enable functional programming style, making code more concise, readable, and maintainable. Combined with method references and built-in functional interfaces, they provide powerful tools for data processing and transformation.

**Key Concepts:**
- Lambda expressions for concise anonymous functions
- Functional interfaces as lambda targets
- Method references for cleaner syntax
- Stream API for declarative data processing
- Collectors for flexible data aggregation
- Optional for null-safe operations

---

## Official Exam Objectives (1Z0-830)

This chapter covers the following Java SE 21 certification objectives:

1. **Use lambda expressions and method references**
   - Create and use lambda expressions with functional interfaces
   - Use method references (static, instance, constructor)
   - Understand variable capture and effectively final

2. **Use built-in functional interfaces from java.util.function**
   - Predicate, Function, Consumer, Supplier
   - BiPredicate, BiFunction, BiConsumer
   - UnaryOperator, BinaryOperator
   - Primitive specializations

---

# PART 1: Lambda Expressions & Functional Interfaces

## 1. Lambda Expressions

### What are Lambda Expressions?

Lambda expressions are anonymous functions that can be used to create instances of functional interfaces. They enable treating functionality as a method argument (passing behavior). Lambda expressions provide a concise way to represent one method interface using an expression.

**Syntax:** `(parameters) -> expression` or `(parameters) -> { statements; }`

**Benefits:**
- More concise code compared to anonymous inner classes
- Enables functional programming patterns
- Better readability for simple operations
- Facilitates parallel processing with Stream API
- Reduces boilerplate code

### When to Use Lambda Expressions

| Scenario | Use Lambda | Example Use Case |
|----------|------------|------------------|
| **Single method interface** | Yes | Event handlers, comparators |
| **Simple operation** | Yes | Filtering, mapping data |
| **Short-lived functionality** | Yes | Stream operations |
| **Callback functions** | Yes | Asynchronous operations |
| **Complex multi-method logic** | No | Use regular class instead |
| **Need instance variables** | No | Use anonymous class |
| **Reusable across classes** | Maybe | Consider method reference |

### Lambda Syntax Variations

```java
// 1. No parameters - must include empty parentheses
Runnable r1 = () -> System.out.println("Hello");
Supplier<Integer> s1 = () -> 42;

// 2. Single parameter - parentheses optional
Consumer<String> c1 = s -> System.out.println(s);
Consumer<String> c2 = (s) -> System.out.println(s);  // Also valid
Predicate<String> p1 = s -> s.isEmpty();

// 3. Multiple parameters - parentheses required
BiPredicate<String, String> bp1 = (s1, s2) -> s1.equals(s2);
Comparator<String> comp1 = (s1, s2) -> s1.compareTo(s2);

// 4. Explicit type declaration - optional but can improve clarity
BiFunction<Integer, Integer, Integer> add = (Integer a, Integer b) -> a + b;
// Type inference - cleaner
BiFunction<Integer, Integer, Integer> add2 = (a, b) -> a + b;

// 5. Single expression - no braces or return needed
Function<Integer, Integer> square = x -> x * x;
Predicate<Integer> isEven = x -> x % 2 == 0;

// 6. Block body - braces required, explicit return needed
Function<Integer, Integer> factorial = n -> {
    int result = 1;
    for (int i = 2; i <= n; i++) {
        result *= i;
    }
    return result;  // Explicit return required
};

// 7. Void return type - block without return
Consumer<List<String>> print = list -> {
    for (String s : list) {
        System.out.println(s);
    }
};

// 8. Multiple statements - block required
BiConsumer<String, Integer> printMultiple = (str, times) -> {
    for (int i = 0; i < times; i++) {
        System.out.println(str);
    }
};
```

### Type Inference in Lambdas

```java
// Compiler infers types from functional interface
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Type inferred from List<String>
names.forEach(name -> System.out.println(name));

// Compiler knows name is String
names.stream()
     .filter(name -> name.startsWith("A"))
     .forEach(name -> System.out.println(name.toUpperCase()));

// Can't mix: all parameters must be inferred or all declared
// BiFunction<String, Integer, String> bad = (String s, i) -> s + i;  // ❌ Won't compile
BiFunction<String, Integer, String> good = (s, i) -> s + i;          // ✅ All inferred

// Explicit types when needed for clarity or overloading
Function<Number, Integer> f1 = (Number n) -> n.intValue();
```

### Variable Capture and Effectively Final

```java
// Local variables used in lambda must be effectively final
String prefix = "Hello";
Consumer<String> greeter = name -> System.out.println(prefix + " " + name);
// prefix = "Hi";  // ❌ Would cause compilation error - not effectively final

// Effectively final - variable never reassigned after initialization
int multiplier = 2;
Function<Integer, Integer> multiply = x -> x * multiplier;  // ✅ OK
System.out.println(multiply.apply(5));  // 10
// multiplier = 3;  // ❌ Would break effectively final requirement

// Instance variables and static variables CAN be modified
class Counter {
    private int count = 0;  // Instance variable
    
    public void increment() {
        Runnable r = () -> count++;  // ✅ OK - instance variable
        r.run();
    }
}

// Mutable object references are effectively final, but contents can change
List<String> list = new ArrayList<>();
Consumer<String> addToList = s -> list.add(s);  // ✅ OK
addToList.accept("Hello");  // Modifies list contents
// list = new ArrayList<>();  // ❌ Would break effectively final
```

### Lambda vs Anonymous Class: this Reference

```java
class LambdaVsAnonymous {
    private String name = "Outer";
    
    public void testThis() {
        // Lambda: 'this' refers to enclosing class instance
        Runnable r1 = () -> System.out.println(this.name);  // Prints "Outer"
        r1.run();
        
        // Anonymous class: 'this' refers to anonymous class instance
        Runnable r2 = new Runnable() {
            private String name = "Inner";
            
            @Override
            public void run() {
                System.out.println(this.name);  // Prints "Inner"
            }
        };
        r2.run();
    }
}

// Lambda can't shadow variables from enclosing scope
void example() {
    int value = 10;
    // Consumer<Integer> c = value -> System.out.println(value);  // ❌ Won't compile
    Consumer<Integer> c = x -> System.out.println(value + x);     // ✅ OK
}
```

---

## 2. Functional Interfaces

### What is a Functional Interface?

A functional interface is an interface with exactly one abstract method (SAM - Single Abstract Method). It can have multiple default and static methods, but only one abstract method. The `@FunctionalInterface` annotation (optional but recommended) ensures the interface has exactly one abstract method.

**Key Characteristics:**
- Exactly one abstract method
- Can have default methods (Java 8+)
- Can have static methods
- Can have private methods (Java 9+)
- Serves as target type for lambda expressions and method references

### Custom Functional Interfaces

```java
// Simple functional interface
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    
    // Can have default methods
    default int add(int a, int b) {
        return a + b;
    }
    
    // Can have static methods
    static int multiply(int a, int b) {
        return a * b;
    }
}

// Usage with lambda
Calculator addition = (a, b) -> a + b;
Calculator subtraction = (a, b) -> a - b;
System.out.println(addition.calculate(5, 3));      // 8
System.out.println(subtraction.calculate(5, 3));   // 2

// Generic functional interface
@FunctionalInterface
interface Transformer<T, R> {
    R transform(T input);
}

Transformer<String, Integer> lengthFinder = s -> s.length();
System.out.println(lengthFinder.transform("Hello"));  // 5

// ❌ NOT a functional interface - two abstract methods
/*
@FunctionalInterface  // Would cause compilation error
interface InvalidFI {
    void method1();
    void method2();  // Second abstract method
}
*/

// ✅ Valid - inherits one abstract method
@FunctionalInterface
interface ValidFI extends Runnable {
    // Inherits run() from Runnable
    // No additional abstract methods
}
```

### Built-in Functional Interfaces (java.util.function)

Java provides a rich set of functional interfaces in `java.util.function` package. These cover most common use cases.

#### Complete Functional Interface Reference Table

| Interface | Abstract Method | Input | Output | Use Case |
|-----------|----------------|-------|--------|----------|
| **Predicate<T>** | test(T) | T | boolean | Testing/filtering |
| **Function<T,R>** | apply(T) | T | R | Transformation |
| **Consumer<T>** | accept(T) | T | void | Side effects |
| **Supplier<T>** | get() | none | T | Generation/factory |
| **UnaryOperator<T>** | apply(T) | T | T | Same-type transformation |
| **BinaryOperator<T>** | apply(T,T) | T,T | T | Combining two values |
| **BiPredicate<T,U>** | test(T,U) | T,U | boolean | Two-arg testing |
| **BiFunction<T,U,R>** | apply(T,U) | T,U | R | Two-arg transformation |
| **BiConsumer<T,U>** | accept(T,U) | T,U | void | Two-arg side effects |

---

### 2.1 Predicate<T> - Testing

**Purpose:** Represents a boolean-valued function of one argument. Used for filtering and testing.

**Method Signature:** `boolean test(T t)`

**Default Methods:** `and()`, `or()`, `negate()`

**Static Methods:** `isEqual(Object)`

```java
import java.util.function.Predicate;

// Basic usage
Predicate<String> isEmpty = s -> s.isEmpty();
System.out.println(isEmpty.test(""));        // true
System.out.println(isEmpty.test("Hello"));   // false

// Common predicates
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<String> startsWithA = s -> s.startsWith("A");
Predicate<String> longerThan5 = s -> s.length() > 5;

// and() - both conditions must be true
Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);
System.out.println(isEvenAndPositive.test(4));   // true
System.out.println(isEvenAndPositive.test(-4));  // false

// or() - at least one condition must be true
Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
System.out.println(isEvenOrPositive.test(3));    // true
System.out.println(isEvenOrPositive.test(-4));   // true

// negate() - logical NOT
Predicate<String> isNotEmpty = isEmpty.negate();
System.out.println(isNotEmpty.test("Hello"));    // true

// Complex combinations
Predicate<String> complexPredicate = startsWithA
    .and(longerThan5)
    .or(s -> s.contains("xyz"));

// Static method: isEqual()
Predicate<String> isHello = Predicate.isEqual("Hello");
System.out.println(isHello.test("Hello"));       // true

// Practical example: filtering
List<String> names = Arrays.asList("Alice", "Bob", "Amanda", "Charlie");
List<String> filtered = names.stream()
    .filter(startsWithA.and(longerThan5))
    .collect(Collectors.toList());
System.out.println(filtered);  // [Amanda]
```

### 2.2 Function<T, R> - Transformation

**Purpose:** Represents a function that accepts one argument and produces a result.

**Method Signature:** `R apply(T t)`

**Default Methods:** `andThen()`, `compose()`

**Static Methods:** `identity()`

```java
import java.util.function.Function;

// Basic usage
Function<String, Integer> stringLength = s -> s.length();
System.out.println(stringLength.apply("Hello"));  // 5

// Common functions
Function<Integer, String> intToString = i -> String.valueOf(i);
Function<String, String> toUpperCase = s -> s.toUpperCase();
Function<Double, Integer> round = d -> (int) Math.round(d);

// andThen() - this function first, then next
Function<String, Integer> lengthThenDouble = stringLength.andThen(i -> i * 2);
System.out.println(lengthThenDouble.apply("Hi"));  // 4

// compose() - parameter function first, then this
Function<String, String> trim = s -> s.trim();
Function<String, Integer> trimThenLength = stringLength.compose(trim);
System.out.println(trimThenLength.apply("  Hi  "));  // 2

// Complex composition
Function<String, String> processString = trim
    .andThen(toUpperCase)
    .andThen(s -> s + "!");
System.out.println(processString.apply("  hello  "));  // "HELLO!"

// Static method: identity()
Function<String, String> identity = Function.identity();
System.out.println(identity.apply("test"));  // "test"

// Practical example: mapping
List<String> words = Arrays.asList("hello", "world", "java");
List<Integer> lengths = words.stream()
    .map(stringLength)
    .collect(Collectors.toList());
System.out.println(lengths);  // [5, 5, 4]
```

### 2.3 Consumer<T> - Side Effects

**Purpose:** Represents an operation that accepts a single argument and returns no result.

**Method Signature:** `void accept(T t)`

**Default Methods:** `andThen()`

```java
import java.util.function.Consumer;

// Basic usage
Consumer<String> printer = s -> System.out.println(s);
printer.accept("Hello");

// Common consumers
Consumer<String> logger = s -> System.out.println("LOG: " + s);
Consumer<List<String>> listClearer = list -> list.clear();

// andThen() - chain consumers
Consumer<String> printAndLog = printer.andThen(logger);
printAndLog.accept("Test");

// Multiple chaining
Consumer<String> processString = printer
    .andThen(s -> System.out.println("Length: " + s.length()))
    .andThen(s -> System.out.println("Uppercase: " + s.toUpperCase()));

// Practical example: forEach
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(printer);

// Side effect example: collecting results
List<String> results = new ArrayList<>();
Consumer<String> addToResults = s -> results.add(s.toUpperCase());
names.forEach(addToResults);
System.out.println(results);  // [ALICE, BOB, CHARLIE]
```

### 2.4 Supplier<T> - Generation

**Purpose:** Represents a supplier of results. No input, produces output.

**Method Signature:** `T get()`

```java
import java.util.function.Supplier;

// Basic usage
Supplier<Double> randomSupplier = () -> Math.random();
System.out.println(randomSupplier.get());

// Common suppliers
Supplier<String> helloSupplier = () -> "Hello";
Supplier<Integer> zeroSupplier = () -> 0;
Supplier<List<String>> listFactory = () -> new ArrayList<>();

// Lazy evaluation - computed only when needed
Supplier<String> expensive = () -> {
    System.out.println("Computing expensive value...");
    return "Result";
};
System.out.println("Before get()");  // No computation yet
String result = expensive.get();     // Now it computes

// Factory pattern with constructor reference
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
List<String> list1 = listSupplier.get();
List<String> list2 = listSupplier.get();  // Different instances

// Practical example: Optional.orElseGet()
Optional<String> optional = Optional.empty();
String value = optional.orElseGet(() -> "Default");

// Practical example: Stream.generate()
List<Double> randoms = Stream.generate(randomSupplier)
    .limit(5)
    .collect(Collectors.toList());
```

### 2.5 UnaryOperator<T> - Same-Type Transformation

**Purpose:** Extends `Function<T, T>` - takes one argument and returns same type.

**Method Signature:** `T apply(T t)`

**Static Methods:** `identity()`

```java
import java.util.function.UnaryOperator;

// Basic usage
UnaryOperator<String> toUpper = s -> s.toUpperCase();
System.out.println(toUpper.apply("hello"));  // HELLO

// Common unary operators
UnaryOperator<Integer> increment = n -> n + 1;
UnaryOperator<Integer> square = n -> n * n;
UnaryOperator<String> trim = s -> s.trim();

// Static method: identity()
UnaryOperator<String> identity = UnaryOperator.identity();
System.out.println(identity.apply("test"));  // test

// Chaining
UnaryOperator<Integer> incrementThenSquare = increment.andThen(square);
System.out.println(incrementThenSquare.apply(3));  // 16

// Practical example: List.replaceAll()
List<String> words = new ArrayList<>(Arrays.asList("hello", "world"));
words.replaceAll(String::toUpperCase);
System.out.println(words);  // [HELLO, WORLD]
```

### 2.6 BinaryOperator<T> - Combining Two Values

**Purpose:** Extends `BiFunction<T, T, T>` - takes two arguments of same type, returns same type.

**Method Signature:** `T apply(T t1, T t2)`

**Static Methods:** `minBy()`, `maxBy()`

```java
import java.util.function.BinaryOperator;

// Basic usage
BinaryOperator<Integer> add = (a, b) -> a + b;
System.out.println(add.apply(5, 3));  // 8

// Common binary operators
BinaryOperator<Integer> multiply = (a, b) -> a * b;
BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;

// Static method: minBy() - returns min according to Comparator
BinaryOperator<String> shorter = BinaryOperator.minBy(
    Comparator.comparingInt(String::length)
);
System.out.println(shorter.apply("hello", "hi"));  // "hi"

// Static method: maxBy() - returns max according to Comparator
BinaryOperator<String> longer = BinaryOperator.maxBy(
    Comparator.comparingInt(String::length)
);
System.out.println(longer.apply("hello", "hi"));  // "hello"

// Practical example: Stream.reduce()
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream().reduce(0, add);  // BinaryOperator
System.out.println(sum);  // 15

int product = numbers.stream().reduce(1, multiply);
System.out.println(product);  // 120

// Find longest string
List<String> words = Arrays.asList("hi", "hello", "world");
Optional<String> longest = words.stream().reduce(longer);
System.out.println(longest.get());  // "hello" or "world"
```

### 2.7 BiPredicate<T, U> - Two-Argument Testing

**Purpose:** Represents a predicate (boolean-valued function) of two arguments.

**Method Signature:** `boolean test(T t, U u)`

**Default Methods:** `and()`, `or()`, `negate()`

```java
import java.util.function.BiPredicate;

// Basic usage
BiPredicate<String, String> areEqual = (s1, s2) -> s1.equals(s2);
System.out.println(areEqual.test("hello", "hello"));  // true

// Common bi-predicates
BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
BiPredicate<String, Integer> longerThan = (s, len) -> s.length() > len;
BiPredicate<List<Integer>, Integer> contains = (list, val) -> list.contains(val);

// Combining with and()
BiPredicate<String, String> startsWithSame = (s1, s2) -> 
    !s1.isEmpty() && !s2.isEmpty() && s1.charAt(0) == s2.charAt(0);
BiPredicate<String, String> sameStartAndEqual = startsWithSame.and(areEqual);

// Combining with or()
BiPredicate<Integer, Integer> isEqualOrGreater = 
    (a, b) -> a.equals(b) || a > b;

// negate()
BiPredicate<String, String> areNotEqual = areEqual.negate();
System.out.println(areNotEqual.test("hello", "world"));  // true

// Practical example: Map filtering
Map<String, Integer> map = new HashMap<>();
map.put("Alice", 25);
map.put("Bob", 30);
map.put("Charlie", 25);

BiPredicate<String, Integer> nameStartsWithAAndAgeOver20 = 
    (name, age) -> name.startsWith("A") && age > 20;

map.forEach((k, v) -> {
    if (nameStartsWithAAndAgeOver20.test(k, v)) {
        System.out.println(k + ": " + v);  // Alice: 25
    }
});
```

### 2.8 BiFunction<T, U, R> - Two-Argument Transformation

**Purpose:** Represents a function that accepts two arguments and produces a result.

**Method Signature:** `R apply(T t, U u)`

**Default Methods:** `andThen()`

```java
import java.util.function.BiFunction;

// Basic usage
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
System.out.println(add.apply(5, 3));  // 8

// Common bi-functions
BiFunction<String, String, String> concat = (s1, s2) -> s1 + s2;
BiFunction<Integer, Integer, Double> average = (a, b) -> (a + b) / 2.0;
BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);

// andThen() - chain with Function
BiFunction<Integer, Integer, String> sumThenToString = 
    add.andThen(result -> "Sum: " + result);
System.out.println(sumThenToString.apply(5, 3));  // "Sum: 8"

// Complex transformation
BiFunction<String, String, Integer> totalLength = 
    concat.andThen(String::length);
System.out.println(totalLength.apply("Hello", "World"));  // 10

// Practical example: Map.merge()
Map<String, Integer> inventory = new HashMap<>();
inventory.put("apple", 10);

BiFunction<Integer, Integer, Integer> sumValues = (old, val) -> old + val;
inventory.merge("apple", 5, sumValues);  // 10 + 5 = 15
System.out.println(inventory.get("apple"));  // 15

// Practical example: custom computation
BiFunction<List<Integer>, Integer, List<Integer>> addToAll = 
    (list, value) -> list.stream()
        .map(n -> n + value)
        .collect(Collectors.toList());

List<Integer> numbers = Arrays.asList(1, 2, 3);
List<Integer> result = addToAll.apply(numbers, 10);
System.out.println(result);  // [11, 12, 13]
```

### 2.9 BiConsumer<T, U> - Two-Argument Side Effects

**Purpose:** Represents an operation that accepts two arguments and returns no result.

**Method Signature:** `void accept(T t, U u)`

**Default Methods:** `andThen()`

```java
import java.util.function.BiConsumer;

// Basic usage
BiConsumer<String, Integer> printWithCount = 
    (str, count) -> System.out.println(str + ": " + count);
printWithCount.accept("Count", 5);  // Count: 5

// Common bi-consumers
BiConsumer<String, String> printPair = 
    (k, v) -> System.out.println(k + " -> " + v);
BiConsumer<List<String>, String> addToList = 
    (list, str) -> list.add(str);

// andThen() - chain bi-consumers
BiConsumer<String, Integer> logAndPrint = printWithCount
    .andThen((s, i) -> System.out.println("Logged: " + s));

// Practical example: Map.forEach()
Map<String, Integer> ages = new HashMap<>();
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.forEach(printPair);  // BiConsumer
// Alice -> 25
// Bob -> 30

// Practical example: accumulating results
Map<String, List<Integer>> grouped = new HashMap<>();
BiConsumer<String, Integer> addToGroup = (key, value) -> {
    grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
};
addToGroup.accept("even", 2);
addToGroup.accept("odd", 3);
addToGroup.accept("even", 4);
System.out.println(grouped);  // {even=[2, 4], odd=[3]}
```

### 2.10 Primitive Specializations

To avoid boxing/unboxing overhead, Java provides primitive specializations for common types.

```java
// IntPredicate - avoids boxing Integer
IntPredicate isEven = n -> n % 2 == 0;
System.out.println(isEven.test(4));  // true (no boxing)

// IntFunction<R> - int input, generic output
IntFunction<String> intToString = n -> "Number: " + n;
System.out.println(intToString.apply(42));  // "Number: 42"

// ToIntFunction<T> - generic input, int output
ToIntFunction<String> lengthFunction = s -> s.length();
System.out.println(lengthFunction.applyAsInt("Hello"));  // 5

// IntUnaryOperator - int to int
IntUnaryOperator square = n -> n * n;
System.out.println(square.applyAsInt(5));  // 25

// IntBinaryOperator - two ints to int
IntBinaryOperator multiply = (a, b) -> a * b;
System.out.println(multiply.applyAsInt(5, 3));  // 15

// IntConsumer - consumes int
IntConsumer printer = n -> System.out.println(n);
IntStream.of(1, 2, 3).forEach(printer);

// IntSupplier - supplies int
IntSupplier random = () -> (int) (Math.random() * 100);
System.out.println(random.getAsInt());

// Similar for Long and Double:
// LongPredicate, LongFunction, ToLongFunction, LongUnaryOperator, etc.
// DoublePredicate, DoubleFunction, ToDoubleFunction, DoubleUnaryOperator, etc.

// Practical example: avoiding boxing in Stream
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// With boxing (slower)
int sum1 = numbers.stream()
    .filter(n -> n % 2 == 0)  // Predicate<Integer> - boxing
    .mapToInt(n -> n)
    .sum();

// Without boxing (faster)
int sum2 = numbers.stream()
    .mapToInt(Integer::intValue)
    .filter(n -> n % 2 == 0)  // IntPredicate - no boxing
    .sum();
```

---

## 3. Method References

### What are Method References?

Method references are a shorthand notation of a lambda expression to call a method. If a lambda expression only calls an existing method, you can use a method reference instead. They make code more readable and concise.

**Syntax:** `TargetReference::methodName`

### Types of Method References

#### 3.1 Static Method Reference: `ClassName::staticMethod`

```java
// Lambda
Function<String, Integer> lambda = s -> Integer.parseInt(s);

// Method reference
Function<String, Integer> methodRef = Integer::parseInt;

System.out.println(methodRef.apply("123"));  // 123

// More examples
Consumer<String> printer = System.out::println;  // Static method
BiFunction<Integer, Integer, Integer> max = Math::max;
Function<Double, Double> sqrt = Math::sqrt;

// Practical example
List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");
List<Integer> ints = numbers.stream()
    .map(Integer::parseInt)  // Static method reference
    .collect(Collectors.toList());
```

#### 3.2 Instance Method Reference (Bound): `instance::instanceMethod`

Bound to a specific object instance.

```java
String prefix = "Hello";

// Lambda
Function<String, String> lambda = s -> prefix.concat(s);

// Method reference (bound to 'prefix' instance)
Function<String, String> methodRef = prefix::concat;

System.out.println(methodRef.apply(" World"));  // "Hello World"

// More examples
List<String> words = Arrays.asList("apple", "banana", "cherry");
words.forEach(System.out::println);  // Bound to System.out instance

StringBuilder sb = new StringBuilder("Start");
Consumer<String> appender = sb::append;  // Bound to sb instance
appender.accept(" Middle");
appender.accept(" End");
System.out.println(sb);  // "Start Middle End"

// Practical example
Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;
BiPredicate<String, String> equalIgnoreCase = comparator::equals;
// Bound to comparator instance
```

#### 3.3 Instance Method Reference (Unbound): `ClassName::instanceMethod`

Not bound to specific instance - first parameter becomes the instance.

```java
// Lambda - first parameter is the instance
Function<String, Integer> lambda = s -> s.length();

// Method reference - String::length (unbound)
Function<String, Integer> methodRef = String::length;

System.out.println(methodRef.apply("Hello"));  // 5

// More examples
Predicate<String> isEmpty = String::isEmpty;  // Unbound
BiPredicate<String, String> equals = String::equals;  // Unbound

// First parameter becomes the instance
System.out.println(equals.test("hello", "hello"));  // true

// Practical example: sorting
List<String> words = Arrays.asList("banana", "apple", "cherry");
words.sort(String::compareToIgnoreCase);  // Unbound method reference
System.out.println(words);  // [apple, banana, cherry]

// More practical examples
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<Integer> lengths = names.stream()
    .map(String::length)  // Unbound: each string calls its length()
    .collect(Collectors.toList());

// Filtering
List<String> nonEmpty = names.stream()
    .filter(String::isEmpty)  // Unbound: each string calls isEmpty()
    .collect(Collectors.toList());
```

#### 3.4 Constructor Reference: `ClassName::new`

```java
// Lambda
Supplier<ArrayList<String>> lambda = () -> new ArrayList<>();

// Constructor reference
Supplier<ArrayList<String>> constructorRef = ArrayList::new;

List<String> list = constructorRef.get();

// More examples with parameters
Function<Integer, ArrayList<String>> listWithCapacity = ArrayList::new;
List<String> list10 = listWithCapacity.apply(10);  // new ArrayList<>(10)

BiFunction<String, Integer, String> stringConstructor = String::new;
// Creates String from char array and count

// Practical example: creating objects from strings
List<String> stringNumbers = Arrays.asList("1", "2", "3", "4", "5");
List<Integer> integers = stringNumbers.stream()
    .map(Integer::new)  // Constructor reference
    .collect(Collectors.toList());

// Creating custom objects
class Person {
    String name;
    int age;
    
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

BiFunction<String, Integer, Person> personFactory = Person::new;
Person alice = personFactory.apply("Alice", 25);

// Array constructor reference
Function<Integer, String[]> arrayCreator = String[]::new;
String[] array = arrayCreator.apply(5);  // new String[5]

// Practical: toArray with constructor reference
List<String> words = Arrays.asList("hello", "world");
String[] wordsArray = words.stream()
    .toArray(String[]::new);  // Constructor reference for array
```

### Method Reference vs Lambda: When to Use

| Scenario | Use Method Reference | Use Lambda |
|----------|---------------------|------------|
| **Calling existing method** | Yes | No |
| **Simple operation** | Yes | Maybe |
| **Need additional logic** | No | Yes |
| **Multiple statements** | No | Yes |
| **Better readability** | Usually | Sometimes |

```java
// Method reference - cleaner
list.forEach(System.out::println);

// Lambda - when you need more
list.forEach(s -> {
    if (s != null) {
        System.out.println(s.toUpperCase());
    }
});
```

---

## 4. Real-World Examples

### Example 1: Data Validation System

```java
public class ValidationSystem {
    // Validators using Predicates
    private static final Predicate<String> NOT_EMPTY = s -> !s.isEmpty();
    private static final Predicate<String> VALID_EMAIL = 
        s -> s.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Predicate<String> MIN_LENGTH = 
        s -> s.length() >= 8;
    private static final Predicate<String> HAS_UPPERCASE = 
        s -> s.chars().anyMatch(Character::isUpperCase);
    private static final Predicate<String> HAS_DIGIT = 
        s -> s.chars().anyMatch(Character::isDigit);
    
    // Validate email
    public static boolean validateEmail(String email) {
        return NOT_EMPTY.and(VALID_EMAIL).test(email);
    }
    
    // Validate password - combine multiple predicates
    public static boolean validatePassword(String password) {
        Predicate<String> strongPassword = NOT_EMPTY
            .and(MIN_LENGTH)
            .and(HAS_UPPERCASE)
            .and(HAS_DIGIT);
        
        return strongPassword.test(password);
    }
    
    // Get validation errors
    public static List<String> getPasswordErrors(String password) {
        List<String> errors = new ArrayList<>();
        
        Map<Predicate<String>, String> validations = new HashMap<>();
        validations.put(NOT_EMPTY, "Password cannot be empty");
        validations.put(MIN_LENGTH, "Password must be at least 8 characters");
        validations.put(HAS_UPPERCASE, "Password must contain uppercase letter");
        validations.put(HAS_DIGIT, "Password must contain a digit");
        
        validations.forEach((predicate, message) -> {
            if (!predicate.test(password)) {
                errors.add(message);
            }
        });
        
        return errors;
    }
    
    // Usage
    public static void main(String[] args) {
        String email = "user@example.com";
        String password = "Weak1";
        
        System.out.println("Email valid: " + validateEmail(email));
        System.out.println("Password valid: " + validatePassword(password));
        
        List<String> errors = getPasswordErrors(password);
        errors.forEach(System.out::println);
    }
}
```
**Why Functional Interfaces:** Predicates provide a clean way to compose validation rules. Easy to add new validators, combine them, and test individual rules.

### Example 2: Data Transformation Pipeline

```java
public class DataProcessor {
    // Data transformers using Functions
    private static final Function<String, String> TRIM = String::trim;
    private static final Function<String, String> TO_UPPER = String::toUpperCase;
    private static final Function<String, String> REMOVE_SPACES = 
        s -> s.replaceAll("\\s+", "");
    private static final Function<String, Integer> TO_LENGTH = String::length;
    
    // Pipeline builder
    public static class Pipeline<T, R> {
        private Function<T, R> function;
        
        public Pipeline(Function<T, R> function) {
            this.function = function;
        }
        
        public <V> Pipeline<T, V> andThen(Function<R, V> next) {
            return new Pipeline<>(function.andThen(next));
        }
        
        public R process(T input) {
            return function.apply(input);
        }
    }
    
    // Build processing pipeline
    public static Pipeline<String, Integer> buildTextProcessor() {
        return new Pipeline<>(TRIM)
            .andThen(TO_UPPER)
            .andThen(REMOVE_SPACES)
            .andThen(TO_LENGTH);
    }
    
    // Process collection with pipeline
    public static <T, R> List<R> processAll(
            List<T> items, 
            Function<T, R> processor) {
        return items.stream()
            .map(processor)
            .collect(Collectors.toList());
    }
    
    // Usage
    public static void main(String[] args) {
        List<String> inputs = Arrays.asList(
            "  hello world  ",
            "  JAVA  ",
            "  Lambda Expressions  "
        );
        
        // Process individual
        Pipeline<String, Integer> processor = buildTextProcessor();
        String input = "  hello world  ";
        int result = processor.process(input);
        System.out.println("Length: " + result);  // HELLOWORLD = 10
        
        // Process collection
        List<Integer> lengths = processAll(inputs, processor::process);
        System.out.println(lengths);  // [10, 4, 17]
        
        // Different pipeline
        Function<String, String> cleanAndPrefix = TRIM
            .andThen(TO_UPPER)
            .andThen(s -> "PREFIX_" + s);
        
        List<String> processed = processAll(inputs, cleanAndPrefix);
        processed.forEach(System.out::println);
    }
}
```
**Why Functional Interfaces:** Functions enable building reusable transformation pipelines. Easy to compose, test independently, and chain in different orders.

### Example 3: Event Handler System

```java
public class EventSystem {
    // Event handlers using Consumers
    private Map<String, List<Consumer<String>>> listeners = new HashMap<>();
    
    // Register event listener
    public void on(String event, Consumer<String> handler) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(handler);
    }
    
    // Emit event - all handlers executed
    public void emit(String event, String data) {
        List<Consumer<String>> handlers = listeners.get(event);
        if (handlers != null) {
            handlers.forEach(handler -> handler.accept(data));
        }
    }
    
    // Remove all listeners for event
    public void off(String event) {
        listeners.remove(event);
    }
    
    // Usage example
    public static void main(String[] args) {
        EventSystem events = new EventSystem();
        
        // Register multiple handlers for same event
        events.on("user.login", data -> 
            System.out.println("LOG: User logged in: " + data)
        );
        
        events.on("user.login", data -> 
            System.out.println("EMAIL: Sending welcome email to " + data)
        );
        
        events.on("user.login", data -> 
            System.out.println("METRICS: Recording login for " + data)
        );
        
        // Emit event - all handlers execute
        events.emit("user.login", "alice@example.com");
        
        // Register handler with multiple actions using andThen
        Consumer<String> logAndNotify = 
            (data -> System.out.println("Logging: " + data))
            .andThen(data -> System.out.println("Notifying: " + data));
        
        events.on("user.logout", logAndNotify);
        events.emit("user.logout", "alice@example.com");
    }
}
```
**Why Functional Interfaces:** Consumers are perfect for event handlers that perform side effects without returning values. Easy to chain multiple handlers and compose actions.

### Example 4: Configuration Factory with Suppliers

```java
public class ConfigFactory {
    // Configuration suppliers - lazy evaluation
    private static final Supplier<Properties> DEFAULT_CONFIG = () -> {
        Properties props = new Properties();
        props.setProperty("db.host", "localhost");
        props.setProperty("db.port", "5432");
        props.setProperty("db.name", "mydb");
        return props;
    };
    
    private static final Supplier<Properties> TEST_CONFIG = () -> {
        Properties props = new Properties();
        props.setProperty("db.host", "test-server");
        props.setProperty("db.port", "5433");
        props.setProperty("db.name", "testdb");
        return props;
    };
    
    // Lazy configuration provider
    private Map<String, Supplier<Properties>> configs = new HashMap<>();
    
    public ConfigFactory() {
        configs.put("default", DEFAULT_CONFIG);
        configs.put("test", TEST_CONFIG);
        configs.put("production", () -> loadFromFile("prod.properties"));
    }
    
    // Get config - only computed when requested
    public Properties getConfig(String environment) {
        Supplier<Properties> supplier = configs.getOrDefault(
            environment, 
            DEFAULT_CONFIG
        );
        return supplier.get();  // Lazy evaluation happens here
    }
    
    // Factory method with Supplier
    public static <T> T createInstance(Supplier<T> factory, Consumer<T> initializer) {
        T instance = factory.get();
        initializer.accept(instance);
        return instance;
    }
    
    // Expensive computation cached with Supplier
    private static class CachedSupplier<T> implements Supplier<T> {
        private Supplier<T> supplier;
        private T cached;
        private boolean computed = false;
        
        public CachedSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }
        
        @Override
        public T get() {
            if (!computed) {
                cached = supplier.get();
                computed = true;
            }
            return cached;
        }
    }
    
    private static Properties loadFromFile(String filename) {
        System.out.println("Loading config from: " + filename);
        // Expensive operation
        Properties props = new Properties();
        // Load from file...
        return props;
    }
    
    // Usage
    public static void main(String[] args) {
        ConfigFactory factory = new ConfigFactory();
        
        // Get config - lazy evaluation
        Properties config = factory.getConfig("test");
        System.out.println("DB Host: " + config.getProperty("db.host"));
        
        // Create instance with initialization
        StringBuilder sb = createInstance(
            StringBuilder::new,
            builder -> {
                builder.append("Initial");
                builder.append(" Value");
            }
        );
        System.out.println(sb);  // "Initial Value"
        
        // Cached supplier - expensive operation only once
        Supplier<String> expensive = new CachedSupplier<>(() -> {
            System.out.println("Computing expensive value...");
            return "Result";
        });
        
        System.out.println(expensive.get());  // Prints "Computing..." and "Result"
        System.out.println(expensive.get());  // Only prints "Result" (cached)
    }
}
```
**Why Functional Interfaces:** Suppliers enable lazy evaluation - values computed only when needed. Perfect for expensive operations, configuration loading, and factory patterns.

---

## Exam Traps

- ❌ **TRAP 1:** Effectively final requirement for captured variables
  ```java
  int multiplier = 2;
  Function<Integer, Integer> f = x -> x * multiplier;
  multiplier = 3;  // ❌ Compilation error - not effectively final
  ```

- ❌ **TRAP 2:** Lambda can't have same parameter name as local variable
  ```java
  int value = 10;
  // Consumer<Integer> c = value -> System.out.println(value);  // ❌ Won't compile
  Consumer<Integer> c = x -> System.out.println(value + x);     // ✅ OK
  ```

- ❌ **TRAP 3:** 'this' in lambda refers to enclosing class, not lambda
  ```java
  class Example {
      private int value = 10;
      
      public void test() {
          Runnable r = () -> System.out.println(this.value);  // ✅ 'this' is Example
          // In anonymous class, 'this' would be the Runnable instance
      }
  }
  ```

- ❌ **TRAP 4:** Can't mix type inference - all or none
  ```java
  // BiFunction<String, Integer, String> bad = (String s, i) -> s + i;  // ❌
  BiFunction<String, Integer, String> good1 = (s, i) -> s + i;          // ✅
  BiFunction<String, Integer, String> good2 = (String s, Integer i) -> s + i;  // ✅
  ```

- ❌ **TRAP 5:** Single parameter lambda - parentheses optional only without type
  ```java
  Consumer<String> c1 = s -> System.out.println(s);           // ✅ OK
  Consumer<String> c2 = (s) -> System.out.println(s);         // ✅ OK
  // Consumer<String> c3 = String s -> System.out.println(s); // ❌ Won't compile
  Consumer<String> c4 = (String s) -> System.out.println(s);  // ✅ OK
  ```

- ❌ **TRAP 6:** Block body requires explicit return (except void)
  ```java
  // Function<Integer, Integer> f1 = x -> { x * 2 };  // ❌ Missing return
  Function<Integer, Integer> f2 = x -> { return x * 2; };    // ✅ OK
  Function<Integer, Integer> f3 = x -> x * 2;                // ✅ OK (expression)
  ```

- ❌ **TRAP 7:** Checked exceptions not allowed in lambda without handling
  ```java
  // Runnable r = () -> Thread.sleep(1000);  // ❌ Unhandled InterruptedException
  Runnable r = () -> {
      try {
          Thread.sleep(1000);  // ✅ Must handle
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  };
  ```

- ❌ **TRAP 8:** @FunctionalInterface requires exactly ONE abstract method
  ```java
  @FunctionalInterface
  interface Calculator {
      int calculate(int a, int b);
      // int multiply(int a, int b);  // ❌ Would cause compilation error
      default int add(int a, int b) { return a + b; }  // ✅ Default methods OK
      static int subtract(int a, int b) { return a - b; }  // ✅ Static methods OK
  }
  ```

- ❌ **TRAP 9:** Method reference type must match functional interface
  ```java
  // Function<String, Integer> f = String::length;  // ✅ OK
  // Consumer<String> c = String::length;  // ❌ Wrong - returns int, not void
  Consumer<String> c = System.out::println;  // ✅ OK - returns void
  ```

- ❌ **TRAP 10:** Mutable object contents can change even if reference is effectively final
  ```java
  List<String> list = new ArrayList<>();  // Effectively final
  Consumer<String> add = s -> list.add(s);  // ✅ OK - reference not reassigned
  add.accept("Hello");  // ✅ Modifies list contents
  // list = new ArrayList<>();  // ❌ Would break effectively final
  ```

- ❌ **TRAP 11:** Predicate.and() vs && - different evaluation
  ```java
  Predicate<String> notNull = s -> s != null;
  Predicate<String> notEmpty = s -> s.isEmpty();
  
  // and() - both always evaluated
  Predicate<String> combined1 = notNull.and(notEmpty);
  // combined1.test(null);  // NullPointerException - both evaluated!
  
  // Lambda with && - short-circuit evaluation
  Predicate<String> combined2 = s -> s != null && s.isEmpty();
  combined2.test(null);  // false - short-circuits, no NPE
  ```

- ❌ **TRAP 12:** Unbound method reference - first parameter is the instance
  ```java
  BiPredicate<String, String> equals = String::equals;  // Unbound
  // First parameter is the instance, second is the argument
  equals.test("hello", "hello");  // ✅ "hello".equals("hello")
  
  // NOT the same as:
  BiPredicate<String, String> notSame = (s1, s2) -> s1.equals(s2);  // Same
  // But could be confused with:
  Predicate<String> isHello = "hello"::equals;  // Bound to "hello" instance
  ```

---

## Exam Questions

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **Lambda/Functional Interface questions:** ~4-6 questions (8-12% of exam)

---

### Question 1: Lambda syntax
```java
// Which lambda expression is INVALID?
Function<Integer, Integer> f1 = x -> x * 2;
Function<Integer, Integer> f2 = (x) -> x * 2;
Function<Integer, Integer> f3 = (Integer x) -> x * 2;
Function<Integer, Integer> f4 = Integer x -> x * 2;
```
**What is the result?**  
A. All are valid  
B. f4 is invalid  
C. f3 and f4 are invalid  
D. All are invalid  

**Answer:** B  
**Explanation:** f4 is invalid because when you specify the type (Integer), you MUST include parentheses: `(Integer x)`. Without parentheses, the type cannot be specified.

---

### Question 2: Effectively final
```java
int value = 10;
Consumer<Integer> c = x -> System.out.println(value + x);
value = 20;
c.accept(5);
```
**What is the result?**  
A. Prints: 15  
B. Prints: 25  
C. Compilation error  
D. Runtime exception  

**Answer:** C  
**Explanation:** The variable `value` is not effectively final because it's reassigned after being used in the lambda. Variables captured by lambdas must be effectively final (never reassigned).

---

### Question 3: Method reference types
```java
Function<String, Integer> f1 = String::length;
Function<String, Integer> f2 = s -> s.length();
BiPredicate<String, String> p1 = String::equals;
```
**Which statements are true?**  
A. f1 and f2 are equivalent  
B. p1 is an unbound method reference  
C. Both A and B  
D. Neither A nor B  

**Answer:** C  
**Explanation:** f1 (method reference) and f2 (lambda) are equivalent - both call length() on a String. p1 is an unbound method reference where the first parameter (String) becomes the instance and equals() is called on it with the second parameter.

---

### Question 4: Predicate combination
```java
Predicate<String> p1 = s -> s.length() > 5;
Predicate<String> p2 = s -> s.startsWith("A");
Predicate<String> combined = p1.and(p2);
System.out.println(combined.test("Alice"));
```
**What is the output?**  
A. true  
B. false  
C. Compilation error  
D. Runtime exception  

**Answer:** A  
**Explanation:** "Alice" has length > 5 (true) AND starts with "A" (true), so and() returns true. Both conditions must be true for and() to return true.

---

### Question 5: Function composition
```java
Function<Integer, Integer> f1 = x -> x * 2;
Function<Integer, Integer> f2 = x -> x + 10;
Function<Integer, Integer> composed = f1.andThen(f2);
System.out.println(composed.apply(5));
```
**What is the output?**  
A. 30  
B. 20  
C. 25  
D. 15  

**Answer:** B  
**Explanation:** andThen() applies f1 first, then f2. So: 5 * 2 = 10, then 10 + 10 = 20. If it were compose(), it would be: 5 + 10 = 15, then 15 * 2 = 30.

---

### Question 6: this reference in lambda
```java
class Example {
    private String value = "Outer";
    
    public void test() {
        Consumer<String> c = s -> System.out.println(this.value + s);
        c.accept(" World");
    }
}
```
**What does this print when test() is called?**  
A. Outer World  
B. Compilation error (this not accessible)  
C. null World  
D. Runtime exception  

**Answer:** A  
**Explanation:** In a lambda, 'this' refers to the enclosing class instance (Example), not the lambda itself. So this.value accesses Example's value field = "Outer".

---

### Question 7: BinaryOperator
```java
BinaryOperator<Integer> op = (a, b) -> a + b;
System.out.println(op.apply(5, 3));
```
**What is the output?**  
A. 8  
B. 53  
C. Compilation error  
D. 15  

**Answer:** A  
**Explanation:** BinaryOperator<Integer> takes two Integers and returns an Integer. This lambda adds them: 5 + 3 = 8.

---

### Question 8: Constructor reference
```java
Supplier<ArrayList<String>> s1 = () -> new ArrayList<>();
Supplier<ArrayList<String>> s2 = ArrayList::new;
```
**Which statements are true?**  
A. s1 and s2 are equivalent  
B. Only s1 is valid  
C. Only s2 is valid  
D. Neither is valid  

**Answer:** A  
**Explanation:** Both create a Supplier that returns a new ArrayList when get() is called. ArrayList::new is a constructor reference equivalent to () -> new ArrayList<>().

---

### Question 9: Functional interface
```java
@FunctionalInterface
interface MyFunction {
    void process();
    default void log() { }
    static void print() { }
}
```
**Is this valid?**  
A. Yes  
B. No - too many methods  
C. No - missing abstract method  
D. No - can't have default methods  

**Answer:** A  
**Explanation:** Valid functional interface - it has exactly ONE abstract method (process). Default and static methods don't count toward the single abstract method requirement.

---

### Question 10: BiConsumer
```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);
BiConsumer<String, Integer> bc = (k, v) -> System.out.println(k + v);
map.forEach(bc);
```
**What is the output?**  
A. A1  
   B2  
B. {A=1, B=2}  
C. Compilation error  
D. Runtime exception  

**Answer:** A  
**Explanation:** Map.forEach() takes a BiConsumer<K, V>. For each entry, it prints key + value: "A" + 1 = "A1", "B" + 2 = "B2" (order may vary as HashMap is unordered).

---

**[Continue to Part 2: Stream API →](CHAPTER-06-PART2-STREAMS.md)**
