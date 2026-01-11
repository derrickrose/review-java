# Chapter 3: Utilizing Java Object-Oriented Approach

[← Previous Chapter](CHAPTER-02.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-04.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Java Versions** | 1.0, 5, 8, 16, 17, 21 |
| **Code Examples** | 85+ |
| **Study Time** | 20-25 hours |
| **Exam Weight** | ~25% (HIGHEST!) |
| **Difficulty** | ⭐⭐⭐⭐ Very Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Declare and instantiate Java objects including nested class objects
- Explain object life-cycle including creation, reassigning references, and garbage collection
- Understand variable scopes, apply encapsulation, and create immutable objects
- Use local variable type inference (var)
- Implement inheritance, including abstract and sealed types as well as record classes
- Override methods, including that of the Object class
- Implement polymorphism and differentiate between object type and reference type
- Perform reference type casting, identify object types using instanceof operator
- Pattern matching with instanceof operator and switch construct
- Create and use interfaces, identify functional interfaces
- Utilize private, static, and default interface methods
- Create and use enum types with fields, methods, and constructors

---

## 1. Classes and Objects

**Exam Objective:** Declare and instantiate Java objects

### What is a Class?
A class is a blueprint for creating objects. It defines fields (data) and methods (behavior). Objects are instances of classes.

### When to Use
- Need to model real-world entities
- Need multiple instances with same structure
- Need encapsulation of data and behavior

### Why Use Classes
- Code reusability
- Encapsulation (hide internal details)
- Maintainability

### Why Use this and super
- Distinguish parameters from fields
- Avoid constructor duplication
- Call parent functionality

### Why Use Abstract Classes
- Share code among subclasses
- Enforce contract (abstract methods)
- Partial implementation (template pattern)

### Why Use Polymorphism
- Write flexible code (one method, many types)
- Extensibility (add new types easily)
- Loose coupling

### Why Understand Scopes
- Avoid naming conflicts
- Memory management
- Design clean APIs

### Why Use Casting
- Access subclass-specific methods
- Work with polymorphic code
- API compatibility

### Real-World Example
```java
public class BankAccount {
    private double balance;
    private String accountNumber;
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= balance) balance -= amount;
    }
}

BankAccount account = new BankAccount(); // Object created from class
```
**Why:** Encapsulates account data and ensures balance can't be directly manipulated.

### Exam Traps
- ❌ **TRAP:** Order matters - `extends` BEFORE `implements`
  ```java
  class Dog extends Animal implements Runnable { } // ✅
  class Dog implements Runnable extends Animal { } // ❌ Won't compile
  ```

---

## 2. Initialization Order

**Exam Objective:** Explain object life-cycle including creation

### What is Initialization Order?
The sequence in which static/instance variables, blocks, and constructors execute.

### When to Use
- Understanding object creation lifecycle
- Debugging initialization issues

### Why Understand Initialization Order
- Debug initialization bugs
- Predict object state
- Avoid NullPointerException

### Order (MEMORIZE THIS!)
1. Static variables (parent → child)
2. Static blocks (parent → child)
3. Instance variables (parent → child)
4. Instance blocks (parent → child)
5. Constructor (parent → child)

### Real-World Example
```java
class Parent {
    static { System.out.print("1"); }
    { System.out.print("3"); }
    Parent() { System.out.print("4"); }
}

class Child extends Parent {
    static { System.out.print("2"); }
    { System.out.print("5"); }
    Child() { System.out.print("6"); }
}

new Child(); // Output: 123456
new Child(); // Output: 3456 (static blocks run only ONCE)
```

### Exam Traps
- ❌ **TRAP:** Static blocks run ONLY ONCE when class loads
- ❌ **TRAP:** Parent initialization happens BEFORE child

---

## 3. this and super Keywords

**Exam Objective:** Declare and instantiate Java objects

### What are this and super?
- `this` = current object instance
- `super` = parent class reference

### When to Use
- `this`: Distinguish fields from parameters, constructor chaining
- `super`: Call parent constructor/methods

### Why Use this and super
- Avoid naming conflicts (parameter vs field)
- Code reuse (constructor chaining)
- Access parent functionality

### Real-World Example
```java
class Employee {
    private String name;
    
    public Employee(String name) {
        this.name = name; // this distinguishes field from parameter
    }
    
    public Employee() {
        this("Unknown"); // this() calls other constructor
    }
}

class Manager extends Employee {
    public Manager(String name) {
        super(name); // super() calls parent constructor
    }
}
```

### Exam Traps
- ❌ **TRAP:** `this()` or `super()` MUST be FIRST statement
  ```java
  public Child() {
      System.out.println("Hi"); // ❌ Can't come before super()
      super();
  }
  ```
- ❌ **TRAP:** Can't have BOTH `this()` and `super()` in same constructor

---

## 4. Access Modifiers

**Exam Objective:** Understand variable scopes, apply encapsulation

### What are Access Modifiers?
Keywords controlling visibility: `public`, `protected`, `default`, `private`

### When to Use
- `public`: API methods, anyone can access
- `protected`: Subclasses need access
- `default` (package-private): Package-level access
- `private`: Internal implementation only

### Why Use Access Modifiers
- Encapsulation (hide implementation)
- Security (control access)
- Maintainability (change internals safely)

### Access Table
| Modifier | Same Class | Same Package | Subclass (diff pkg) | World |
|----------|:----------:|:------------:|:------------------:|:-----:|
| public | ✅ | ✅ | ✅ | ✅ |
| protected | ✅ | ✅ | ✅ (inheritance only) | ❌ |
| default | ✅ | ✅ | ❌ | ❌ |
| private | ✅ | ❌ | ❌ | ❌ |

### Real-World Example
```java
public class BankAccount {
    private double balance;        // Only this class
    protected void audit() { }     // Subclasses can override
    void packageMethod() { }       // Package-level utilities
    public void deposit() { }      // Public API
}
```

### Exam Traps
- ❌ **TRAP:** `protected` in different package works ONLY through inheritance
  ```java
  // In different package
  class Child extends Parent {
      void test() {
          this.protectedMethod();    // ✅ Works (inheritance)
          
          Parent p = new Parent();
          p.protectedMethod();       // ❌ Won't compile!
      }
  }
  ```

---

## 5. Records (Java 16)

**Exam Objective:** Implement inheritance, including abstract and sealed types as well as record classes

### What is a Record?
Immutable data carrier that auto-generates constructor, getters, `equals()`, `hashCode()`, `toString()`.

### When to Use
- DTOs (Data Transfer Objects)
- Immutable value objects
- API request/response objects
- Need auto-generated methods

### Why Use Records
- Eliminates boilerplate (50+ lines → 1 line)
- Immutable by default
- Type-safe

### Real-World Example
```java
// Before: ~50 lines
public final class PersonOld {
    private final String name;
    private final int age;
    // + constructor, getters, equals, hashCode, toString
}

// After: 1 line!
public record Person(String name, int age) { }

// Usage
Person p = new Person("Alice", 30);
p.name(); // ✅ name(), NOT getName()
```
**Why:** REST API DTOs - clean, immutable, auto JSON serialization.

### Compact Constructor
```java
public record Range(int min, int max) {
    public Range {  // No parameters!
        if (min > max) throw new IllegalArgumentException();
        // Fields auto-assigned after block
    }
}
```

### Exam Traps
- ❌ **TRAP:** Accessor is `name()`, NOT `getName()`
- ❌ **TRAP:** Records CAN have static fields/methods
- ❌ **TRAP:** Records CANNOT extend classes
- ❌ **TRAP:** Records are implicitly `final`
- ❌ **TRAP:** Records CANNOT have instance initializer blocks

---

## 6. Sealed Classes (Java 17)

**Exam Objective:** Implement inheritance, including abstract and sealed types as well as record classes

### What is a Sealed Class?
Class that restricts which classes can extend/implement it using `permits` clause.

### When to Use
- Fixed set of subtypes (3-10 types)
- Need exhaustive pattern matching
- Domain modeling (state machines, result types)

### Why Use Sealed Classes
- Compiler-enforced completeness
- Exhaustive switch (no `default` needed)
- Clear domain boundaries

### Real-World Example
```java
public sealed interface Payment
    permits CreditCard, Cash, BankTransfer { }

public final class CreditCard implements Payment { }
public final class Cash implements Payment { }
public final class BankTransfer implements Payment { }

// Exhaustive switch - no default needed!
void process(Payment p) {
    switch (p) {
        case CreditCard cc -> processCreditCard(cc);
        case Cash cash -> processCash(cash);
        case BankTransfer bt -> processTransfer(bt);
    } // Compiler ensures all cases covered
}
```
**Why:** Adding new payment type forces code update everywhere - no bugs!

### Exam Traps
- ❌ **TRAP:** Permitted classes MUST be `final`, `sealed`, or `non-sealed`
  ```java
  public sealed class Base permits Child { }
  public class Child extends Base { } // ❌ Must be final/sealed/non-sealed
  ```
- ❌ **TRAP:** All permitted classes must be accessible

---

## 7. Inheritance

**Exam Objective:** Implement inheritance, override methods

### What is Inheritance?
A class inherits fields/methods from parent class using `extends`.

### When to Use
- True "is-a" relationship (Dog IS-A Animal)
- Code reuse from parent
- Need polymorphism

### Why Use Inheritance
- Code reusability
- Polymorphic behavior
- Hierarchical organization

### Real-World Example
```java
public class Animal {
    public void makeSound() {
        System.out.println("Some sound");
    }
}

public class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof");
    }
}

Animal a = new Dog();
a.makeSound(); // "Woof" (runtime polymorphism)
```
**Why:** Payment processing - one method handles all payment types polymorphically.

### Method Overriding Rules
| Rule | Valid | Invalid |
|------|-------|---------|
| Return type | Same or covariant | Broader type |
| Access | Same or more accessible | More restrictive |
| Exceptions | Same, narrower, or none | Broader |

### Exam Traps
- ❌ **TRAP:** Cannot override `static` methods (hides instead)
- ❌ **TRAP:** Cannot override `final` methods
- ❌ **TRAP:** Covariant return: `Animal → Dog` ✅, `Dog → Animal` ❌

---

## 8. Abstract Classes

**Exam Objective:** Implement inheritance, including abstract types

### What is an Abstract Class?
Class that cannot be instantiated, may have abstract methods (no implementation).

### When to Use
- Shared code among subclasses
- Force subclasses to implement certain methods
- Partial implementation

### Why Use Abstract Classes
- Code reuse (shared implementation)
- Enforce contract (abstract methods)
- Template pattern

### Real-World Example
```java
public abstract class Shape {
    protected String color;
    
    public abstract double area(); // No implementation
    
    public void display() { // Has implementation
        System.out.println("Color: " + color);
    }
}

public class Circle extends Shape {
    private double radius;
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

### Exam Traps
- ❌ **TRAP:** Cannot instantiate abstract class
  ```java
  Shape s = new Shape(); // ❌ Won't compile
  ```
- ❌ **TRAP:** First concrete subclass MUST implement all abstract methods

---

## 9. Interfaces

**Exam Objective:** Create and use interfaces, identify functional interfaces, utilize private, static, and default interface methods

### What is an Interface?
Contract defining methods classes must implement. Can have default/static/private methods (Java 8+).

### When to Use
- Multiple inheritance of type
- Define capabilities (Runnable, Comparable)
- Unrelated classes need same behavior

### Why Use Interfaces
- Multiple "inheritance"
- Loose coupling
- Polymorphism across unrelated classes

### Real-World Example
```java
public interface Flyable {
    void fly();
    
    default int getMaxAltitude() { // Java 8
        return 10000;
    }
    
    static void info() { // Java 8
        System.out.println("Can fly");
    }
}

public class Duck implements Flyable {
    public void fly() {
        System.out.println("Duck flying");
    }
}
```
**Why:** Plugin systems - define interface, load implementations at runtime.

### Interface Default Method Conflict
```java
interface A {
    default void method() { System.out.println("A"); }
}
interface B {
    default void method() { System.out.println("B"); }
}

class C implements A, B {
    @Override
    public void method() {
        A.super.method(); // Must explicitly choose
    }
}
```

### Exam Traps
- ❌ **TRAP:** Class method WINS over interface default method
  ```java
  class Parent {
      public void test() { }
  }
  interface I {
      default void test() { }
  }
  class Child extends Parent implements I { }
  // Parent.test() is used, NOT I.test()
  ```
- ❌ **TRAP:** Multiple interface defaults conflict - MUST override

---

## 10. Enums

**Exam Objective:** Create and use enum types with fields, methods, and constructors

### What is an Enum?
Type-safe constants with optional fields/methods/constructors.

### When to Use
- Fixed set of constants (days, states, types)
- Type safety over int constants
- Need behavior with constants

### Why Use Enums
- Type-safe (can't pass wrong value)
- Built-in methods: `values()`, `valueOf()`, `ordinal()`
- Can have fields and methods

### Real-World Example
```java
public enum Size {
    SMALL(8, 9.99),
    MEDIUM(10, 12.99),
    LARGE(12, 15.99);
    
    private final int inches;
    private final double price;
    
    Size(int inches, double price) { // Constructor
        this.inches = inches;
        this.price = price;
    }
    
    public double getPrice() { return price; }
}

Size s = Size.MEDIUM;
s.getPrice(); // 12.99
Size.values(); // [SMALL, MEDIUM, LARGE]
Size.valueOf("LARGE"); // LARGE
s.ordinal(); // 1 (starts at 0)
```
**Why:** Pizza ordering - sizes with prices, type-safe selection.

### Exam Traps
- ❌ **TRAP:** `valueOf()` is case-sensitive
  ```java
  Size.valueOf("MEDIUM"); // ✅
  Size.valueOf("medium"); // ❌ IllegalArgumentException
  ```
- ❌ **TRAP:** `ordinal()` starts at 0, not 1
- ❌ **TRAP:** Enum constructors are implicitly `private`

---

## 11. Nested Classes

**Exam Objective:** Declare and instantiate Java objects including nested class objects

### What are Nested Classes?
Classes defined inside other classes. Four types: static nested, inner, local, anonymous.

### When to Use Each

| Type | When to Use |
|------|-------------|
| Static nested | Helper class, no access to outer instance needed |
| Inner | Need access to outer instance fields |
| Local | Class used only in one method |
| Anonymous | One-time implementation |

### Why Use Nested Classes
- Encapsulation (hide helper classes)
- Logical grouping (class only used by outer)
- Access to outer class members

### Real-World Example: Static Nested (Builder Pattern)
```java
// Building database connection with many optional params
public class DatabaseConnection {
    private final String host;
    private final int port;
    private final String database;
    private final boolean useSSL;
    
    private DatabaseConnection(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.useSSL = builder.useSSL;
    }
    
    public static class Builder {
        private String host = "localhost";
        private int port = 5432;
        private String database;
        private boolean useSSL = false;
        
        public Builder database(String db) {
            this.database = db;
            return this;
        }
        
        public Builder host(String host) {
            this.host = host;
            return this;
        }
        
        public Builder useSSL(boolean ssl) {
            this.useSSL = ssl;
            return this;
        }
        
        public DatabaseConnection build() {
            return new DatabaseConnection(this);
        }
    }
}

// Clean, readable API
DatabaseConnection conn = new DatabaseConnection.Builder()
    .database("mydb")
    .host("prod.com")
    .useSSL(true)
    .build();
```
**Why:** Avoid telescoping constructors, optional parameters, clean API.

### Real-World Example: Inner Class (Iterator)
```java
public class MyList<T> {
    private Object[] elements = new Object[10];
    private int size = 0;
    
    public void add(T element) {
        elements[size++] = element;
    }
    
    // Inner class - needs outer's elements
    public class MyIterator {
        private int index = 0;
        
        public boolean hasNext() {
            return index < size;  // Access outer size
        }
        
        @SuppressWarnings("unchecked")
        public T next() {
            return (T) elements[index++];  // Access outer elements
        }
    }
    
    public MyIterator iterator() {
        return new MyIterator();
    }
}

MyList<String> list = new MyList<>();
list.add("A");
list.add("B");
MyList<String>.MyIterator it = list.iterator();
while (it.hasNext()) System.out.println(it.next());
```
**Why:** Iterator needs direct access to list internals.

### Real-World Example: Local Class (Event Handler)
```java
public class Button {
    public void onClick(String action) {
        // Local class - only used in this method
        class ClickHandler {
            void handle() {
                switch (action) {
                    case "SAVE" -> System.out.println("Saving...");
                    case "DELETE" -> System.out.println("Deleting...");
                }
            }
        }
        new ClickHandler().handle();
    }
}
```
**Why:** Handler logic scoped to method, encapsulated.

### Real-World Example: Anonymous Class (Comparator)
```java
List<Employee> emps = getEmployees();

// Sort by salary - anonymous comparator
Collections.sort(emps, new Comparator<Employee>() {
    @Override
    public int compare(Employee e1, Employee e2) {
        return Double.compare(e1.getSalary(), e2.getSalary());
    }
});
```
**Why:** One-time sorting logic, no separate file needed.

### Exam Traps
- ❌ **TRAP:** Static nested can ONLY access static members
- ❌ **TRAP:** Inner class needs outer instance: `outer.new Inner()`

---

## 12. Object Class Methods

**Exam Objective:** Override methods, including that of the Object class

### What are Object Class Methods?
Every class inherits from `Object` class. Key methods: `equals()`, `hashCode()`, `toString()`, `clone()`, `finalize()`.

### When to Use
- Override `equals()`: Custom equality logic (compare by ID, not memory address)
- Override `hashCode()`: Using objects in HashMap, HashSet
- Override `toString()`: Debugging, logging

### Why Override Object Methods
- Custom equality logic
- Use objects in collections (HashMap/HashSet)
- Better debugging (toString)

### equals() and hashCode() Contract (MEMORIZE!)
- If `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()`
- If you override `equals()`, MUST override `hashCode()`

### Real-World Example
```java
public class Employee {
    private int id;
    private String name;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        return id == other.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```
**Why:** Using objects as HashMap keys requires proper equals/hashCode.

### Exam Traps
- ❌ **TRAP:** Override `equals()` but forget `hashCode()` → broken HashMap

---

## 13. Polymorphism

**Exam Objective:** Implement polymorphism and differentiate between object type and reference type

### What is Polymorphism?
Same interface, multiple implementations. Compile-time (overloading) or runtime (overriding).

### When to Use
- Write flexible, reusable code
- Treat different types uniformly

### Why Use Polymorphism
- Flexibility (one interface, many implementations)
- Extensibility (add new types easily)
- Loose coupling

### Real-World Example
```java
public void processPayments(List<Payment> payments) {
    for (Payment p : payments) {
        p.process(); // Calls correct implementation at runtime
    }
}
```

### Casting
```java
Animal a = new Dog(); // Upcasting (automatic)
Dog d = (Dog) a; // Downcasting (explicit)

if (a instanceof Dog d) { // Pattern matching (Java 16+)
    d.bark(); // d already cast
}
```

### Exam Traps
- ❌ **TRAP:** Downcasting non-compatible type → `ClassCastException`

---

## 14. Composition vs Inheritance

**Exam Objective:** Implement inheritance (IS-A vs HAS-A relationships)

### What is Composition?
"Has-a" relationship - class contains other objects.

### When to Use
| Inheritance (IS-A) | Composition (HAS-A) |
|--------------------|---------------------|
| Dog IS-A Animal | Car HAS-A Engine |
| True specialization | Combine behaviors |
| Single parent | Multiple components |

### Real-World Example
```java
// Composition
class Car {
    private Engine engine; // HAS-A engine
    private Transmission transmission; // HAS-A transmission
    
    public void start() {
        engine.start();
    }
}

// Can swap components at runtime!
car.setEngine(new V8Engine());
```

### Why Favor Composition
- More flexible
- Change behavior at runtime
- Avoid deep inheritance hierarchies

### Exam Traps
- ❌ **TRAP:** Don't confuse "IS-A" with "HAS-A"
  ```java
  class Car extends Engine { }  // ❌ Wrong! Car is not an Engine
  class Car {                   // ✅ Correct! Car has an Engine
      private Engine engine;
  }
  ```
- ❌ **TRAP:** Inheritance is permanent, composition is flexible
  ```java
  // Inheritance - can't change at runtime
  class SportsCar extends V8Engine { }
  
  // Composition - can swap engines
  class SportsCar {
      private Engine engine;
      void setEngine(Engine e) { this.engine = e; }
  }
  ```

---

## 15. Variable Scopes

**Exam Objective:** Understand variable scopes

### What are Variable Scopes?
Scope determines where a variable can be accessed. Three types: local (method), instance (object), class (static).

### When to Use Each

| Scope | Where Declared | Lifetime | Access |
|-------|----------------|----------|--------|
| **Local** | Inside method/block | Method execution | Only in that method |
| **Instance** | Class level (non-static) | Object lifetime | All instance methods |
| **Class** | Class level (static) | Class loaded to unloaded | All methods (static/instance) |

### Why Understand Scopes
- Avoid naming conflicts
- Memory efficiency (local variables freed quickly)
- Design clean APIs

### Real-World Example
```java
public class ScopeDemo {
    // Class scope (static)
    private static int classCounter = 0;
    
    // Instance scope
    private int instanceId;
    
    public void method() {
        // Local scope
        int localVar = 10;
        
        System.out.println(classCounter);   // ✅ Can access class variable
        System.out.println(instanceId);     // ✅ Can access instance variable
        System.out.println(localVar);       // ✅ Can access local variable
    }
    
    public void another() {
        // System.out.println(localVar);    // ❌ Can't access localVar from other method
    }
}
```

### Exam Traps
- ❌ **TRAP:** Local variables MUST be initialized before use
  ```java
  int x;
  System.out.println(x); // ❌ Won't compile - not initialized
  ```
- ❌ **TRAP:** Local variables shadow instance variables
  ```java
  class Test {
      int x = 10;
      void method() {
          int x = 20; // Shadows instance variable
          System.out.println(x); // 20, not 10
      }
  }
  ```

---

## 16. Local Variable Type Inference (var)

**Exam Objective:** Use local variable type inference

### What is var?
Keyword that lets compiler infer variable type from initializer (Java 10+).

### When to Use
- Local variables with obvious types
- Enhanced for loops
- Try-with-resources

### Why Use var
- Less verbose code
- Easier refactoring
- Code stays type-safe

### Real-World Example
```java
// Without var
Map<String, List<Integer>> map = new HashMap<>();
StringBuilder builder = new StringBuilder();

// With var
var map = new HashMap<String, List<Integer>>();
var builder = new StringBuilder();
var list = List.of(1, 2, 3);

// For loops
for (var entry : map.entrySet()) {
    var key = entry.getKey();
    var value = entry.getValue();
}
```

### Exam Traps
- ❌ **TRAP:** var ONLY for local variables
  ```java
  class Test {
      // var x = 10;        // ❌ Can't use for instance variables
      // static var y = 5;  // ❌ Can't use for class variables
      
      void method(var x) { } // ❌ Can't use for parameters
  }
  ```
- ❌ **TRAP:** MUST initialize when declaring
  ```java
  var x;          // ❌ Won't compile
  var y = null;   // ❌ Won't compile - can't infer type
  var z = 10;     // ✅ OK
  ```

---

## 17. Object Lifecycle and Garbage Collection

**Exam Objective:** Explain object life-cycle including creation, reassigning references, and garbage collection

### What is Object Lifecycle?
1. **Creation:** `new` keyword allocates memory
2. **In Use:** Object is reachable and being used
3. **Eligible for GC:** No references point to it
4. **Garbage Collected:** Memory reclaimed by GC

### When to Understand This
- Debugging memory leaks
- Optimizing performance
- Understanding when objects are freed
- Exam questions on GC eligibility

### Why Understand GC
- Avoid memory leaks
- Optimize performance
- Debug OutOfMemoryError

### When Objects Become Eligible for GC
- All references set to `null`
- References go out of scope
- Object isolated in "island of isolation"

### Real-World Example
```java
public class GCDemo {
    public void createObjects() {
        Employee e1 = new Employee("Alice");  // Object created
        Employee e2 = new Employee("Bob");
        
        e1 = null;  // Alice object now eligible for GC
        
        e2 = new Employee("Charlie");  // Bob object now eligible for GC
        
    } // Charlie object eligible for GC when method ends
    
    public void islandOfIsolation() {
        Employee e1 = new Employee("X");
        Employee e2 = new Employee("Y");
        
        e1.manager = e2;
        e2.manager = e1;  // Circular reference
        
        e1 = null;
        e2 = null;  // Both eligible for GC (island of isolation)
    }
}
```
**Why:** JVM automatically reclaims memory - no manual memory management needed.

### Exam Traps
- ❌ **TRAP:** Calling `System.gc()` suggests GC, doesn't guarantee it
  ```java
  System.gc();  // Only a suggestion to JVM!
  ```
- ❌ **TRAP:** `finalize()` deprecated (Java 9+) - don't rely on it

---

## 18. Immutable Objects

**Exam Objective:** Apply encapsulation, and create immutable objects

### What is an Immutable Object?
Object whose state cannot change after creation. All fields are `final`, no setters.

### When to Use
- Thread safety needed
- HashMap/HashSet keys
- Value objects (Money, Date)
- String-like behavior

### Why Use Immutability
- Thread-safe (no synchronization needed)
- Safe as HashMap keys
- Easier to reason about
- Cacheable

### Real-World Example
```java
public final class Money {  // Class is final
    private final double amount;      // Fields are final
    private final String currency;
    
    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    // Only getters, NO setters
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    
    // Return NEW object for operations
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount + other.amount, currency);
    }
}

Money m1 = new Money(100, "USD");
Money m2 = m1.add(new Money(50, "USD")); // Returns NEW object
// m1 is unchanged!
```
**Why:** Bank account balances - no accidental modifications, thread-safe.

### Immutability Rules
1. Make class `final`
2. Make all fields `private final`
3. No setter methods
4. Don't expose mutable objects
5. Return defensive copies

### Exam Traps
- ❌ **TRAP:** `final` on field prevents reassignment, not mutation
  ```java
  private final List<String> list = new ArrayList<>();
  // list = new ArrayList<>();  // ❌ Can't reassign
  list.add("x");                // ✅ Can mutate contents!
  ```

---

## 19. Functional Interfaces

**Exam Objective:** Create and use interfaces, identify functional interfaces

### What is a Functional Interface?
Interface with exactly ONE abstract method. Can have default/static methods.

### When to Use
- Lambda expressions
- Method references
- Streams API
- Callback patterns

### Why Use Functional Interfaces
- Enable functional programming
- Concise code with lambdas
- Higher-order functions

### Real-World Example
```java
@FunctionalInterface  // Optional but recommended
public interface Calculator {
    int calculate(int a, int b);  // ONE abstract method
    
    // Can have default methods
    default int square(int x) {
        return calculate(x, x);
    }
    
    // Can have static methods
    static int add(int a, int b) {
        return a + b;
    }
}

// Usage with lambda
Calculator multiply = (a, b) -> a * b;
System.out.println(multiply.calculate(5, 3)); // 15

Calculator divide = (a, b) -> a / b;
System.out.println(divide.calculate(10, 2)); // 5
```

### Built-in Functional Interfaces
```java
// Predicate<T> - takes T, returns boolean
Predicate<String> isEmpty = s -> s.isEmpty();
isEmpty.test(""); // true

// Consumer<T> - takes T, returns void
Consumer<String> printer = s -> System.out.println(s);
printer.accept("Hello");

// Function<T,R> - takes T, returns R
Function<String, Integer> length = s -> s.length();
length.apply("Java"); // 4

// Supplier<T> - takes nothing, returns T
Supplier<Double> random = () -> Math.random();
random.get(); // 0.xyz
```

### Exam Traps
- ❌ **TRAP:** Must have exactly ONE abstract method
  ```java
  @FunctionalInterface
  interface Invalid {
      void method1();
      void method2();  // ❌ Won't compile - two abstract methods
  }
  ```

---

## 20. Pattern Matching with instanceof and switch

**Exam Objective:** Pattern matching with instanceof operator and switch construct

### What is Pattern Matching?
Combines type check and cast in one step (Java 16+).

### When to Use
- Type checking and casting
- Switch with different types (Java 21)
- Record deconstruction

### Why Use Pattern Matching
- Less boilerplate
- No ClassCastException risk
- Type-safe
- More readable code

### Real-World Example: Payment Processing
```java
// Processing different payment types
public class PaymentProcessor {
    public void process(Object payment) {
        // OLD WAY - verbose, error-prone
        if (payment instanceof CreditCard) {
            CreditCard cc = (CreditCard) payment;
            processCreditCard(cc.getCardNumber(), cc.getCvv());
        } else if (payment instanceof BankTransfer) {
            BankTransfer bt = (BankTransfer) payment;
            processBankTransfer(bt.getAccountNumber(), bt.getRoutingNumber());
        }
        
        // NEW WAY - pattern matching (Java 16+)
        if (payment instanceof CreditCard cc) {
            processCreditCard(cc.getCardNumber(), cc.getCvv());
        } else if (payment instanceof BankTransfer bt) {
            processBankTransfer(bt.getAccountNumber(), bt.getRoutingNumber());
        }
        
        // EVEN BETTER - switch with pattern matching (Java 21)
        switch (payment) {
            case CreditCard cc -> processCreditCard(cc.getCardNumber(), cc.getCvv());
            case BankTransfer bt -> processBankTransfer(bt.getAccountNumber(), bt.getRoutingNumber());
            case Cash cash -> processCash(cash.getAmount());
            case null -> throw new IllegalArgumentException("Payment required");
            default -> throw new UnsupportedOperationException("Unknown payment type");
        }
    }
}

// With sealed types + pattern matching = COMPILER ENFORCED!
sealed interface Payment permits CreditCard, BankTransfer, Cash { }
record CreditCard(String cardNumber, String cvv) implements Payment { }
record BankTransfer(String accountNumber, String routingNumber) implements Payment { }
record Cash(double amount) implements Payment { }

public void processPayment(Payment payment) {
    // No default needed - compiler knows all types!
    switch (payment) {
        case CreditCard(var num, var cvv) -> processCreditCard(num, cvv);
        case BankTransfer(var acc, var routing) -> processBankTransfer(acc, routing);
        case Cash(var amount) -> processCash(amount);
    }
}
```
**Why:** Payment systems handle multiple types - pattern matching eliminates casting, reduces bugs, compiler ensures all types handled.

### instanceof Pattern Matching
```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;  // Manual cast
    System.out.println(s.toUpperCase());
}

// New way (Java 16+)
if (obj instanceof String s) {  // Pattern variable 's'
    System.out.println(s.toUpperCase());  // No cast needed!
}

// With negation
if (!(obj instanceof String s)) {
    return;
}
// s NOT in scope here!
```

### Pattern Matching in switch (Java 21)
```java
public String describe(Object obj) {
    return switch (obj) {
        case null -> "null";
        case String s -> "String of length " + s.length();
        case Integer i -> "Integer: " + i;
        case Long l -> "Long: " + l;
        case double[] arr -> "Array of length " + arr.length;
        default -> "Unknown type";
    };
}
```

### Record Pattern Matching
```java
record Point(int x, int y) { }

Object obj = new Point(10, 20);

// Deconstruct in instanceof
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y); // 30
}

// Deconstruct in switch
switch (obj) {
    case Point(int x, int y) when x > 0 && y > 0 -> 
        System.out.println("Quadrant 1");
    case Point(int x, int y) -> 
        System.out.println("Other quadrant");
}
```

### Exam Traps
- ❌ **TRAP:** Pattern variable scope
  ```java
  if (obj instanceof String s || s.length() > 0) // ❌ s not in scope!
  if (obj instanceof String s && s.length() > 0) // ✅ s in scope
  ```
- ❌ **TRAP:** Can't redeclare pattern variable
  ```java
  if (obj instanceof String s) {
      String s = "test"; // ❌ s already declared
  }
  ```

---

## 21. Reference Type Casting

**Exam Objective:** Perform reference type casting, identify object types using instanceof operator

### What is Casting?
Converting one reference type to another. Upcasting (automatic) vs Downcasting (explicit).

### When to Use
- **Upcasting:** Always safe, automatic
- **Downcasting:** Need specific type methods

### Why Use Casting
- Access subclass-specific methods
- Work with polymorphic APIs
- Compatibility with legacy code

### Real-World Example
```java
class Animal {
    void eat() { }
}

class Dog extends Animal {
    void bark() { }
}

// Upcasting (automatic)
Dog dog = new Dog();
Animal animal = dog;  // Automatic upcasting

// Downcasting (explicit, risky)
Animal a = new Dog();
Dog d = (Dog) a;  // Explicit cast needed
d.bark();  // Now can call Dog methods

// Safe downcasting with instanceof
if (a instanceof Dog) {
    Dog d2 = (Dog) a;
    d2.bark();
}

// Better: Pattern matching (Java 16+)
if (a instanceof Dog d3) {
    d3.bark();  // Already cast!
}
```

### Exam Traps
- ❌ **TRAP:** Downcasting wrong type → ClassCastException
  ```java
  Animal a = new Animal();
  Dog d = (Dog) a;  // ❌ Runtime ClassCastException!
  ```
- ❌ **TRAP:** Incompatible types won't compile
  ```java
  String s = "test";
  Integer i = (Integer) s;  // ❌ Won't compile - incompatible types
  ```

---

## Real Exam Questions (From Shared Sources)

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes** (2.4 minutes per question)
- **Passing score:** 68% (34 correct answers minimum)
- **Question types:** Multiple choice (5-8 options), Multi-select
- **Difficulty:** Very High - reported as toughest Java exam
- **Style:** Code-heavy scenarios, multiple topics per question

**Based on actual test-taker experiences from Enthuware and exam forums**

---

### Question 1: Sealed Classes with Pattern Matching
```java
sealed class Account permits BankAccount { }
sealed class BankAccount extends Account permits CreditAccount { }
non-sealed class CreditAccount extends BankAccount { }

public class Test {
    public static void main(String[] args) {
        Account cA = new CreditAccount();
        if (cA instanceof BankAccount ba) {
            ba.methodB();
            if (ba instanceof CreditAccount ca) {
                ca.methodC();
            }
        }
    }
}
```
**What compiles?**
A. All code compiles
B. Compilation error - sealed class cannot permit sealed class
C. Compilation error - non-sealed must be final
D. Runtime error

**Answer:** A (assuming methods exist)

---

### Question 2: Record Restrictions
```java
public record Customer(String customerId, String name) {
    // Line 3
    private static int counter = 0;
    
    // Line 4
    public String getFormattedName() {
        return name.toUpperCase();
    }
    
    // Line 5
    public static String getNextId() {
        return "CUST-" + (++counter);
    }
    
    // Line 6
    static {
        counter = 1000;
    }
    
    // Line 7
    public record Address(String street) { }
}
```
**Which is TRUE?**
A. Compilation error at Line 3 - no static fields
B. Compilation error at Line 4 - no instance methods
C. Compilation error at Line 6 - no static blocks
D. All lines compile successfully

**Answer:** D (records CAN have static members, instance methods, nested records)

---

### Question 3: Initialization Order
```java
class Base {
    static { System.out.print("A"); }
    { System.out.print("B"); }
    Base() { System.out.print("C"); }
}

class Derived extends Base {
    static { System.out.print("D"); }
    { System.out.print("E"); }
    Derived() { System.out.print("F"); }
}

public class Test {
    public static void main(String[] args) {
        new Derived();
        new Derived();
    }
}
```
**Output?**
A. ADBCEFBCEF
B. ADBCEFDADBCEF
C. BCEFBCEF
D. ABCDEF

**Answer:** A (static blocks run once: AD, then each object: BCEF)

---

### Question 4: Access Modifiers (EXAM TRAP!)
```java
package com.alpha;
public class Parent {
    protected void process() { }
}
```
```java
package com.beta;
public class Child extends Parent {
    void test() {
        this.process();        // Line 1
        Parent p = new Parent();
        p.process();           // Line 2
    }
}
```
**Result?**
A. Both lines compile
B. Line 1 fails
C. Line 2 fails
D. Both lines fail

**Answer:** C (protected only works through inheritance, not reference!)

---

### Question 5: Enum Methods
```java
enum Size { SMALL, MEDIUM, LARGE }

public class Test {
    public static void main(String[] args) {
        Size s = Size.valueOf("MEDIUM");
        System.out.println(s.ordinal());
    }
}
```
**Output?**
A. 0
B. 1
C. 2
D. IllegalArgumentException

**Answer:** B (MEDIUM is at index 1, ordinal starts at 0)

---

### Question 6: Interface Default Method Conflict
```java
interface A {
    default void test() { System.out.println("A"); }
}
interface B {
    default void test() { System.out.println("B"); }
}
class Parent {
    public void test() { System.out.println("Parent"); }
}

class Child extends Parent implements A, B { }

new Child().test();
```
**Output?**
A. A
B. B
C. Parent
D. Compilation error

**Answer:** C (class method wins over interface defaults!)

---

### Question 7: Constructor Chaining
```java
class Parent {
    Parent() { System.out.print("A"); }
    Parent(int x) { this(); System.out.print("B"); }
}

class Child extends Parent {
    Child() {
        System.out.print("C");
        super(10);
    }
}
```
**Result?**
A. ABC
B. CAB
C. Compilation error
D. Runtime error

**Answer:** C (super() must be FIRST statement!)

---

### Question 8: Record Pattern Matching
```java
record Point(int x, int y) { }

Object obj = new Point(10, 20);

if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y);
}
```
**Output?**
A. 30
B. Compilation error - pattern matching not allowed
C. ClassCastException
D. 1020

**Answer:** A (record pattern matching with destructuring)

---

### Question 9: Covariant Return Types
```java
class Animal {
    public Animal reproduce() { return new Animal(); }
}

class Dog extends Animal {
    @Override
    public Dog reproduce() { return new Dog(); }
}
```
**Compiles?**
A. Yes - covariant return type
B. No - must return Animal
C. No - cannot override
D. Runtime error

**Answer:** A (Dog is subtype of Animal - covariant return allowed!)

---

### Question 10: Sealed Class Rules
```java
public sealed class Shape permits Circle { }
public class Circle extends Shape { }
```
**Result?**
A. Compiles
B. Circle must be final
C. Circle must be sealed or non-sealed
D. Both B and C

**Answer:** D (permitted class must be final, sealed, or non-sealed)

---

### Question 11: Local Variable Type Inference (var)
```java
public class Test {
    public static void main(String[] args) {
        var list = List.of(1, 2, 3);
        var x = null;
        var y;
    }
}
```
**Which lines compile?**
A. All lines compile
B. Only var list compiles
C. var list and var x compile
D. var list and var y compile

**Answer:** B (var needs initializer, can't infer from null)

---

### Question 12: Variable Scope
```java
public class Test {
    static int x = 10;
    
    public static void main(String[] args) {
        int x = 20;
        System.out.println(x);
    }
}
```
**Output?**
A. 10
B. 20
C. Compilation error
D. Runtime error

**Answer:** B (local variable shadows static variable)

---

### Question 13: Garbage Collection
```java
public class Test {
    public static void main(String[] args) {
        String s1 = new String("Java");
        String s2 = new String("Java");
        s1 = s2;
        // Line X
    }
}
```
**At Line X, how many objects are eligible for GC?**
A. 0
B. 1
C. 2
D. 3

**Answer:** B (first "Java" object created by s1 is now eligible)

---

### Question 14: Immutable Objects
```java
public final class Person {
    private final String name;
    private final List<String> hobbies;
    
    public Person(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = hobbies;
    }
    
    public List<String> getHobbies() {
        return hobbies;
    }
}
```
**Is this class truly immutable?**
A. Yes
B. No - name can be changed
C. No - hobbies list can be modified
D. No - class should not be final

**Answer:** C (hobbies list can be modified externally - needs defensive copy)

---

### Question 15: Functional Interface
```java
@FunctionalInterface
interface MyInterface {
    void method1();
    default void method2() { }
    static void method3() { }
}
```
**Does this compile?**
A. Yes
B. No - too many methods
C. No - can't have default methods
D. No - can't have static methods

**Answer:** A (functional interface = 1 abstract method, can have default/static)

---

### Question 16: Pattern Matching Scope
```java
Object obj = "test";

if (obj instanceof String s && s.length() > 2) {
    System.out.println("A");
}

if (obj instanceof String s || s.length() > 2) {
    System.out.println("B");
}
```
**Result?**
A. A
B. B
C. AB
D. Compilation error at second if

**Answer:** D (s not in scope after ||)

---

### Question 17: Record Deconstruction
```java
record Point(int x, int y) { }

Object obj = new Point(5, 10);

if (obj instanceof Point(int a, int b)) {
    System.out.println(a + b);
}
```
**Output?**
A. 15
B. 510
C. Compilation error
D. ClassCastException

**Answer:** A (record pattern matching with deconstruction)

---

### Question 18: Downcasting
```java
Object obj = new StringBuilder("Java");

StringBuilder sb = (StringBuilder) obj;
System.out.println(sb.length());

String s = (String) obj;
System.out.println(s.length());
```
**Result?**
A. 4<br>4
B. 4<br>ClassCastException
C. ClassCastException at first cast
D. Compilation error

**Answer:** B (first cast OK, second throws ClassCastException)

---

### Question 19: Abstract Class vs Interface
```java
abstract class A {
    abstract void method();
}

interface B {
    void method();
}

class C extends A implements B {
    void method() {
        System.out.println("C");
    }
}
```
**Does this compile?**
A. Yes
B. No - method() must be public in C
C. No - can't extend and implement
D. No - A and B both have method()

**Answer:** B (interface methods are public, override must be public)

---

### Question 20: Object Methods
```java
class Employee {
    int id;
    
    public boolean equals(Object obj) {
        if (obj instanceof Employee e) {
            return id == e.id;
        }
        return false;
    }
}

Employee e1 = new Employee();
e1.id = 1;
Employee e2 = new Employee();
e2.id = 1;

Set<Employee> set = new HashSet<>();
set.add(e1);
set.add(e2);

System.out.println(set.size());
```
**Output?**
A. 1
B. 2
C. Compilation error
D. Runtime error

**Answer:** B (hashCode() not overridden - violates contract, two objects in Set)

---

## Summary

### Key Points
- `extends` before `implements`
- Records: immutable, auto methods, can have static members
- Sealed: fixed subtypes, exhaustive switch
- Initialization: static → instance → constructor (parent → child)
- Access: protected in diff package only works through inheritance
- equals() + hashCode() contract
- Enums: `valueOf()` case-sensitive, `ordinal()` starts at 0


### Common Exam Patterns (Reported by Test-Takers)
- **Initialization order questions** - combining static/instance, parent/child
- **Access modifier traps** - especially `protected` across packages
- **Record restrictions** - what's allowed vs not allowed
- **Sealed class exhaustiveness** - pattern matching without default
- **Interface default method conflicts** - class vs interface resolution
- **Constructor chaining traps** - this()/super() must be first
- **equals/hashCode contract** - breaking HashMap behavior
- **Pattern matching scope** - when variables are in scope
- **Covariant returns** - allowed return types in overriding
- **Enum methods** - values(), valueOf(), ordinal() behavior

---

## Next Steps

✅ **Completed:** OOP with real exam questions  
➡️ **Next:** [Exception Handling](CHAPTER-04.md)

[← Previous Chapter](CHAPTER-02.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-04.md)
