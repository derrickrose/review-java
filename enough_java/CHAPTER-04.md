# Chapter 4: Handling Exceptions

[← Previous Chapter](CHAPTER-03.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-05.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Handling Exceptions |
| **Java Versions** | 1.0, 7, 9 |
| **Code Examples** | 30+ |
| **Study Time** | 8-10 hours |
| **Exam Weight** | ~10% |
| **Difficulty** | ⭐⭐⭐ Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Handle exceptions using try-catch-finally blocks, try-with-resources, and multi-catch blocks
- Create and invoke a method that throws an exception
- Recognize common exception classes and categories

---

## 1. Exception Hierarchy

**Exam Objective:** Recognize common exception classes and categories

### What is the Exception Hierarchy?
Java exceptions organized in tree: Throwable → Error/Exception → RuntimeException (unchecked) vs checked exceptions.

### When to Use Each Type
| Type | When | Must Declare? |
|------|------|---------------|
| **Checked** | Recoverable errors (IOException, SQLException) | Yes (throws) |
| **Unchecked (Runtime)** | Programming bugs (NullPointerException, IndexOutOfBoundsException) | No |
| **Error** | JVM problems (OutOfMemoryError) | No (don't catch) |

### Why Understand Hierarchy
- Know which exceptions require try-catch
- Understand checked vs unchecked
- Catch at appropriate level

### Real-World Example
```java
// Exception hierarchy in file processing
public class FileProcessor {
    // Checked exception - must declare or handle
    public void readFile(String path) throws IOException {
        FileReader reader = new FileReader(path); // Checked: FileNotFoundException
        reader.read(); // Checked: IOException
    }
    
    // Unchecked exception - no declaration needed
    public void processData(String[] data) {
        int value = Integer.parseInt(data[0]); // Unchecked: NumberFormatException
        int result = 100 / value; // Unchecked: ArithmeticException
    }
    
    // Error - don't catch, let it propagate
    public void createLargeArray() {
        int[] huge = new int[Integer.MAX_VALUE]; // Error: OutOfMemoryError
    }
}
```
**Why:** File operations use checked (recoverable), programming bugs use unchecked (fix code), JVM issues are Errors (can't recover).

### Exam Traps
- ❌ **TRAP:** Error vs Exception
  ```java
  try {
      int[] huge = new int[Integer.MAX_VALUE];
  } catch (Exception e) { } // Won't catch OutOfMemoryError!
  ```
- ❌ **TRAP:** RuntimeException is unchecked
  ```java
  // This compiles - no need to declare RuntimeException
  public void method() {
      throw new NullPointerException(); // No throws needed
  }
  ```
- ❌ **TRAP:** Checked exceptions must be handled
  ```java
  public void read() {
      new FileReader("file.txt"); // ❌ Won't compile - unhandled IOException
  }
  ```

---

## 2. try-catch-finally

**Exam Objective:** Handle exceptions using try-catch-finally blocks

### What is try-catch-finally?
Exception handling structure: try (risky code), catch (handle exception), finally (always executes).

### When to Use
- **try:** Code that might throw exception
- **catch:** Handle specific exceptions
- **finally:** Cleanup (close resources, release locks)

### Why Use try-catch-finally
- Prevent program crash
- Clean error handling
- Guaranteed cleanup (finally always runs)

### Real-World Example
```java
// Database connection handling
public class DatabaseService {
    public List<User> getUsers() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM users");
            
            while (rs.next()) {
                users.add(new User(rs.getString("name")));
            }
            
        } catch (SQLException e) {
            logger.error("Database error: " + e.getMessage());
            throw new RuntimeException("Failed to fetch users", e);
            
        } finally {
            // Cleanup - ALWAYS executes
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("Failed to close resources", e);
            }
        }
        
        return users;
    }
}
```
**Why:** Database connections must be closed even if query fails - finally ensures cleanup.

### Exam Traps
- ❌ **TRAP:** Catch order matters (specific before general)
  ```java
  try {
      throw new FileNotFoundException();
  } catch (Exception e) {
      System.out.println("Exception");
  } catch (IOException e) { // ❌ Won't compile - unreachable
      System.out.println("IOException");
  }
  ```
- ❌ **TRAP:** finally executes even with return
  ```java
  public int test() {
      try {
          return 1;
      } finally {
          System.out.println("Finally!"); // Prints before return
      }
  }
  ```
- ❌ **TRAP:** finally return overrides try return
  ```java
  public int getValue() {
      try {
          return 10;
      } finally {
          return 20; // ⚠️ This overwrites try return - returns 20!
      }
  }
  ```

---

## 3. try-with-resources

**Exam Objective:** Handle exceptions using try-with-resources

### What is try-with-resources?
Automatic resource management (Java 7). Resources auto-closed after try block. Resource must implement AutoCloseable.

### When to Use
- Files, streams, database connections
- Network sockets
- Any AutoCloseable resource
- Replaces try-catch-finally for resource cleanup

### Why Use try-with-resources
- Automatic cleanup (no finally needed)
- Prevents resource leaks
- Cleaner, safer code
- Handles close() exceptions properly

### Real-World Example
```java
// File processing with automatic cleanup
public class ReportGenerator {
    // OLD way - manual cleanup
    public void generateReportOld(String path) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader("input.txt"));
            writer = new BufferedWriter(new FileWriter(path));
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(processLine(line));
                writer.newLine();
            }
        } finally {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        }
    }
    
    // NEW way - automatic cleanup
    public void generateReport(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(processLine(line));
                writer.newLine();
            }
            // Resources auto-closed here (in reverse order)
        }
    }
}
```
**Why:** File processing - eliminates boilerplate, prevents resource leaks, handles edge cases automatically.

### Exam Traps
- ❌ **TRAP:** Resource must be final or effectively final
  ```java
  FileReader fr = new FileReader("file.txt");
  fr = new FileReader("other.txt"); // Modifying
  try (fr) { } // ❌ Won't compile - fr not effectively final
  ```
- ❌ **TRAP:** Resources closed in reverse order
  ```java
  try (Resource1 r1 = new Resource1();
       Resource2 r2 = new Resource2()) {
      // use resources
  } // r2.close() called BEFORE r1.close()
  ```
- ❌ **TRAP:** Must implement AutoCloseable
  ```java
  class MyClass { }
  
  try (MyClass obj = new MyClass()) { } // ❌ Won't compile
  ```

---

## 4. Throwing Exceptions

**Exam Objective:** Create and invoke a method that throws an exception

### What is Throwing Exceptions?
Using `throw` to throw exception, `throws` to declare exception in method signature.

### When to Use
- **throw:** When error condition detected
- **throws:** Declare checked exceptions method might throw
- Propagate exceptions to caller

### Why Throw Exceptions
- Signal error conditions
- Let caller decide how to handle
- Separate error detection from handling

### Real-World Example
```java
// Order validation service
public class OrderValidator {
    public void validateOrder(Order order) throws InvalidOrderException {
        // Check required fields
        if (order.getCustomerId() == null) {
            throw new InvalidOrderException("Customer ID is required");
        }
        
        if (order.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }
        
        // Check total amount
        if (order.getTotal() < 0) {
            throw new IllegalArgumentException("Order total cannot be negative");
        }
        
        // Check inventory
        for (Item item : order.getItems()) {
            if (!inventoryService.isAvailable(item)) {
                throw new OutOfStockException(
                    "Item " + item.getName() + " is out of stock"
                );
            }
        }
    }
}

// Custom checked exception
class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) {
        super(message);
    }
}

// Custom unchecked exception
class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
```
**Why:** E-commerce validation - clear error messages, typed exceptions for different failure scenarios, caller decides handling.

### Exam Traps
- ❌ **TRAP:** throw vs throws
  ```java
  public void method() throw Exception { } // ❌ Wrong - throws not throw
  public void method() throws Exception { } // ✅ Correct
  
  if (x < 0) throws new Exception(); // ❌ Wrong - throw not throws
  if (x < 0) throw new Exception();  // ✅ Correct
  ```
- ❌ **TRAP:** Can't throw broader exception than declared
  ```java
  public void method() throws IOException {
      throw new Exception(); // ❌ Won't compile - Exception broader than IOException
  }
  ```
- ❌ **TRAP:** Unreachable catch
  ```java
  try {
      System.out.println("Hello");
  } catch (IOException e) { } // ❌ Won't compile - IOException never thrown
  ```

---

## 5. Custom Exceptions

**Exam Objective:** Create and invoke a method that throws an exception

### What are Custom Exceptions?
User-defined exception classes extending Exception (checked) or RuntimeException (unchecked).

### When to Use
- Domain-specific errors
- Add context to exceptions
- Typed exception handling
- Business rule violations

### Why Create Custom Exceptions
- Meaningful names (InsufficientFundsException vs Exception)
- Add custom fields/methods
- Caller can catch specific types
- Better error documentation

### Real-World Example
```java
// Banking system with custom exceptions
public class BankAccount {
    private double balance;
    private boolean frozen;
    
    public void withdraw(double amount) 
            throws InsufficientFundsException, AccountFrozenException {
        
        if (frozen) {
            throw new AccountFrozenException(
                "Account is frozen. Contact support."
            );
        }
        
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient funds. Balance: " + balance + 
                ", Requested: " + amount,
                balance,
                amount
            );
        }
        
        balance -= amount;
    }
}

// Custom checked exception with additional data
class InsufficientFundsException extends Exception {
    private final double balance;
    private final double requestedAmount;
    
    public InsufficientFundsException(String message, 
                                      double balance, 
                                      double requestedAmount) {
        super(message);
        this.balance = balance;
        this.requestedAmount = requestedAmount;
    }
    
    public double getShortfall() {
        return requestedAmount - balance;
    }
}

// Custom checked exception
class AccountFrozenException extends Exception {
    public AccountFrozenException(String message) {
        super(message);
    }
}

// Usage
try {
    account.withdraw(1000);
} catch (InsufficientFundsException e) {
    System.out.println("Short by: $" + e.getShortfall());
} catch (AccountFrozenException e) {
    System.out.println(e.getMessage());
}
```
**Why:** Banking - specific exception types with relevant data, clear error scenarios, type-safe handling.

### Exam Traps
- ❌ **TRAP:** Checked vs unchecked inheritance
  ```java
  class MyException extends Exception { } // Checked - must handle
  class MyException extends RuntimeException { } // Unchecked - optional
  ```
- ❌ **TRAP:** Can't add throws in override
  ```java
  class Parent {
      void method() { }
  }
  
  class Child extends Parent {
      @Override
      void method() throws IOException { } // ❌ Can't add checked exception
  }
  ```
- ❌ **TRAP:** Can narrow exception in override
  ```java
  class Parent {
      void method() throws IOException { }
  }
  
  class Child extends Parent {
      @Override
      void method() throws FileNotFoundException { } // ✅ Narrower is OK
  }
  ```

---

## Real Exam Questions (From Shared Sources)

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes** (2.4 minutes per question)
- **Passing score:** 68% (34 correct answers minimum)
- **Question types:** Multiple choice (5-8 options), Multi-select
- **Difficulty:** Medium-Hard for exceptions
- **Style:** Exception hierarchy, try-catch order, resource management

**Based on actual test-taker experiences**

---

### Question 1: Catch Order
```java
try {
    throw new FileNotFoundException();
} catch (IOException e) {
    System.out.println("IOException");
} catch (Exception e) {
    System.out.println("Exception");
}
```
**Output?**
A. IOException
B. Exception
C. Compilation error
D. Runtime error

**Answer:** A
**Explanation:** FileNotFoundException is subclass of IOException. First matching catch executes.

---

### Question 2: finally with return
```java
public int test() {
    try {
        return 10;
    } finally {
        return 20;
    }
}
System.out.println(test());
```
**Output?**
A. 10
B. 20
C. Compilation error
D. Runtime error

**Answer:** B
**Explanation:** finally return overrides try return. Returns 20.

---

### Question 3: Unreachable catch
```java
try {
    System.out.println("Hello");
} catch (IOException e) {
    System.out.println("Caught");
}
```
**Result?**
A. Prints Hello
B. Prints Caught
C. Compilation error
D. Runtime error

**Answer:** C
**Explanation:** IOException never thrown in try block - unreachable catch.

---

### Question 4: Multi-catch
```java
try {
    throw new IOException();
} catch (IOException | SQLException e) {
    e = new Exception(); // Line X
}
```
**Result?**
A. Compiles
B. Compilation error at Line X
C. Runtime error
D. NullPointerException

**Answer:** B
**Explanation:** Multi-catch variable is implicitly final - can't reassign.

---

### Question 5: try-with-resources order
```java
class Resource implements AutoCloseable {
    String name;
    Resource(String name) { this.name = name; }
    public void close() { System.out.print(name + " "); }
}

try (Resource r1 = new Resource("A");
     Resource r2 = new Resource("B")) {
    System.out.print("Try ");
}
```
**Output?**
A. Try A B
B. Try B A
C. A B Try
D. B A Try

**Answer:** B
**Explanation:** Try block executes, then resources closed in reverse order: B, A.

---

### Question 6: Exception Hierarchy
```java
try {
    throw new OutOfMemoryError();
} catch (Exception e) {
    System.out.println("Caught");
}
```
**Result?**
A. Prints Caught
B. Exception propagates
C. Compilation error
D. NullPointerException

**Answer:** B
**Explanation:** OutOfMemoryError extends Error, not Exception. Not caught.

---

### Question 7: throw vs throws
```java
public void method() throw IOException {
    throw new IOException();
}
```
**Result?**
A. Compiles
B. Compilation error - throw should be throws
C. Runtime error
D. Method never throws

**Answer:** B
**Explanation:** Keyword is "throws" not "throw" in method declaration.

---

### Question 8: finally execution
```java
try {
    System.out.print("A");
    throw new Exception();
} catch (Exception e) {
    System.out.print("B");
    return;
} finally {
    System.out.print("C");
}
System.out.print("D");
```
**Output?**
A. ABC
B. ABCD
C. AB
D. A

**Answer:** A
**Explanation:** Prints A, catches and prints B, return executes but finally prints C first. D never reached.

---

### Question 9: Overriding with exceptions
```java
class Parent {
    void method() throws IOException { }
}

class Child extends Parent {
    void method() throws Exception { }
}
```
**Result?**
A. Compiles
B. Compilation error
C. Runtime error
D. Child method never called

**Answer:** B
**Explanation:** Can't throw broader exception (Exception) than parent (IOException) in override.

---

### Question 10: try-with-resources requirement
```java
class MyResource {
    public void close() {
        System.out.println("Closed");
    }
}

try (MyResource r = new MyResource()) {
    System.out.println("Using");
}
```
**Result?**
A. Using<br>Closed
B. Using
C. Compilation error
D. Closed<br>Using

**Answer:** C
**Explanation:** MyResource must implement AutoCloseable to use in try-with-resources.

---

### Question 11: Multiple catches specific first
```java
try {
    throw new IOException();
} catch (Exception e) {
    System.out.println("A");
} catch (IOException e) {
    System.out.println("B");
}
```
**Result?**
A. A
B. B
C. Compilation error
D. AB

**Answer:** C
**Explanation:** IOException catch unreachable - Exception (broader) comes first.

---

### Question 12: Checked vs Unchecked
```java
public void method() {
    throw new NullPointerException();
}
```
**Result?**
A. Must add throws NullPointerException
B. Compiles without throws
C. Compilation error
D. Must wrap in try-catch

**Answer:** B
**Explanation:** NullPointerException is unchecked - no throws declaration needed.

---

### Question 13: finally override return
```java
public String test() {
    try {
        return "try";
    } finally {
        return "finally";
    }
}
```
**Returns?**
A. try
B. finally
C. Compilation error
D. Runtime error

**Answer:** B
**Explanation:** finally return overrides try return.

---

### Question 14: Exception in finally
```java
try {
    throw new IOException("try");
} finally {
    throw new RuntimeException("finally");
}
```
**Result?**
A. IOException thrown
B. RuntimeException thrown
C. Both thrown
D. Compilation error

**Answer:** B
**Explanation:** finally exception suppresses try exception. RuntimeException thrown.

---

### Question 15: Custom exception inheritance
```java
class MyException extends RuntimeException {
    public MyException(String msg) { super(msg); }
}

public void method() {
    throw new MyException("error");
}
```
**Result?**
A. Must add throws MyException
B. Compiles without throws
C. Compilation error
D. Must use try-catch

**Answer:** B
**Explanation:** MyException extends RuntimeException - unchecked, no throws needed.

---

### Question 16: try-with-resources effectively final
```java
FileReader fr = new FileReader("file.txt");
fr = new FileReader("other.txt");
try (fr) {
    System.out.println("Reading");
}
```
**Result?**
A. Compiles, reads other.txt
B. Compiles, reads file.txt
C. Compilation error
D. Runtime error

**Answer:** C
**Explanation:** fr not effectively final (reassigned) - can't use in try-with-resources.

---

### Question 17: Multi-catch related types
```java
try {
    throw new IOException();
} catch (IOException | FileNotFoundException e) {
    System.out.println("Caught");
}
```
**Result?**
A. Compiles
B. Compilation error
C. Prints Caught
D. Runtime error

**Answer:** B
**Explanation:** FileNotFoundException is subclass of IOException - can't use related types in multi-catch.

---

### Question 18: throw in constructor
```java
class MyClass {
    public MyClass() throws IOException {
        throw new IOException();
    }
}

MyClass obj = new MyClass();
```
**Result?**
A. Compiles
B. Compilation error - unhandled IOException
C. Runtime error
D. obj = null

**Answer:** B
**Explanation:** Checked exception from constructor must be handled or declared.

---

### Question 19: Exception getMessage
```java
try {
    throw new Exception("Error message");
} catch (Exception e) {
    System.out.println(e.getMessage());
}
```
**Output?**
A. Error message
B. Exception: Error message
C. java.lang.Exception
D. null

**Answer:** A
**Explanation:** getMessage() returns the message string only.

---

### Question 20: Narrowing exception in override
```java
class Parent {
    void method() throws IOException { }
}

class Child extends Parent {
    void method() throws FileNotFoundException { }
}
```
**Result?**
A. Compiles - FileNotFoundException narrower
B. Compilation error
C. Runtime error
D. Must be exact same exception

**Answer:** A
**Explanation:** Can throw same or narrower (subclass) exception in override. FileNotFoundException is subclass of IOException.

---

## Summary

### Exam Objectives Checklist
- ✅ **Handle exceptions using try-catch-finally:** Covered in Topic 2
- ✅ **Handle exceptions using try-with-resources:** Covered in Topic 3
- ✅ **Create and invoke methods that throw exceptions:** Covered in Topics 4, 5
- ✅ **Recognize common exception classes:** Covered in Topic 1

### Key Points for Exam (Most Tested)
1. **Catch order** - specific before general
2. **finally always executes** - even with return
3. **finally return overrides** try/catch return
4. **try-with-resources** - must implement AutoCloseable
5. **Resources closed** in reverse order
6. **Checked vs unchecked** - RuntimeException is unchecked
7. **Override exception rules** - can't throw broader
8. **Multi-catch** - variable is final, types can't be related
9. **Error vs Exception** - Error not caught by catch(Exception)
10. **Unreachable catch** - exception never thrown in try

### Common Exam Patterns
- **Catch order errors** - broader exception before specific
- **finally with return** - overrides try return value
- **try-with-resources** - non-AutoCloseable resource
- **Effectively final** - modified variable in try-with-resources
- **Multi-catch related types** - IOException | FileNotFoundException
- **Override throwing broader** - Exception instead of IOException
- **Unreachable catch** - IOException when nothing throws it
- **Error not caught** - OutOfMemoryError not Exception
- **throw vs throws** - keyword confusion
- **Custom exception hierarchy** - extends RuntimeException = unchecked

---

## Next Steps

✅ **Completed:** Handling Exceptions
➡️ **Next:** [Chapter 5: Working with Arrays and Collections](CHAPTER-05.md)

[← Previous Chapter](CHAPTER-03.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-05.md)
