# Chapter 9: Streams API

[← Previous Chapter](CHAPTER-08.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-10.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Work with streams |
| **Java Versions** | 8 (Streams), 9 (takeWhile, dropWhile), 16 (toList()) |
| **Code Examples** | 65+ |
| **Study Time** | 12-15 hours |
| **Exam Weight** | ~10-15% |
| **Difficulty** | ⭐⭐⭐⭐ High |

**Official 1Z0-830 Exam Objectives Covered:**
- Use Java object and primitive Streams, including lambda expressions implementing functional interfaces, to supply, filter, map, consume, and sort data
- Perform decomposition, concatenation and reduction, and grouping and partitioning on sequential and parallel streams

---

## 1. Stream Basics and Creation

**Exam Objective:** Use Java object and primitive Streams

### What are Streams?
Streams are sequences of elements supporting sequential and parallel aggregate operations. Streams don't store data - they convey elements from a source through a pipeline of operations.

### When to Use
- Processing collections (filter, transform, aggregate)
- Bulk operations on data
- Functional-style programming
- Parallel processing of large datasets

### Why Use Streams
- Declarative (what, not how)
- Composable operations
- Lazy evaluation (performance)
- Parallel processing support
- Less boilerplate code

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class StreamBasicsExample {
    public static void main(String[] args) {
        // 1. Creating streams from collections
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        Stream<String> stream1 = names.stream();
        Stream<String> parallelStream = names.parallelStream();
        
        // 2. Creating streams from arrays
        String[] array = {"A", "B", "C"};
        Stream<String> stream2 = Arrays.stream(array);
        Stream<String> stream3 = Stream.of("X", "Y", "Z");
        
        // 3. Creating infinite streams
        Stream<Integer> infinite = Stream.iterate(0, n -> n + 2); // 0, 2, 4, 6...
        Stream<Double> random = Stream.generate(Math::random);
        
        // 4. Creating primitive streams
        IntStream intStream = IntStream.range(1, 5); // 1, 2, 3, 4
        IntStream intStreamClosed = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5
        LongStream longStream = LongStream.of(1L, 2L, 3L);
        DoubleStream doubleStream = DoubleStream.of(1.5, 2.5, 3.5);
        
        // 5. Creating streams from files
        try (Stream<String> lines = Files.lines(Paths.get("data.txt"))) {
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 6. Creating streams with builder
        Stream<String> built = Stream.<String>builder()
            .add("a")
            .add("b")
            .add("c")
            .build();
        
        // 7. Empty streams
        Stream<String> empty = Stream.empty();
        
        // 8. Practical example - filtering names
        List<String> filtered = names.stream()
            .filter(name -> name.length() > 4)
            .collect(Collectors.toList());
        System.out.println(filtered); // [Alice, Charlie, David]
    }
}
```
**Why:** Report generation from database - stream query results, filter, transform, and aggregate without loading all data into memory.

### Exam Traps
- ❌ **TRAP:** Streams can only be used once
  ```java
  Stream<String> stream = Stream.of("A", "B", "C");
  stream.forEach(System.out::println); // Consumes stream
  stream.forEach(System.out::println); // ❌ IllegalStateException: stream has already been operated upon
  ```
- ❌ **TRAP:** Streams are lazy - intermediate operations don't execute until terminal operation
  ```java
  Stream<String> stream = Stream.of("A", "B", "C")
      .filter(s -> {
          System.out.println("Filter: " + s);
          return true;
      });
  // Nothing printed yet - no terminal operation!
  
  stream.count(); // NOW filter executes and prints
  ```
- ❌ **TRAP:** range() is exclusive, rangeClosed() is inclusive
  ```java
  IntStream.range(1, 5).forEach(System.out::print);       // 1234
  IntStream.rangeClosed(1, 5).forEach(System.out::print); // 12345
  ```
- ❌ **TRAP:** Stream.of() with array creates single element stream
  ```java
  int[] array = {1, 2, 3};
  Stream<int[]> wrong = Stream.of(array); // Stream of ONE element (the array)
  IntStream correct = Arrays.stream(array); // Stream of THREE elements
  ```

---

## 2. Intermediate Operations (filter, map, flatMap)

**Exam Objective:** Filter, map data using streams

### What are Intermediate Operations?
Operations that transform a stream into another stream. They are lazy - don't execute until a terminal operation is called.

### When to Use
| Operation | When to Use |
|-----------|-------------|
| **filter** | Select elements matching condition |
| **map** | Transform each element (1:1 mapping) |
| **flatMap** | Transform each element to stream and flatten (1:many) |
| **distinct** | Remove duplicates |
| **sorted** | Sort elements |
| **peek** | Debug or side-effects |
| **limit** | Take first n elements |
| **skip** | Skip first n elements |

### Why Use Intermediate Operations
- Chain multiple operations
- Lazy evaluation (performance)
- Readable code
- Immutable operations

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class IntermediateOpsExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "apricot", "blueberry", "avocado");
        
        // 1. filter - select elements
        List<String> startsWithA = words.stream()
            .filter(w -> w.startsWith("a"))
            .collect(Collectors.toList());
        System.out.println(startsWithA); // [apple, apricot, avocado]
        
        // 2. map - transform elements (1:1)
        List<Integer> lengths = words.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println(lengths); // [5, 6, 7, 9, 7]
        
        // 3. flatMap - flatten nested structures
        List<List<String>> nested = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d", "e")
        );
        
        List<String> flattened = nested.stream()
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());
        System.out.println(flattened); // [a, b, c, d, e]
        
        // 4. distinct - remove duplicates
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
        List<Integer> unique = numbers.stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println(unique); // [1, 2, 3, 4]
        
        // 5. sorted - sort elements
        List<String> sorted = words.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println(sorted); // [apple, apricot, avocado, banana, blueberry]
        
        // 6. limit and skip
        List<String> limited = words.stream()
            .limit(3)
            .collect(Collectors.toList());
        System.out.println(limited); // [apple, banana, apricot]
        
        List<String> skipped = words.stream()
            .skip(2)
            .collect(Collectors.toList());
        System.out.println(skipped); // [apricot, blueberry, avocado]
        
        // 7. peek - debug pipeline
        List<String> result = words.stream()
            .peek(w -> System.out.println("Before filter: " + w))
            .filter(w -> w.length() > 6)
            .peek(w -> System.out.println("After filter: " + w))
            .collect(Collectors.toList());
        
        // 8. Chaining operations
        List<String> complex = words.stream()
            .filter(w -> w.startsWith("a"))
            .map(String::toUpperCase)
            .sorted()
            .limit(2)
            .collect(Collectors.toList());
        System.out.println(complex); // [APPLE, APRICOT]
        
        // 9. flatMap with split
        String sentence = "Java streams are powerful";
        List<String> splitWords = Stream.of(sentence)
            .flatMap(s -> Arrays.stream(s.split(" ")))
            .collect(Collectors.toList());
        System.out.println(splitWords); // [Java, streams, are, powerful]
    }
}
```
**Why:** E-commerce search - filter products by category, map to DTOs, sort by price, limit to 10 results.

### Exam Traps
- ❌ **TRAP:** map() vs flatMap() - when to use each
  ```java
  List<String> words = Arrays.asList("Hello", "World");
  
  // map - returns Stream<String[]>
  Stream<String[]> wrong = words.stream()
      .map(w -> w.split("")); // Stream of arrays
  
  // flatMap - returns Stream<String>
  Stream<String> correct = words.stream()
      .flatMap(w -> Arrays.stream(w.split(""))); // Stream of characters
  ```
- ❌ **TRAP:** peek() doesn't modify stream elements (returns same stream)
  ```java
  List<String> words = Arrays.asList("a", "b", "c");
  
  words.stream()
      .peek(s -> s.toUpperCase()) // ❌ Does nothing! Strings are immutable
      .forEach(System.out::println); // Prints: a, b, c
  
  words.stream()
      .map(String::toUpperCase) // ✅ Correct way to transform
      .forEach(System.out::println); // Prints: A, B, C
  ```
- ❌ **TRAP:** sorted() without Comparator requires Comparable elements
  ```java
  class Person {
      String name;
      Person(String name) { this.name = name; }
  }
  
  List<Person> people = Arrays.asList(new Person("Bob"), new Person("Alice"));
  
  people.stream()
      .sorted() // ❌ ClassCastException! Person doesn't implement Comparable
      .forEach(System.out::println);
  
  people.stream()
      .sorted(Comparator.comparing(p -> p.name)) // ✅ Provide Comparator
      .forEach(System.out::println);
  ```
- ❌ **TRAP:** distinct() uses equals() and hashCode()
  ```java
  class Point {
      int x, y;
      Point(int x, int y) { this.x = x; this.y = y; }
      // No equals/hashCode override
  }
  
  List<Point> points = Arrays.asList(new Point(1,1), new Point(1,1));
  long count = points.stream().distinct().count(); // 2 (not 1!) - different objects
  ```

---

## 3. Terminal Operations (forEach, collect, reduce)

**Exam Objective:** Consume and collect stream data

### What are Terminal Operations?
Operations that produce a result or side-effect and close the stream. After a terminal operation, the stream can't be reused.

### When to Use
| Operation | When to Use | Return Type |
|-----------|-------------|-------------|
| **forEach** | Side-effects (print, save) | void |
| **collect** | Collect into collection | Collection |
| **reduce** | Combine elements to single value | Optional<T> |
| **count** | Count elements | long |
| **anyMatch** | Check if any element matches | boolean |
| **allMatch** | Check if all elements match | boolean |
| **noneMatch** | Check if no elements match | boolean |
| **findFirst** | Get first element | Optional<T> |
| **findAny** | Get any element | Optional<T> |
| **min/max** | Find min/max | Optional<T> |

### Why Use Terminal Operations
- Trigger stream processing
- Produce final result
- Collect data into collections
- Aggregate values

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class TerminalOpsExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. forEach - side effects
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print(n + " ")); // 2 4 6 8 10
        System.out.println();
        
        // 2. collect - to List
        List<Integer> evens = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        
        // 3. collect - to Set
        Set<Integer> evenSet = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toSet());
        
        // 4. collect - to Map
        Map<Integer, String> map = numbers.stream()
            .collect(Collectors.toMap(
                n -> n,
                n -> n % 2 == 0 ? "even" : "odd"
            ));
        
        // 5. reduce - sum
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        System.out.println("Sum: " + sum); // 55
        
        // 6. reduce - product
        int product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product); // 3628800
        
        // 7. reduce - max
        Optional<Integer> max = numbers.stream()
            .reduce((a, b) -> a > b ? a : b);
        max.ifPresent(System.out::println); // 10
        
        // 8. count
        long count = numbers.stream()
            .filter(n -> n > 5)
            .count();
        System.out.println("Count > 5: " + count); // 5
        
        // 9. anyMatch, allMatch, noneMatch
        boolean anyGreaterThan5 = numbers.stream().anyMatch(n -> n > 5);   // true
        boolean allGreaterThan0 = numbers.stream().allMatch(n -> n > 0);   // true
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);     // true
        
        // 10. findFirst and findAny
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        first.ifPresent(System.out::println); // 6
        
        Optional<Integer> any = numbers.stream()
            .filter(n -> n > 5)
            .findAny(); // May return different element in parallel
        
        // 11. min and max
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        Optional<Integer> maxVal = numbers.stream().max(Integer::compareTo);
        
        // 12. Practical example - statistics
        IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        
        System.out.println("Count: " + stats.getCount());
        System.out.println("Sum: " + stats.getSum());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
        System.out.println("Average: " + stats.getAverage());
    }
}
```
**Why:** Analytics dashboard - stream user activity logs, filter by date, aggregate metrics (count, sum, average), collect into report.

### Exam Traps
- ❌ **TRAP:** reduce() with no identity returns Optional
  ```java
  List<Integer> numbers = Arrays.asList(1, 2, 3);
  
  int sum1 = numbers.stream().reduce(0, (a, b) -> a + b); // int
  Optional<Integer> sum2 = numbers.stream().reduce((a, b) -> a + b); // Optional
  
  List<Integer> empty = Arrays.asList();
  int result = empty.stream().reduce(0, (a, b) -> a + b); // 0 (identity)
  Optional<Integer> result2 = empty.stream().reduce((a, b) -> a + b); // Optional.empty()
  ```
- ❌ **TRAP:** collect() with duplicate keys in toMap() throws exception
  ```java
  List<String> words = Arrays.asList("apple", "apricot", "banana");
  
  Map<Integer, String> map = words.stream()
      .collect(Collectors.toMap(
          String::length,  // Key
          w -> w           // Value
      )); // ❌ IllegalStateException! "apple" and "banana" both have length 6
  
  // Fix with merge function
  Map<Integer, String> fixed = words.stream()
      .collect(Collectors.toMap(
          String::length,
          w -> w,
          (existing, replacement) -> existing // Keep first
      ));
  ```
- ❌ **TRAP:** forEach() vs forEachOrdered() in parallel streams
  ```java
  List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
  
  numbers.parallelStream()
      .forEach(System.out::print); // May print: 31254 (random order)
  
  numbers.parallelStream()
      .forEachOrdered(System.out::print); // Always prints: 12345
  ```
- ❌ **TRAP:** findAny() may return different element each time in parallel
  ```java
  List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
  
  Optional<Integer> any = numbers.parallelStream().findAny();
  // May return 1, 2, 3, 4, or 5 - non-deterministic!
  ```

---

## 4. Collectors and Grouping

**Exam Objective:** Grouping and partitioning on streams

### What are Collectors?
Mutable reduction operations that accumulate elements into collections or aggregate results. Part of java.util.stream.Collectors.

### When to Use
| Collector | When to Use |
|-----------|-------------|
| **toList/toSet** | Collect into collection |
| **toMap** | Collect into map |
| **groupingBy** | Group elements by key |
| **partitioningBy** | Split into two groups (true/false) |
| **joining** | Concatenate strings |
| **counting** | Count elements |
| **summingInt/Double** | Sum values |
| **averagingInt/Double** | Calculate average |

### Why Use Collectors
- Rich set of collectors
- Composable
- Efficient mutable reduction
- Support for downstream collectors

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

class Person {
    String name;
    int age;
    String city;
    
    Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCity() { return city; }
}

public class CollectorsExample {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25, "NYC"),
            new Person("Bob", 30, "LA"),
            new Person("Charlie", 25, "NYC"),
            new Person("David", 30, "SF"),
            new Person("Eve", 35, "NYC")
        );
        
        // 1. toList() - Java 16+ shorthand
        List<String> names = people.stream()
            .map(Person::getName)
            .toList(); // Java 16+, returns immutable list
        
        // 2. groupingBy - group by age
        Map<Integer, List<Person>> byAge = people.stream()
            .collect(Collectors.groupingBy(Person::getAge));
        System.out.println("By age: " + byAge);
        // {25=[Alice, Charlie], 30=[Bob, David], 35=[Eve]}
        
        // 3. groupingBy with downstream collector - count per age
        Map<Integer, Long> countByAge = people.stream()
            .collect(Collectors.groupingBy(
                Person::getAge,
                Collectors.counting()
            ));
        System.out.println("Count by age: " + countByAge);
        // {25=2, 30=2, 35=1}
        
        // 4. groupingBy - get names per city
        Map<String, List<String>> namesByCity = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.mapping(Person::getName, Collectors.toList())
            ));
        System.out.println("Names by city: " + namesByCity);
        // {NYC=[Alice, Charlie, Eve], LA=[Bob], SF=[David]}
        
        // 5. partitioningBy - split by age > 30
        Map<Boolean, List<Person>> partitioned = people.stream()
            .collect(Collectors.partitioningBy(p -> p.getAge() > 30));
        System.out.println("Over 30: " + partitioned.get(true).size());
        System.out.println("30 or under: " + partitioned.get(false).size());
        
        // 6. joining - concatenate names
        String allNames = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining(", "));
        System.out.println("All names: " + allNames);
        // Alice, Bob, Charlie, David, Eve
        
        // 7. joining with prefix/suffix
        String formatted = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(formatted); // [Alice, Bob, Charlie, David, Eve]
        
        // 8. summingInt - total age
        int totalAge = people.stream()
            .collect(Collectors.summingInt(Person::getAge));
        System.out.println("Total age: " + totalAge); // 145
        
        // 9. averagingInt - average age
        double avgAge = people.stream()
            .collect(Collectors.averagingInt(Person::getAge));
        System.out.println("Average age: " + avgAge); // 29.0
        
        // 10. maxBy/minBy
        Optional<Person> oldest = people.stream()
            .collect(Collectors.maxBy(Comparator.comparing(Person::getAge)));
        oldest.ifPresent(p -> System.out.println("Oldest: " + p.getName()));
        
        // 11. Multiple grouping - by city then age
        Map<String, Map<Integer, List<Person>>> complex = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.groupingBy(Person::getAge)
            ));
        
        // 12. toMap
        Map<String, Integer> nameToAge = people.stream()
            .collect(Collectors.toMap(
                Person::getName,
                Person::getAge
            ));
    }
}
```
**Why:** Sales report - group transactions by product category, sum amounts, calculate averages, find top performers.

### Exam Traps
- ❌ **TRAP:** groupingBy() returns Map with List by default
  ```java
  Map<Integer, List<Person>> byAge = people.stream()
      .collect(Collectors.groupingBy(Person::getAge)); // List<Person> values
  
  Map<Integer, Set<Person>> byAgeSet = people.stream()
      .collect(Collectors.groupingBy(
          Person::getAge,
          Collectors.toSet() // Downstream collector for Set
      ));
  ```
- ❌ **TRAP:** partitioningBy() always returns 2 keys (true and false)
  ```java
  List<Integer> numbers = Arrays.asList(1, 3, 5); // No evens
  
  Map<Boolean, List<Integer>> partitioned = numbers.stream()
      .collect(Collectors.partitioningBy(n -> n % 2 == 0));
  
  System.out.println(partitioned);
  // {false=[1, 3, 5], true=[]} - true key exists with empty list!
  ```
- ❌ **TRAP:** toList() in Java 16+ returns immutable list
  ```java
  List<String> list = Stream.of("A", "B").toList(); // Java 16+
  list.add("C"); // ❌ UnsupportedOperationException! Immutable
  
  List<String> mutable = Stream.of("A", "B")
      .collect(Collectors.toList()); // Mutable
  mutable.add("C"); // ✅ Works
  ```
- ❌ **TRAP:** joining() only works with strings
  ```java
  List<Integer> numbers = Arrays.asList(1, 2, 3);
  
  String joined = numbers.stream()
      .collect(Collectors.joining(", ")); // ❌ Compilation error!
  
  String fixed = numbers.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(", ")); // ✅ "1, 2, 3"
  ```

---

## 5. Primitive Streams (IntStream, LongStream, DoubleStream)

**Exam Objective:** Use primitive Streams

### What are Primitive Streams?
Specialized stream implementations for int, long, and double primitives. Avoid boxing/unboxing overhead and provide specialized methods.

### When to Use
- Working with numeric primitives
- Need performance (avoid boxing)
- Using specialized methods (sum, average, range)
- Large datasets of numbers

### Why Use Primitive Streams
- Performance (no boxing/unboxing)
- Specialized operations (sum, average, min, max)
- Range generation (range, rangeClosed)
- Memory efficiency

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class PrimitiveStreamsExample {
    public static void main(String[] args) {
        // 1. IntStream - creating
        IntStream intStream1 = IntStream.range(1, 5);        // 1, 2, 3, 4
        IntStream intStream2 = IntStream.rangeClosed(1, 5);  // 1, 2, 3, 4, 5
        IntStream intStream3 = IntStream.of(10, 20, 30);
        
        // 2. Specialized operations
        int sum = IntStream.range(1, 101).sum();
        System.out.println("Sum 1-100: " + sum); // 5050
        
        OptionalDouble avg = IntStream.range(1, 11).average();
        avg.ifPresent(System.out::println); // 5.5
        
        OptionalInt max = IntStream.of(5, 2, 8, 1).max();
        max.ifPresent(System.out::println); // 8
        
        // 3. Summary statistics
        IntSummaryStatistics stats = IntStream.range(1, 101)
            .summaryStatistics();
        
        System.out.println("Count: " + stats.getCount());     // 100
        System.out.println("Sum: " + stats.getSum());         // 5050
        System.out.println("Min: " + stats.getMin());         // 1
        System.out.println("Max: " + stats.getMax());         // 100
        System.out.println("Average: " + stats.getAverage()); // 50.5
        
        // 4. Boxing and unboxing
        IntStream intStream = IntStream.of(1, 2, 3);
        Stream<Integer> boxed = intStream.boxed(); // IntStream → Stream<Integer>
        
        Stream<Integer> stream = Stream.of(1, 2, 3);
        IntStream unboxed = stream.mapToInt(Integer::intValue); // Stream<Integer> → IntStream
        
        // 5. mapToInt, mapToLong, mapToDouble
        List<String> strings = Arrays.asList("1", "2", "3");
        
        int sumFromStrings = strings.stream()
            .mapToInt(Integer::parseInt)
            .sum();
        System.out.println("Sum: " + sumFromStrings); // 6
        
        // 6. flatMapToInt
        List<int[]> arrays = Arrays.asList(
            new int[]{1, 2},
            new int[]{3, 4, 5}
        );
        
        int flatSum = arrays.stream()
            .flatMapToInt(Arrays::stream)
            .sum();
        System.out.println("Flat sum: " + flatSum); // 15
        
        // 7. LongStream
        long factorial = LongStream.rangeClosed(1, 20)
            .reduce(1, (a, b) -> a * b);
        System.out.println("20! = " + factorial);
        
        // 8. DoubleStream
        DoubleStream doubles = DoubleStream.of(1.5, 2.5, 3.5);
        double avg2 = doubles.average().orElse(0.0);
        System.out.println("Average: " + avg2); // 2.5
        
        // 9. Practical example - calculate grades
        int[] scores = {85, 92, 78, 90, 88};
        
        double average = Arrays.stream(scores).average().orElse(0.0);
        int highest = Arrays.stream(scores).max().orElse(0);
        int lowest = Arrays.stream(scores).min().orElse(0);
        long passing = Arrays.stream(scores).filter(s -> s >= 80).count();
        
        System.out.println("Average: " + average);
        System.out.println("Highest: " + highest);
        System.out.println("Lowest: " + lowest);
        System.out.println("Passing (≥80): " + passing);
        
        // 10. Generate random numbers
        DoubleStream.generate(Math::random)
            .limit(5)
            .forEach(System.out::println);
    }
}
```
**Why:** Financial calculations - sum account balances, calculate averages, find min/max transactions without boxing overhead.

### Exam Traps
- ❌ **TRAP:** range() vs rangeClosed() - one is exclusive, one inclusive
  ```java
  IntStream.range(1, 5).forEach(System.out::print);       // 1234
  IntStream.rangeClosed(1, 5).forEach(System.out::print); // 12345
  ```
- ❌ **TRAP:** sum() returns primitive, not Optional
  ```java
  int sum = IntStream.of(1, 2, 3).sum(); // Returns int (0 if empty)
  OptionalInt max = IntStream.of(1, 2, 3).max(); // Returns OptionalInt
  ```
- ❌ **TRAP:** average() returns OptionalDouble (not double)
  ```java
  IntStream stream = IntStream.of(1, 2, 3);
  double avg = stream.average(); // ❌ Compilation error!
  
  OptionalDouble avg2 = stream.average(); // ✅ Correct
  double result = avg2.orElse(0.0);
  ```
- ❌ **TRAP:** Can't use mapToInt() on IntStream (already primitive)
  ```java
  IntStream intStream = IntStream.of(1, 2, 3);
  
  intStream.mapToInt(i -> i * 2); // ❌ Compilation error! Already IntStream
  intStream.map(i -> i * 2);      // ✅ Correct
  ```
- ❌ **TRAP:** boxed() required to convert to Stream<Integer>
  ```java
  IntStream intStream = IntStream.of(1, 2, 3);
  
  Stream<Integer> wrong = intStream.map(i -> Integer.valueOf(i)); // ❌ Still IntStream!
  Stream<Integer> correct = intStream.boxed(); // ✅ Stream<Integer>
  ```

---

## 6. Stream Operations Order and Lazy Evaluation

**Exam Objective:** Understand stream processing

### What is Lazy Evaluation?
Intermediate operations are not executed until a terminal operation is invoked. This allows optimization of the processing pipeline.

### When to Use
Understanding lazy evaluation helps:
- Optimize stream pipelines
- Understand when operations execute
- Debug stream behavior
- Avoid unnecessary computation

### Why Lazy Evaluation Matters
- Performance optimization
- Short-circuit operations
- Infinite streams possible
- Order of operations matters

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class LazyEvaluationExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "apricot", "blueberry");
        
        // 1. Demonstrating lazy evaluation
        System.out.println("=== Lazy Evaluation ===");
        Stream<String> stream = words.stream()
            .filter(w -> {
                System.out.println("Filter: " + w);
                return w.startsWith("a");
            })
            .map(w -> {
                System.out.println("Map: " + w);
                return w.toUpperCase();
            });
        
        System.out.println("Stream created, nothing printed yet!");
        System.out.println("Now calling terminal operation:");
        
        List<String> result = stream.collect(Collectors.toList());
        // NOW filter and map execute
        
        // 2. Short-circuit operations
        System.out.println("\n=== Short-Circuit ===");
        String found = words.stream()
            .filter(w -> {
                System.out.println("Checking: " + w);
                return w.startsWith("b");
            })
            .findFirst()
            .orElse("");
        // Only checks until first match found!
        // Output: Checking: apple, Checking: banana
        
        // 3. Order matters for performance
        System.out.println("\n=== Order Matters ===");
        
        // Inefficient - filter after map
        long count1 = words.stream()
            .map(w -> {
                System.out.println("Map: " + w);
                return w.toUpperCase();
            })
            .filter(w -> w.startsWith("A"))
            .count();
        // Maps all 4 elements
        
        // Efficient - filter before map
        long count2 = words.stream()
            .filter(w -> {
                System.out.println("Filter: " + w);
                return w.startsWith("a");
            })
            .map(w -> {
                System.out.println("Map: " + w);
                return w.toUpperCase();
            })
            .count();
        // Only maps 2 elements (apple, apricot)
        
        // 4. limit() with infinite stream
        System.out.println("\n=== Infinite Stream ===");
        List<Integer> first10Even = Stream.iterate(0, n -> n + 2)
            .limit(10)
            .collect(Collectors.toList());
        System.out.println(first10Even);
        // [0, 2, 4, 6, 8, 10, 12, 14, 16, 18]
        
        // 5. Practical optimization example
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Find first number > 5 that's even
        Optional<Integer> result1 = numbers.stream()
            .filter(n -> {
                System.out.println("Filter >5: " + n);
                return n > 5;
            })
            .filter(n -> {
                System.out.println("Filter even: " + n);
                return n % 2 == 0;
            })
            .findFirst();
        // Stops at 6!
        
        // 6. peek() for debugging
        List<String> debugResult = words.stream()
            .peek(w -> System.out.println("Original: " + w))
            .filter(w -> w.length() > 5)
            .peek(w -> System.out.println("After filter: " + w))
            .map(String::toUpperCase)
            .peek(w -> System.out.println("After map: " + w))
            .collect(Collectors.toList());
    }
}
```
**Why:** API pagination - fetch only needed pages by using limit(), avoiding loading all data.

### Exam Traps
- ❌ **TRAP:** Intermediate operations don't execute without terminal operation
  ```java
  Stream<String> stream = Stream.of("a", "b", "c")
      .filter(s -> {
          System.out.println("Filter: " + s);
          return true;
      });
  // Nothing printed! No terminal operation called
  ```
- ❌ **TRAP:** Order matters for performance
  ```java
  // Inefficient
  list.stream()
      .map(expensiveOperation)  // Executes on all elements
      .filter(n -> n > 10)      // Then filters
      .findFirst();
  
  // Efficient
  list.stream()
      .filter(n -> n > 10)         // Filters first
      .map(expensiveOperation)     // Only maps remaining
      .findFirst();                // Short-circuits early
  ```
- ❌ **TRAP:** sorted() is expensive - use limit() after if possible
  ```java
  // Sorts all elements
  stream.sorted().limit(10); // Sorts everything, then takes 10
  
  // Better for finding top N
  stream.sorted().limit(10); // Still needs to sort all to find top 10
  ```
- ❌ **TRAP:** distinct() requires processing all elements
  ```java
  Stream.of(1, 2, 2, 3, 3, 3)
      .distinct()
      .limit(2); // Can't stop early - must check all for duplicates
  ```

---

## 7. Parallel Streams

**Exam Objective:** Sequential and parallel streams

### What are Parallel Streams?
Streams that divide operations into multiple tasks running concurrently on multiple CPU cores using ForkJoinPool.

### When to Use
- Large datasets (thousands+ elements)
- CPU-intensive operations
- Independent operations (no shared state)
- Operations take significant time

### Why Use Parallel Streams
- Leverage multi-core processors
- Improve performance for large datasets
- Simple API (parallel() or parallelStream())
- Automatic work distribution

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;
import java.time.*;

public class ParallelStreamsExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 1_000_000; i++) {
            numbers.add(i);
        }
        
        // 1. Sequential vs Parallel comparison
        Instant start1 = Instant.now();
        long sum1 = numbers.stream()
            .mapToLong(Integer::longValue)
            .sum();
        Duration duration1 = Duration.between(start1, Instant.now());
        System.out.println("Sequential: " + duration1.toMillis() + "ms");
        
        Instant start2 = Instant.now();
        long sum2 = numbers.parallelStream()
            .mapToLong(Integer::longValue)
            .sum();
        Duration duration2 = Duration.between(start2, Instant.now());
        System.out.println("Parallel: " + duration2.toMillis() + "ms");
        
        // 2. Creating parallel streams
        Stream<Integer> parallel1 = numbers.parallelStream();
        Stream<Integer> parallel2 = numbers.stream().parallel();
        
        // 3. Checking if parallel
        boolean isParallel = numbers.parallelStream().isParallel();
        System.out.println("Is parallel: " + isParallel); // true
        
        // 4. Converting between sequential and parallel
        Stream<Integer> seq = numbers.stream();
        Stream<Integer> par = seq.parallel();     // Sequential → Parallel
        Stream<Integer> seqAgain = par.sequential(); // Parallel → Sequential
        
        // 5. Order considerations
        System.out.println("\n=== Order in Parallel ===");
        
        // forEach - no order guaranteed
        System.out.print("forEach: ");
        Stream.of(1, 2, 3, 4, 5)
            .parallel()
            .forEach(n -> System.out.print(n + " ")); // Random order
        System.out.println();
        
        // forEachOrdered - maintains order (slower)
        System.out.print("forEachOrdered: ");
        Stream.of(1, 2, 3, 4, 5)
            .parallel()
            .forEachOrdered(n -> System.out.print(n + " ")); // 1 2 3 4 5
        System.out.println();
        
        // 6. When NOT to use parallel streams
        System.out.println("\n=== Parallel Stream Pitfalls ===");
        
        // DON'T: Small datasets
        List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);
        small.parallelStream().forEach(System.out::println); // Overhead > benefit
        
        // DON'T: Shared mutable state
        List<Integer> unsafeList = new ArrayList<>(); // Not thread-safe!
        numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .forEach(unsafeList::add); // ❌ Race condition!
        
        // DO: Use collect() for thread-safe accumulation
        List<Integer> safeList = numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList()); // ✅ Thread-safe
        
        // 7. Practical example - CPU-intensive operation
        List<String> passwords = Arrays.asList("pass123", "admin", "secure456");
        
        // Simulate expensive hash computation
        List<String> hashed = passwords.parallelStream()
            .map(ParallelStreamsExample::expensiveHash)
            .collect(Collectors.toList());
        
        // 8. reduce() with parallel streams
        int sum = numbers.parallelStream()
            .reduce(0, Integer::sum); // Associative operation - safe
        
        // 9. ForkJoinPool size
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + processors);
        
        // 10. Performance considerations
        System.out.println("\n=== Performance Test ===");
        
        // Test with CPU-intensive operation
        Instant startSeq = Instant.now();
        numbers.stream()
            .filter(n -> isPrime(n))
            .count();
        long seqTime = Duration.between(startSeq, Instant.now()).toMillis();
        
        Instant startPar = Instant.now();
        numbers.parallelStream()
            .filter(n -> isPrime(n))
            .count();
        long parTime = Duration.between(startPar, Instant.now()).toMillis();
        
        System.out.println("Sequential: " + seqTime + "ms");
        System.out.println("Parallel: " + parTime + "ms");
        System.out.println("Speedup: " + (double)seqTime/parTime + "x");
    }
    
    static String expensiveHash(String password) {
        // Simulate expensive operation
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        return Integer.toHexString(password.hashCode());
    }
    
    static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
```
**Why:** Image processing - apply filters to millions of pixels in parallel for faster rendering.

### Exam Traps
- ❌ **TRAP:** Parallel streams don't guarantee order
  ```java
  Stream.of(1, 2, 3, 4, 5)
      .parallel()
      .forEach(System.out::print); // May print: 31524
  
  Stream.of(1, 2, 3, 4, 5)
      .parallel()
      .forEachOrdered(System.out::print); // Always: 12345
  ```
- ❌ **TRAP:** Parallel overhead for small datasets
  ```java
  List<Integer> small = Arrays.asList(1, 2, 3);
  
  small.parallelStream().sum(); // Slower than sequential! Overhead > benefit
  ```
- ❌ **TRAP:** Shared mutable state causes race conditions
  ```java
  List<Integer> list = new ArrayList<>(); // NOT thread-safe
  
  Stream.of(1, 2, 3)
      .parallel()
      .forEach(list::add); // ❌ Race condition! May lose elements
  
  List<Integer> safe = Stream.of(1, 2, 3)
      .parallel()
      .collect(Collectors.toList()); // ✅ Thread-safe
  ```
- ❌ **TRAP:** reduce() requires associative operation
  ```java
  // Safe - associative
  int sum = stream.parallel().reduce(0, Integer::sum);
  
  // Unsafe - not associative (order matters)
  String concat = stream.parallel().reduce("", (a, b) -> a + "-" + b);
  // May produce different results in parallel!
  ```

---

## 8. Optional and Stream Integration

**Exam Objective:** Use streams with Optional

### What is Optional in Streams?
Many terminal operations return Optional to handle potentially absent values. Integration between Optional and Stream simplifies null handling.

### When to Use
- Terminal operations may not have results (findFirst, findAny, min, max, reduce)
- Avoiding NullPointerException
- Functional-style null handling
- Chaining operations safely

### Why Use Optional with Streams
- Type-safe null handling
- Avoid explicit null checks
- Composable operations
- Clear API contracts

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class OptionalStreamExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "apricot", "blueberry");
        List<String> empty = Arrays.asList();
        
        // 1. findFirst() returns Optional
        Optional<String> first = words.stream()
            .filter(w -> w.startsWith("a"))
            .findFirst();
        
        first.ifPresent(System.out::println); // apple
        
        String result = first.orElse("default"); // apple
        String result2 = first.orElseGet(() -> "computed"); // apple
        
        // 2. findAny() returns Optional
        Optional<String> any = words.stream()
            .filter(w -> w.length() > 6)
            .findAny();
        
        // 3. min/max return Optional
        Optional<String> shortest = words.stream()
            .min(Comparator.comparing(String::length));
        shortest.ifPresent(s -> System.out.println("Shortest: " + s));
        
        Optional<String> longest = words.stream()
            .max(Comparator.comparing(String::length));
        
        // 4. reduce() without identity returns Optional
        Optional<String> reduced = words.stream()
            .reduce((a, b) -> a + ", " + b);
        reduced.ifPresent(System.out::println); // apple, banana, apricot, blueberry
        
        // 5. Handling empty Optional
        Optional<String> notFound = empty.stream()
            .findFirst();
        
        String value1 = notFound.orElse("Not found");
        String value2 = notFound.orElseGet(() -> "Computed default");
        
        try {
            String value3 = notFound.orElseThrow(); // Throws NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("Optional was empty");
        }
        
        // 6. Optional.of() and Optional.ofNullable()
        Optional<String> opt1 = Optional.of("value"); // NPE if null
        Optional<String> opt2 = Optional.ofNullable(null); // Empty if null
        
        // 7. flatMap() with Optional
        Optional<String> upperFirst = words.stream()
            .findFirst()
            .map(String::toUpperCase);
        System.out.println(upperFirst.orElse("")); // APPLE
        
        // 8. filter() on Optional
        Optional<String> filtered = words.stream()
            .findFirst()
            .filter(s -> s.length() > 5);
        System.out.println(filtered.orElse("Too short")); // apple (5 chars - fails filter)
        
        // 9. Stream from Optional (Java 9+)
        words.stream()
            .findFirst()
            .stream() // Optional → Stream
            .forEach(System.out::println);
        
        // 10. Practical example - safe navigation
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30)
        );
        
        // Find oldest person's name
        String oldestName = people.stream()
            .max(Comparator.comparing(Person::getAge))
            .map(Person::getName)
            .orElse("No people");
        System.out.println("Oldest: " + oldestName);
        
        // 11. Combining Optional operations
        Optional<Integer> result11 = words.stream()
            .filter(w -> w.startsWith("z"))
            .findFirst()
            .map(String::length);
        
        if (result11.isPresent()) {
            System.out.println("Length: " + result11.get());
        } else {
            System.out.println("No match found");
        }
        
        // 12. Optional.ifPresentOrElse() (Java 9+)
        words.stream()
            .findFirst()
            .ifPresentOrElse(
                s -> System.out.println("Found: " + s),
                () -> System.out.println("Not found")
            );
    }
    
    static class Person {
        String name;
        int age;
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        String getName() { return name; }
        int getAge() { return age; }
    }
}
```
**Why:** User search - safely handle case when no user matches query without NullPointerException.

### Exam Traps
- ❌ **TRAP:** get() throws exception if Optional is empty
  ```java
  Optional<String> empty = Optional.empty();
  String value = empty.get(); // ❌ NoSuchElementException!
  
  String safe = empty.orElse("default"); // ✅ Returns "default"
  ```
- ❌ **TRAP:** orElse() vs orElseGet() - execution timing
  ```java
  Optional<String> opt = Optional.of("value");
  
  String s1 = opt.orElse(expensiveComputation()); // ALWAYS calls method
  String s2 = opt.orElseGet(() -> expensiveComputation()); // Only if empty
  ```
- ❌ **TRAP:** Optional.of() vs Optional.ofNullable()
  ```java
  String nullValue = null;
  
  Optional<String> opt1 = Optional.of(nullValue); // ❌ NullPointerException!
  Optional<String> opt2 = Optional.ofNullable(nullValue); // ✅ Optional.empty()
  ```
- ❌ **TRAP:** map() vs flatMap() on Optional
  ```java
  Optional<String> opt = Optional.of("hello");
  
  Optional<Optional<String>> wrong = opt.map(s -> Optional.of(s.toUpperCase()));
  // Returns Optional<Optional<String>> - nested!
  
  Optional<String> correct = opt.flatMap(s -> Optional.of(s.toUpperCase()));
  // Returns Optional<String> - flattened
  ```

---

## 9. Advanced Stream Operations (takeWhile, dropWhile, iterate)

**Exam Objective:** Use streams with lambda expressions

### What are Advanced Operations?
Java 9+ additions: takeWhile, dropWhile, and enhanced iterate providing more control over stream processing.

### When to Use
| Operation | When to Use |
|-----------|-------------|
| **takeWhile** | Take elements while condition is true (ordered) |
| **dropWhile** | Skip elements while condition is true (ordered) |
| **iterate (2-arg)** | Infinite streams with termination condition |
| **ofNullable** | Create stream from potentially null value |

### Why Use Advanced Operations
- More expressive API
- Finite infinite streams (iterate with predicate)
- Conditional processing
- Better control flow

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

public class AdvancedStreamsExample {
    public static void main(String[] args) {
        // 1. takeWhile - takes elements until condition fails
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 3, 2, 1);
        
        List<Integer> taken = numbers.stream()
            .takeWhile(n -> n < 4)
            .collect(Collectors.toList());
        System.out.println("takeWhile: " + taken); // [1, 2, 3]
        // Stops at first 4, doesn't process remaining
        
        // 2. dropWhile - drops elements until condition fails
        List<Integer> dropped = numbers.stream()
            .dropWhile(n -> n < 4)
            .collect(Collectors.toList());
        System.out.println("dropWhile: " + dropped); // [4, 5, 3, 2, 1]
        // Drops 1,2,3 then keeps rest
        
        // 3. iterate with predicate (Java 9+) - finite infinite stream
        List<Integer> powersOf2 = Stream.iterate(1, n -> n < 100, n -> n * 2)
            .collect(Collectors.toList());
        System.out.println("Powers of 2: " + powersOf2); // [1, 2, 4, 8, 16, 32, 64]
        
        // Compare with Java 8 approach
        List<Integer> powersOf2Old = Stream.iterate(1, n -> n * 2)
            .takeWhile(n -> n < 100)
            .collect(Collectors.toList());
        
        // 4. ofNullable (Java 9+) - handle potentially null values
        String value = null;
        
        long count = Stream.ofNullable(value).count();
        System.out.println("Count: " + count); // 0
        
        Stream<String> stream = Stream.ofNullable("hello");
        stream.forEach(System.out::println); // hello
        
        // 5. Practical example - processing log lines
        List<String> logs = Arrays.asList(
            "INFO Starting application",
            "INFO Loading configuration",
            "WARN Connection slow",
            "ERROR Connection failed",
            "INFO Retrying",
            "ERROR Timeout"
        );
        
        // Take logs until first ERROR
        List<String> beforeError = logs.stream()
            .takeWhile(log -> !log.startsWith("ERROR"))
            .collect(Collectors.toList());
        System.out.println("Before error: " + beforeError);
        
        // Drop logs until first ERROR
        List<String> fromError = logs.stream()
            .dropWhile(log -> !log.startsWith("ERROR"))
            .collect(Collectors.toList());
        System.out.println("From error: " + fromError);
        
        // 6. Generating sequences
        List<Integer> fibonacci = Stream.iterate(
            new int[]{0, 1},
            f -> f[0] < 100,
            f -> new int[]{f[1], f[0] + f[1]}
        )
        .map(f -> f[0])
        .collect(Collectors.toList());
        System.out.println("Fibonacci: " + fibonacci);
        
        // 7. takeWhile vs filter difference
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 3, 2, 1);
        
        List<Integer> takeResult = nums.stream()
            .takeWhile(n -> n < 4)
            .collect(Collectors.toList());
        System.out.println("takeWhile < 4: " + takeResult); // [1, 2, 3]
        
        List<Integer> filterResult = nums.stream()
            .filter(n -> n < 4)
            .collect(Collectors.toList());
        System.out.println("filter < 4: " + filterResult); // [1, 2, 3, 3, 2, 1]
        
        // 8. Pagination example
        List<String> items = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
        int pageSize = 3;
        
        // Page 1
        List<String> page1 = items.stream()
            .limit(pageSize)
            .collect(Collectors.toList());
        
        // Page 2
        List<String> page2 = items.stream()
            .skip(pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
        
        System.out.println("Page 1: " + page1); // [A, B, C]
        System.out.println("Page 2: " + page2); // [D, E, F]
        
        // 9. Conditional stream creation
        String nullableValue = Math.random() > 0.5 ? "value" : null;
        
        List<String> result = Stream.ofNullable(nullableValue)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Result: " + result);
        
        // 10. Processing until sentinel value
        List<String> data = Arrays.asList("data1", "data2", "STOP", "data3", "data4");
        
        List<String> beforeStop = data.stream()
            .takeWhile(s -> !s.equals("STOP"))
            .collect(Collectors.toList());
        System.out.println("Before STOP: " + beforeStop);
    }
}
```
**Why:** Log file analysis - process entries until first error, skip warnings, take only relevant entries.

### Exam Traps
- ❌ **TRAP:** takeWhile() stops at first false, filter() checks all
  ```java
  List<Integer> nums = Arrays.asList(1, 2, 3, 4, 3, 2, 1);
  
  List<Integer> taken = nums.stream()
      .takeWhile(n -> n < 4)
      .collect(Collectors.toList()); // [1, 2, 3] - stops at 4
  
  List<Integer> filtered = nums.stream()
      .filter(n -> n < 4)
      .collect(Collectors.toList()); // [1, 2, 3, 3, 2, 1] - checks all
  ```
- ❌ **TRAP:** takeWhile/dropWhile order-dependent
  ```java
  Set<Integer> unordered = new HashSet<>(Arrays.asList(3, 1, 4, 2));
  
  List<Integer> result = unordered.stream()
      .takeWhile(n -> n < 4)
      .collect(Collectors.toList());
  // Unpredictable! takeWhile requires ordered stream
  ```
- ❌ **TRAP:** iterate with predicate (3-arg) is Java 9+
  ```java
  // Java 9+
  Stream.iterate(1, n -> n < 10, n -> n + 1); // ✅
  
  // Java 8
  Stream.iterate(1, n -> n + 1)
      .limit(10); // Must use limit
  ```
- ❌ **TRAP:** ofNullable() vs of() with null
  ```java
  Stream<String> s1 = Stream.of(null); // ✅ Stream with one null element
  Stream<String> s2 = Stream.ofNullable(null); // ✅ Empty stream
  ```

---

## 10. Practical Stream Patterns

**Exam Objective:** Supply, filter, map, consume, and sort data

### What are Stream Patterns?
Common real-world usage patterns combining multiple stream operations to solve practical problems.

### When to Use
- Complex data transformations
- Aggregation and reporting
- Data validation and filtering
- Collection manipulations

### Why Use Stream Patterns
- Reusable solutions
- Readable code
- Best practices
- Performance optimizations

### Real-World Example
```java
import java.util.*;
import java.util.stream.*;

class Employee {
    String name;
    String department;
    double salary;
    int age;
    
    Employee(String name, String department, double salary, int age) {
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.age = age;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public int getAge() { return age; }
    
    @Override
    public String toString() {
        return name + " (" + department + ", $" + salary + ")";
    }
}

public class StreamPatternsExample {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "IT", 80000, 28),
            new Employee("Bob", "HR", 60000, 35),
            new Employee("Charlie", "IT", 90000, 32),
            new Employee("David", "Finance", 75000, 29),
            new Employee("Eve", "IT", 85000, 27),
            new Employee("Frank", "HR", 65000, 31)
        );
        
        // Pattern 1: Filter → Map → Collect
        List<String> itEmployeeNames = employees.stream()
            .filter(e -> e.getDepartment().equals("IT"))
            .map(Employee::getName)
            .collect(Collectors.toList());
        System.out.println("IT Employees: " + itEmployeeNames);
        
        // Pattern 2: Group and aggregate
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));
        System.out.println("Average salaries: " + avgSalaryByDept);
        
        // Pattern 3: Find top N
        List<Employee> top3Earners = employees.stream()
            .sorted(Comparator.comparing(Employee::getSalary).reversed())
            .limit(3)
            .collect(Collectors.toList());
        System.out.println("Top 3 earners: " + top3Earners);
        
        // Pattern 4: Partition by condition
        Map<Boolean, List<Employee>> partitioned = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.getSalary() > 70000));
        System.out.println("High earners: " + partitioned.get(true).size());
        System.out.println("Lower earners: " + partitioned.get(false).size());
        
        // Pattern 5: Calculate statistics
        DoubleSummaryStatistics salaryStats = employees.stream()
            .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("Salary stats:");
        System.out.println("  Count: " + salaryStats.getCount());
        System.out.println("  Sum: " + salaryStats.getSum());
        System.out.println("  Average: " + salaryStats.getAverage());
        System.out.println("  Min: " + salaryStats.getMin());
        System.out.println("  Max: " + salaryStats.getMax());
        
        // Pattern 6: Distinct values
        List<String> departments = employees.stream()
            .map(Employee::getDepartment)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Departments: " + departments);
        
        // Pattern 7: Check conditions
        boolean anyHighEarner = employees.stream()
            .anyMatch(e -> e.getSalary() > 100000);
        boolean allAdults = employees.stream()
            .allMatch(e -> e.getAge() >= 18);
        boolean noneRetired = employees.stream()
            .noneMatch(e -> e.getAge() > 65);
        
        // Pattern 8: Reduce to single value
        double totalPayroll = employees.stream()
            .mapToDouble(Employee::getSalary)
            .sum();
        System.out.println("Total payroll: $" + totalPayroll);
        
        // Pattern 9: Custom collector - department report
        String report = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.mapping(
                    Employee::getName,
                    Collectors.joining(", ")
                )
            ))
            .entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining("\n"));
        System.out.println("Department Report:\n" + report);
        
        // Pattern 10: Flatten nested structures
        List<List<String>> nestedNames = Arrays.asList(
            Arrays.asList("Alice", "Bob"),
            Arrays.asList("Charlie", "David"),
            Arrays.asList("Eve", "Frank")
        );
        
        List<String> flatNames = nestedNames.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        System.out.println("Flat names: " + flatNames);
        
        // Pattern 11: Custom comparator
        List<Employee> sortedEmployees = employees.stream()
            .sorted(Comparator
                .comparing(Employee::getDepartment)
                .thenComparing(Comparator.comparing(Employee::getSalary).reversed())
            )
            .collect(Collectors.toList());
        
        // Pattern 12: Peek for debugging
        List<String> processed = employees.stream()
            .peek(e -> System.out.println("Processing: " + e.getName()))
            .filter(e -> e.getSalary() > 70000)
            .peek(e -> System.out.println("After filter: " + e.getName()))
            .map(Employee::getName)
            .collect(Collectors.toList());
    }
}
```
**Why:** Business reporting - analyze employee data, generate statistics, create summaries, find insights.

### Exam Traps
- ❌ **TRAP:** Stream reuse attempt
  ```java
  Stream<String> stream = Stream.of("A", "B", "C");
  stream.forEach(System.out::println);
  stream.count(); // ❌ IllegalStateException - stream already used
  ```
- ❌ **TRAP:** Stateful lambda expressions
  ```java
  int[] sum = {0}; // Mutable state
  
  Stream.of(1, 2, 3)
      .forEach(n -> sum[0] += n); // ❌ Works but BAD practice
  
  int correctSum = Stream.of(1, 2, 3)
      .mapToInt(Integer::intValue)
      .sum(); // ✅ Use built-in operations
  ```
- ❌ **TRAP:** Collecting to immutable collections
  ```java
  List<String> immutable = Stream.of("A", "B")
      .toList(); // Java 16+ - immutable!
  
  immutable.add("C"); // ❌ UnsupportedOperationException
  
  List<String> mutable = Stream.of("A", "B")
      .collect(Collectors.toList()); // Mutable
  ```
- ❌ **TRAP:** Sorted() is expensive
  ```java
  // Inefficient - sorts all then takes 5
  stream.sorted().limit(5);
  
  // Still needs full sort for top 5
  // Consider PriorityQueue for large datasets
  ```

---

## Exam Questions

**Exam Format:**
- 50 questions total (5-8 on streams)
- 120 minutes
- 68% passing score (34/50)
- Multiple choice, code analysis

---

### Question 1
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

long count = numbers.stream()
    .filter(n -> n > 2)
    .peek(System.out::println)
    .count();
```

What is printed?

A. Nothing  
B. 3  
C. 3, 4, 5  
D. 1, 2, 3, 4, 5  
E. Compilation error

**Answer:** A

**Explanation:** count() is a short-circuit operation that doesn't need to process elements individually. The peek() may not execute. Output depends on JVM optimization but typically nothing is printed. (Exam Objective: Use streams to supply, filter, map, consume data)

---

### Question 2
```java
Stream<String> stream = Stream.of("a", "b", "c");
stream.forEach(System.out::println);
stream.forEach(System.out::println);
```

What happens?

A. Prints a, b, c twice  
B. Prints a, b, c once  
C. IllegalStateException on second forEach  
D. Compilation error  
E. Prints nothing

**Answer:** C

**Explanation:** Streams can only be operated on once. After first forEach(), stream is closed. Second forEach() throws IllegalStateException: stream has already been operated upon. (Exam Objective: Use Java object streams)

---

### Question 3
```java
IntStream.range(1, 5).forEach(System.out::print);
System.out.println();
IntStream.rangeClosed(1, 5).forEach(System.out::print);
```

What is the output?

A. 1234  
   12345  
B. 12345  
   12345  
C. 1234  
   1234  
D. 12345  
   123456  
E. Compilation error

**Answer:** A

**Explanation:** range(1, 5) is exclusive of end value (1,2,3,4). rangeClosed(1, 5) is inclusive of end value (1,2,3,4,5). (Exam Objective: Use primitive Streams)

---

### Question 4
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);

int sum = nums.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .reduce(0, (a, b) -> a + b);

System.out.println(sum);
```

What is printed?

A. 6  
B. 12  
C. 30  
D. 15  
E. Compilation error

**Answer:** B

**Explanation:** Filters even numbers (2, 4), maps to (4, 8), reduces: 0 + 4 = 4, 4 + 8 = 12. (Exam Objective: Perform reduction on streams)

---

### Question 5
```java
List<String> words = Arrays.asList("apple", "banana", "apricot");

Map<Integer, String> map = words.stream()
    .collect(Collectors.toMap(
        String::length,
        w -> w
    ));
```

What happens?

A. Map created successfully  
B. IllegalStateException - duplicate key  
C. Compilation error  
D. NullPointerException  
E. Map is empty

**Answer:** B

**Explanation:** "apple" and "banana" both have length 6, creating duplicate keys. toMap() throws IllegalStateException when duplicate keys found without merge function. (Exam Objective: Perform grouping on streams)

---

### Question 6
```java
Stream<Integer> stream = Stream.of(1, 2, 3);

Optional<Integer> result = stream
    .filter(n -> n > 5)
    .findFirst();

int value = result.get();
```

What happens?

A. Prints 6  
B. Prints 0  
C. NoSuchElementException  
D. Compilation error  
E. Prints null

**Answer:** C

**Explanation:** No element matches filter (n > 5), so Optional is empty. Calling get() on empty Optional throws NoSuchElementException. Should use orElse() or orElseGet(). (Exam Objective: Use streams with Optional)

---

### Question 7
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 3, 2, 1);

List<Integer> result = nums.stream()
    .takeWhile(n -> n < 4)
    .collect(Collectors.toList());

System.out.println(result);
```

What is printed?

A. [1, 2, 3]  
B. [1, 2, 3, 3, 2, 1]  
C. [1, 2, 3, 4]  
D. [4, 5]  
E. Compilation error

**Answer:** A

**Explanation:** takeWhile() takes elements until condition fails. Takes 1, 2, 3 then stops at 4 (4 is not < 4). Remaining elements ignored. (Exam Objective: Use streams with lambda expressions)

---

### Question 8
```java
List<String> words = List.of("a", "bb", "ccc");

long count = words.stream()
    .flatMap(s -> Stream.of(s.length()))
    .count();

System.out.println(count);
```

What is printed?

A. 3  
B. 6  
C. 9  
D. 1  
E. Compilation error

**Answer:** A

**Explanation:** flatMap creates stream of lengths: 1, 2, 3. count() returns 3 elements. flatMap flattens but doesn't expand single values. (Exam Objective: Filter and map data)

---

### Question 9
```java
Stream<String> stream = Stream.of("A", "B", "C")
    .parallel();

stream.forEach(System.out::print);
```

What is a possible output?

A. ABC only  
B. CBA only  
C. ABC or any permutation  
D. Compilation error  
E. Nothing printed

**Answer:** C

**Explanation:** Parallel streams don't guarantee order with forEach(). May print ABC, BAC, CBA, etc. Use forEachOrdered() for ordered output. (Exam Objective: Sequential and parallel streams)

---

### Question 10
```java
IntStream stream = IntStream.of(1, 2, 3);

double avg = stream.average();
System.out.println(avg);
```

What happens?

A. Prints 2.0  
B. Prints 2  
C. Compilation error  
D. ClassCastException  
E. NoSuchElementException

**Answer:** C

**Explanation:** average() returns OptionalDouble, not double. Cannot assign OptionalDouble to double. Must use .orElse() or .getAsDouble(). (Exam Objective: Use primitive Streams)

---

### Question 11
```java
List<String> list = Stream.of("A", "B", "C")
    .toList();

list.add("D");
```

What happens?

A. List contains A, B, C, D  
B. UnsupportedOperationException  
C. Compilation error  
D. NullPointerException  
E. List unchanged

**Answer:** B

**Explanation:** toList() (Java 16+) returns immutable list. Attempting to modify throws UnsupportedOperationException. Use collect(Collectors.toList()) for mutable list. (Exam Objective: Collect stream data)

---

### Question 12
```java
List<Integer> nums = Arrays.asList(5, 2, 8, 1, 9);

Optional<Integer> result = nums.stream()
    .reduce((a, b) -> a > b ? a : b);

System.out.println(result.get());
```

What is printed?

A. 9  
B. 1  
C. 25  
D. NoSuchElementException  
E. Compilation error

**Answer:** A

**Explanation:** reduce() with no identity returns Optional. Lambda finds maximum: 5 vs 2 → 5, 5 vs 8 → 8, 8 vs 1 → 8, 8 vs 9 → 9. (Exam Objective: Perform reduction)

---

### Question 13
```java
List<Integer> nums = Arrays.asList(1, 2, 3);

nums.stream()
    .peek(n -> n * 2)
    .forEach(System.out::println);
```

What is printed?

A. 2, 4, 6  
B. 1, 2, 3  
C. Nothing  
D. Compilation error  
E. 1, 4, 9

**Answer:** B

**Explanation:** peek() performs action but doesn't modify stream. n * 2 result is discarded. Integers are immutable. Prints original values 1, 2, 3. (Exam Objective: Filter and map data)

---

### Question 14
```java
List<String> words = Arrays.asList("apple", "banana");

Map<String, Integer> map = words.stream()
    .collect(Collectors.groupingBy(
        w -> w.substring(0, 1),
        Collectors.summingInt(String::length)
    ));

System.out.println(map);
```

What is printed?

A. {a=5, b=6}  
B. {a=11}  
C. {a=2}  
D. {a=5, b=1}  
E. Compilation error

**Answer:** A

**Explanation:** Groups by first letter: "a" for apple (length 5), "b" for banana (length 6). summingInt sums lengths per group. (Exam Objective: Grouping and partitioning)

---

### Question 15
```java
Stream<Integer> infinite = Stream.iterate(1, n -> n * 2);

List<Integer> result = infinite
    .limit(5)
    .collect(Collectors.toList());

System.out.println(result);
```

What is printed?

A. [1, 2, 4, 8, 16]  
B. Program hangs  
C. OutOfMemoryError  
D. [1, 3, 9, 27, 81]  
E. Compilation error

**Answer:** A

**Explanation:** iterate() creates infinite stream: 1, 2, 4, 8, 16, 32... limit(5) makes it finite, takes first 5 elements. (Exam Objective: Use streams with lambda expressions)

---

### Question 16
```java
List<Integer> nums = Arrays.asList();

int sum = nums.stream()
    .reduce(0, (a, b) -> a + b);

System.out.println(sum);
```

What is printed?

A. 0  
B. NoSuchElementException  
C. null  
D. Compilation error  
E. Nothing

**Answer:** A

**Explanation:** Empty stream with identity value (0) returns identity. reduce(0, ...) always returns int, never Optional. (Exam Objective: Perform reduction)

---

### Question 17
```java
List<Integer> nums = Arrays.asList(1, 2, 3);

nums.parallelStream()
    .forEachOrdered(System.out::print);
```

What is printed?

A. 123  
B. 321  
C. Any permutation  
D. Compilation error  
E. Nothing

**Answer:** A

**Explanation:** forEachOrdered() maintains encounter order even in parallel streams. Always prints 123. (Exam Objective: Sequential and parallel streams)

---

### Question 18
```java
IntStream stream = IntStream.range(1, 5);

int sum = stream.sum();
OptionalInt max = stream.max();
```

What happens?

A. sum=10, max=4  
B. IllegalStateException  
C. Compilation error  
D. sum=10, max=empty  
E. NoSuchElementException

**Answer:** B

**Explanation:** Stream is consumed by sum(). Calling max() on already-consumed stream throws IllegalStateException. Must create new stream or store results. (Exam Objective: Use primitive Streams)

---

### Question 19
```java
Stream<String> stream = Stream.of("a", "b", "c");

String result = stream
    .filter(s -> s.length() > 5)
    .findFirst()
    .orElse("default");

System.out.println(result);
```

What is printed?

A. a  
B. abc  
C. default  
D. NoSuchElementException  
E. Compilation error

**Answer:** C

**Explanation:** No string has length > 5. filter() returns empty stream. findFirst() returns Optional.empty(). orElse() returns "default". (Exam Objective: Use streams with Optional)

---

### Question 20
```java
List<Integer> nums = Arrays.asList(3, 1, 4, 1, 5);

List<Integer> result = nums.stream()
    .distinct()
    .sorted()
    .limit(3)
    .collect(Collectors.toList());

System.out.println(result);
```

What is printed?

A. [3, 1, 4]  
B. [1, 3, 4]  
C. [1, 1, 3]  
D. [5, 4, 3]  
E. Compilation error

**Answer:** B

**Explanation:** distinct() removes duplicate 1: [3, 1, 4, 5]. sorted() orders: [1, 3, 4, 5]. limit(3) takes first 3: [1, 3, 4]. (Exam Objective: Sort data with streams)

---

## Chapter Summary

### Official 1Z0-830 Exam Objectives Checklist

✅ **Use Java object and primitive Streams, including lambda expressions**
- Stream creation (from collections, arrays, methods)
- Primitive streams (IntStream, LongStream, DoubleStream)
- Lambda expressions in stream operations
- Method references

✅ **Supply, filter, map, consume, and sort data**
- Intermediate operations (filter, map, flatMap, distinct, sorted)
- Terminal operations (forEach, collect, reduce)
- Sorting with Comparator
- Data transformation pipelines

✅ **Perform decomposition, concatenation and reduction**
- reduce() operations
- Collectors (toList, toSet, toMap, groupingBy)
- flatMap() for decomposition
- Stream concatenation

✅ **Grouping and partitioning on sequential and parallel streams**
- Collectors.groupingBy()
- Collectors.partitioningBy()
- Downstream collectors
- Parallel stream processing

---

### Key Points for Exam

1. **Stream Lifecycle**
   - Streams can only be used once
   - Intermediate operations are lazy
   - Terminal operations trigger execution
   - Streams are NOT data structures

2. **Primitive Streams**
   - IntStream, LongStream, DoubleStream
   - range() is exclusive, rangeClosed() is inclusive
   - sum() returns primitive, average() returns Optional
   - Boxing/unboxing with boxed() and mapToInt()

3. **Intermediate Operations**
   - filter() - selects elements
   - map() - transforms 1:1
   - flatMap() - transforms and flattens
   - distinct() - removes duplicates
   - sorted() - sorts elements
   - limit() and skip() - pagination

4. **Terminal Operations**
   - forEach() - side effects
   - collect() - accumulate results
   - reduce() - combine to single value
   - count(), min(), max() - aggregations
   - anyMatch(), allMatch(), noneMatch() - predicates
   - findFirst(), findAny() - search

5. **Collectors**
   - toList(), toSet(), toMap()
   - groupingBy() for grouping
   - partitioningBy() for boolean split
   - joining() for strings
   - counting(), summing(), averaging()

6. **Lazy Evaluation**
   - Intermediate ops don't execute until terminal op
   - Enables short-circuit optimization
   - Order matters for performance
   - Infinite streams possible with limit()

7. **Parallel Streams**
   - Use parallelStream() or parallel()
   - Order not guaranteed with forEach()
   - Use forEachOrdered() for order
   - Avoid shared mutable state
   - Overhead for small datasets

8. **Optional Integration**
   - findFirst(), findAny(), min(), max() return Optional
   - reduce() without identity returns Optional
   - Use orElse(), orElseGet(), orElseThrow()
   - get() throws exception if empty

9. **Advanced Operations (Java 9+)**
   - takeWhile() - takes until false
   - dropWhile() - drops until false
   - iterate() with predicate - finite infinite streams
   - ofNullable() - null-safe stream creation

10. **Common Patterns**
    - Filter → Map → Collect
    - Group and aggregate
    - Find top N with sorted().limit()
    - Partition by condition
    - Calculate statistics
    - Flatten nested structures

---

### Common Exam Patterns

1. **Stream Reuse Traps**
   - Can't use stream twice
   - IllegalStateException on reuse

2. **Primitive vs Object Streams**
   - range() vs rangeClosed()
   - sum() returns primitive
   - average() returns Optional

3. **map() vs flatMap()**
   - map() for 1:1 transformation
   - flatMap() for flattening

4. **Optional Handling**
   - get() throws if empty
   - Use orElse() or orElseGet()

5. **Parallel Stream Order**
   - forEach() unordered
   - forEachOrdered() ordered

6. **Collector Traps**
   - toMap() fails on duplicate keys
   - toList() (Java 16+) is immutable
   - groupingBy() returns Map<K, List<V>>

7. **Performance Order**
   - Filter before map
   - Limit after sorted (can't optimize)
   - Short-circuit with findFirst()

---

### Study Tips

1. **Practice Stream Creation**
   - From collections, arrays, primitives
   - Infinite streams with iterate() and generate()
   - Empty streams and single-element streams

2. **Master Operations**
   - Know all intermediate operations
   - Know all terminal operations
   - Understand lazy evaluation

3. **Collectors Focus**
   - toList(), toSet(), toMap()
   - groupingBy() with downstream
   - partitioningBy()
   - Custom collectors

4. **Primitive Streams**
   - When to use IntStream vs Stream<Integer>
   - Range methods
   - Specialized operations (sum, average)

5. **Optional Integration**
   - When operations return Optional
   - Safe extraction methods
   - Avoid get() in production

6. **Parallel Considerations**
   - When to use parallel streams
   - Order implications
   - Stateless vs stateful operations

7. **Code Reading**
   - Trace stream pipelines
   - Identify lazy vs eager evaluation
   - Spot reuse errors

8. **Common Mistakes**
   - Stream reuse
   - Optional.get() without check
   - Stateful lambdas
   - Incorrect collector usage

---

### Quick Reference Card

#### Stream Creation
```java
// From collection
list.stream()
list.parallelStream()

// From array
Arrays.stream(array)
Stream.of(elements...)

// Primitive
IntStream.range(1, 10)
IntStream.rangeClosed(1, 10)

// Infinite
Stream.iterate(0, n -> n + 1)
Stream.generate(Math::random)
```

#### Intermediate Operations
```java
.filter(predicate)
.map(function)
.flatMap(function)
.distinct()
.sorted()
.sorted(comparator)
.limit(n)
.skip(n)
.peek(consumer)
.takeWhile(predicate)  // Java 9+
.dropWhile(predicate)  // Java 9+
```

#### Terminal Operations
```java
.forEach(consumer)
.collect(collector)
.reduce(identity, accumulator)
.reduce(accumulator)
.count()
.anyMatch(predicate)
.allMatch(predicate)
.noneMatch(predicate)
.findFirst()
.findAny()
.min(comparator)
.max(comparator)
```

#### Collectors
```java
Collectors.toList()
Collectors.toSet()
Collectors.toMap(keyMapper, valueMapper)
Collectors.groupingBy(classifier)
Collectors.partitioningBy(predicate)
Collectors.joining(delimiter)
Collectors.counting()
Collectors.summingInt(mapper)
Collectors.averagingInt(mapper)
```

---

### Next Steps

**Practice Tasks:**
1. Create streams 5 different ways
2. Chain 5+ stream operations
3. Implement groupingBy() with downstream collector
4. Compare sequential vs parallel performance
5. Handle Optional from stream operations

**Review Before Exam:**
- Streams can only be used once
- range() exclusive, rangeClosed() inclusive
- toList() (Java 16+) is immutable
- toMap() fails on duplicate keys
- takeWhile() stops at first false

**Common Exam Traps:**
- Stream reuse → IllegalStateException
- Optional.get() → NoSuchElementException
- Parallel forEach() → unordered output
- peek() → doesn't modify stream
- reduce() without identity → returns Optional

---

[← Previous Chapter](CHAPTER-08.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-10.md)

---

**Chapter 9 Complete!** ✅  
Streams API - Essential for functional programming in Java.
