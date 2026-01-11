# Chapter 6: Lambda Expressions and Stream API - PART 3

[← Part 2: Stream API Fundamentals](CHAPTER-06-PART2-STREAMS.md) | [Back to Main Guide](README.md)

---

# PART 3: Advanced Collectors, Optional, and Parallel Streams

## 1. Advanced Collectors

### 1.1 Grouping with groupingBy()

**Purpose:** Group elements by a classifier function into a Map

**Signatures:**
- `Collector<T,?,Map<K,List<T>>> groupingBy(Function<T,K> classifier)`
- `Collector<T,?,Map<K,D>> groupingBy(Function<T,K> classifier, Collector<T,A,D> downstream)`
- `Collector<T,?,Map<K,D>> groupingBy(Function<T,K> classifier, Supplier<M> mapFactory, Collector<T,A,D> downstream)`

```java
import java.util.*;
import java.util.stream.*;

class Employee {
    String name;
    String department;
    int salary;
    
    public Employee(String name, String department, int salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }
    
    // Getters...
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getSalary() { return salary; }
}

public class GroupingExample {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "IT", 80000),
            new Employee("Bob", "HR", 60000),
            new Employee("Charlie", "IT", 90000),
            new Employee("David", "HR", 65000),
            new Employee("Eve", "IT", 85000)
        );
        
        // 1. Simple grouping - group by department
        Map<String, List<Employee>> byDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment));
        
        System.out.println("By Department:");
        byDept.forEach((dept, emps) -> 
            System.out.println(dept + ": " + emps.size() + " employees"));
        // IT: 3 employees
        // HR: 2 employees
        
        // 2. Grouping with counting
        Map<String, Long> deptCounts = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
            ));
        System.out.println(deptCounts);  // {IT=3, HR=2}
        
        // 3. Grouping with average salary
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingInt(Employee::getSalary)
            ));
        System.out.println(avgSalaryByDept);  // {IT=85000.0, HR=62500.0}
        
        // 4. Grouping with summation
        Map<String, Integer> totalSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.summingInt(Employee::getSalary)
            ));
        System.out.println(totalSalaryByDept);  // {IT=255000, HR=125000}
        
        // 5. Grouping to Set instead of List
        Map<String, Set<String>> namesByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.mapping(Employee::getName, Collectors.toSet())
            ));
        System.out.println(namesByDept);
        
        // 6. Grouping with custom Map type (TreeMap for sorted keys)
        Map<String, List<Employee>> sortedByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                TreeMap::new,  // Map factory
                Collectors.toList()
            ));
        
        // 7. Multi-level grouping (nested grouping)
        Map<String, Map<Boolean, List<Employee>>> byDeptAndHighPay = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.groupingBy(e -> e.getSalary() > 70000)
            ));
        System.out.println("Multi-level grouping:");
        byDeptAndHighPay.forEach((dept, map) -> {
            System.out.println(dept + ":");
            System.out.println("  High pay: " + map.get(true).size());
            System.out.println("  Normal pay: " + map.getOrDefault(false, Collections.emptyList()).size());
        });
        
        // 8. Grouping with maxBy/minBy
        Map<String, Optional<Employee>> highestPaidByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))
            ));
        
        highestPaidByDept.forEach((dept, optEmp) -> 
            optEmp.ifPresent(e -> 
                System.out.println(dept + " highest paid: " + e.getName() + " ($" + e.getSalary() + ")")
            )
        );
    }
}
```

### 1.2 Partitioning with partitioningBy()

**Purpose:** Partition elements into two groups based on a Predicate (true/false)

**Signatures:**
- `Collector<T,?,Map<Boolean,List<T>>> partitioningBy(Predicate<T> predicate)`
- `Collector<T,?,Map<Boolean,D>> partitioningBy(Predicate<T> predicate, Collector<T,A,D> downstream)`

```java
List<Employee> employees = Arrays.asList(
    new Employee("Alice", "IT", 80000),
    new Employee("Bob", "HR", 60000),
    new Employee("Charlie", "IT", 90000),
    new Employee("David", "HR", 65000),
    new Employee("Eve", "IT", 85000)
);

// 1. Simple partitioning - high salary vs normal
Map<Boolean, List<Employee>> highSalaryPartition = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 70000));

System.out.println("High salary: " + highSalaryPartition.get(true).size());   // 3
System.out.println("Normal salary: " + highSalaryPartition.get(false).size()); // 2

// 2. Partitioning with counting
Map<Boolean, Long> salaryPartitionCounts = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.getSalary() > 70000,
        Collectors.counting()
    ));
System.out.println(salaryPartitionCounts);  // {false=2, true=3}

// 3. Partitioning with average
Map<Boolean, Double> avgSalaryPartition = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.getSalary() > 70000,
        Collectors.averagingInt(Employee::getSalary)
    ));
System.out.println("High salary avg: " + avgSalaryPartition.get(true));
System.out.println("Normal salary avg: " + avgSalaryPartition.get(false));

// 4. Partitioning with mapping to names
Map<Boolean, List<String>> namesByHighSalary = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.getSalary() > 70000,
        Collectors.mapping(Employee::getName, Collectors.toList())
    ));
System.out.println(namesByHighSalary);
// {false=[Bob, David], true=[Alice, Charlie, Eve]}

// 5. Partitioning with maxBy
Map<Boolean, Optional<Employee>> maxByPartition = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.getSalary() > 70000,
        Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))
    ));
```

### 1.3 Downstream Collectors

Downstream collectors are used with `groupingBy()` and `partitioningBy()` to further process grouped elements.

**Common Downstream Collectors:**

```java
import java.util.*;
import java.util.stream.*;

List<String> words = Arrays.asList(
    "apple", "banana", "apricot", "cherry", "avocado", "blueberry"
);

// 1. counting() - count elements in each group
Map<Character, Long> countByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.counting()
    ));
System.out.println(countByFirstLetter);  // {a=3, b=2, c=1}

// 2. summingInt/summingLong/summingDouble
Map<Character, Integer> lengthSumByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.summingInt(String::length)
    ));
System.out.println(lengthSumByFirstLetter);  // {a=21, b=16, c=6}

// 3. averagingInt/averagingLong/averagingDouble
Map<Character, Double> avgLengthByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.averagingDouble(String::length)
    ));
System.out.println(avgLengthByFirstLetter);

// 4. mapping() - transform then collect
Map<Character, List<Integer>> lengthsByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.mapping(String::length, Collectors.toList())
    ));
System.out.println(lengthsByFirstLetter);  // {a=[5, 6, 7, 7], b=[6, 9], c=[6]}

// 5. filtering() - filter then collect (Java 9+)
Map<Character, List<String>> longWordsByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.filtering(s -> s.length() > 6, Collectors.toList())
    ));
System.out.println(longWordsByFirstLetter);

// 6. maxBy/minBy - find max/min in each group
Map<Character, Optional<String>> longestByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.maxBy(Comparator.comparingInt(String::length))
    ));
System.out.println(longestByFirstLetter);

// 7. reducing() - custom reduction
Map<Character, Optional<String>> concatenatedByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.reducing((s1, s2) -> s1 + "," + s2)
    ));
System.out.println(concatenatedByFirstLetter);

// 8. collectingAndThen() - transform result after collecting
Map<Character, Integer> countDoubledByFirstLetter = words.stream()
    .collect(Collectors.groupingBy(
        s -> s.charAt(0),
        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
    ));
System.out.println(countDoubledByFirstLetter);
```

### 1.4 summarizingInt/Long/Double

**Purpose:** Collect statistics (count, sum, min, max, average) in one pass

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Get all statistics at once
IntSummaryStatistics stats = numbers.stream()
    .collect(Collectors.summarizingInt(Integer::intValue));

System.out.println("Count: " + stats.getCount());       // 10
System.out.println("Sum: " + stats.getSum());           // 55
System.out.println("Min: " + stats.getMin());           // 1
System.out.println("Max: " + stats.getMax());           // 10
System.out.println("Average: " + stats.getAverage());   // 5.5

// With grouping
List<Employee> employees = Arrays.asList(
    new Employee("Alice", "IT", 80000),
    new Employee("Bob", "HR", 60000),
    new Employee("Charlie", "IT", 90000)
);

Map<String, IntSummaryStatistics> salaryStatsByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.summarizingInt(Employee::getSalary)
    ));

salaryStatsByDept.forEach((dept, stat) -> {
    System.out.println(dept + ":");
    System.out.println("  Average: $" + stat.getAverage());
    System.out.println("  Min: $" + stat.getMin());
    System.out.println("  Max: $" + stat.getMax());
});
```

### 1.5 teeing() - Combining Two Collectors (Java 12+)

**Purpose:** Process stream with two collectors simultaneously and combine results

```java
// Calculate both sum and count in one pass
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Signature: teeing(Collector downstream1, Collector downstream2, BiFunction merger)
String result = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.summingInt(Integer::intValue),  // First collector
        Collectors.counting(),                      // Second collector
        (sum, count) -> "Sum: " + sum + ", Count: " + count  // Merger
    ));
System.out.println(result);  // Sum: 15, Count: 5

// Calculate average manually using teeing
Double average = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.summingInt(Integer::intValue),
        Collectors.counting(),
        (sum, count) -> sum / (double) count
    ));
System.out.println("Average: " + average);  // 3.0

// Find min and max simultaneously
class MinMax {
    int min, max;
    MinMax(int min, int max) { this.min = min; this.max = max; }
    public String toString() { return "Min: " + min + ", Max: " + max; }
}

MinMax minMax = numbers.stream()
    .collect(Collectors.teeing(
        Collectors.minBy(Comparator.naturalOrder()),
        Collectors.maxBy(Comparator.naturalOrder()),
        (min, max) -> new MinMax(min.orElse(0), max.orElse(0))
    ));
System.out.println(minMax);  // Min: 1, Max: 5
```

---

## 2. Optional API

### What is Optional?

`Optional<T>` is a container object that may or may not contain a non-null value. It's designed to prevent `NullPointerException` and make null-handling explicit.

**Key Characteristics:**
- Immutable container
- Either contains a value or is empty
- Forces explicit null handling
- Provides functional methods for null-safe operations

### Why Use Optional?

**Benefits:**
- Eliminates `NullPointerException` when used correctly
- Makes API contracts clearer (Optional return = value might not exist)
- Enables functional-style null handling
- Reduces boilerplate null checks

**When NOT to use:**
- As class fields (use null instead)
- As method parameters (use overloading or null checks)
- In collections (use empty collections instead)
- For primitive types (use OptionalInt, OptionalLong, OptionalDouble)

### 2.1 Creating Optional

```java
import java.util.Optional;

// 1. Optional.of() - value must not be null
Optional<String> opt1 = Optional.of("Hello");
// Optional<String> opt2 = Optional.of(null);  // ❌ NullPointerException

// 2. Optional.ofNullable() - value can be null
Optional<String> opt3 = Optional.ofNullable("Hello");  // Contains "Hello"
Optional<String> opt4 = Optional.ofNullable(null);     // Empty Optional

// 3. Optional.empty() - explicitly empty
Optional<String> opt5 = Optional.empty();

// 4. From stream operations
List<String> words = Arrays.asList("apple", "banana", "cherry");
Optional<String> first = words.stream()
    .filter(w -> w.startsWith("b"))
    .findFirst();  // Returns Optional<String>
```

### 2.2 Checking Presence

```java
Optional<String> opt = Optional.of("Hello");

// 1. isPresent() - returns boolean
if (opt.isPresent()) {
    System.out.println(opt.get());  // "Hello"
}

// 2. isEmpty() - returns boolean (Java 11+)
if (opt.isEmpty()) {
    System.out.println("No value");
}

// 3. ifPresent(Consumer) - execute action if present
opt.ifPresent(value -> System.out.println("Value: " + value));
opt.ifPresent(System.out::println);

// 4. ifPresentOrElse(Consumer, Runnable) - Java 9+
opt.ifPresentOrElse(
    value -> System.out.println("Present: " + value),
    () -> System.out.println("Empty")
);
```

### 2.3 Retrieving Values

```java
Optional<String> opt1 = Optional.of("Hello");
Optional<String> opt2 = Optional.empty();

// 1. get() - throws NoSuchElementException if empty
String value1 = opt1.get();  // "Hello"
// String value2 = opt2.get();  // ❌ NoSuchElementException

// 2. orElse(T) - return default if empty
String value3 = opt1.orElse("Default");  // "Hello"
String value4 = opt2.orElse("Default");  // "Default"

// 3. orElseGet(Supplier) - lazy default (only called if empty)
String value5 = opt1.orElseGet(() -> {
    System.out.println("Computing default...");
    return "Default";
});  // "Hello" - supplier NOT called

String value6 = opt2.orElseGet(() -> {
    System.out.println("Computing default...");
    return "Default";
});  // Prints "Computing default...", returns "Default"

// 4. orElseThrow() - throw exception if empty (Java 10+)
String value7 = opt1.orElseThrow();  // "Hello"
// String value8 = opt2.orElseThrow();  // ❌ NoSuchElementException

// 5. orElseThrow(Supplier) - custom exception
String value9 = opt2.orElseThrow(() -> 
    new IllegalStateException("Value not found"));
```

### 2.4 Transforming Optional

```java
Optional<String> opt = Optional.of("hello");

// 1. map(Function) - transform value if present
Optional<String> upper = opt.map(String::toUpperCase);
System.out.println(upper.get());  // "HELLO"

Optional<Integer> length = opt.map(String::length);
System.out.println(length.get());  // 5

// map on empty Optional returns empty Optional
Optional<String> empty = Optional.empty();
Optional<String> result = empty.map(String::toUpperCase);  // Still empty

// 2. flatMap(Function) - for when mapping function returns Optional
Optional<String> name = Optional.of("Alice");

// Helper method that returns Optional
Optional<String> getEmailDomain(String name) {
    if (name.equals("Alice")) {
        return Optional.of("@example.com");
    }
    return Optional.empty();
}

// Wrong: map returns Optional<Optional<String>>
// Optional<Optional<String>> nested = name.map(this::getEmailDomain);

// Correct: flatMap flattens to Optional<String>
Optional<String> domain = name.flatMap(this::getEmailDomain);
System.out.println(domain.get());  // "@example.com"

// 3. Chaining transformations
Optional<String> result = Optional.of("  hello  ")
    .map(String::trim)
    .map(String::toUpperCase)
    .map(s -> s + "!");
System.out.println(result.get());  // "HELLO!"
```

### 2.5 Filtering Optional

```java
Optional<Integer> number = Optional.of(42);

// filter(Predicate) - return empty if predicate doesn't match
Optional<Integer> evenNumber = number.filter(n -> n % 2 == 0);
System.out.println(evenNumber.isPresent());  // true

Optional<Integer> oddNumber = number.filter(n -> n % 2 != 0);
System.out.println(oddNumber.isPresent());  // false (filtered out)

// Practical example: validate and process
Optional<String> email = Optional.of("user@example.com");

Optional<String> validEmail = email
    .filter(e -> e.contains("@"))
    .filter(e -> e.length() > 5)
    .map(String::toLowerCase);

validEmail.ifPresent(e -> System.out.println("Valid: " + e));
```

### 2.6 Combining Optionals

```java
Optional<String> opt1 = Optional.of("Hello");
Optional<String> opt2 = Optional.of("World");
Optional<String> opt3 = Optional.empty();

// 1. or(Supplier) - return alternative Optional if empty (Java 9+)
Optional<String> result1 = opt1.or(() -> opt2);
System.out.println(result1.get());  // "Hello" (first is present)

Optional<String> result2 = opt3.or(() -> opt2);
System.out.println(result2.get());  // "World" (first is empty)

Optional<String> result3 = opt3.or(() -> Optional.of("Default"));
System.out.println(result3.get());  // "Default"

// 2. stream() - convert to Stream (Java 9+)
List<String> list = Stream.of(opt1, opt2, opt3)
    .flatMap(Optional::stream)  // Filters out empty Optionals
    .collect(Collectors.toList());
System.out.println(list);  // [Hello, World]

// Combining two Optionals
Optional<String> firstName = Optional.of("John");
Optional<String> lastName = Optional.of("Doe");

// Using flatMap to combine
Optional<String> fullName = firstName.flatMap(first ->
    lastName.map(last -> first + " " + last)
);
System.out.println(fullName.get());  // "John Doe"
```

### 2.7 Primitive Optionals

For primitives, use specialized Optional classes to avoid boxing overhead.

```java
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.OptionalDouble;

// OptionalInt
OptionalInt optInt1 = OptionalInt.of(42);
OptionalInt optInt2 = OptionalInt.empty();

int value1 = optInt1.orElse(0);
int value2 = optInt2.orElse(0);

// From IntStream
OptionalInt max = IntStream.range(1, 10)
    .max();
System.out.println(max.getAsInt());  // 9

// OptionalDouble
OptionalDouble avg = IntStream.range(1, 10)
    .average();
System.out.println(avg.getAsDouble());  // 5.0

// Methods: getAsInt(), getAsLong(), getAsDouble()
// Avoid boxing with specialized methods
```

---

## 3. Parallel Streams

### What are Parallel Streams?

Parallel streams split the stream into multiple chunks, process them on different CPU cores, and combine results. They use the common ForkJoinPool by default.

### When to Use Parallel Streams

**Good candidates for parallelization:**
- Large datasets (10,000+ elements)
- CPU-intensive operations
- Independent operations (no shared state)
- Stateless operations

**Avoid parallelization for:**
- Small datasets (overhead > benefit)
- I/O-bound operations (waiting on external resources)
- Operations with shared mutable state
- Ordered operations (limit, skip, findFirst)

### 3.1 Creating Parallel Streams

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 1. From Collection
Stream<Integer> parallelStream1 = numbers.parallelStream();

// 2. Convert sequential to parallel
Stream<Integer> parallelStream2 = numbers.stream().parallel();

// 3. Check if parallel
boolean isParallel = parallelStream1.isParallel();
System.out.println(isParallel);  // true

// 4. Convert back to sequential
Stream<Integer> sequential = parallelStream1.sequential();
```

### 3.2 Parallel Stream Execution

```java
List<Integer> numbers = IntStream.rangeClosed(1, 10)
    .boxed()
    .collect(Collectors.toList());

System.out.println("Sequential:");
numbers.stream()
    .map(n -> {
        System.out.println(Thread.currentThread().getName() + ": " + n);
        return n * 2;
    })
    .forEach(System.out::println);
// Prints on main thread

System.out.println("
Parallel:");
numbers.parallelStream()
    .map(n -> {
        System.out.println(Thread.currentThread().getName() + ": " + n);
        return n * 2;
    })
    .forEach(System.out::println);
// Prints on multiple threads (ForkJoinPool)
```

### 3.3 Performance Considerations

```java
// Bad: Parallel overhead exceeds benefit
List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);
int sum1 = small.parallelStream()  // Overhead too high for 5 elements
    .mapToInt(Integer::intValue)
    .sum();

// Good: Large dataset benefits from parallelization
List<Integer> large = IntStream.rangeClosed(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

int sum2 = large.parallelStream()
    .mapToInt(Integer::intValue)
    .sum();

// Benchmark comparison
long start = System.currentTimeMillis();
long sequentialSum = large.stream()
    .mapToInt(i -> i * 2)
    .sum();
long seqTime = System.currentTimeMillis() - start;

start = System.currentTimeMillis();
long parallelSum = large.parallelStream()
    .mapToInt(i -> i * 2)
    .sum();
long parTime = System.currentTimeMillis() - start;

System.out.println("Sequential: " + seqTime + "ms");
System.out.println("Parallel: " + parTime + "ms");
```

### 3.4 Thread Safety Issues

```java
// ❌ BAD: Shared mutable state
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
List<Integer> result = new ArrayList<>();  // NOT thread-safe

numbers.parallelStream()
    .map(n -> n * 2)
    .forEach(result::add);  // ❌ Race condition!

System.out.println(result.size());  // Unpredictable (might be < 10)

// ✅ GOOD: Use thread-safe collector
List<Integer> safeResult = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());  // Thread-safe

System.out.println(safeResult.size());  // Always 10

// ✅ GOOD: Use synchronized collection (but slower)
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
numbers.parallelStream()
    .map(n -> n * 2)
    .forEach(syncList::add);
```

### 3.5 Order Preservation

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// forEach - order NOT guaranteed with parallel
System.out.println("forEach (unordered):");
numbers.parallelStream()
    .forEach(System.out::println);  // May print in any order

// forEachOrdered - order GUARANTEED even with parallel
System.out.println("
forEachOrdered (ordered):");
numbers.parallelStream()
    .forEachOrdered(System.out::println);  // Prints 1,2,3...10

// unordered() - hint that order doesn't matter (may improve performance)
List<Integer> result = numbers.parallelStream()
    .unordered()  // Remove ordering constraint
    .limit(5)     // Can stop earlier without maintaining order
    .collect(Collectors.toList());
```

### 3.6 Parallel Reduction

```java
List<Integer> numbers = IntStream.rangeClosed(1, 100)
    .boxed()
    .collect(Collectors.toList());

// reduce with parallel - need associative operation
int sum = numbers.parallelStream()
    .reduce(0, Integer::sum);  // Associative: (a+b)+c = a+(b+c)
System.out.println(sum);  // 5050

// reduce with three parameters (identity, accumulator, combiner)
int sum2 = numbers.parallelStream()
    .reduce(
        0,                    // Identity
        Integer::sum,         // Accumulator (combines element with result)
        Integer::sum          // Combiner (combines partial results from threads)
    );

// collect with parallel - uses Collector's combiner
Map<Boolean, List<Integer>> partitioned = numbers.parallelStream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
```

---

## 4. Real-World Example: Data Analytics Pipeline

```java
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

class Transaction {
    String id;
    String customerId;
    String product;
    double amount;
    LocalDate date;
    
    public Transaction(String id, String customerId, String product, double amount, LocalDate date) {
        this.id = id;
        this.customerId = customerId;
        this.product = product;
        this.amount = amount;
        this.date = date;
    }
    
    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProduct() { return product; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}

public class DataAnalyticsPipeline {
    public static void main(String[] args) {
        // Sample transactions
        List<Transaction> transactions = Arrays.asList(
            new Transaction("T001", "C001", "Laptop", 1200.00, LocalDate.of(2024, 1, 15)),
            new Transaction("T002", "C002", "Mouse", 25.00, LocalDate.of(2024, 1, 16)),
            new Transaction("T003", "C001", "Keyboard", 75.00, LocalDate.of(2024, 1, 17)),
            new Transaction("T004", "C003", "Monitor", 300.00, LocalDate.of(2024, 1, 18)),
            new Transaction("T005", "C002", "Laptop", 1200.00, LocalDate.of(2024, 1, 19)),
            new Transaction("T006", "C001", "Mouse", 25.00, LocalDate.of(2024, 1, 20)),
            new Transaction("T007", "C004", "Keyboard", 75.00, LocalDate.of(2024, 1, 21)),
            new Transaction("T008", "C003", "Laptop", 1200.00, LocalDate.of(2024, 1, 22)),
            new Transaction("T009", "C004", "Monitor", 300.00, LocalDate.of(2024, 1, 23)),
            new Transaction("T010", "C002", "Mouse", 25.00, LocalDate.of(2024, 1, 24))
        );
        
        // 1. Total revenue by product using groupingBy + summingDouble
        Map<String, Double> revenueByProduct = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getProduct,
                Collectors.summingDouble(Transaction::getAmount)
            ));
        
        System.out.println("=== Revenue by Product ===");
        revenueByProduct.forEach((product, revenue) ->
            System.out.printf("%s: $%.2f%n", product, revenue)
        );
        
        // 2. Top 3 customers by spending using teeing
        class CustomerSpending {
            String customerId;
            double totalSpent;
            long transactionCount;
            
            CustomerSpending(String id, double spent, long count) {
                this.customerId = id;
                this.totalSpent = spent;
                this.transactionCount = count;
            }
        }
        
        Map<String, CustomerSpending> customerStats = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCustomerId,
                Collectors.teeing(
                    Collectors.summingDouble(Transaction::getAmount),
                    Collectors.counting(),
                    (total, count) -> new CustomerSpending("", total, count)
                )
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    CustomerSpending cs = e.getValue();
                    cs.customerId = e.getKey();
                    return cs;
                }
            ));
        
        List<CustomerSpending> top3Customers = customerStats.values().stream()
            .sorted(Comparator.comparingDouble((CustomerSpending cs) -> cs.totalSpent).reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("
=== Top 3 Customers ===");
        top3Customers.forEach(cs ->
            System.out.printf("%s: $%.2f (%d transactions)%n", 
                cs.customerId, cs.totalSpent, cs.transactionCount)
        );
        
        // 3. High-value vs low-value transactions using partitioningBy
        Map<Boolean, List<Transaction>> valuePartition = transactions.stream()
            .collect(Collectors.partitioningBy(t -> t.getAmount() > 100));
        
        System.out.println("
=== Transaction Value Analysis ===");
        System.out.println("High-value (>$100): " + valuePartition.get(true).size());
        System.out.println("Low-value (≤$100): " + valuePartition.get(false).size());
        
        // 4. Product statistics using summarizingDouble
        Map<String, DoubleSummaryStatistics> productStats = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getProduct,
                Collectors.summarizingDouble(Transaction::getAmount)
            ));
        
        System.out.println("
=== Product Statistics ===");
        productStats.forEach((product, stats) -> {
            System.out.printf("%s:%n", product);
            System.out.printf("  Count: %d%n", stats.getCount());
            System.out.printf("  Total: $%.2f%n", stats.getSum());
            System.out.printf("  Average: $%.2f%n", stats.getAverage());
        });
        
        // 5. Find most expensive transaction per customer using Optional
        Map<String, Optional<Transaction>> mostExpensiveByCustomer = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCustomerId,
                Collectors.maxBy(Comparator.comparingDouble(Transaction::getAmount))
            ));
        
        System.out.println("
=== Most Expensive Transaction Per Customer ===");
        mostExpensiveByCustomer.forEach((customerId, optTransaction) ->
            optTransaction.ifPresent(t ->
                System.out.printf("%s: %s ($%.2f)%n", 
                    customerId, t.getProduct(), t.getAmount())
            )
        );
        
        // 6. Parallel processing for large datasets
        long start = System.nanoTime();
        double totalRevenue = transactions.parallelStream()
            .mapToDouble(Transaction::getAmount)
            .sum();
        long end = System.nanoTime();
        
        System.out.printf("
=== Total Revenue (Parallel) ===%n");
        System.out.printf("Total: $%.2f%n", totalRevenue);
        System.out.printf("Time: %.2f ms%n", (end - start) / 1_000_000.0);
    }
}
```

**Why Advanced Streams:** This example demonstrates real-world data analytics combining groupingBy, partitioningBy, teeing, Optional, and parallel processing for comprehensive business insights.

---

## Exam Traps

- ❌ **TRAP 1:** Optional.of() with null throws exception
  ```java
  // ❌ NullPointerException
  Optional<String> opt = Optional.of(null);
  
  // ✅ Use ofNullable for potentially null values
  Optional<String> opt = Optional.ofNullable(possiblyNull);
  ```

- ❌ **TRAP 2:** Optional.get() without checking
  ```java
  Optional<String> opt = Optional.empty();
  // ❌ NoSuchElementException
  String value = opt.get();
  
  // ✅ Check first or use orElse
  if (opt.isPresent()) {
      String value = opt.get();
  }
  // Or better:
  String value = opt.orElse("default");
  ```

- ❌ **TRAP 3:** groupingBy returns Map<K, List<V>> by default
  ```java
  Map<String, List<Employee>> byDept = employees.stream()
      .collect(Collectors.groupingBy(Employee::getDepartment));
  // Returns List<Employee>, not Employee
  
  // To get single employee, use downstream collector
  Map<String, Employee> firstByDept = employees.stream()
      .collect(Collectors.groupingBy(
          Employee::getDepartment,
          Collectors.collectingAndThen(
              Collectors.toList(),
              list -> list.get(0)
          )
      ));
  ```

- ❌ **TRAP 4:** partitioningBy always returns map with both true and false keys
  ```java
  Map<Boolean, List<Integer>> partition = Stream.of(1, 2, 3)
      .collect(Collectors.partitioningBy(n -> n > 5));
  
  System.out.println(partition.get(false));  // [1, 2, 3]
  System.out.println(partition.get(true));   // [] (empty list, NOT null)
  ```

- ❌ **TRAP 5:** Parallel stream with shared mutable state
  ```java
  List<Integer> result = new ArrayList<>();
  IntStream.range(1, 100).parallel()
      .forEach(result::add);  // ❌ Race condition
  // result.size() might be < 99
  
  // ✅ Use collect
  List<Integer> result = IntStream.range(1, 100).parallel()
      .boxed()
      .collect(Collectors.toList());
  ```

- ❌ **TRAP 6:** orElse() vs orElseGet() - orElse always executes
  ```java
  String expensive = computeExpensiveDefault();  // Always called
  String value = opt.orElse(expensive);
  
  // ✅ Use orElseGet for expensive operations
  String value = opt.orElseGet(() -> computeExpensiveDefault());
  // Only called if opt is empty
  ```

- ❌ **TRAP 7:** map() vs flatMap() with Optional
  ```java
  Optional<String> name = Optional.of("Alice");
  
  // Wrong - returns Optional<Optional<String>>
  Optional<Optional<String>> nested = name.map(n -> Optional.of(n.toUpperCase()));
  
  // ✅ Use flatMap
  Optional<String> upper = name.flatMap(n -> Optional.of(n.toUpperCase()));
  ```

- ❌ **TRAP 8:** Parallel stream not always faster
  ```java
  // Small dataset - parallel overhead exceeds benefit
  List<Integer> small = Arrays.asList(1, 2, 3);
  int sum = small.parallelStream().mapToInt(i -> i).sum();
  // Sequential would be faster
  ```

- ❌ **TRAP 9:** Collectors.toMap() requires merge function for duplicates
  ```java
  List<Employee> emps = Arrays.asList(
      new Employee("Alice", "IT", 80000),
      new Employee("Bob", "IT", 90000)
  );
  
  // ❌ IllegalStateException - duplicate key "IT"
  Map<String, Employee> map = emps.stream()
      .collect(Collectors.toMap(Employee::getDepartment, e -> e));
  
  // ✅ Provide merge function
  Map<String, Employee> map = emps.stream()
      .collect(Collectors.toMap(
          Employee::getDepartment,
          e -> e,
          (e1, e2) -> e1  // Keep first
      ));
  ```

- ❌ **TRAP 10:** forEach order with parallel streams
  ```java
  List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
  
  // Unpredictable order
  list.parallelStream().forEach(System.out::println);
  
  // ✅ Use forEachOrdered for guaranteed order
  list.parallelStream().forEachOrdered(System.out::println);
  ```

---

## Exam Questions

### Question 1: Optional creation
```java
String str = null;
Optional<String> opt = Optional.of(str);
System.out.println(opt.isPresent());
```
**What is the result?**  
A. true  
B. false  
C. NullPointerException  
D. Compilation error  

**Answer:** C  
**Explanation:** Optional.of(null) throws NullPointerException. Use Optional.ofNullable(str) for potentially null values.

---

### Question 2: groupingBy return type
```java
List<String> words = Arrays.asList("apple", "banana", "apricot");
Map<Character, ?> map = words.stream()
    .collect(Collectors.groupingBy(s -> s.charAt(0)));
```
**What is the type of map values?**  
A. String  
B. List<String>  
C. Set<String>  
D. Optional<String>  

**Answer:** B  
**Explanation:** groupingBy returns Map<K, List<V>> by default (groups into Lists).

---

### Question 3: partitioningBy keys
```java
List<Integer> nums = Arrays.asList(1, 2, 3);
Map<Boolean, List<Integer>> map = nums.stream()
    .collect(Collectors.partitioningBy(n -> n > 10));
System.out.println(map.size());
```
**What is the output?**  
A. 0  
B. 1  
C. 2  
D. 3  

**Answer:** C  
**Explanation:** partitioningBy always creates map with 2 keys (true and false), even if one group is empty.

---

### Question 4: Optional.get() safety
```java
Optional<String> opt = Optional.empty();
String value = opt.orElse("default");
System.out.println(value);
```
**What is the output?**  
A. empty  
B. null  
C. default  
D. NoSuchElementException  

**Answer:** C  
**Explanation:** orElse returns the default value when Optional is empty.

---

### Question 5: Parallel stream safety
```java
List<Integer> list = new ArrayList<>();
IntStream.range(1, 5).parallel()
    .forEach(list::add);
System.out.println(list.size());
```
**What is guaranteed about the output?**  
A. Always 4  
B. Always 0  
C. Might be less than 4  
D. Compilation error  

**Answer:** C  
**Explanation:** ArrayList is not thread-safe. With parallel forEach, race conditions can cause elements to be lost.

---

### Question 6: orElse vs orElseGet
```java
Optional<String> opt = Optional.of("Hello");
String s1 = opt.orElse(expensiveMethod());
String s2 = opt.orElseGet(() -> expensiveMethod());
```
**Which statements are true?**  
A. expensiveMethod() called twice  
B. expensiveMethod() called once  
C. expensiveMethod() never called  
D. Only called for s2  

**Answer:** B  
**Explanation:** orElse() always evaluates its argument, orElseGet() only calls supplier if empty. Here, opt is present, so expensiveMethod() called once (for s1 only).

---

### Question 7: Collectors.mapping
```java
List<String> words = Arrays.asList("a", "bb", "ccc");
Map<Integer, List<Character>> map = words.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.mapping(s -> s.charAt(0), Collectors.toList())
    ));
System.out.println(map.get(2));
```
**What is the output?**  
A. [b]  
B. [b, b]  
C. [bb]  
D. Compilation error  

**Answer:** A  
**Explanation:** Groups by length (2), then maps each string to its first character. "bb" becomes 'b', resulting in [b].

---

### Question 8: Optional flatMap
```java
Optional<String> opt = Optional.of("hello");
Optional<String> result = opt.flatMap(s -> Optional.of(s.toUpperCase()));
System.out.println(result.get());
```
**What is the output?**  
A. hello  
B. HELLO  
C. Optional[HELLO]  
D. Compilation error  

**Answer:** B  
**Explanation:** flatMap applies the function and flattens the result. get() retrieves "HELLO".

---

### Question 9: Parallel reduce
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 4);
int result = nums.parallelStream()
    .reduce(1, (a, b) -> a * b);
System.out.println(result);
```
**What is the output?**  
A. 10  
B. 24  
C. Greater than 24  
D. Unpredictable  

**Answer:** C  
**Explanation:** With identity 1 and parallel processing, each thread starts with 1, multiplying it with elements, then results are combined. Result will be > 24 due to multiple identity values.

---

### Question 10: Collectors.teeing
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
String result = nums.stream()
    .collect(Collectors.teeing(
        Collectors.counting(),
        Collectors.summingInt(Integer::intValue),
        (count, sum) -> count + ":" + sum
    ));
System.out.println(result);
```
**What is the output?**  
A. 5:15  
B. 15:5  
C. 5,15  
D. Compilation error  

**Answer:** A  
**Explanation:** teeing applies two collectors (counting=5, summingInt=15) and merges results with the function, producing "5:15".

---

**[← Part 2: Stream API Fundamentals](CHAPTER-06-PART2-STREAMS.md) | [Back to Main Guide](README.md)**
