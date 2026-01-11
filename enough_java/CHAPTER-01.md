# Chapter 1: Handling Date, Time, Text, Numeric and Boolean Values

[← Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-02.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Handling Date, Time, Text, Numeric and Boolean Values |
| **Java Versions** | 1.0, 5, 8, 11, 15 |
| **Code Examples** | 40+ |
| **Study Time** | 10-12 hours |
| **Exam Weight** | ~10% |
| **Difficulty** | ⭐⭐ Medium |

**Official 1Z0-830 Exam Objectives Covered:**
- Use primitives and wrapper classes
- Evaluate arithmetic and boolean expressions using the Math API
- Apply precedence rules, type conversions, and casting
- Manipulate text, including text blocks, using String and StringBuilder classes
- Manipulate date, time, duration, period, instant and time-zone objects including daylight saving time using Date-Time API

---

## 1. Primitives and Wrapper Classes

**Exam Objective:** Use primitives and wrapper classes

### What are Primitives and Wrappers?
Primitives are basic data types (byte, short, int, long, float, double, char, boolean). Wrappers are object versions of primitives (Integer, Double, etc.) that allow primitives to be used as objects.

### When to Use
- **Primitives:** Performance-critical code, local variables, simple calculations
- **Wrappers:** Collections (List<Integer>), nullability needed, object methods required

### Why Use Wrappers
- Collections require objects (can't use primitive in List)
- Provide utility methods (Integer.parseInt(), Double.valueOf())
- Allow null values (primitives can't be null)

### Real-World Example
```java
// Primitives for calculations
public class Calculator {
    public double calculateTax(double amount, double rate) {
        return amount * rate; // Fast primitive math
    }
}

// Wrappers for database/API (can be null)
public class Order {
    private Integer customerId; // Can be null if guest checkout
    private Double discount;    // Can be null if no discount
    
    public boolean hasDiscount() {
        return discount != null && discount > 0;
    }
}
```
**Why:** E-commerce - primitives for calculations, wrappers for optional database fields.

### The 8 Primitive Types (MEMORIZE!)
| Type | Size | Range | Default | Wrapper |
|------|------|-------|---------|---------|
| byte | 8 bits | -128 to 127 | 0 | Byte |
| short | 16 bits | -32,768 to 32,767 | 0 | Short |
| int | 32 bits | -2³¹ to 2³¹-1 | 0 | Integer |
| long | 64 bits | -2⁶³ to 2⁶³-1 | 0L | Long |
| float | 32 bits | ~±3.4E38 | 0.0f | Float |
| double | 64 bits | ~±1.7E308 | 0.0d | Double |
| char | 16 bits | 0 to 65,535 | '\u0000' | Character |
| boolean | - | true/false | false | Boolean |

### Autoboxing and Unboxing (Java 5)
```java
// Autoboxing - primitive to wrapper
int primitive = 10;
Integer wrapper = primitive; // Automatic conversion

// Unboxing - wrapper to primitive
Integer wrapperObj = 20;
int primitiveVal = wrapperObj; // Automatic conversion

// In collections
List<Integer> numbers = new ArrayList<>();
numbers.add(5); // Autoboxing: 5 → Integer(5)
int value = numbers.get(0); // Unboxing: Integer(5) → 5
```

### Exam Traps
- ❌ **TRAP:** Integer caching (-128 to 127)
  ```java
  Integer a = 127;
  Integer b = 127;
  System.out.println(a == b); // true (cached)
  
  Integer c = 128;
  Integer d = 128;
  System.out.println(c == d); // false (not cached, different objects!)
  ```
- ❌ **TRAP:** NullPointerException on unboxing
  ```java
  Integer wrapper = null;
  int primitive = wrapper; // ❌ NullPointerException!
  ```
- ❌ **TRAP:** Comparing wrappers with ==
  ```java
  Integer x = 1000;
  Integer y = 1000;
  System.out.println(x == y);       // false (different objects)
  System.out.println(x.equals(y));  // true (use equals!)
  ```

---

## 2. Math API and Arithmetic

**Exam Objective:** Evaluate arithmetic and boolean expressions using the Math API and apply precedence rules, type conversions, and casting

### What is Math API?
Static utility class providing mathematical operations: abs(), min(), max(), pow(), sqrt(), round(), ceil(), floor(), random().

### When to Use
- Complex calculations (power, square root)
- Rounding operations
- Finding min/max values
- Random number generation

### Why Use Math API
- Pre-tested methods (avoid bugs)
- Performance optimized
- Standardized behavior

### Real-World Example
```java
// E-commerce discount calculation
public class PriceCalculator {
    public double calculateFinalPrice(double basePrice, double discountPercent) {
        double discount = basePrice * (discountPercent / 100);
        double discounted = basePrice - discount;
        return Math.round(discounted * 100.0) / 100.0; // Round to 2 decimals
    }
    
    public double calculateShipping(double weight) {
        double baseCost = 5.0;
        double perKg = 2.5;
        return baseCost + (Math.ceil(weight) * perKg); // Round up weight
    }
}
```
**Why:** Financial calculations require precise rounding to avoid cent discrepancies.

### Operator Precedence (MEMORIZE!)
```java
// From highest to lowest priority
int result = 2 + 3 * 4;  // 14, not 20 (* before +)

// Precedence: () → * / % → + -
int x = (2 + 3) * 4;     // 20 (parentheses first)
int y = 10 / 2 * 3;      // 15 (left to right for same priority)
```

### Type Promotion
```java
byte b = 10;
short s = 20;
int i = b + s;  // byte/short promoted to int in arithmetic

float f = 5.5f;
double d = f + 10; // float promoted to double
```

### Exam Traps
- ❌ **TRAP:** Integer division truncates
  ```java
  int result = 5 / 2;      // 2, not 2.5!
  double result = 5 / 2;   // 2.0, not 2.5!
  double result = 5.0 / 2; // 2.5 ✅
  ```
- ❌ **TRAP:** Overflow silently wraps
  ```java
  int max = Integer.MAX_VALUE;
  int overflow = max + 1; // -2147483648 (wraps to MIN_VALUE!)
  ```
- ❌ **TRAP:** Modulo with negatives
  ```java
  System.out.println(-5 % 3);  // -2, not 1!
  System.out.println(5 % -3);  // 2
  ```

---

## 3. String and StringBuilder

**Exam Objective:** Manipulate text, including text blocks, using String and StringBuilder classes

### What are String and StringBuilder?
String is immutable character sequence. StringBuilder is mutable, efficient for building strings in loops.

### When to Use
- **String:** Read-only text, small concatenations, literals
- **StringBuilder:** Loops, heavy concatenation, building complex strings

### Why Use StringBuilder
- Performance (String creates new object each concatenation)
- Memory efficient in loops
- Faster for complex string building

### Real-World Example
```java
// BAD - String in loop (creates 1000 objects!)
public String buildReport(List<Order> orders) {
    String report = "";
    for (Order order : orders) {
        report += order.toString() + "\n"; // New String object each iteration!
    }
    return report;
}

// GOOD - StringBuilder (one object, mutable)
public String buildReport(List<Order> orders) {
    StringBuilder report = new StringBuilder();
    for (Order order : orders) {
        report.append(order.toString()).append("\n");
    }
    return report.toString();
}
```
**Why:** Report generation with 1000+ records - StringBuilder 100x faster.

### String Immutability
```java
String s1 = "Java";
s1.concat(" 21");  // Creates new String, doesn't modify s1
System.out.println(s1); // "Java" (unchanged!)

String s2 = s1.concat(" 21"); // Must capture return value
System.out.println(s2); // "Java 21"
```

### String Pool
```java
String s1 = "hello";        // String pool
String s2 = "hello";        // Reuses from pool
String s3 = new String("hello"); // Heap (new object)

System.out.println(s1 == s2);    // true (same pool reference)
System.out.println(s1 == s3);    // false (different objects)
System.out.println(s1.equals(s3)); // true (same content)
```

### Text Blocks (Java 15)
```java
// Old way - messy
String html = "<html>\n" +
              "  <body>\n" +
              "    <h1>Hello</h1>\n" +
              "  </body>\n" +
              "</html>";

// New way - clean
String html = """
              <html>
                <body>
                  <h1>Hello</h1>
                </body>
              </html>
              """;
```

### Common String Methods
```java
String s = "Java Programming";

// Length and character access
s.length();          // 16
s.charAt(0);         // 'J'
s.indexOf("Pro");    // 5

// Substring
s.substring(5);      // "Programming"
s.substring(0, 4);   // "Java" (end index exclusive!)

// Case
s.toLowerCase();     // "java programming"
s.toUpperCase();     // "JAVA PROGRAMMING"

// Search
s.contains("Pro");   // true
s.startsWith("Ja");  // true
s.endsWith("ing");   // true

// Replace
s.replace("Java", "Python"); // "Python Programming"

// Split
"a,b,c".split(",");  // ["a", "b", "c"]

// Trim and strip (Java 11)
"  text  ".trim();   // "text"
"  text  ".strip();  // "text" (handles Unicode whitespace)
```

### StringBuilder Methods
```java
StringBuilder sb = new StringBuilder();

// Append
sb.append("Hello");
sb.append(" ").append("World"); // Method chaining
// sb = "Hello World"

// Insert
sb.insert(6, "Beautiful "); // "Hello Beautiful World"

// Delete
sb.delete(6, 16);  // "Hello World" (end index exclusive)

// Replace
sb.replace(0, 5, "Hi"); // "Hi World"

// Reverse
sb.reverse(); // "dlroW iH"

// Convert to String
String result = sb.toString();
```

### Exam Traps
- ❌ **TRAP:** String methods don't modify original
  ```java
  String s = "hello";
  s.toUpperCase();
  System.out.println(s); // "hello" (not modified!)
  ```
- ❌ **TRAP:** substring() end index is exclusive
  ```java
  String s = "Programming";
  s.substring(0, 4);  // "Prog" (chars 0,1,2,3)
  ```
- ❌ **TRAP:** String concatenation with null
  ```java
  String s = "Hello" + null; // "Hellonull" (not exception!)
  ```
- ❌ **TRAP:** equals() vs ==
  ```java
  String s1 = new String("test");
  String s2 = new String("test");
  s1 == s2;       // false (different objects)
  s1.equals(s2);  // true (same content)
  ```

---

## 4. Date-Time API (Java 8)

**Exam Objective:** Manipulate date, time, duration, period, instant and time-zone objects including daylight saving time using Date-Time API

### What is Date-Time API?
Modern date/time library (Java 8+) replacing old java.util.Date. Immutable, thread-safe classes: LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant, Period, Duration.

### When to Use Each Type
| Type | Use Case | Example |
|------|----------|---------|
| LocalDate | Date only (no time) | Birthdate, due date |
| LocalTime | Time only (no date) | Opening hours |
| LocalDateTime | Date + Time (no timezone) | Appointment time |
| ZonedDateTime | Date + Time + Timezone | International meeting |
| Instant | Timestamp (UTC) | Log timestamp |
| Period | Date-based duration | 3 months, 2 years |
| Duration | Time-based duration | 5 hours, 30 seconds |

### Why Use Date-Time API
- Immutable (thread-safe)
- Clear, readable code
- Proper timezone handling
- ISO-8601 standard

### Real-World Example: Event Management
```java
public class EventManager {
    public void scheduleEvent() {
        // Local event (no timezone)
        LocalDateTime meeting = LocalDateTime.of(2025, 6, 15, 14, 30);
        
        // International event (with timezone)
        ZonedDateTime webinar = ZonedDateTime.of(
            2025, 6, 15, 14, 0, 0, 0,
            ZoneId.of("America/New_York")
        );
        
        // Convert to another timezone
        ZonedDateTime tokyo = webinar.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        
        // Calculate time until event
        Duration timeUntil = Duration.between(LocalDateTime.now(), meeting);
        System.out.println("Event in " + timeUntil.toHours() + " hours");
    }
    
    public void calculateSubscription(LocalDate start) {
        LocalDate end = start.plusMonths(12); // 1 year subscription
        Period period = Period.between(start, end);
        System.out.println("Subscription: " + period.getMonths() + " months");
    }
}
```
**Why:** SaaS subscription - handle timezones, calculate billing periods, schedule renewals.

### LocalDate (Date Only)
```java
// Creation
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(1990, 5, 15);
LocalDate parsed = LocalDate.parse("2025-12-25");

// Manipulation
LocalDate tomorrow = today.plusDays(1);
LocalDate nextMonth = today.plusMonths(1);
LocalDate lastYear = today.minusYears(1);

// Comparison
boolean isAfter = date1.isAfter(date2);
boolean isBefore = date1.isBefore(date2);

// Extraction
int year = today.getYear();
int month = today.getMonthValue(); // 1-12
int day = today.getDayOfMonth();
DayOfWeek dow = today.getDayOfWeek(); // MONDAY, TUESDAY, etc.
```

### LocalTime (Time Only)
```java
// Creation
LocalTime now = LocalTime.now();
LocalTime lunch = LocalTime.of(12, 30);
LocalTime precise = LocalTime.of(14, 30, 45, 123456789); // hour, min, sec, nano

// Manipulation
LocalTime later = now.plusHours(2);
LocalTime earlier = now.minusMinutes(30);

// Extraction
int hour = now.getHour();
int minute = now.getMinute();
```

### LocalDateTime (Date + Time, No Timezone)
```java
// Creation
LocalDateTime now = LocalDateTime.now();
LocalDateTime dt = LocalDateTime.of(2025, 6, 15, 14, 30);
LocalDateTime combined = LocalDate.now().atTime(LocalTime.of(10, 0));

// Manipulation
LocalDateTime future = now.plusDays(7).plusHours(3);

// Convert to date or time
LocalDate date = dt.toLocalDate();
LocalTime time = dt.toLocalTime();
```

### Period (Date-based Duration)
```java
// Creation
Period month = Period.ofMonths(1);
Period week = Period.ofWeeks(2);
Period custom = Period.of(1, 6, 15); // 1 year, 6 months, 15 days

// Calculate between dates
LocalDate start = LocalDate.of(2024, 1, 1);
LocalDate end = LocalDate.of(2025, 6, 15);
Period between = Period.between(start, end);
System.out.println(between.getYears() + " years, " + between.getMonths() + " months");

// Add to date
LocalDate future = start.plus(Period.ofMonths(3));
```

### Duration (Time-based Duration)
```java
// Creation
Duration hour = Duration.ofHours(1);
Duration minutes = Duration.ofMinutes(90);
Duration seconds = Duration.ofSeconds(3600);

// Calculate between times
LocalTime start = LocalTime.of(9, 0);
LocalTime end = LocalTime.of(17, 30);
Duration workDay = Duration.between(start, end);
System.out.println(workDay.toHours() + " hours");

// Add to time
LocalTime later = start.plus(Duration.ofMinutes(45));
```

### ZonedDateTime (With Timezone)
```java
// Creation
ZonedDateTime now = ZonedDateTime.now();
ZonedDateTime ny = ZonedDateTime.now(ZoneId.of("America/New_York"));
ZonedDateTime specific = ZonedDateTime.of(
    LocalDateTime.of(2025, 6, 15, 14, 0),
    ZoneId.of("Europe/Paris")
);

// Convert timezone (same instant, different time)
ZonedDateTime tokyo = ny.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));

// Daylight Saving Time handled automatically!
```

### Formatting and Parsing
```java
// Formatting
LocalDate date = LocalDate.of(2025, 6, 15);
String formatted = date.format(DateTimeFormatter.ISO_DATE); // "2025-06-15"

DateTimeFormatter custom = DateTimeFormatter.ofPattern("dd/MM/yyyy");
String customFormat = date.format(custom); // "15/06/2025"

// Parsing
LocalDate parsed = LocalDate.parse("2025-06-15");
LocalDate customParsed = LocalDate.parse("15/06/2025", custom);
```

### Exam Traps
- ❌ **TRAP:** Date-Time objects are immutable
  ```java
  LocalDate date = LocalDate.of(2025, 1, 1);
  date.plusDays(10); // Doesn't modify date!
  System.out.println(date); // Still 2025-01-01
  
  date = date.plusDays(10); // ✅ Must reassign
  ```
- ❌ **TRAP:** Period vs Duration
  ```java
  Period p = Period.ofDays(1);
  LocalTime time = LocalTime.now().plus(p); // ❌ Compilation error!
  // Period works with dates, not times
  
  Duration d = Duration.ofHours(24);
  LocalDate date = LocalDate.now().plus(d); // ❌ Compilation error!
  // Duration works with times, not dates
  ```
- ❌ **TRAP:** Months are 1-based (not 0-based like old Date)
  ```java
  LocalDate.of(2025, 1, 15);  // January (not February!)
  ```
- ❌ **TRAP:** ChronoUnit for between()
  ```java
  LocalDate d1 = LocalDate.of(2025, 1, 1);
  LocalDate d2 = LocalDate.of(2025, 12, 31);
  long days = ChronoUnit.DAYS.between(d1, d2); // 364 days
  ```

---

## Real Exam Questions (From Shared Sources)

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes** (2.4 minutes per question)
- **Passing score:** 68% (34 correct answers minimum)
- **Question types:** Multiple choice (5-8 options), Multi-select
- **Difficulty:** Medium for this chapter
- **Style:** Code scenarios, type conversions, date calculations

**Based on actual test-taker experiences**

---

### Question 1: Integer Caching
```java
Integer a = 127;
Integer b = 127;
Integer c = 128;
Integer d = 128;

System.out.println(a == b);
System.out.println(c == d);
```
**Output?**
A. true<br>true
B. true<br>false
C. false<br>true
D. false<br>false

**Answer:** B
**Explanation:** Integer caches -128 to 127. a and b reference same cached object (true). c and d are different objects (false).

---

### Question 2: Integer Division
```java
int x = 5;
int y = 2;
double result = x / y;
System.out.println(result);
```
**Output?**
A. 2.5
B. 2.0
C. 3.0
D. Compilation error

**Answer:** B
**Explanation:** Integer division 5/2 = 2 (truncates). Then 2 promoted to 2.0 for double variable.

---

### Question 3: String Immutability
```java
String s = "Java";
s.concat(" 21");
s.toUpperCase();
System.out.println(s);
```
**Output?**
A. JAVA 21
B. Java 21
C. JAVA
D. Java

**Answer:** D
**Explanation:** String methods return new String, don't modify original. s remains "Java".

---

### Question 4: StringBuilder
```java
StringBuilder sb = new StringBuilder("Java");
sb.append(" 21");
sb.insert(4, " SE");
System.out.println(sb);
```
**Output?**
A. Java 21 SE
B. Java SE 21
C. Java SE
D. Compilation error

**Answer:** B
**Explanation:** append("21") → "Java 21", insert(4, " SE") inserts at index 4 → "Java SE 21"

---

### Question 5: substring() Trap
```java
String s = "Programming";
System.out.println(s.substring(0, 4));
System.out.println(s.substring(4));
```
**Output?**
A. Prog<br>ramming
B. Progr<br>amming
C. Prog<br>gramming
D. Progr<br>gramming

**Answer:** C
**Explanation:** substring(0,4) gets chars 0-3 = "Prog". substring(4) gets from index 4 to end = "ramming"

---

### Question 6: LocalDate Manipulation
```java
LocalDate date = LocalDate.of(2025, 1, 31);
date.plusMonths(1);
System.out.println(date);
```
**Output?**
A. 2025-02-28
B. 2025-01-31
C. 2025-02-31
D. Exception

**Answer:** B
**Explanation:** Date-Time objects are immutable! plusMonths() returns new object but result not assigned. date unchanged.

---

### Question 7: Period vs Duration
```java
LocalDate date = LocalDate.now();
Duration d = Duration.ofDays(1);
date = date.plus(d);
```
**Result?**
A. Compiles, adds 1 day
B. Compilation error
C. Runtime error
D. Adds 24 hours

**Answer:** B
**Explanation:** Duration (time-based) can't be added to LocalDate (date-only). Use Period.ofDays(1) instead.

---

### Question 8: String Pool
```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");
String s4 = s3.intern();

System.out.println(s1 == s2);
System.out.println(s1 == s3);
System.out.println(s1 == s4);
```
**Output?**
A. true<br>true<br>true
B. true<br>false<br>true
C. true<br>false<br>false
D. false<br>false<br>false

**Answer:** B
**Explanation:** s1 and s2 point to pool (true). s3 is new object (false). s4.intern() returns pool reference (true).

---

### Question 9: Math Operations
```java
System.out.println(10 / 3);
System.out.println(10 % 3);
System.out.println(-10 % 3);
```
**Output?**
A. 3<br>1<br>-1
B. 3<br>1<br>1
C. 3.33<br>1<br>-1
D. 3<br>1<br>2

**Answer:** A
**Explanation:** 10/3 = 3 (integer division). 10%3 = 1 (remainder). -10%3 = -1 (sign of dividend).

---

### Question 10: Autoboxing NullPointerException
```java
Integer wrapper = null;
int primitive = wrapper;
```
**Result?**
A. primitive = 0
B. primitive = null
C. NullPointerException
D. Compilation error

**Answer:** C
**Explanation:** Unboxing null wrapper throws NullPointerException at runtime.

---

### Question 11: Text Blocks
```java
String text = """
    Hello
    World
    """;
System.out.println(text.length());
```
**Output?**
A. 11
B. 12
C. 13
D. 17

**Answer:** B
**Explanation:** "Hello\nWorld\n" = 5 + 1 + 5 + 1 = 12 characters

---

### Question 12: ZonedDateTime
```java
ZonedDateTime ny = ZonedDateTime.of(
    LocalDateTime.of(2025, 6, 15, 12, 0),
    ZoneId.of("America/New_York")
);
ZonedDateTime tokyo = ny.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
```
**What happens?**
A. Both show 12:00
B. tokyo shows different time, same instant
C. Compilation error
D. Runtime error

**Answer:** B
**Explanation:** withZoneSameInstant converts to different timezone - same moment, different local time.

---

### Question 13: String equals vs ==
```java
String s1 = new String("test");
String s2 = new String("test");
System.out.println(s1 == s2);
System.out.println(s1.equals(s2));
```
**Output?**
A. true<br>true
B. false<br>true
C. true<br>false
D. false<br>false

**Answer:** B
**Explanation:** == compares references (different objects = false). equals() compares content (same = true).

---

### Question 14: Overflow
```java
int max = Integer.MAX_VALUE;
int result = max + 1;
System.out.println(result);
```
**Output?**
A. Integer.MAX_VALUE
B. Exception
C. Integer.MIN_VALUE
D. 0

**Answer:** C
**Explanation:** Overflow wraps to Integer.MIN_VALUE (-2147483648).

---

### Question 15: LocalTime Period Error
```java
LocalTime time = LocalTime.of(10, 0);
Period p = Period.ofDays(1);
time = time.plus(p);
```
**Result?**
A. Compiles, adds 1 day
B. Compilation error
C. Runtime error
D. time = 10:00

**Answer:** B
**Explanation:** Period (date-based) cannot be added to LocalTime (time-only). Use Duration instead.

---

### Question 16: StringBuilder Chaining
```java
StringBuilder sb = new StringBuilder("Java");
sb.append(" SE").append(" 21");
System.out.println(sb.length());
```
**Output?**
A. 4
B. 7
C. 10
D. 11

**Answer:** C
**Explanation:** "Java" (4) + " SE" (3) + " 21" (3) = 10 characters

---

### Question 17: String Replace
```java
String s = "hello world";
s.replace('l', 'L');
System.out.println(s);
```
**Output?**
A. heLLo worLd
B. hello world
C. Compilation error
D. Runtime error

**Answer:** B
**Explanation:** String methods don't modify original! Must assign: s = s.replace('l', 'L')

---

### Question 18: ChronoUnit
```java
LocalDate start = LocalDate.of(2025, 1, 1);
LocalDate end = LocalDate.of(2025, 12, 31);
long days = ChronoUnit.DAYS.between(start, end);
System.out.println(days);
```
**Output?**
A. 364
B. 365
C. 366
D. Compilation error

**Answer:** A
**Explanation:** 2025 is not leap year. Jan 1 to Dec 31 = 364 days.

---

### Question 19: Wrapper Comparison
```java
Integer a = 1000;
Integer b = 1000;
System.out.println(a == b);
System.out.println(a.equals(b));
```
**Output?**
A. true<br>true
B. false<br>true
C. true<br>false
D. false<br>false

**Answer:** B
**Explanation:** 1000 not cached, different objects (== false). Same value (equals true).

---

### Question 20: Math.round()
```java
System.out.println(Math.round(2.5));
System.out.println(Math.round(3.5));
System.out.println(Math.round(-2.5));
```
**Output?**
A. 3<br>4<br>-3
B. 2<br>3<br>-2
C. 3<br>4<br>-2
D. 2<br>4<br>-3

**Answer:** C
**Explanation:** Math.round() rounds half up: 2.5→3, 3.5→4, -2.5→-2 (towards positive).

---

## Summary

### Exam Objectives Checklist
- ✅ **Use primitives and wrapper classes:** Covered in Topic 1
- ✅ **Evaluate arithmetic and boolean expressions:** Covered in Topic 2
- ✅ **Apply precedence rules, type conversions, casting:** Covered in Topic 2
- ✅ **Manipulate text using String and StringBuilder:** Covered in Topic 3
- ✅ **Manipulate date, time, duration, period, instant:** Covered in Topic 4

### Key Points for Exam (Most Tested)
1. **Integer caching** (-128 to 127) - heavily tested
2. **Integer division** truncates (5/2 = 2)
3. **String immutability** - methods return new String
4. **String pool** - literals vs new String()
5. **StringBuilder** for loops (performance)
6. **Date-Time immutability** - must reassign
7. **Period vs Duration** - date-based vs time-based
8. **Autoboxing NullPointerException** - unboxing null
9. **Overflow** wraps silently (no exception)
10. **Math.round()** rounds half up

### Common Exam Patterns
- **Wrapper caching traps** - 127 vs 128 comparison
- **String method chains** - forgetting to assign result
- **Date manipulation** - forgetting immutability
- **Type conversion** - integer division with double result
- **Period/Duration mixing** - adding wrong type to date/time
- **Null unboxing** - wrapper = null then use as primitive
- **String pool** - == vs equals()
- **Math operations** - precedence, modulo with negatives
- **StringBuilder** - when to use vs String concatenation
- **Text blocks** - proper formatting and indentation

---

## Next Steps

✅ **Completed:** Handling Date, Time, Text, Numeric and Boolean Values
➡️ **Next:** [Chapter 2: Controlling Program Flow](CHAPTER-02.md)

[← Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-02.md)
