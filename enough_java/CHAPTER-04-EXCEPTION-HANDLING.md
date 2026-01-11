# Chapter 7: Exception Handling and Assertions

[Back to Main Guide](README.md)

---

## Chapter Overview

This chapter covers exception handling mechanisms in Java, including the exception hierarchy, try-catch blocks, try-with-resources, custom exceptions, and assertions. Exception handling is crucial for writing robust, production-ready applications.

**What You'll Learn:**
- Exception hierarchy and types
- Exception handling with try-catch-finally
- Try-with-resources for automatic resource management
- Creating and using custom exceptions
- Multi-catch blocks
- Suppressed exceptions
- Assertions for debugging and testing

---

## Official Exam Objectives (1Z0-830)

This chapter covers the following exam objectives:
- Handle exceptions using try-catch and try-with-resources statements
- Create and use custom exceptions
- Understand exception hierarchy (checked vs unchecked)
- Use multi-catch blocks
- Understand suppressed exceptions
- Use assertions appropriately

---

## 1. Exception Hierarchy

### What are Exceptions?

An exception is an event that occurs during program execution that disrupts the normal flow of instructions. Java uses exceptions to handle errors and other exceptional events.

### Exception Class Hierarchy

```
java.lang.Object
    └── java.lang.Throwable
            ├── java.lang.Error
            │       ├── OutOfMemoryError
            │       ├── StackOverflowError
            │       └── VirtualMachineError
            └── java.lang.Exception
                    ├── IOException (checked)
                    ├── SQLException (checked)
                    ├── ClassNotFoundException (checked)
                    └── java.lang.RuntimeException (unchecked)
                            ├── NullPointerException
                            ├── ArrayIndexOutOfBoundsException
                            ├── IllegalArgumentException
                            ├── NumberFormatException
                            └── ClassCastException
```

### Throwable - The Root Class

```java
public class Throwable {
    public String getMessage()           // Returns detail message
    public String getLocalizedMessage()  // Localized description
    public Throwable getCause()          // Returns cause of exception
    public void printStackTrace()        // Prints stack trace
    public StackTraceElement[] getStackTrace()  // Returns stack trace
}
```

### 1.1 Error vs Exception

**Error:**
- Serious problems that applications should NOT catch
- Usually external to the application (JVM issues)
- Examples: OutOfMemoryError, StackOverflowError
- Indicates unrecoverable conditions

**Exception:**
- Conditions that applications should catch
- Can be anticipated and handled
- Examples: IOException, SQLException
- Allows graceful error handling

```java
// Don't catch Error
try {
    // Some code
} catch (OutOfMemoryError e) {  // ❌ Bad practice - don't catch Errors
    // Can't really recover from OOM
}

// Do catch Exception
try {
    Files.readAllLines(Paths.get("file.txt"));
} catch (IOException e) {  // ✅ Good - handle expected exceptions
    System.err.println("File not found: " + e.getMessage());
}
```

### 1.2 Checked vs Unchecked Exceptions

**Checked Exceptions (compile-time exceptions):**
- Must be declared with `throws` or handled with try-catch
- Checked at compile time
- Extend `Exception` but NOT `RuntimeException`
- Examples: IOException, SQLException, ClassNotFoundException

**Unchecked Exceptions (runtime exceptions):**
- No compile-time checking required
- Can occur at any time during execution
- Extend `RuntimeException`
- Examples: NullPointerException, ArrayIndexOutOfBoundsException, IllegalArgumentException

```java
// Checked exception - must handle or declare
public void readFile() throws IOException {  // Must declare
    Files.readAllLines(Paths.get("file.txt"));
}

// Or handle it
public void readFileSafely() {
    try {
        Files.readAllLines(Paths.get("file.txt"));
    } catch (IOException e) {  // Must catch
        e.printStackTrace();
    }
}

// Unchecked exception - no requirement
public void divide(int a, int b) {  // No throws needed
    int result = a / b;  // Can throw ArithmeticException
}

public String upper(String s) {  // No throws needed
    return s.toUpperCase();  // Can throw NullPointerException
}
```

### 1.3 Common Exception Types

**Unchecked (RuntimeException):**

```java
// NullPointerException - accessing null reference
String str = null;
int length = str.length();  // NPE

// ArrayIndexOutOfBoundsException - invalid array index
int[] arr = {1, 2, 3};
int value = arr[5];  // AIOOBE

// IllegalArgumentException - invalid method argument
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Invalid age: " + age);
    }
}

// NumberFormatException - invalid number format (extends IllegalArgumentException)
int num = Integer.parseInt("abc");  // NFE

// ClassCastException - invalid type cast
Object obj = "Hello";
Integer num = (Integer) obj;  // CCE

// ArithmeticException - arithmetic error (division by zero)
int result = 10 / 0;  // ArithmeticException

// IllegalStateException - method called at inappropriate time
Iterator<String> iter = list.iterator();
iter.remove();  // ISE - next() must be called first
```

**Checked (Exception):**

```java
// IOException - I/O operation failed
FileReader fr = new FileReader("file.txt");  // May throw IOException

// FileNotFoundException - specific type of IOException
Files.readAllLines(Paths.get("missing.txt"));  // FNFE

// SQLException - database access error
Connection conn = DriverManager.getConnection(url);  // May throw SQLException

// ClassNotFoundException - class not found during runtime loading
Class.forName("com.example.MissingClass");  // CNFE

// InterruptedException - thread interrupted
Thread.sleep(1000);  // May throw InterruptedException

// ParseException - parsing error
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
Date date = sdf.parse("invalid");  // May throw ParseException
```

---

## 2. Exception Handling

### 2.1 try-catch Block

Basic exception handling structure.

```java
try {
    // Code that might throw exception
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // Handle the exception
    System.out.println("Cannot divide by zero");
}

// Program continues normally
System.out.println("Program continues");
```

**Execution Flow:**
1. Code in try block executes
2. If exception thrown, control jumps to catch block
3. Catch block executes
4. Execution continues after catch block

```java
public class ExceptionFlow {
    public static void main(String[] args) {
        System.out.println("1");
        try {
            System.out.println("2");
            int result = 10 / 0;  // Exception here
            System.out.println("3");  // NOT executed
        } catch (ArithmeticException e) {
            System.out.println("4");
        }
        System.out.println("5");
    }
}
// Output: 1, 2, 4, 5 (NOT 3)
```

### 2.2 Multiple catch Blocks

Handle different exception types differently.

```java
public void processFile(String filename) {
    try {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        int number = Integer.parseInt(line);
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filename);
    } catch (IOException e) {
        System.err.println("Error reading file: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.err.println("Invalid number format");
    }
}
```

**Catch Block Order Rules:**
- More specific exceptions MUST come before more general
- Subclass before superclass
- Compiler enforces this

```java
// ❌ WRONG - won't compile
try {
    // code
} catch (Exception e) {          // Too general first
    System.out.println("Exception");
} catch (IOException e) {         // Unreachable - already caught by Exception
    System.out.println("IOException");
}

// ✅ CORRECT
try {
    // code
} catch (IOException e) {         // Specific first
    System.out.println("IOException");
} catch (Exception e) {           // General last
    System.out.println("Exception");
}
```

### 2.3 Multi-catch Block (Java 7+)

Catch multiple exception types in one block when handling them the same way.

```java
// Before Java 7 - duplicated code
try {
    // code
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
    e.printStackTrace();
} catch (SQLException e) {
    System.err.println("Error: " + e.getMessage());
    e.printStackTrace();
}

// Java 7+ - multi-catch
try {
    // code
} catch (IOException | SQLException e) {
    System.err.println("Error: " + e.getMessage());
    e.printStackTrace();
}
```

**Multi-catch Rules:**
- Exception types separated by `|` (pipe)
- Exception variable is implicitly `final`
- Cannot catch exceptions with parent-child relationship

```java
// ❌ WRONG - exceptions have inheritance relationship
try {
    // code
} catch (Exception | IOException e) {  // Won't compile - IOException extends Exception
    // handle
}

// ❌ WRONG - exception variable is final
try {
    // code
} catch (IOException | SQLException e) {
    e = new IOException();  // Won't compile - e is final
}

// ✅ CORRECT
try {
    // code
} catch (IOException | SQLException e) {
    System.out.println(e.getClass().getName());  // Can call methods
}
```

### 2.4 finally Block

Code that ALWAYS executes, regardless of exception.

```java
FileReader fr = null;
try {
    fr = new FileReader("file.txt");
    // Process file
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
} finally {
    // ALWAYS executes - perfect for cleanup
    if (fr != null) {
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**finally Execution Rules:**
- Executes after try block (if no exception)
- Executes after catch block (if exception caught)
- Executes even if try/catch has return statement
- Does NOT execute if JVM exits (System.exit()) or thread killed

```java
public static int testFinally() {
    try {
        return 1;
    } finally {
        System.out.println("Finally executes!");
        // return 2;  // Would override try's return (but bad practice)
    }
}
// Output: "Finally executes!" then returns 1
```

**finally and return:**

```java
public static int method1() {
    try {
        return 10;
    } finally {
        return 20;  // ⚠️ Overrides try's return
    }
    // Returns 20
}

public static int method2() {
    int x = 10;
    try {
        return x;  // x = 10 at this point
    } finally {
        x = 20;  // Changes x, but return value already determined
    }
    // Returns 10 (not 20)
}
```

### 2.5 try-with-resources (Java 7+)

Automatically close resources that implement `AutoCloseable`.

**Syntax:**
```java
try (ResourceType resource = new ResourceType()) {
    // Use resource
} catch (Exception e) {
    // Handle exception
}
// resource.close() automatically called
```

**Single Resource:**

```java
// Old way - verbose
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    String line = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// New way - try-with-resources
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
}
// br.close() automatically called
```

**Multiple Resources:**

```java
// Multiple resources separated by semicolon
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt");
     BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
    
    String line;
    while ((line = br.readLine()) != null) {
        fos.write(line.getBytes());
    }
} catch (IOException e) {
    e.printStackTrace();
}
// All resources closed in REVERSE order: br, fos, fis
```

**AutoCloseable Interface:**

```java
public interface AutoCloseable {
    void close() throws Exception;
}

// Custom resource
class MyResource implements AutoCloseable {
    public void doSomething() {
        System.out.println("Doing something");
    }
    
    @Override
    public void close() throws Exception {
        System.out.println("Closing resource");
    }
}

// Usage
try (MyResource resource = new MyResource()) {
    resource.doSomething();
}
// Output: "Doing something" then "Closing resource"
```

### 2.6 Suppressed Exceptions

When both try block and close() throw exceptions, close() exception is suppressed.

```java
class Resource implements AutoCloseable {
    @Override
    public void close() throws Exception {
        throw new Exception("Close exception");
    }
    
    public void use() throws Exception {
        throw new Exception("Use exception");
    }
}

public static void main(String[] args) {
    try (Resource r = new Resource()) {
        r.use();  // Throws "Use exception"
    } catch (Exception e) {
        System.out.println("Main: " + e.getMessage());           // "Use exception"
        
        Throwable[] suppressed = e.getSuppressed();
        for (Throwable t : suppressed) {
            System.out.println("Suppressed: " + t.getMessage()); // "Close exception"
        }
    }
}
```

**Suppressed Exception Rules:**
- Primary exception is from try block
- close() exceptions are suppressed
- Access via `getSuppressed()`
- If try succeeds but close() fails, close() exception is NOT suppressed

```java
try (Resource r = new Resource()) {
    // No exception here
} catch (Exception e) {
    // Catches close() exception (not suppressed)
    System.out.println(e.getMessage());  // "Close exception"
}
```

---

## 3. Throwing Exceptions

### 3.1 throw Statement

Explicitly throw an exception.

```java
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative: " + age);
    }
    this.age = age;
}

public void withdraw(double amount) {
    if (amount > balance) {
        throw new IllegalStateException("Insufficient funds");
    }
    balance -= amount;
}

// Throwing checked exception
public void processFile(String filename) throws IOException {
    if (filename == null) {
        throw new IOException("Filename cannot be null");
    }
    // Process file
}
```

### 3.2 throws Clause

Declare that a method might throw exceptions (passes responsibility to caller).

```java
// Method declares it might throw IOException
public void readFile(String filename) throws IOException {
    Files.readAllLines(Paths.get(filename));
}

// Caller must handle or declare
public void caller1() {
    try {
        readFile("file.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void caller2() throws IOException {
    readFile("file.txt");
}

// Multiple exceptions
public void method() throws IOException, SQLException {
    // code
}
```

### 3.3 Exception and Method Overriding

Rules for overriding methods that throw exceptions:

**Rule 1:** Overriding method CAN throw fewer or narrower exceptions
**Rule 2:** Overriding method CANNOT throw broader or new checked exceptions
**Rule 3:** Unchecked exceptions can be added freely

```java
class Parent {
    public void method1() throws IOException {
        // code
    }
    
    public void method2() throws Exception {
        // code
    }
    
    public void method3() {
        // code
    }
}

class Child extends Parent {
    // ✅ OK - narrower exception (FileNotFoundException extends IOException)
    @Override
    public void method1() throws FileNotFoundException {
        // code
    }
    
    // ✅ OK - no exception (fewer)
    @Override
    public void method2() {
        // code
    }
    
    // ❌ WRONG - broader exception
    // @Override
    // public void method3() throws Exception {  // Won't compile
    //     // code
    // }
    
    // ✅ OK - unchecked exception can be added
    @Override
    public void method3() throws NullPointerException {
        // code
    }
}
```

---

## 4. Custom Exceptions

### When to Create Custom Exceptions

Create custom exceptions when:
- Need domain-specific error handling
- Want to add additional context/data
- Need to distinguish between different error types
- Building a framework or library

### 4.1 Creating Custom Exceptions

```java
// Custom checked exception
public class InsufficientFundsException extends Exception {
    private double deficit;
    
    public InsufficientFundsException(double deficit) {
        super("Insufficient funds. Deficit: $" + deficit);
        this.deficit = deficit;
    }
    
    public double getDeficit() {
        return deficit;
    }
}

// Custom unchecked exception
public class InvalidAccountException extends RuntimeException {
    private String accountId;
    
    public InvalidAccountException(String accountId) {
        super("Invalid account: " + accountId);
        this.accountId = accountId;
    }
    
    public InvalidAccountException(String accountId, Throwable cause) {
        super("Invalid account: " + accountId, cause);
        this.accountId = accountId;
    }
    
    public String getAccountId() {
        return accountId;
    }
}
```

### 4.2 Best Practices

```java
// ✅ Good - provides constructors for different scenarios
public class ValidationException extends Exception {
    public ValidationException() {
        super();
    }
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ValidationException(Throwable cause) {
        super(cause);
    }
}

// ✅ Good - includes relevant data
public class OrderNotFoundException extends Exception {
    private final String orderId;
    private final String customerId;
    
    public OrderNotFoundException(String orderId, String customerId) {
        super(String.format("Order %s not found for customer %s", orderId, customerId));
        this.orderId = orderId;
        this.customerId = customerId;
    }
    
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
}
```

### 4.3 Usage Example

```java
public class BankAccount {
    private String accountId;
    private double balance;
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            double deficit = amount - balance;
            throw new InsufficientFundsException(deficit);
        }
        balance -= amount;
    }
    
    public void transfer(BankAccount target, double amount) 
            throws InsufficientFundsException, InvalidAccountException {
        
        if (target == null) {
            throw new InvalidAccountException("null");
        }
        
        withdraw(amount);  // May throw InsufficientFundsException
        target.deposit(amount);
    }
}

// Usage
try {
    account.withdraw(1000);
} catch (InsufficientFundsException e) {
    System.err.println("Cannot withdraw: " + e.getMessage());
    System.err.println("Deficit: $" + e.getDeficit());
    // Possibly retry with different amount
}
```

---

## 5. Assertions

### What are Assertions?

Assertions are debugging tools to test assumptions about program state. They're disabled by default in production.

**Syntax:**
```java
assert condition;
assert condition : message;
```

### 5.1 Basic Assertions

```java
public void setAge(int age) {
    assert age >= 0 : "Age cannot be negative: " + age;
    this.age = age;
}

public int divide(int a, int b) {
    assert b != 0 : "Divisor cannot be zero";
    return a / b;
}

private void validateState() {
    assert isValid() : "Object in invalid state";
    // continue with processing
}
```

### 5.2 Enabling Assertions

Assertions are disabled by default.

**Enable all assertions:**
```bash
java -ea MyClass
java -enableassertions MyClass
```

**Enable for specific package:**
```bash
java -ea:com.example... MyClass
```

**Enable for specific class:**
```bash
java -ea:com.example.MyClass MyClass
```

**Disable assertions:**
```bash
java -da MyClass
java -disableassertions MyClass
```

### 5.3 AssertionError

When assertion fails, `AssertionError` is thrown (extends Error).

```java
public class AssertionExample {
    public static void main(String[] args) {
        int value = -5;
        assert value >= 0 : "Value must be non-negative";  // Fails if -ea enabled
        System.out.println("Value: " + value);
    }
}

// Run with: java -ea AssertionExample
// Output: Exception in thread "main" java.lang.AssertionError: Value must be non-negative
```

**Important:** DO NOT catch AssertionError in production code!

```java
// ❌ BAD - defeats purpose of assertions
try {
    assert false : "This should fail";
} catch (AssertionError e) {
    // Don't do this
}
```

### 5.4 Assertions vs Exceptions

**Use Assertions for:**
- Internal program logic (developer errors)
- Validating invariants and postconditions
- Testing assumptions during development
- Private method preconditions

**Use Exceptions for:**
- Invalid user input
- External errors (file not found, network issues)
- Public method preconditions
- Recoverable errors

```java
// ✅ Use exception for public method
public void withdraw(double amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
    }
    // process withdrawal
}

// ✅ Use assertion for internal validation
private void processInternal(List<String> items) {
    assert items != null : "Items should never be null here";
    assert !items.isEmpty() : "Items should have been validated";
    // process items
}

// ✅ Use assertion for postcondition
private int calculate(int x) {
    int result = complexCalculation(x);
    assert result >= 0 : "Result should always be non-negative";
    return result;
}
```

### 5.5 Assertions Best Practices

```java
// ❌ DON'T use assertions for argument checking in public methods
public void publicMethod(String param) {
    assert param != null;  // WRONG - use exception instead
}

// ✅ DO use exceptions for public methods
public void publicMethod(String param) {
    if (param == null) {
        throw new IllegalArgumentException("param cannot be null");
    }
}

// ❌ DON'T change program state in assertions
assert list.remove(item);  // WRONG - side effect

// ✅ DO keep assertions side-effect free
assert list.contains(item);  // OK - no side effect

// ❌ DON'T use assertions for command-line argument validation
public static void main(String[] args) {
    assert args.length > 0;  // WRONG - use exception
}

// ✅ DO use assertions for internal invariants
private void internalMethod() {
    assert invariantHolds() : "Invariant violated";
}
```

---

## 6. Real-World Examples

### Example 1: File Processing with Exception Handling

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileProcessor {
    
    public List<String> processFile(String filename) {
        List<String> validLines = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String processed = processLine(line);
                    validLines.add(processed);
                } catch (IllegalArgumentException e) {
                    System.err.println("Line " + lineNumber + " skipped: " + e.getMessage());
                    // Continue processing other lines
                }
            }
        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return validLines;
    }
    
    private String processLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty line");
        }
        return line.trim().toUpperCase();
    }
    
    public static void main(String[] args) {
        FileProcessor processor = new FileProcessor();
        List<String> results = processor.processFile("data.txt");
        System.out.println("Processed " + results.size() + " valid lines");
    }
}
```

**Why Exception Handling:** Robust file processing that continues despite individual line errors, with proper resource cleanup via try-with-resources.

### Example 2: Database Connection Management

```java
import java.sql.*;

public class DatabaseManager implements AutoCloseable {
    private Connection connection;
    
    public DatabaseManager(String url, String user, String password) 
            throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }
    
    public List<User> getUsers() throws DatabaseException {
        String sql = "SELECT id, name, email FROM users";
        List<User> users = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                    users.add(user);
                } catch (SQLException e) {
                    // Log and skip invalid row
                    System.err.println("Invalid row: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching users", e);
        }
        
        return users;
    }
    
    public void updateUser(User user) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new UserNotFoundException(user.getId());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user", e);
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

// Custom exceptions
class DatabaseException extends Exception {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

class UserNotFoundException extends DatabaseException {
    public UserNotFoundException(int userId) {
        super("User not found: " + userId, null);
    }
}

// Usage
try (DatabaseManager db = new DatabaseManager(url, user, password)) {
    List<User> users = db.getUsers();
    // Process users
} catch (SQLException e) {
    System.err.println("Database connection error: " + e.getMessage());
} catch (DatabaseException e) {
    System.err.println("Database operation error: " + e.getMessage());
}
```

**Why Exception Handling:** Multi-layered exception handling with custom exceptions, proper resource cleanup, and graceful error recovery.

### Example 3: Validation Framework with Assertions

```java
public class ValidationFramework {
    
    public static class Validator {
        private boolean debugMode;
        
        public Validator(boolean debugMode) {
            this.debugMode = debugMode;
        }
        
        public void validateEmail(String email) throws ValidationException {
            // Public method - use exceptions
            if (email == null || email.isEmpty()) {
                throw new ValidationException("Email cannot be empty");
            }
            
            if (!email.contains("@")) {
                throw new ValidationException("Email must contain @");
            }
            
            // Internal assertion - only active in debug mode
            assert isValidEmailFormat(email) : "Email format validation failed: " + email;
        }
        
        private boolean isValidEmailFormat(String email) {
            // Complex validation logic
            return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        }
        
        public void validateAge(int age) throws ValidationException {
            // Public method - use exceptions
            if (age < 0 || age > 150) {
                throw new ValidationException("Age must be between 0 and 150");
            }
            
            // Post-condition assertion
            assert age >= 0 && age <= 150 : "Age validation postcondition failed";
        }
        
        private void internalValidation(Object obj) {
            // Private method - use assertions for preconditions
            assert obj != null : "Object should not be null in internal validation";
            assert obj instanceof String : "Object should be String";
            
            // Continue with validation
        }
    }
    
    static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
    
    public static void main(String[] args) {
        Validator validator = new Validator(true);
        
        try {
            validator.validateEmail("user@example.com");
            System.out.println("Email valid");
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
        }
        
        try {
            validator.validateAge(200);  // Invalid
        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
        }
    }
}
```

**Why Assertions:** Demonstrates proper use of exceptions for public API validation and assertions for internal invariants, with clear separation of concerns.

---

## Exam Traps

- ❌ **TRAP 1:** Catch blocks must be ordered from specific to general
  ```java
  // ❌ Won't compile
  try {
      // code
  } catch (Exception e) {
      // handle
  } catch (IOException e) {  // Unreachable
      // handle
  }
  ```

- ❌ **TRAP 2:** Multi-catch cannot have parent-child relationship
  ```java
  // ❌ Won't compile
  try {
      // code
  } catch (IOException | Exception e) {  // IOException extends Exception
      // handle
  }
  ```

- ❌ **TRAP 3:** Multi-catch exception variable is implicitly final
  ```java
  try {
      // code
  } catch (IOException | SQLException e) {
      e = new IOException();  // ❌ Won't compile - e is final
  }
  ```

- ❌ **TRAP 4:** finally always executes (except JVM exit)
  ```java
  try {
      return 1;
  } finally {
      System.out.println("Executes!");  // Prints even though return
  }
  ```

- ❌ **TRAP 5:** try-with-resources closes in REVERSE order
  ```java
  try (First f = new First(); Second s = new Second()) {
      // use resources
  }
  // Closes: s.close() then f.close() (reverse order)
  ```

- ❌ **TRAP 6:** Suppressed exceptions from close()
  ```java
  try (Resource r = new Resource()) {
      throw new Exception("Try");  // Primary
  }
  // If close() throws, it's suppressed (not primary)
  ```

- ❌ **TRAP 7:** Overriding method cannot throw broader checked exceptions
  ```java
  class Parent {
      void method() throws IOException { }
  }
  class Child extends Parent {
      // ❌ Won't compile - Exception broader than IOException
      void method() throws Exception { }
  }
  ```

- ❌ **TRAP 8:** Assertions can be disabled at runtime
  ```java
  assert false : "This fails";
  // No error if run without -ea flag
  ```

- ❌ **TRAP 9:** Don't use assertions for public method argument validation
  ```java
  // ❌ Bad - assertions can be disabled
  public void withdraw(double amount) {
      assert amount > 0;  // WRONG - use exception
  }
  ```

- ❌ **TRAP 10:** Error should not be caught
  ```java
  // ❌ Bad practice
  try {
      // code
  } catch (OutOfMemoryError e) {  // Don't catch Error
      // Can't recover
  }
  ```

- ❌ **TRAP 11:** throw vs throws confusion
  ```java
  // throw - throws actual exception
  throw new IOException();
  
  // throws - declares method might throw
  public void method() throws IOException { }
  ```

- ❌ **TRAP 12:** Exception in finally can mask try exception
  ```java
  try {
      throw new IOException("Try");
  } finally {
      throw new SQLException("Finally");  // Masks IOException
  }
  // SQLException thrown, IOException lost
  ```

---

## Exam Questions

### Question 1: Exception hierarchy
```java
Which statements are true about exceptions?
```
**Options:**  
A. All exceptions must be caught or declared  
B. RuntimeException is a checked exception  
C. Error should generally not be caught  
D. IOException is an unchecked exception  

**Answer:** C  
**Explanation:** Errors represent serious problems that applications should not catch. A is false (unchecked exceptions don't need to be caught/declared), B is false (RuntimeException is unchecked), D is false (IOException is checked).

---

### Question 2: Multi-catch block
```java
try {
    // code
} catch (FileNotFoundException | IOException e) {
    System.out.println(e);
}
```
**What is the result?**  
A. Compiles successfully  
B. Compilation error - IOException is superclass of FileNotFoundException  
C. Compilation error - syntax error  
D. Runtime exception  

**Answer:** B  
**Explanation:** Multi-catch cannot contain exceptions with parent-child relationship. FileNotFoundException extends IOException.

---

### Question 3: finally execution
```java
public static int test() {
    try {
        return 10;
    } finally {
        return 20;
    }
}
System.out.println(test());
```
**What is the output?**  
A. 10  
B. 20  
C. Compilation error  
D. Runtime exception  

**Answer:** B  
**Explanation:** finally block executes after try, and its return statement overrides the try block's return value.

---

### Question 4: try-with-resources
```java
class Resource implements AutoCloseable {
    public void close() { System.out.print("C"); }
}

try (Resource r1 = new Resource(); Resource r2 = new Resource()) {
    System.out.print("T");
}
```
**What is the output?**  
A. TC  
B. TCC  
C. CC  
D. Compilation error  

**Answer:** B  
**Explanation:** Prints "T" from try block, then closes r2 ("C"), then r1 ("C") in reverse order. Output: "TCC".

---

### Question 5: Exception variable scope
```java
try {
    throw new IOException();
} catch (IOException e) {
    System.out.println("IO");
} catch (Exception e) {
    System.out.println("Exception");
}
System.out.println(e);
```
**What is the result?**  
A. Prints "IO" then compilation error  
B. Prints "IO" then runtime error  
C. Compilation error on line with catch  
D. Compilation error on last line  

**Answer:** D  
**Explanation:** Variable 'e' is only in scope within its catch block, not outside. Last line causes compilation error.

---

### Question 6: Suppressed exceptions
```java
class R implements AutoCloseable {
    public void use() throws Exception { throw new Exception("Use"); }
    public void close() throws Exception { throw new Exception("Close"); }
}

try (R r = new R()) {
    r.use();
} catch (Exception e) {
    System.out.println(e.getMessage());
    System.out.println(e.getSuppressed().length);
}
```
**What is the output?**  
A. Use  
   0  
B. Use  
   1  
C. Close  
   0  
D. Close  
   1  

**Answer:** B  
**Explanation:** Primary exception is from use() ("Use"), close() exception is suppressed. getSuppressed() returns array with 1 element.

---

### Question 7: Method overriding
```java
class Parent {
    void method() throws IOException { }
}
class Child extends Parent {
    void method() throws FileNotFoundException { }
}
```
**What is the result?**  
A. Compiles successfully  
B. Compilation error - FileNotFoundException is not declared in Parent  
C. Compilation error - must throw IOException  
D. Runtime exception  

**Answer:** A  
**Explanation:** Overriding method can throw narrower exceptions. FileNotFoundException extends IOException, so it's narrower.

---

### Question 8: Assertion behavior
```java
public static void main(String[] args) {
    int x = -5;
    assert x >= 0 : "Negative";
    System.out.println(x);
}
```
**What happens when run with `java -ea Main`?**  
A. Prints -5  
B. Prints "Negative"  
C. AssertionError with message "Negative"  
D. No output  

**Answer:** C  
**Explanation:** With -ea flag, assertion is enabled. Since x < 0, assertion fails and throws AssertionError with message "Negative".

---

### Question 9: Exception in finally
```java
try {
    throw new IOException("Try");
} finally {
    throw new SQLException("Finally");
}
```
**Which exception is thrown?**  
A. IOException  
B. SQLException  
C. Both exceptions  
D. Compilation error  

**Answer:** B  
**Explanation:** Exception from finally block masks the exception from try block. SQLException is thrown, IOException is lost.

---

### Question 10: AutoCloseable order
```java
class First implements AutoCloseable {
    public void close() { System.out.print("1"); }
}
class Second implements AutoCloseable {
    public void close() { System.out.print("2"); }
}

try (First f = new First(); Second s = new Second()) {
    System.out.print("T");
}
```
**What is the output?**  
A. T12  
B. T21  
C. 12T  
D. 21T  

**Answer:** B  
**Explanation:** Prints "T" from try block, then closes resources in REVERSE order: s.close() prints "2", then f.close() prints "1". Output: "T21".

---

**[Back to Main Guide](README.md)**
