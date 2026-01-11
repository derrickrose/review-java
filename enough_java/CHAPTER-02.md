# Chapter 2: Controlling Program Flow

[← Previous Chapter](CHAPTER-01.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-03.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Controlling Program Flow |
| **Java Versions** | 1.0, 5, 14, 16, 21 |
| **Code Examples** | 35+ |
| **Study Time** | 8-10 hours |
| **Exam Weight** | ~12% |
| **Difficulty** | ⭐⭐⭐ Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Create program flow control constructs including if/else statements
- Create switch statements and expressions
- Use loops including for, while, and do-while loops
- Apply break and continue statements
- Pattern matching with switch construct

---

## 1. if-else Statements

**Exam Objective:** Create program flow control constructs including if/else statements

### What are if-else Statements?
Conditional statements that execute code blocks based on boolean expressions. Includes if, if-else, if-else-if, and ternary operator.

### When to Use
- **if:** Single condition check
- **if-else:** Two mutually exclusive branches
- **if-else-if:** Multiple conditions (order matters)
- **Ternary:** Simple assignment based on condition

### Why Use if-else
- Control program flow based on conditions
- Handle different scenarios
- Avoid unnecessary code execution

### Real-World Example
```java
// E-commerce discount calculator
public class DiscountCalculator {
    public double calculateDiscount(double amount, String customerType) {
        double discount = 0;
        
        if (customerType.equals("VIP")) {
            discount = amount * 0.20; // 20% for VIP
        } else if (customerType.equals("PREMIUM")) {
            discount = amount * 0.15; // 15% for Premium
        } else if (customerType.equals("REGULAR")) {
            discount = amount * 0.10; // 10% for Regular
        } else {
            discount = 0; // No discount for guests
        }
        
        return discount;
    }
    
    // Ternary for simple conditions
    public String getShippingSpeed(boolean isPremium) {
        return isPremium ? "Express" : "Standard";
    }
}
```
**Why:** Different customer tiers get different discounts - clean conditional logic.

### Ternary Operator
```java
// Syntax: condition ? valueIfTrue : valueIfFalse
int max = (a > b) ? a : b;

String status = (age >= 18) ? "Adult" : "Minor";

// Can be nested (but avoid for readability)
String category = (age < 13) ? "Child" : 
                  (age < 20) ? "Teen" : 
                  (age < 60) ? "Adult" : "Senior";
```

### Exam Traps
- ❌ **TRAP:** Missing braces leads to wrong association
  ```java
  if (x > 0)
      System.out.println("Positive");
      System.out.println("This always prints!"); // Not in if block!
  ```
- ❌ **TRAP:** Assignment vs comparison
  ```java
  if (x = 5) { } // ❌ Compilation error (assignment, not boolean)
  if (x == 5) { } // ✅ Correct (comparison)
  ```
- ❌ **TRAP:** Ternary operands must be compatible
  ```java
  int x = true ? 1 : "two"; // ❌ Won't compile (int vs String)
  ```

---

## 2. Switch Statements and Expressions

**Exam Objective:** Create switch statements and expressions

### What is Switch?
Multi-way branch statement. Traditional switch (Java 1.0) vs switch expression (Java 14).

### When to Use
- **Switch statement:** Multiple equality checks on single variable, side effects needed
- **Switch expression:** Return value based on cases, cleaner syntax
- Use switch over if-else-if when checking same variable against multiple constants

### Why Use Switch
- Cleaner than long if-else-if chains
- Better performance (can use jump table)
- Switch expressions eliminate fall-through bugs
- Exhaustiveness checking with sealed types

### Real-World Example
```java
// Order status processor
public class OrderProcessor {
    // Old switch statement (Java 1.0)
    public void processOrderOld(String status) {
        switch (status) {
            case "PENDING":
                notifyWarehouse();
                break;
            case "SHIPPED":
                notifyCustomer();
                trackShipment();
                break;
            case "DELIVERED":
                requestReview();
                break;
            case "CANCELLED":
                refundPayment();
                break;
            default:
                logError("Unknown status");
        }
    }
    
    // New switch expression (Java 14)
    public String getStatusMessage(String status) {
        return switch (status) {
            case "PENDING" -> "Order is being processed";
            case "SHIPPED" -> "Order is on the way";
            case "DELIVERED" -> "Order has arrived";
            case "CANCELLED" -> "Order was cancelled";
            default -> "Unknown status";
        };
    }
    
    // Multiple labels
    public double getShippingCost(String region) {
        return switch (region) {
            case "US", "CA", "MX" -> 5.0;  // North America
            case "UK", "FR", "DE" -> 10.0; // Europe
            case "JP", "CN", "KR" -> 15.0; // Asia
            default -> 20.0;
        };
    }
}
```
**Why:** Order management - clean status handling, avoid fall-through bugs with arrow syntax.

### Switch Statement (Java 1.0)
```java
int day = 3;
switch (day) {
    case 1:
        System.out.println("Monday");
        break; // Prevents fall-through
    case 2:
        System.out.println("Tuesday");
        break;
    case 3:
        System.out.println("Wednesday");
        break;
    default:
        System.out.println("Other day");
}
```

### Switch Expression (Java 14)
```java
// Returns a value - must be exhaustive
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    case 4 -> "Thursday";
    case 5 -> "Friday";
    case 6 -> "Saturday";
    case 7 -> "Sunday";
    default -> "Invalid";
};

// Multiple statements with yield
String message = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        System.out.println("Weekday");
        yield "Work day";
    }
    case 6, 7 -> {
        System.out.println("Weekend");
        yield "Rest day";
    }
    default -> "Invalid day";
};
```

### Exam Traps
- ❌ **TRAP:** Fall-through without break
  ```java
  switch (x) {
      case 1:
          System.out.println("One");
      case 2: // Falls through from case 1!
          System.out.println("Two");
          break;
  }
  // If x=1, prints "One" then "Two"
  ```
- ❌ **TRAP:** Switch expression must be exhaustive
  ```java
  int x = 5;
  String result = switch (x) {
      case 1 -> "One";
      case 2 -> "Two";
      // ❌ Missing default - won't compile!
  };
  ```
- ❌ **TRAP:** Can't mix -> and : syntax
  ```java
  switch (x) {
      case 1 -> System.out.println("One");
      case 2: // ❌ Can't mix styles
          System.out.println("Two");
  }
  ```

---

## 3. Pattern Matching with Switch

**Exam Objective:** Pattern matching with switch construct

### What is Pattern Matching with Switch?
Switch on type (not just constants). Destructure records. Guard conditions with when (Java 21).

### When to Use
- Processing different object types
- Handling sealed type hierarchies
- Destructuring records
- Type-safe polymorphic code

### Why Use Pattern Matching
- Type-safe (compile-time checking)
- Exhaustiveness with sealed types (no default needed)
- Cleaner than instanceof chains
- Record deconstruction

### Real-World Example
```java
// Payment processing with sealed types
sealed interface Payment permits CreditCard, Cash, BankTransfer { }
record CreditCard(String number, String cvv, double amount) implements Payment { }
record Cash(double amount) implements Payment { }
record BankTransfer(String account, String routing, double amount) implements Payment { }

public class PaymentProcessor {
    public void process(Payment payment) {
        switch (payment) {
            case CreditCard(var num, var cvv, var amt) -> {
                validateCard(num, cvv);
                chargeCard(amt);
            }
            case Cash(var amt) -> {
                recordCashPayment(amt);
                openCashDrawer();
            }
            case BankTransfer(var acc, var routing, var amt) -> {
                validateBankDetails(acc, routing);
                initiateTransfer(amt);
            }
            // No default needed - sealed type exhaustive!
        }
    }
    
    // With guard conditions
    public String categorize(Object obj) {
        return switch (obj) {
            case String s when s.length() > 10 -> "Long string";
            case String s -> "Short string";
            case Integer i when i > 100 -> "Large number";
            case Integer i -> "Small number";
            case null -> "Null value";
            default -> "Other type";
        };
    }
}
```
**Why:** Payment systems handle multiple types safely - compiler ensures all types handled.

### Type Pattern Matching
```java
public void describe(Object obj) {
    switch (obj) {
        case null -> System.out.println("Null");
        case String s -> System.out.println("String: " + s);
        case Integer i -> System.out.println("Integer: " + i);
        case Double d -> System.out.println("Double: " + d);
        case int[] arr -> System.out.println("Array length: " + arr.length);
        default -> System.out.println("Other type");
    }
}
```

### Record Pattern Matching
```java
record Point(int x, int y) { }

public void processPoint(Object obj) {
    switch (obj) {
        case Point(int x, int y) when x == 0 && y == 0 -> 
            System.out.println("Origin");
        case Point(int x, int y) when x == y -> 
            System.out.println("Diagonal");
        case Point(int x, int y) -> 
            System.out.println("Point at " + x + "," + y);
        default -> 
            System.out.println("Not a point");
    }
}
```

### Exam Traps
- ❌ **TRAP:** Pattern variable scope
  ```java
  switch (obj) {
      case String s when s.length() > 0 -> // s in scope
      case Integer i -> System.out.println(s); // ❌ s not in scope!
  }
  ```
- ❌ **TRAP:** Guard condition must be boolean
  ```java
  case String s when s.length() -> // ❌ int, not boolean
  case String s when s.length() > 0 -> // ✅ boolean
  ```
- ❌ **TRAP:** Null must be explicit case
  ```java
  switch (obj) {
      case String s -> // ❌ NPE if obj is null
  }
  
  switch (obj) {
      case null -> // ✅ Handle null explicitly
      case String s ->
  }
  ```

---

## 4. Loops

**Exam Objective:** Use loops including for, while, and do-while loops

### What are Loops?
Repetitive execution: while, do-while, for, enhanced for.

### When to Use Each
| Loop Type | When to Use |
|-----------|-------------|
| **while** | Unknown iterations, condition first |
| **do-while** | At least one execution guaranteed |
| **for** | Known iterations, counter needed |
| **enhanced for** | Iterate collections/arrays |

### Why Use Loops
- Avoid code duplication
- Process collections
- Repeat until condition met

### Real-World Example
```java
// Data processing
public class DataProcessor {
    // while - unknown iterations
    public void processQueue(Queue<Task> tasks) {
        while (!tasks.isEmpty()) {
            Task task = tasks.poll();
            task.execute();
        }
    }
    
    // do-while - at least one attempt
    public void retryConnection() {
        int attempts = 0;
        boolean connected = false;
        
        do {
            connected = tryConnect();
            attempts++;
        } while (!connected && attempts < 3);
    }
    
    // for - known iterations
    public void generateReport(int months) {
        for (int i = 0; i < months; i++) {
            processMonth(i);
        }
    }
    
    // enhanced for - iterate collection
    public double calculateTotal(List<Order> orders) {
        double total = 0;
        for (Order order : orders) {
            total += order.getAmount();
        }
        return total;
    }
}
```
**Why:** Different loop types for different scenarios - while for queues, do-while for retries, for for counters, enhanced for for collections.

### while Loop
```java
int i = 0;
while (i < 5) {
    System.out.println(i);
    i++;
}
// Prints: 0 1 2 3 4

// May not execute at all
while (false) {
    System.out.println("Never prints");
}
```

### do-while Loop
```java
int i = 0;
do {
    System.out.println(i);
    i++;
} while (i < 5);
// Prints: 0 1 2 3 4

// Executes at least once
do {
    System.out.println("Prints once");
} while (false);
```

### for Loop
```java
// Standard for
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}

// Multiple variables
for (int i = 0, j = 10; i < 5; i++, j--) {
    System.out.println(i + " " + j);
}

// Infinite loop
for (;;) {
    // Never ends
}
```

### Enhanced for Loop (Java 5)
```java
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

List<String> names = List.of("Alice", "Bob", "Charlie");
for (String name : names) {
    System.out.println(name);
}
```

### Exam Traps
- ❌ **TRAP:** for loop scope
  ```java
  for (int i = 0; i < 5; i++) {
      // i visible here
  }
  System.out.println(i); // ❌ i not visible here
  ```
- ❌ **TRAP:** Enhanced for can't modify array
  ```java
  int[] arr = {1, 2, 3};
  for (int num : arr) {
      num = num * 2; // Doesn't modify arr!
  }
  ```
- ❌ **TRAP:** do-while semicolon required
  ```java
  do {
      System.out.println("Hi");
  } while (false) // ❌ Missing semicolon!
  ```

---

## 5. Control Flow Keywords

**Exam Objective:** Apply break and continue statements

### What are Control Flow Keywords?
Keywords that alter loop/switch execution: break, continue, return.

### When to Use
- **break:** Exit loop/switch immediately
- **continue:** Skip to next iteration
- **return:** Exit method
- **Labels:** Break/continue outer loops

### Why Use Control Keywords
- Early exit (optimization)
- Skip invalid iterations
- Cleaner than complex conditions

### Real-World Example
```java
// Search with early exit
public class SearchEngine {
    // break - stop when found
    public String findUser(List<User> users, String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user.getName(); // Found, exit immediately
            }
        }
        return null; // Not found
    }
    
    // continue - skip invalid entries
    public void processOrders(List<Order> orders) {
        for (Order order : orders) {
            if (order.isCancelled()) {
                continue; // Skip cancelled orders
            }
            if (order.getAmount() < 0) {
                continue; // Skip invalid amounts
            }
            processOrder(order);
        }
    }
    
    // Labeled break - exit nested loops
    public boolean findInMatrix(int[][] matrix, int target) {
        outer: // Label
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == target) {
                    break outer; // Exits both loops
                }
            }
        }
        return false;
    }
}
```
**Why:** Performance - exit early when condition met, skip invalid data efficiently.

### break
```java
// Break in loop
for (int i = 0; i < 10; i++) {
    if (i == 5) break; // Exits loop
    System.out.println(i); // Prints 0-4
}

// Break in switch
switch (x) {
    case 1:
        System.out.println("One");
        break; // Exits switch
    case 2:
        System.out.println("Two");
        break;
}
```

### continue
```java
// Skip even numbers
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue; // Skip to next iteration
    System.out.println(i); // Prints 1, 3, 5, 7, 9
}
```

### Labels
```java
// Labeled break - exit outer loop
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) {
            break outer; // Exits both loops
        }
        System.out.println(i + "," + j);
    }
}

// Labeled continue - continue outer loop
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) {
            continue outer; // Skips to next i
        }
        System.out.println(i + "," + j);
    }
}
```

### Exam Traps
- ❌ **TRAP:** break without loop/switch
  ```java
  if (x > 0) {
      break; // ❌ Can't use break outside loop/switch
  }
  ```
- ❌ **TRAP:** Labeled statement must be loop
  ```java
  label:
  if (true) {
      break label; // ❌ Can't break to if statement
  }
  ```
- ❌ **TRAP:** Unreachable code after break/return
  ```java
  for (int i = 0; i < 10; i++) {
      break;
      System.out.println(i); // ❌ Unreachable
  }
  ```

---

## Real Exam Questions (From Shared Sources)

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes** (2.4 minutes per question)
- **Passing score:** 68% (34 correct answers minimum)
- **Question types:** Multiple choice (5-8 options), Multi-select
- **Difficulty:** Medium-Hard for control flow
- **Style:** Code execution, pattern matching, loop behavior

**Based on actual test-taker experiences**

---

### Question 1: if-else Braces
```java
int x = 10;
if (x > 5)
    System.out.println("A");
    System.out.println("B");
```
**Output?**
A. A<br>B
B. A
C. B
D. Compilation error

**Answer:** A
**Explanation:** Only first statement is in if block. "B" always prints.

---

### Question 2: Switch Fall-Through
```java
int day = 2;
switch (day) {
    case 1:
        System.out.print("Mon ");
    case 2:
        System.out.print("Tue ");
    case 3:
        System.out.print("Wed ");
        break;
    default:
        System.out.print("Other");
}
```
**Output?**
A. Tue
B. Tue Wed
C. Mon Tue Wed
D. Tue Wed Other

**Answer:** B
**Explanation:** Case 2 executes, falls through to case 3, then breaks.

---

### Question 3: Switch Expression Exhaustiveness
```java
int x = 5;
String result = switch (x) {
    case 1 -> "One";
    case 2 -> "Two";
};
```
**Result?**
A. result = null
B. Compilation error
C. Runtime error
D. result = ""

**Answer:** B
**Explanation:** Switch expression must be exhaustive. Missing default or other cases.

---

### Question 4: Enhanced for Modification
```java
int[] arr = {1, 2, 3};
for (int num : arr) {
    num = num * 2;
}
System.out.println(arr[0]);
```
**Output?**
A. 1
B. 2
C. 3
D. Compilation error

**Answer:** A
**Explanation:** Enhanced for uses copy of value. Modifying num doesn't modify arr.

---

### Question 5: do-while Semicolon
```java
int i = 0;
do {
    System.out.println(i);
    i++;
} while (i < 3)
```
**Result?**
A. 0 1 2
B. 0 1 2 3
C. Compilation error
D. Infinite loop

**Answer:** C
**Explanation:** Missing semicolon after while condition.

---

### Question 6: Pattern Matching Scope
```java
Object obj = "test";
switch (obj) {
    case String s when s.length() > 2 -> System.out.println("Long");
    case Integer i -> System.out.println(s);
}
```
**Result?**
A. Prints "Long"
B. Compilation error
C. Runtime error
D. Prints nothing

**Answer:** B
**Explanation:** Variable s not in scope in Integer case.

---

### Question 7: for Loop Scope
```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
System.out.println(i);
```
**Result?**
A. 0 1 2 3 4 5
B. 0 1 2 3 4 4
C. 0 1 2 3 4 then error
D. Compilation error

**Answer:** D
**Explanation:** Variable i not visible outside for loop.

---

### Question 8: break in Nested Loops
```java
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) break;
        System.out.print(i + "" + j + " ");
    }
}
```
**Output?**
A. 00 10 20
B. 00 01 02 10 11 12 20 21 22
C. 00
D. 00 10 11 20 21

**Answer:** A
**Explanation:** break exits inner loop only. Prints i,0 for each i.

---

### Question 9: Ternary Type Compatibility
```java
boolean flag = true;
int x = flag ? 1 : "two";
```
**Result?**
A. x = 1
B. Compilation error
C. Runtime error
D. x = 0

**Answer:** B
**Explanation:** Ternary operands must be compatible types. int vs String won't compile.

---

### Question 10: Switch with null
```java
String s = null;
switch (s) {
    case "A" -> System.out.println("A");
    default -> System.out.println("Default");
}
```
**Result?**
A. Prints "Default"
B. NullPointerException
C. Compilation error
D. Prints nothing

**Answer:** B
**Explanation:** Switch on null throws NPE unless explicit null case.

---

### Question 11: continue in Loop
```java
for (int i = 0; i < 5; i++) {
    if (i == 2) continue;
    System.out.print(i + " ");
}
```
**Output?**
A. 0 1 3 4
B. 0 1 2 3 4
C. 0 1
D. 3 4

**Answer:** A
**Explanation:** continue skips i==2, prints 0 1 3 4.

---

### Question 12: Labeled Break
```java
outer:
for (int i = 0; i < 2; i++) {
    for (int j = 0; j < 2; j++) {
        if (i == 1) break outer;
        System.out.print(i + "" + j + " ");
    }
}
```
**Output?**
A. 00 01
B. 00 01 10 11
C. 00
D. 00 10

**Answer:** A
**Explanation:** When i==1, break outer exits both loops. Prints only i==0 cases.

---

### Question 13: Record Pattern Matching
```java
record Point(int x, int y) { }

Object obj = new Point(5, 10);
switch (obj) {
    case Point(int a, int b) -> System.out.println(a + b);
    default -> System.out.println("Not point");
}
```
**Output?**
A. 15
B. 510
C. Compilation error
D. ClassCastException

**Answer:** A
**Explanation:** Record deconstruction: a=5, b=10, sum=15.

---

### Question 14: Switch Expression with yield
```java
int x = 2;
String result = switch (x) {
    case 1 -> "One";
    case 2 -> {
        System.out.println("Two");
        yield "2";
    }
    default -> "Other";
};
```
**What prints and what's result?**
A. Two, result="2"
B. 2, result="Two"
C. Two, result="Two"
D. Compilation error

**Answer:** A
**Explanation:** Prints "Two", then yields "2" as result.

---

### Question 15: while vs do-while
```java
int i = 10;
while (i < 5) {
    System.out.println("while");
}

do {
    System.out.println("do-while");
} while (i < 5);
```
**Output?**
A. Nothing
B. while
C. do-while
D. while<br>do-while

**Answer:** C
**Explanation:** while never executes (condition false). do-while executes once.

---

### Question 16: for Loop Multiple Variables
```java
for (int i = 0, j = 5; i < 3; i++, j--) {
    System.out.print(i + j + " ");
}
```
**Output?**
A. 5 5 5
B. 0 1 2
C. 5 4 3
D. 5 6 7

**Answer:** A
**Explanation:** i+j: 0+5=5, 1+4=5, 2+3=5.

---

### Question 17: Pattern Matching Guard
```java
Object obj = "hello";
switch (obj) {
    case String s when s.length() -> System.out.println("Has length");
    default -> System.out.println("Other");
}
```
**Result?**
A. Prints "Has length"
B. Prints "Other"
C. Compilation error
D. Runtime error

**Answer:** C
**Explanation:** Guard must be boolean. s.length() returns int.

---

### Question 18: Unreachable Code
```java
for (int i = 0; i < 10; i++) {
    return;
    System.out.println(i);
}
```
**Result?**
A. Prints 0
B. Compilation error
C. Runtime error
D. Prints nothing

**Answer:** B
**Explanation:** Unreachable code after return.

---

### Question 19: Switch Multiple Labels
```java
int day = 6;
String type = switch (day) {
    case 1, 2, 3, 4, 5 -> "Weekday";
    case 6, 7 -> "Weekend";
    default -> "Invalid";
};
System.out.println(type);
```
**Output?**
A. Weekday
B. Weekend
C. Invalid
D. 6

**Answer:** B
**Explanation:** day=6 matches case 6,7 -> "Weekend".

---

### Question 20: Break without Loop
```java
int x = 5;
if (x > 0) {
    break;
}
```
**Result?**
A. x = 5
B. Compilation error
C. Runtime error
D. Nothing happens

**Answer:** B
**Explanation:** break only valid in loop or switch.

---

## Summary

### Exam Objectives Checklist
- ✅ **Create if/else statements:** Covered in Topic 1
- ✅ **Create switch statements and expressions:** Covered in Topic 2
- ✅ **Use loops:** Covered in Topic 4
- ✅ **Apply break and continue:** Covered in Topic 5
- ✅ **Pattern matching with switch:** Covered in Topic 3

### Key Points for Exam (Most Tested)
1. **if-else braces** - only first statement without braces
2. **Switch fall-through** - missing break continues to next case
3. **Switch expression exhaustiveness** - must cover all cases
4. **Enhanced for can't modify** array/collection
5. **do-while semicolon** required after while
6. **Pattern variable scope** - only in its case
7. **for loop variable scope** - not visible outside
8. **break/continue** - only in loops/switch
9. **Labeled break/continue** - exit/skip outer loops
10. **Unreachable code** after return/break

### Common Exam Patterns
- **Missing braces in if** - second statement not in block
- **Switch fall-through bugs** - forgetting break
- **Switch expression missing default** - not exhaustive
- **Modifying in enhanced for** - doesn't change original
- **Pattern matching scope** - variable not available in other cases
- **Nested loops with break** - only exits inner loop
- **do-while without semicolon** - compilation error
- **Ternary type mismatch** - incompatible operand types
- **Switch on null** - NullPointerException without null case
- **Unreachable code** - after return/break in loop

---

## Next Steps

✅ **Completed:** Controlling Program Flow
➡️ **Next:** [Chapter 3: Object-Oriented Concepts](CHAPTER-03.md)

[← Previous Chapter](CHAPTER-01.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-03.md)
