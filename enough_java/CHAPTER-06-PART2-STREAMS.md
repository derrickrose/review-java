# Chapter 6: Lambda Expressions and Stream API - PART 2

[← Part 1: Lambdas & Functional Interfaces](CHAPTER-06-PART1-LAMBDAS-FUNCTIONAL.md) | [Part 3: Advanced Streams & Collectors →](CHAPTER-06-PART3-COLLECTORS-OPTIONAL.md) | [Back to Main Guide](README.md)

---

# PART 2: Stream API Fundamentals

## 1. Introduction to Stream API

### What is Stream API?

The Stream API (introduced in Java 8) provides a declarative way to process collections of data. A stream represents a sequence of elements supporting sequential and parallel aggregate operations. Streams enable functional-style operations on collections, making code more concise and readable.

**Key Characteristics:**
- **Not a data structure** - streams don't store elements
- **Functional in nature** - operations produce a result but don't modify source
- **Lazy evaluation** - intermediate operations are not executed until terminal operation
- **Possibly unbounded** - collections have finite size, streams may not
- **Consumable** - elements can be visited only once (like Iterator)

### Stream vs Collection

| Aspect | Collection | Stream |
|--------|-----------|---------|
| **Purpose** | Store and access elements | Process elements |
| **Modification** | Can add/remove elements | Cannot modify source |
| **Iteration** | External (for loop) | Internal (Stream methods) |
| **Traversal** | Multiple times | Only once |
| **Eager/Lazy** | Eager (all at once) | Lazy (on demand) |
| **Memory** | Stores all elements | Processes elements on-the-fly |

### Why Use Streams?

**Benefits:**
- More readable code with declarative style
- Easy parallelization with `.parallel()`
- Lazy evaluation for performance
- Functional composition of operations
- Reduced boilerplate code
- Built-in optimization opportunities

```java
// Without Stream - imperative style
List<String> result = new ArrayList<>();
for (String name : names) {
    if (name.startsWith("A")) {
        String upper = name.toUpperCase();
        result.add(upper);
    }
}

// With Stream - declarative style
List<String> result = names.stream()
    .filter(name -> name.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

---

## 2. Creating Streams

### Stream Creation Methods

```java
import java.util.stream.*;
import java.util.*;

// 1. From Collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();
Stream<String> parallelStream1 = list.parallelStream();

// 2. From Array
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);
Stream<String> stream3 = Stream.of("a", "b", "c");  // varargs

// 3. From values
Stream<Integer> stream4 = Stream.of(1, 2, 3, 4, 5);
Stream<String> stream5 = Stream.of("single");
Stream<Object> emptyStream = Stream.empty();

// 4. Stream.builder()
Stream<String> stream6 = Stream.<String>builder()
    .add("a")
    .add("b")
    .add("c")
    .build();

// 5. Stream.iterate() - infinite stream
Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2);  // 0, 2, 4, 6...
Stream<Integer> limited = Stream.iterate(0, n -> n + 2)
    .limit(10);  // First 10 even numbers

// Java 9+: iterate with predicate (takes while condition is true)
Stream<Integer> upTo20 = Stream.iterate(0, n -> n < 20, n -> n + 2);

// 6. Stream.generate() - infinite stream with Supplier
Stream<Double> randomStream = Stream.generate(Math::random);
Stream<String> helloStream = Stream.generate(() -> "Hello")
    .limit(5);

// 7. From String
String str = "Hello";
IntStream charStream = str.chars();  // Stream of char codes
Stream<String> lineStream = str.lines();  // For multi-line strings

// 8. From range (primitive streams)
IntStream intStream = IntStream.range(1, 5);        // 1, 2, 3, 4 (exclusive)
IntStream intStream2 = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5 (inclusive)
LongStream longStream = LongStream.range(1L, 100L);

// 9. From file
Stream<String> lines = Files.lines(Paths.get("file.txt"));

// 10. From Pattern (regex)
Pattern pattern = Pattern.compile("\s+");
Stream<String> wordStream = pattern.splitAsStream("Hello World Java");
```

### Primitive Streams

Specialized streams for `int`, `long`, and `double` to avoid boxing overhead.

```java
// IntStream
IntStream intStream = IntStream.of(1, 2, 3, 4, 5);
IntStream range = IntStream.range(1, 10);            // 1 to 9
IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1 to 10

// Statistics methods (not available on Stream<Integer>)
IntSummaryStatistics stats = intStream.summaryStatistics();
System.out.println("Count: " + stats.getCount());
System.out.println("Sum: " + stats.getSum());
System.out.println("Min: " + stats.getMin());
System.out.println("Max: " + stats.getMax());
System.out.println("Average: " + stats.getAverage());

// LongStream
LongStream longStream = LongStream.rangeClosed(1L, 1000000L);

// DoubleStream
DoubleStream doubleStream = DoubleStream.of(1.1, 2.2, 3.3);

// Converting between types
IntStream ints = IntStream.range(1, 5);
Stream<Integer> boxed = ints.boxed();           // int -> Integer
DoubleStream doubles = ints.asDoubleStream();   // int -> double
LongStream longs = ints.asLongStream();         // int -> long

// From Stream<Integer> to IntStream
Stream<Integer> integers = Stream.of(1, 2, 3, 4, 5);
IntStream primitiveInts = integers.mapToInt(Integer::intValue);
```

---

## 3. Intermediate Operations

Intermediate operations return a Stream and are **lazy** - they're not executed until a terminal operation is invoked.

**Common Characteristic:** All return `Stream<T>` (chainable)

### 3.1 filter(Predicate) - Filtering

**Purpose:** Keep only elements that match the predicate

**Signature:** `Stream<T> filter(Predicate<? super T> predicate)`

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Filter even numbers
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
System.out.println(evens);  // [2, 4, 6, 8, 10]

// Multiple filters (can chain)
List<Integer> result = numbers.stream()
    .filter(n -> n > 3)
    .filter(n -> n < 8)
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
System.out.println(result);  // [4, 6]

// Filter with method reference
List<String> words = Arrays.asList("", "hello", "", "world", "");
List<String> nonEmpty = words.stream()
    .filter(s -> !s.isEmpty())  // or: .filter(Predicate.not(String::isEmpty))
    .collect(Collectors.toList());
System.out.println(nonEmpty);  // [hello, world]

// Complex filtering
List<String> names = Arrays.asList("Alice", "Bob", "Amanda", "Charlie", "Andrew");
List<String> filtered = names.stream()
    .filter(name -> name.startsWith("A"))
    .filter(name -> name.length() > 5)
    .collect(Collectors.toList());
System.out.println(filtered);  // [Amanda, Andrew]
```

### 3.2 map(Function) - Transformation

**Purpose:** Transform each element to another type or value

**Signature:** `<R> Stream<R> map(Function<? super T, ? extends R> mapper)`

```java
List<String> words = Arrays.asList("hello", "world", "java");

// Transform to uppercase
List<String> upper = words.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
System.out.println(upper);  // [HELLO, WORLD, JAVA]

// Transform to lengths
List<Integer> lengths = words.stream()
    .map(String::length)
    .collect(Collectors.toList());
System.out.println(lengths);  // [5, 5, 4]

// Transform to different type
List<Person> people = Arrays.asList(
    new Person("Alice", 25),
    new Person("Bob", 30)
);
List<String> peopleNames = people.stream()
    .map(Person::getName)
    .collect(Collectors.toList());

// Chain transformations
List<String> result = words.stream()
    .map(String::toUpperCase)
    .map(s -> s + "!")
    .map(s -> ">" + s + "<")
    .collect(Collectors.toList());
System.out.println(result);  // [>HELLO!<, >WORLD!<, >JAVA!<]

// mapToInt/mapToLong/mapToDouble - for primitive streams
List<String> strings = Arrays.asList("1", "2", "3");
int sum = strings.stream()
    .mapToInt(Integer::parseInt)
    .sum();
System.out.println(sum);  // 6
```

### 3.3 flatMap(Function) - Flattening

**Purpose:** Flatten nested structures (Stream of Streams to single Stream)

**Signature:** `<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)`

```java
// Flatten list of lists
List<List<Integer>> listOfLists = Arrays.asList(
    Arrays.asList(1, 2, 3),
    Arrays.asList(4, 5),
    Arrays.asList(6, 7, 8, 9)
);

List<Integer> flattened = listOfLists.stream()
    .flatMap(List::stream)  // Convert each list to stream, then flatten
    .collect(Collectors.toList());
System.out.println(flattened);  // [1, 2, 3, 4, 5, 6, 7, 8, 9]

// Split strings and flatten
List<String> sentences = Arrays.asList(
    "Hello World",
    "Java Stream API",
    "Functional Programming"
);

List<String> words = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .collect(Collectors.toList());
System.out.println(words);  // [Hello, World, Java, Stream, API, Functional, Programming]

// Get all characters from list of strings
List<String> wordList = Arrays.asList("Hello", "World");
List<String> characters = wordList.stream()
    .flatMap(word -> word.chars()
        .mapToObj(c -> String.valueOf((char) c)))
    .collect(Collectors.toList());
System.out.println(characters);  // [H, e, l, l, o, W, o, r, l, d]

// flatMapToInt/flatMapToLong/flatMapToDouble
List<int[]> arrays = Arrays.asList(
    new int[]{1, 2},
    new int[]{3, 4, 5}
);
int total = arrays.stream()
    .flatMapToInt(Arrays::stream)
    .sum();
System.out.println(total);  // 15
```

### 3.4 distinct() - Remove Duplicates

**Purpose:** Return stream with distinct elements (uses equals())

**Signature:** `Stream<T> distinct()`

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 5);

List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
System.out.println(unique);  // [1, 2, 3, 4, 5]

// With objects - uses equals()
List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana");
List<String> uniqueWords = words.stream()
    .distinct()
    .collect(Collectors.toList());
System.out.println(uniqueWords);  // [apple, banana, cherry]

// Case-insensitive distinct (transform first)
List<String> names = Arrays.asList("Alice", "alice", "Bob", "BOB", "Charlie");
List<String> uniqueNames = names.stream()
    .map(String::toLowerCase)
    .distinct()
    .collect(Collectors.toList());
System.out.println(uniqueNames);  // [alice, bob, charlie]
```

### 3.5 sorted() - Sorting

**Purpose:** Sort elements (natural order or with Comparator)

**Signatures:**
- `Stream<T> sorted()` - natural ordering
- `Stream<T> sorted(Comparator<? super T> comparator)` - custom ordering

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);

// Natural order
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());
System.out.println(sorted);  // [1, 2, 3, 5, 8, 9]

// Reverse order
List<Integer> reversed = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());
System.out.println(reversed);  // [9, 8, 5, 3, 2, 1]

// Sort strings by length
List<String> words = Arrays.asList("banana", "pie", "apple", "strawberry");
List<String> sortedByLength = words.stream()
    .sorted(Comparator.comparingInt(String::length))
    .collect(Collectors.toList());
System.out.println(sortedByLength);  // [pie, apple, banana, strawberry]

// Multiple criteria
List<Person> people = Arrays.asList(
    new Person("Alice", 25),
    new Person("Bob", 30),
    new Person("Alice", 30)
);
List<Person> sortedPeople = people.stream()
    .sorted(Comparator.comparing(Person::getName)
                      .thenComparingInt(Person::getAge))
    .collect(Collectors.toList());
// Sorted by name first, then by age

// Reverse with custom comparator
List<String> reverseLength = words.stream()
    .sorted(Comparator.comparingInt(String::length).reversed())
    .collect(Collectors.toList());
System.out.println(reverseLength);  // [strawberry, banana, apple, pie]
```

### 3.6 limit(long) - Truncate Stream

**Purpose:** Truncate stream to given maximum size

**Signature:** `Stream<T> limit(long maxSize)`

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Get first 5 elements
List<Integer> first5 = numbers.stream()
    .limit(5)
    .collect(Collectors.toList());
System.out.println(first5);  // [1, 2, 3, 4, 5]

// Limit infinite stream
List<Integer> firstEvens = Stream.iterate(0, n -> n + 2)
    .limit(10)
    .collect(Collectors.toList());
System.out.println(firstEvens);  // [0, 2, 4, 6, 8, 10, 12, 14, 16, 18]

// Top 3 after sorting
List<Integer> top3 = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .limit(3)
    .collect(Collectors.toList());
System.out.println(top3);  // [10, 9, 8]

// With filtering
List<Integer> firstEvenAbove5 = numbers.stream()
    .filter(n -> n > 5)
    .filter(n -> n % 2 == 0)
    .limit(2)
    .collect(Collectors.toList());
System.out.println(firstEvenAbove5);  // [6, 8]
```

### 3.7 skip(long) - Skip Elements

**Purpose:** Discard first n elements of stream

**Signature:** `Stream<T> skip(long n)`

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Skip first 3
List<Integer> afterSkip = numbers.stream()
    .skip(3)
    .collect(Collectors.toList());
System.out.println(afterSkip);  // [4, 5, 6, 7, 8, 9, 10]

// Pagination: skip + limit
List<Integer> page2 = numbers.stream()
    .skip(3)   // Skip first page (3 items)
    .limit(3)  // Get second page (3 items)
    .collect(Collectors.toList());
System.out.println(page2);  // [4, 5, 6]

// Skip with infinite stream
List<Integer> skip5Evens = Stream.iterate(0, n -> n + 2)
    .skip(5)
    .limit(5)
    .collect(Collectors.toList());
System.out.println(skip5Evens);  // [10, 12, 14, 16, 18]
```

### 3.8 peek(Consumer) - Debug/Side Effects

**Purpose:** Perform action on each element without consuming stream (for debugging)

**Signature:** `Stream<T> peek(Consumer<? super T> action)`

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Debug pipeline
List<Integer> result = numbers.stream()
    .peek(n -> System.out.println("Original: " + n))
    .map(n -> n * 2)
    .peek(n -> System.out.println("After map: " + n))
    .filter(n -> n > 5)
    .peek(n -> System.out.println("After filter: " + n))
    .collect(Collectors.toList());

// Modify mutable objects (not recommended, but possible)
List<StringBuilder> builders = Arrays.asList(
    new StringBuilder("hello"),
    new StringBuilder("world")
);
builders.stream()
    .peek(sb -> sb.append("!"))  // Side effect
    .forEach(System.out::println);
// Prints: hello! world!

// WARNING: peek() is lazy and may not execute if no terminal operation
numbers.stream()
    .peek(System.out::println);  // Nothing printed! No terminal operation
```

---

## 4. Terminal Operations

Terminal operations trigger stream pipeline execution and produce a result or side effect. After terminal operation, stream is consumed and cannot be reused.

**Common Characteristic:** All return non-Stream result or void

### 4.1 forEach(Consumer) - Iteration

**Purpose:** Perform action for each element

**Signature:** `void forEach(Consumer<? super T> action)`

```java
List<String> words = Arrays.asList("Hello", "World", "Java");

// Print each element
words.stream().forEach(System.out::println);
// Hello
// World
// Java

// With lambda
words.stream().forEach(word -> System.out.println(">>> " + word));

// forEachOrdered - maintains order (important for parallel streams)
words.parallelStream().forEachOrdered(System.out::println);  // Guaranteed order
```

### 4.2 collect(Collector) - Collect to Collection

**Purpose:** Accumulate elements into collection or other data structure

**Signature:** `<R, A> R collect(Collector<? super T, A, R> collector)`

```java
List<String> words = Arrays.asList("hello", "world", "java", "stream");

// Collect to List
List<String> list = words.stream()
    .filter(w -> w.length() > 4)
    .collect(Collectors.toList());

// Collect to Set
Set<String> set = words.stream()
    .collect(Collectors.toSet());

// Collect to specific collection
LinkedList<String> linkedList = words.stream()
    .collect(Collectors.toCollection(LinkedList::new));

// Collect to Map
Map<String, Integer> wordLengths = words.stream()
    .collect(Collectors.toMap(
        w -> w,              // key
        String::length       // value
    ));

// Collect to Map with duplicate key handling
List<String> wordsWithDuplicates = Arrays.asList("apple", "banana", "apple");
Map<String, Integer> counts = wordsWithDuplicates.stream()
    .collect(Collectors.toMap(
        w -> w,
        w -> 1,
        Integer::sum  // Merge function for duplicate keys
    ));

// Joining strings
String joined = words.stream()
    .collect(Collectors.joining());  // "helloworldjavastream"

String joinedWithDelimiter = words.stream()
    .collect(Collectors.joining(", "));  // "hello, world, java, stream"

String joinedWithPrefixSuffix = words.stream()
    .collect(Collectors.joining(", ", "[", "]"));  // "[hello, world, java, stream]"
```

### 4.3 reduce() - Reduction

**Purpose:** Reduce stream to single value by repeatedly applying operation

**Signatures:**
- `Optional<T> reduce(BinaryOperator<T> accumulator)`
- `T reduce(T identity, BinaryOperator<T> accumulator)`
- `<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)`

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sum with identity
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);  // or Integer::sum
System.out.println(sum);  // 15

// Product
int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);
System.out.println(product);  // 120

// Max without identity (returns Optional)
Optional<Integer> max = numbers.stream()
    .reduce((a, b) -> a > b ? a : b);  // or Integer::max
System.out.println(max.get());  // 5

// Min
Optional<Integer> min = numbers.stream()
    .reduce(Integer::min);
System.out.println(min.get());  // 1

// String concatenation
List<String> words = Arrays.asList("Hello", "World", "Java");
String concatenated = words.stream()
    .reduce("", (a, b) -> a + " " + b);
System.out.println(concatenated.trim());  // "Hello World Java"

// Complex reduction
List<Person> people = Arrays.asList(
    new Person("Alice", 25),
    new Person("Bob", 30),
    new Person("Charlie", 35)
);
int totalAge = people.stream()
    .reduce(0,
        (sum, person) -> sum + person.getAge(),  // Accumulator
        Integer::sum);                            // Combiner (for parallel)
System.out.println(totalAge);  // 90
```

### 4.4 count() - Count Elements

**Purpose:** Return number of elements in stream

**Signature:** `long count()`

```java
List<String> words = Arrays.asList("hello", "world", "java", "stream");

long count = words.stream()
    .filter(w -> w.length() > 4)
    .count();
System.out.println(count);  // 3

// Count with distinct
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
long uniqueCount = numbers.stream()
    .distinct()
    .count();
System.out.println(uniqueCount);  // 4
```

### 4.5 min() / max() - Find Extremes

**Purpose:** Find minimum or maximum element

**Signatures:**
- `Optional<T> min(Comparator<? super T> comparator)`
- `Optional<T> max(Comparator<? super T> comparator)`

```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3);

Optional<Integer> min = numbers.stream()
    .min(Comparator.naturalOrder());
System.out.println(min.get());  // 1

Optional<Integer> max = numbers.stream()
    .max(Comparator.naturalOrder());
System.out.println(max.get());  // 9

// Find longest string
List<String> words = Arrays.asList("banana", "pie", "apple", "strawberry");
Optional<String> longest = words.stream()
    .max(Comparator.comparingInt(String::length));
System.out.println(longest.get());  // "strawberry"

// Find oldest person
List<Person> people = Arrays.asList(
    new Person("Alice", 25),
    new Person("Bob", 30),
    new Person("Charlie", 35)
);
Optional<Person> oldest = people.stream()
    .max(Comparator.comparingInt(Person::getAge));
System.out.println(oldest.get().getName());  // "Charlie"
```

### 4.6 findFirst() / findAny() - Find Elements

**Purpose:** Find first or any element (returns Optional)

**Signatures:**
- `Optional<T> findFirst()` - returns first element
- `Optional<T> findAny()` - returns any element (nondeterministic in parallel)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

Optional<Integer> first = numbers.stream()
    .filter(n -> n > 3)
    .findFirst();
System.out.println(first.get());  // 4

Optional<Integer> any = numbers.stream()
    .filter(n -> n > 3)
    .findAny();
System.out.println(any.get());  // Could be 4 or 5 (nondeterministic with parallel)

// With empty stream
Optional<Integer> notFound = numbers.stream()
    .filter(n -> n > 10)
    .findFirst();
System.out.println(notFound.isPresent());  // false

// Practical usage
String firstLongWord = words.stream()
    .filter(w -> w.length() > 5)
    .findFirst()
    .orElse("none");
```

### 4.7 anyMatch() / allMatch() / noneMatch() - Matching

**Purpose:** Check if elements match predicate

**Signatures:**
- `boolean anyMatch(Predicate<? super T> predicate)` - true if ANY match
- `boolean allMatch(Predicate<? super T> predicate)` - true if ALL match
- `boolean noneMatch(Predicate<? super T> predicate)` - true if NONE match

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// anyMatch - at least one matches
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);
System.out.println(hasEven);  // true

// allMatch - all match
boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);
System.out.println(allPositive);  // true

boolean allEven = numbers.stream()
    .allMatch(n -> n % 2 == 0);
System.out.println(allEven);  // false

// noneMatch - none match
boolean noneNegative = numbers.stream()
    .noneMatch(n -> n < 0);
System.out.println(noneNegative);  // true

// Short-circuiting behavior
boolean result = Stream.iterate(1, n -> n + 1)
    .limit(1000000)
    .anyMatch(n -> n > 100);  // Stops at 101, doesn't process all 1M
System.out.println(result);  // true
```

### 4.8 toArray() - Convert to Array

**Purpose:** Convert stream to array

**Signatures:**
- `Object[] toArray()`
- `<A> A[] toArray(IntFunction<A[]> generator)`

```java
List<String> words = Arrays.asList("hello", "world", "java");

// To Object[]
Object[] array1 = words.stream().toArray();

// To String[] - need array generator
String[] array2 = words.stream().toArray(String[]::new);

// With transformation
Integer[] lengths = words.stream()
    .map(String::length)
    .toArray(Integer[]::new);
System.out.println(Arrays.toString(lengths));  // [5, 5, 4]
```

---

## 5. Stream Pipeline Execution

### Lazy Evaluation

Intermediate operations are **lazy** - they're not executed until a terminal operation is called.

```java
List<String> words = Arrays.asList("hello", "world", "java");

// Nothing executed yet - no terminal operation
Stream<String> stream = words.stream()
    .filter(w -> {
        System.out.println("Filter: " + w);
        return w.length() > 4;
    })
    .map(w -> {
        System.out.println("Map: " + w);
        return w.toUpperCase();
    });

System.out.println("Before terminal operation");

// Now execution happens
List<String> result = stream.collect(Collectors.toList());

// Output:
// Before terminal operation
// Filter: hello
// Map: hello
// Filter: world
// Map: world
// Filter: java
```

### Short-Circuiting Operations

Some operations can terminate processing early:

**Short-circuiting intermediate:** `limit()`, `skip()`  
**Short-circuiting terminal:** `findFirst()`, `findAny()`, `anyMatch()`, `allMatch()`, `noneMatch()`

```java
// Stops after finding first match
Optional<Integer> first = Stream.iterate(1, n -> n + 1)
    .filter(n -> {
        System.out.println("Testing: " + n);
        return n > 5;
    })
    .findFirst();
// Prints: Testing: 1, 2, 3, 4, 5, 6 (stops at 6)
System.out.println(first.get());  // 6

// limit() short-circuits
Stream.generate(Math::random)
    .limit(5)  // Only 5 generated
    .forEach(System.out::println);
```

### Operation Order Matters

Order of operations can significantly impact performance:

```java
List<String> words = Arrays.asList("hello", "world", "java", "stream", "api");

// Less efficient - map all, then filter
long count1 = words.stream()
    .map(String::toUpperCase)  // Transforms all 5
    .filter(w -> w.length() > 4)  // Then filters
    .count();

// More efficient - filter first, then map
long count2 = words.stream()
    .filter(w -> w.length() > 4)  // Filters to 3
    .map(String::toUpperCase)  // Only transforms 3
    .count();

// Best - no map needed for count
long count3 = words.stream()
    .filter(w -> w.length() > 4)
    .count();
```

---

## 6. Real-World Examples

### Example 1: E-commerce Order Processing

```java
public class OrderProcessor {
    static class Order {
        String id;
        String customer;
        double amount;
        String status;
        LocalDate date;
        
        public Order(String id, String customer, double amount, String status, LocalDate date) {
            this.id = id;
            this.customer = customer;
            this.amount = amount;
            this.status = status;
            this.date = date;
        }
        
        // Getters...
    }
    
    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
            new Order("O001", "Alice", 150.0, "COMPLETED", LocalDate.of(2024, 1, 15)),
            new Order("O002", "Bob", 250.0, "PENDING", LocalDate.of(2024, 1, 16)),
            new Order("O003", "Alice", 75.0, "COMPLETED", LocalDate.of(2024, 1, 17)),
            new Order("O004", "Charlie", 500.0, "CANCELLED", LocalDate.of(2024, 1, 18)),
            new Order("O005", "Alice", 300.0, "COMPLETED", LocalDate.of(2024, 1, 19)),
            new Order("O006", "Bob", 100.0, "COMPLETED", LocalDate.of(2024, 1, 20))
        );
        
        // 1. Total revenue from completed orders
        double totalRevenue = orders.stream()
            .filter(o -> "COMPLETED".equals(o.status))
            .mapToDouble(Order::getAmount)
            .sum();
        System.out.println("Total Revenue: $" + totalRevenue);  // $625.0
        
        // 2. Top 3 customers by total spending
        Map<String, Double> customerTotals = orders.stream()
            .filter(o -> "COMPLETED".equals(o.status))
            .collect(Collectors.groupingBy(
                Order::getCustomer,
                Collectors.summingDouble(Order::getAmount)
            ));
        
        List<String> top3Customers = customerTotals.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        System.out.println("Top 3 Customers: " + top3Customers);
        
        // 3. Average order value
        double avgOrderValue = orders.stream()
            .filter(o -> "COMPLETED".equals(o.status))
            .mapToDouble(Order::getAmount)
            .average()
            .orElse(0.0);
        System.out.println("Average Order Value: $" + avgOrderValue);
        
        // 4. Orders by status count
        Map<String, Long> ordersByStatus = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.counting()
            ));
        System.out.println("Orders by Status: " + ordersByStatus);
        
        // 5. Find high-value orders (>$200)
        List<String> highValueOrders = orders.stream()
            .filter(o -> o.amount > 200)
            .sorted(Comparator.comparingDouble(Order::getAmount).reversed())
            .map(o -> o.id + " - $" + o.amount)
            .collect(Collectors.toList());
        System.out.println("High-Value Orders: " + highValueOrders);
        
        // 6. Check if any pending orders exist
        boolean hasPending = orders.stream()
            .anyMatch(o -> "PENDING".equals(o.status));
        System.out.println("Has Pending Orders: " + hasPending);
    }
}
```
**Why Streams:** Declarative order processing with clear business logic. Easy to filter, aggregate, and analyze data without complex loops.

### Example 2: Student Grade Analysis

```java
public class GradeAnalyzer {
    static class Student {
        String name;
        int grade;
        String subject;
        
        public Student(String name, int grade, String subject) {
            this.name = name;
            this.grade = grade;
            this.subject = subject;
        }
        
        // Getters...
    }
    
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, "Math"),
            new Student("Bob", 92, "Math"),
            new Student("Alice", 78, "Science"),
            new Student("Charlie", 95, "Math"),
            new Student("Bob", 88, "Science"),
            new Student("Alice", 91, "English"),
            new Student("Charlie", 87, "Science")
        );
        
        // 1. Average grade per subject
        Map<String, Double> avgBySubject = students.stream()
            .collect(Collectors.groupingBy(
                Student::getSubject,
                Collectors.averagingInt(Student::getGrade)
            ));
        System.out.println("Average by Subject: " + avgBySubject);
        
        // 2. Top 3 students by grade
        List<String> top3 = students.stream()
            .sorted(Comparator.comparingInt(Student::getGrade).reversed())
            .limit(3)
            .map(Student::getName)
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Top 3 Students: " + top3);
        
        // 3. Students who passed all subjects (>= 75)
        Set<String> passedAll = students.stream()
            .collect(Collectors.groupingBy(
                Student::getName,
                Collectors.minBy(Comparator.comparingInt(Student::getGrade))
            ))
            .entrySet().stream()
            .filter(e -> e.getValue().isPresent() && e.getValue().get().grade >= 75)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        System.out.println("Passed All Subjects: " + passedAll);
        
        // 4. Grade distribution
        Map<String, Long> gradeDistribution = students.stream()
            .collect(Collectors.groupingBy(
                s -> {
                    if (s.grade >= 90) return "A";
                    if (s.grade >= 80) return "B";
                    if (s.grade >= 70) return "C";
                    return "F";
                },
                Collectors.counting()
            ));
        System.out.println("Grade Distribution: " + gradeDistribution);
        
        // 5. Subjects with average > 85
        List<String> strongSubjects = avgBySubject.entrySet().stream()
            .filter(e -> e.getValue() > 85)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        System.out.println("Strong Subjects: " + strongSubjects);
    }
}
```
**Why Streams:** Complex grade analysis with grouping, filtering, and statistics made simple and readable.

### Example 3: Log File Processing

```java
public class LogProcessor {
    static class LogEntry {
        LocalDateTime timestamp;
        String level;
        String message;
        
        public LogEntry(LocalDateTime timestamp, String level, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
        }
        
        public static LogEntry parse(String line) {
            // Simplified parsing
            String[] parts = line.split("\|");
            return new LogEntry(
                LocalDateTime.parse(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim()
            );
        }
        
        // Getters...
    }
    
    public static void main(String[] args) throws IOException {
        // Simulate log entries
        List<String> logLines = Arrays.asList(
            "2024-01-15T10:00:00|ERROR|Database connection failed",
            "2024-01-15T10:01:00|INFO|Application started",
            "2024-01-15T10:02:00|ERROR|Null pointer exception in UserService",
            "2024-01-15T10:03:00|WARN|Slow query detected: 5s",
            "2024-01-15T10:04:00|ERROR|Database connection failed",
            "2024-01-15T10:05:00|INFO|User login: alice@example.com"
        );
        
        List<LogEntry> logs = logLines.stream()
            .map(LogEntry::parse)
            .collect(Collectors.toList());
        
        // 1. Count by log level
        Map<String, Long> countByLevel = logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::getLevel,
                Collectors.counting()
            ));
        System.out.println("Count by Level: " + countByLevel);
        
        // 2. Find all ERROR messages
        List<String> errors = logs.stream()
            .filter(log -> "ERROR".equals(log.level))
            .map(LogEntry::getMessage)
            .collect(Collectors.toList());
        System.out.println("Errors: " + errors);
        
        // 3. Find unique error messages (deduplication)
        List<String> uniqueErrors = logs.stream()
            .filter(log -> "ERROR".equals(log.level))
            .map(LogEntry::getMessage)
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Unique Errors: " + uniqueErrors);
        
        // 4. Logs in last 5 minutes
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<LogEntry> recentLogs = logs.stream()
            .filter(log -> log.timestamp.isAfter(fiveMinutesAgo))
            .collect(Collectors.toList());
        
        // 5. Extract all mentioned services from messages
        List<String> services = logs.stream()
            .flatMap(log -> Arrays.stream(log.message.split("\s+")))
            .filter(word -> word.endsWith("Service"))
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Mentioned Services: " + services);
        
        // 6. Check if critical errors exist
        boolean hasCritical = logs.stream()
            .anyMatch(log -> "ERROR".equals(log.level) && 
                           log.message.contains("Database"));
        System.out.println("Has Critical Errors: " + hasCritical);
    }
}
```
**Why Streams:** Log processing requires filtering, grouping, and pattern matching. Streams make complex log analysis concise and maintainable.

---

## Exam Traps

- ❌ **TRAP 1:** Streams are one-use only
  ```java
  Stream<String> stream = list.stream();
  stream.forEach(System.out::println);  // ✅ OK
  stream.forEach(System.out::println);  // ❌ IllegalStateException - already consumed
  ```

- ❌ **TRAP 2:** Intermediate operations are lazy
  ```java
  Stream<String> stream = list.stream()
      .filter(s -> {
          System.out.println("Filter: " + s);  // NOT printed yet
          return s.length() > 3;
      });
  // Nothing printed - no terminal operation yet
  stream.count();  // NOW filter executes
  ```

- ❌ **TRAP 3:** map() vs flatMap() confusion
  ```java
  List<List<Integer>> lists = Arrays.asList(
      Arrays.asList(1, 2),
      Arrays.asList(3, 4)
  );
  
  // Wrong - Stream<Stream<Integer>>
  // lists.stream().map(List::stream);
  
  // Correct - Stream<Integer>
  lists.stream().flatMap(List::stream);
  ```

- ❌ **TRAP 4:** Modifying source during stream processing
  ```java
  List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
  list.stream().forEach(s -> list.add("x"));  // ❌ ConcurrentModificationException
  ```

- ❌ **TRAP 5:** Reusing stream after terminal operation
  ```java
  Stream<Integer> stream = Stream.of(1, 2, 3);
  int sum = stream.reduce(0, Integer::sum);
  int count = stream.count();  // ❌ IllegalStateException
  ```

- ❌ **TRAP 6:** findFirst() vs findAny() in parallel streams
  ```java
  List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
  
  // Sequential - always returns 1
  list.stream().findAny().get();  // 1
  
  // Parallel - could return any
  list.parallelStream().findAny().get();  // Could be 1, 2, 3, 4, or 5
  ```

- ❌ **TRAP 7:** forEach() vs forEachOrdered() in parallel
  ```java
  List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
  
  // Parallel - unpredictable order
  list.parallelStream().forEach(System.out::println);
  
  // Parallel but maintains order
  list.parallelStream().forEachOrdered(System.out::println);  // 1, 2, 3, 4, 5
  ```

- ❌ **TRAP 8:** Stream.of() with array creates Stream<int[]>, not IntStream
  ```java
  int[] array = {1, 2, 3};
  
  // Wrong - Stream<int[]> with ONE element (the array)
  Stream<int[]> stream1 = Stream.of(array);
  
  // Correct - IntStream with 3 elements
  IntStream stream2 = Arrays.stream(array);
  ```

- ❌ **TRAP 9:** Collectors.toMap() duplicate keys
  ```java
  List<String> words = Arrays.asList("apple", "banana", "apple");
  
  // ❌ IllegalStateException - duplicate key "apple"
  // Map<String, Integer> map = words.stream()
  //     .collect(Collectors.toMap(w -> w, String::length));
  
  // ✅ Provide merge function
  Map<String, Integer> map = words.stream()
      .collect(Collectors.toMap(w -> w, String::length, (v1, v2) -> v1));
  ```

- ❌ **TRAP 10:** Primitive stream boxing/unboxing
  ```java
  // Inefficient - boxing
  IntStream.range(1, 1000000)
      .boxed()  // int -> Integer boxing
      .collect(Collectors.toList());
  
  // Efficient - stay primitive
  int sum = IntStream.range(1, 1000000).sum();
  ```

- ❌ **TRAP 11:** Optional.get() without checking
  ```java
  List<Integer> list = Arrays.asList();
  
  // ❌ NoSuchElementException if empty
  // int max = list.stream().max(Comparator.naturalOrder()).get();
  
  // ✅ Check first or use orElse
  int max = list.stream()
      .max(Comparator.naturalOrder())
      .orElse(0);
  ```

- ❌ **TRAP 12:** sorted() on infinite stream
  ```java
  // ❌ Never completes - tries to sort infinite stream
  // Stream.generate(Math::random).sorted().limit(10);
  
  // ✅ Limit first, then sort
  Stream.generate(Math::random).limit(10).sorted();
  ```

---

## Exam Questions

### Question 1: Stream characteristics
```java
Which statement is TRUE about streams?
```
**Options:**  
A. Streams store elements  
B. Streams can be traversed multiple times  
C. Intermediate operations are lazy  
D. Terminal operations return Stream  

**Answer:** C  
**Explanation:** Intermediate operations (filter, map, etc.) are lazy and only execute when a terminal operation is called. A is false (streams don't store elements), B is false (streams are one-use), D is false (terminal operations don't return Stream).

---

### Question 2: Stream creation
```java
int[] array = {1, 2, 3, 4, 5};
Stream<int[]> stream = Stream.of(array);
long count = stream.count();
System.out.println(count);
```
**What is the output?**  
A. 5  
B. 1  
C. Compilation error  
D. Runtime exception  

**Answer:** B  
**Explanation:** Stream.of(array) creates Stream<int[]> with ONE element (the entire array). To create IntStream with 5 elements, use Arrays.stream(array).

---

### Question 3: flatMap usage
```java
List<List<Integer>> lists = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4, 5)
);
long count = lists.stream()
    .flatMap(List::stream)
    .count();
System.out.println(count);
```
**What is the output?**  
A. 2  
B. 5  
C. 3  
D. Compilation error  

**Answer:** B  
**Explanation:** flatMap flattens the nested lists into a single stream of integers: [1, 2, 3, 4, 5], so count = 5.

---

### Question 4: Lazy evaluation
```java
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream()
    .filter(s -> {
        System.out.print(s);
        return true;
    });
System.out.print("END");
```
**What is the output?**  
A. abcEND  
B. END  
C. ENDabc  
D. Compilation error  

**Answer:** B  
**Explanation:** No terminal operation, so the filter (intermediate operation) never executes due to lazy evaluation. Only "END" is printed.

---

### Question 5: Stream reuse
```java
Stream<Integer> stream = Stream.of(1, 2, 3);
stream.count();
stream.forEach(System.out::println);
```
**What is the result?**  
A. Prints 1 2 3  
B. Runtime exception  
C. Compilation error  
D. Prints nothing  

**Answer:** B  
**Explanation:** IllegalStateException - streams can only be used once. After count() (terminal operation), the stream is consumed and cannot be reused.

---

### Question 6: map vs mapToInt
```java
List<String> list = Arrays.asList("1", "2", "3");
int sum1 = list.stream().map(Integer::parseInt).reduce(0, Integer::sum);
int sum2 = list.stream().mapToInt(Integer::parseInt).sum();
```
**Which statements are true?**  
A. sum1 and sum2 are equal  
B. sum2 is more efficient (no boxing)  
C. Both A and B  
D. Neither A nor B  

**Answer:** C  
**Explanation:** Both produce the same result (6), but sum2 is more efficient because mapToInt avoids boxing/unboxing by using primitive int stream.

---

### Question 7: Terminal operations
```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
list.stream()
    .filter(n -> n > 2)
    .peek(System.out::println);
```
**What is the output?**  
A. 3 4 5  
B. Nothing  
C. 1 2 3 4 5  
D. Compilation error  

**Answer:** B  
**Explanation:** No terminal operation, so nothing executes (lazy evaluation). peek() is intermediate and requires a terminal operation to trigger execution.

---

### Question 8: findFirst vs findAny
```java
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
Optional<Integer> result = list.parallelStream()
    .filter(n -> n > 2)
    .findAny();
```
**Which value could result contain?**  
A. Only 3  
B. Only 5  
C. 3, 4, or 5  
D. Any from 1 to 5  

**Answer:** C  
**Explanation:** findAny() with parallel stream is nondeterministic and could return any element that matches the filter (n > 2), so 3, 4, or 5.

---

### Question 9: reduce operation
```java
List<Integer> list = Arrays.asList(1, 2, 3, 4);
int result = list.stream()
    .reduce(0, (a, b) -> a - b);
System.out.println(result);
```
**What is the output?**  
A. -10  
B. 10  
C. 0  
D. -2  

**Answer:** A  
**Explanation:** reduce with subtraction: ((((0 - 1) - 2) - 3) - 4) = -10.

---

### Question 10: Collectors.toMap
```java
List<String> words = Arrays.asList("apple", "banana", "apple");
Map<String, Integer> map = words.stream()
    .collect(Collectors.toMap(w -> w, String::length));
```
**What is the result?**  
A. {apple=5, banana=6}  
B. Runtime exception  
C. Compilation error  
D. {apple=5, banana=6, apple=5}  

**Answer:** B  
**Explanation:** IllegalStateException because "apple" appears twice (duplicate key) and no merge function is provided to handle duplicates.

---

**[← Part 1: Lambdas & Functional Interfaces](CHAPTER-06-PART1-LAMBDAS-FUNCTIONAL.md) | [Part 3: Advanced Streams & Collectors →](CHAPTER-06-PART3-COLLECTORS-OPTIONAL.md)**
