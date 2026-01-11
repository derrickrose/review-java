# Chapter 6: Lambda Expressions and Stream API - COMPLETE

## Chapter Overview

This chapter provides comprehensive coverage of Lambda Expressions, Functional Interfaces, Stream API, Advanced Collectors, Optional, and Parallel Streams for the Java SE 21 Certification (1Z0-830).

**Chapter Structure:**
- **Part 1:** Lambda Expressions & Functional Interfaces (1,536 lines)
- **Part 2:** Stream API Fundamentals (1,425 lines)
- **Part 3:** Advanced Collectors, Optional & Parallel Streams (1,239 lines)

**Total:** 4,200 lines | 132 code blocks | 30 exam questions | 34 exam traps

---

## Part 1: Lambda Expressions & Functional Interfaces

[📥 Download Part 1](CHAPTER-06-PART1-LAMBDAS-FUNCTIONAL.md)

### Topics Covered

**1. Lambda Expressions**
- 8 syntax variations (no params, single param, multiple params, blocks)
- Type inference and limitations
- Variable capture and effectively final
- Lambda vs anonymous class (this reference)
- When to use decision table

**2. Functional Interfaces (All 9)**
- Custom functional interfaces with @FunctionalInterface
- **Predicate<T>** - test(T) → boolean, and(), or(), negate()
- **Function<T,R>** - apply(T) → R, andThen(), compose(), identity()
- **Consumer<T>** - accept(T) → void, andThen()
- **Supplier<T>** - get() → T, lazy evaluation
- **UnaryOperator<T>** - apply(T) → T (extends Function<T,T>)
- **BinaryOperator<T>** - apply(T,T) → T, minBy(), maxBy()
- **BiPredicate<T,U>** - test(T,U) → boolean
- **BiFunction<T,U,R>** - apply(T,U) → R
- **BiConsumer<T,U>** - accept(T,U) → void
- Primitive specializations (IntPredicate, IntFunction, etc.)

**3. Method References (All 4 Types)**
- Static method reference: `ClassName::staticMethod`
- Bound instance reference: `instance::method`
- Unbound instance reference: `ClassName::instanceMethod`
- Constructor reference: `ClassName::new`

**4. Real-World Examples**
- Validation system with Predicate composition
- Data processor with Function pipelines
- Event system with Consumer chaining
- Configuration factory with Supplier lazy loading

**Statistics:** 1,536 lines | 46 code blocks | 10 questions | 12 traps

---

## Part 2: Stream API Fundamentals

[📥 Download Part 2](CHAPTER-06-PART2-STREAMS.md)

### Topics Covered

**1. Introduction to Stream API**
- What is Stream API
- Stream vs Collection comparison table
- Why use Streams (benefits)
- Lazy evaluation explained

**2. Creating Streams (10+ Methods)**
- Collection.stream() / parallelStream()
- Arrays.stream() / Stream.of()
- Stream.builder() / Stream.iterate() / Stream.generate()
- IntStream.range() / rangeClosed()
- Primitive streams (IntStream, LongStream, DoubleStream)
- IntSummaryStatistics and boxed()

**3. Intermediate Operations (All 8)**
- **filter(Predicate)** - filtering elements
- **map(Function)** - transforming elements
- **flatMap(Function)** - flattening nested structures
- **distinct()** - removing duplicates
- **sorted()** - natural and custom sorting
- **limit(long)** - truncating stream
- **skip(long)** - skipping elements
- **peek(Consumer)** - debugging/side effects

**4. Terminal Operations (All 8 Types)**
- **forEach()** - iteration
- **collect(Collector)** - collecting to collections
- **reduce()** - reduction operations (3 signatures)
- **count()** - counting elements
- **min() / max()** - finding extremes
- **findFirst() / findAny()** - finding elements
- **anyMatch() / allMatch() / noneMatch()** - matching
- **toArray()** - converting to array

**5. Stream Pipeline Execution**
- Lazy evaluation mechanics
- Short-circuiting operations
- Operation order optimization

**6. Real-World Examples**
- E-commerce order processing
- Student grade analysis
- Log file processing

**Statistics:** 1,425 lines | 47 code blocks | 10 questions | 12 traps

---

## Part 3: Advanced Collectors, Optional & Parallel Streams

[📥 Download Part 3](CHAPTER-06-PART3-COLLECTORS-OPTIONAL.md)

### Topics Covered

**1. Advanced Collectors**
- **groupingBy()** - grouping with downstream collectors
- **partitioningBy()** - binary partitioning
- Downstream collectors: counting(), summingInt(), averagingInt(), mapping(), filtering(), maxBy(), minBy(), reducing(), collectingAndThen()
- **summarizingInt/Long/Double()** - statistics collection
- **teeing()** - combining two collectors (Java 12+)

**2. Optional API (Complete Coverage)**
- Creating Optional: of(), ofNullable(), empty()
- Checking presence: isPresent(), isEmpty(), ifPresent(), ifPresentOrElse()
- Retrieving values: get(), orElse(), orElseGet(), orElseThrow()
- Transforming: map(), flatMap()
- Filtering: filter()
- Combining: or(), stream()
- Primitive Optionals: OptionalInt, OptionalLong, OptionalDouble

**3. Parallel Streams**
- Creating parallel streams
- Parallel stream execution (ForkJoinPool)
- Performance considerations
- Thread safety issues
- Order preservation (forEach vs forEachOrdered)
- Parallel reduction

**4. Real-World Example**
- Data analytics pipeline with transactions
- Multi-level grouping and partitioning
- teeing for customer statistics
- Optional for safe value retrieval
- Parallel processing for large datasets

**Statistics:** 1,239 lines | 39 code blocks | 10 questions | 10 traps

---

## Complete Chapter Statistics

| Metric | Part 1 | Part 2 | Part 3 | **Total** |
|--------|--------|--------|--------|-----------|
| **Lines** | 1,536 | 1,425 | 1,239 | **4,200** |
| **Code Blocks** | 46 | 47 | 39 | **132** |
| **Exam Questions** | 10 | 10 | 10 | **30** |
| **Exam Traps** | 12 | 12 | 10 | **34** |
| **Real-World Examples** | 4 | 3 | 1 | **8** |

---

## Key Exam Topics Coverage

### ✅ Lambda Expressions
- All syntax variations documented
- Variable capture rules explained
- this reference behavior clarified
- Common pitfalls identified

### ✅ Functional Interfaces
- All 9 built-in interfaces with full method documentation
- Primitive specializations covered
- Method chaining demonstrated
- Practical usage scenarios provided

### ✅ Method References
- All 4 types explained with examples
- Conversion rules documented
- When to use guidance provided

### ✅ Stream API
- 10+ creation methods
- All 8 intermediate operations
- All 8 terminal operation types
- Lazy evaluation mechanics
- Short-circuiting behavior

### ✅ Advanced Collectors
- groupingBy with all variations
- partitioningBy for binary splits
- All downstream collectors
- Statistics collectors
- teeing for dual collection

### ✅ Optional
- Complete API coverage
- All creation methods
- All retrieval methods
- Transformation and filtering
- Combining techniques
- Primitive variants

### ✅ Parallel Streams
- Creation and execution
- Performance considerations
- Thread safety
- Order preservation
- Reduction strategies

---

## Exam Preparation Summary

### Critical Traps to Remember

**Lambda & Functional Interfaces:**
1. Effectively final requirement for captured variables
2. this reference in lambda vs anonymous class
3. Type inference all-or-nothing rule
4. @FunctionalInterface validation

**Stream API:**
5. Streams are one-use only (IllegalStateException)
6. Intermediate operations are lazy
7. map() vs flatMap() for nested structures
8. Modifying source during stream causes ConcurrentModificationException

**Advanced Features:**
9. Optional.of(null) throws NullPointerException
10. Optional.get() without checking throws NoSuchElementException
11. groupingBy returns List<V> by default
12. partitioningBy always creates 2 keys (true/false)
13. Parallel streams with shared mutable state = race conditions
14. orElse() always executes vs orElseGet() lazy
15. Parallel not always faster (overhead for small datasets)

### Common Exam Question Patterns

**Pattern 1: Lambda Syntax**
- Parentheses rules (required with types, optional for single param)
- Block body requires explicit return
- Can't shadow local variables

**Pattern 2: Functional Interface Matching**
- Match method signature to functional interface
- Count parameters and return type
- Consider primitive specializations

**Pattern 3: Stream Pipeline Results**
- Trace lazy evaluation
- Identify short-circuiting
- Count elements after filter/limit/skip

**Pattern 4: Optional Safety**
- Identify NullPointerException risks
- Choose safe retrieval methods
- Recognize map vs flatMap needs

**Pattern 5: Collector Selection**
- Choose appropriate collector for task
- Identify downstream collector needs
- Recognize merge function requirements

**Pattern 6: Parallel Concerns**
- Identify thread safety issues
- Recognize when sequential is better
- Spot incorrect reduce identity values

---

## Official Exam Objectives Covered

From Oracle's 1Z0-830 exam objectives:

✅ **Use Java object-oriented approach**
- Utilize lambda expressions and method references

✅ **Use Java functional programming features**
- Use built-in functional interfaces from java.util.function package
- Create and use lambda expressions including statement lambdas
- Use method references with lambdas
- Implement functional interfaces using lambda expressions

✅ **Use Java Streams API**
- Use Stream API to implement functional programming
- Use lambda expressions and method references to write functional code
- Create streams from collections
- Use intermediate and terminal operations
- Implement parallel streams
- Use the Optional class
- Use Collectors class to create collections from streams

---

## Download All Parts

1. [📥 Part 1: Lambda Expressions & Functional Interfaces](CHAPTER-06-PART1-LAMBDAS-FUNCTIONAL.md)
2. [📥 Part 2: Stream API Fundamentals](CHAPTER-06-PART2-STREAMS.md)
3. [📥 Part 3: Advanced Collectors, Optional & Parallel Streams](CHAPTER-06-PART3-COLLECTORS-OPTIONAL.md)

---

## Study Recommendations

**Week 1: Foundations**
- Master lambda syntax variations
- Memorize all 9 functional interfaces
- Practice method references

**Week 2: Stream Operations**
- Learn all intermediate operations
- Master terminal operations
- Understand lazy evaluation

**Week 3: Advanced Topics**
- Practice complex collectors
- Master Optional API
- Understand parallel streams

**Week 4: Exam Preparation**
- Review all 34 exam traps
- Practice all 30 exam questions
- Study 8 real-world examples

**Practice Strategy:**
1. Write code for each example
2. Predict output before running
3. Explain why traps fail
4. Create variations of examples

---

**[Back to Main Guide](README.md)**

---

*Chapter 6 - Lambda Expressions and Stream API - Complete*  
*Java SE 21 Certification Study Guide*  
*Total: 4,200 lines | 132 examples | 30 questions | 34 traps*
