# Chapter 7: Modules (Java Platform Module System)

[← Previous Chapter](CHAPTER-06.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-08.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Packaging and Deploying Java Code |
| **Java Versions** | 9 (JPMS introduced) |
| **Code Examples** | 30+ |
| **Study Time** | 8-10 hours |
| **Exam Weight** | ~10-15% |
| **Difficulty** | ⭐⭐⭐⭐ Very Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Define modules and expose module content, including that by reflection, and declare module dependencies
- Define services, providers, and consumers
- Compile Java code, create modular and non-modular jars, runtime images
- Implement migration to modules using unnamed and automatic modules

---

## 1. Module System Basics

**Exam Objective:** Define modules and expose module content

### What is the Java Platform Module System (JPMS)?
JPMS (Project Jigsaw) groups related packages and resources into modules with explicit dependencies. Each module declares what it requires and what it exports.

### When to Use
- Large applications with multiple components
- Libraries that need strong encapsulation
- Applications requiring reduced runtime footprint
- Preventing accidental usage of internal APIs

### Why Use Modules
- **Strong encapsulation** - internal packages remain hidden
- **Reliable configuration** - explicit dependencies prevent classpath hell
- **Reduced runtime size** - create custom runtime images with only needed modules
- **Better performance** - JVM can optimize based on module graph

### Real-World Example
```java
// Traditional classpath problem (Before Java 9)
// Internal implementation accidentally exposed
package com.myapp.internal;
public class InternalUtil { } // Anyone can access this!

// With modules (Java 9+)
// module-info.java
module com.myapp {
    exports com.myapp.api;  // Only API package exposed
    // com.myapp.internal is NOT exported = hidden from other modules
}
```
**Why:** Prevents accidental dependencies on internal implementation details.

### Exam Traps
- ❌ **TRAP:** Module names use dots like packages, but they're NOT hierarchical
  ```java
  module com.myapp { }
  module com.myapp.utils { } // NOT a submodule! Completely separate module
  ```
- ❌ **TRAP:** Modules can't have cyclic dependencies
  ```java
  // module A
  module com.a {
      requires com.b; // ❌ Won't compile if B also requires A
  }
  ```
- ❌ **TRAP:** module-info.java must be in root of module (with packages)
  ```java
  src/
    module-info.java  // ✅ Correct location
    com/
      myapp/
        Main.java
  ```

---

## 2. Module Declaration (module-info.java)

**Exam Objective:** Define modules and declare module dependencies

### What is module-info.java?
Module descriptor file that declares module name, dependencies (requires), and exposed packages (exports).

### When to Use
- Every module must have module-info.java
- Place at root of source directory
- One module-info per module

### Why Use module-info.java
- Explicit contract of module's API
- Compiler verifies all dependencies
- Strong encapsulation at compile and runtime
- Self-documenting module structure

### Real-World Example
```java
// module-info.java for an order processing module
module com.mystore.order {
    // Dependencies - what this module needs
    requires com.mystore.customer;
    requires java.sql;  // JDK module
    
    // API - what this module exposes
    exports com.mystore.order.api;
    exports com.mystore.order.dto;
    
    // Internal packages NOT exported (hidden):
    // - com.mystore.order.internal
    // - com.mystore.order.impl
}
```
**Why:** REST API service needs customer module but keeps implementation details private.

### Exam Traps
- ❌ **TRAP:** Filename must be exactly `module-info.java` (case-sensitive)
  ```java
  Module-Info.java  // ❌ Wrong!
  moduleinfo.java   // ❌ Wrong!
  module-info.java  // ✅ Correct
  ```
- ❌ **TRAP:** Module name must be unique - no duplicate names allowed
  ```java
  // Project A
  module com.myapp { }
  
  // Project B (different project)
  module com.myapp { } // ❌ Conflict! Module names must be globally unique
  ```
- ❌ **TRAP:** java.base is always implicitly required (don't need to declare)
  ```java
  module com.myapp {
      requires java.base;  // ❌ Redundant! Already implicit
  }
  ```
- ❌ **TRAP:** Packages can't be split across modules
  ```java
  // Module A
  module com.a {
      exports com.shared;  // contains: com.shared.ClassA
  }
  
  // Module B
  module com.b {
      exports com.shared;  // contains: com.shared.ClassB
  }
  // ❌ Illegal! Same package in two modules - "split package"
  ```

---

## 3. requires Directive

**Exam Objective:** Declare module dependencies

### What is requires?
Directive that declares a module dependency. The module needs classes from the required module to compile and run.

### When to Use
| Modifier | When to Use | Description |
|----------|-------------|-------------|
| `requires` | Always need module | Normal dependency |
| `requires transitive` | Re-export dependency | Consumers get transitive access |
| `requires static` | Compile-time only | Optional at runtime |

### Why Use requires
- Explicit dependencies (no more hidden classpath dependencies)
- Compiler verifies all required modules exist
- Runtime checks module is available
- Documentation of module relationships

### Real-World Example
```java
// Payment module depends on order module
module com.mystore.payment {
    requires com.mystore.order;  // Need order classes
    
    // Can now use:
    import com.mystore.order.Order;
    
    public class PaymentProcessor {
        public void process(Order order) { }
    }
}

// transitive example - logging facade
module com.myapp.logging {
    requires transitive org.slf4j;  // Re-export SLF4J
    exports com.myapp.logging.api;
}

module com.myapp {
    requires com.myapp.logging;
    // Gets org.slf4j automatically via transitive!
}

// static example - annotation processor
module com.myapp {
    requires static lombok;  // Only needed at compile-time
    // Not required at runtime
}
```
**Why:** Payment needs Order DTOs; logging facade re-exports SLF4J; Lombok only for compilation.

### Exam Traps
- ❌ **TRAP:** requires without exports won't help
  ```java
  // Module A
  module com.a {
      // No exports! Nothing to use
  }
  
  // Module B
  module com.b {
      requires com.a;  // ❌ Can't access anything - A exports nothing!
  }
  ```
- ❌ **TRAP:** transitive must be explicit - doesn't chain automatically
  ```java
  // Module A
  module com.a {
      requires com.b;  // Not transitive!
  }
  
  // Module C
  module com.c {
      requires com.a;
      // ❌ Can't access com.b - need "requires com.b" explicitly
  }
  ```
- ❌ **TRAP:** static means optional at runtime, but class must still exist at compile-time
  ```java
  module com.myapp {
      requires static org.junit;  // ✅ Compiles even if JUnit missing at runtime
  }
  // But if JUnit classes actually used at runtime → ClassNotFoundException
  ```
- ❌ **TRAP:** Can't require same module twice
  ```java
  module com.myapp {
      requires java.sql;
      requires java.sql;  // ❌ Duplicate requires!
  }
  ```

---

## 4. exports Directive

**Exam Objective:** Expose module content

### What is exports?
Directive that makes a package accessible to other modules. Only public/protected types in exported packages are accessible.

### When to Use
| Type | Syntax | When |
|------|--------|------|
| **Unqualified export** | `exports com.pkg;` | Make package public to ALL modules |
| **Qualified export** | `exports com.pkg to com.module;` | Make package public to SPECIFIC modules only |

### Why Use exports
- Strong encapsulation - internal packages stay private
- Clear API contract
- Prevents accidental usage of internal classes
- Breaking changes only affect exported packages

### Real-World Example
```java
// User service module with API and implementation separation
module com.mystore.user {
    requires com.mystore.database;
    
    // Public API - available to everyone
    exports com.mystore.user.api;
    exports com.mystore.user.dto;
    
    // Qualified export - only for testing
    exports com.mystore.user.internal to com.mystore.user.test;
    
    // NOT exported (completely hidden):
    // - com.mystore.user.impl
    // - com.mystore.user.util
}

// Other modules can only use:
import com.mystore.user.api.UserService;  // ✅ Exported
import com.mystore.user.dto.User;          // ✅ Exported
import com.mystore.user.impl.UserServiceImpl;  // ❌ Not exported!
```
**Why:** Clear separation between public API and implementation. Test module gets special access.

### Exam Traps
- ❌ **TRAP:** exports makes package public, not module
  ```java
  module com.myapp {
      exports com.myapp;  // Exports PACKAGE com.myapp
      // Does NOT export whole module!
  }
  ```
- ❌ **TRAP:** Sub-packages are NOT automatically exported
  ```java
  module com.myapp {
      exports com.myapp.api;
      // Does NOT export com.myapp.api.dto automatically!
  }
  ```
- ❌ **TRAP:** Can't export package you don't own
  ```java
  module com.myapp {
      exports java.lang;  // ❌ Can't export JDK packages!
  }
  ```
- ❌ **TRAP:** Qualified export requires target module to exist
  ```java
  module com.myapp {
      exports com.myapp.internal to com.nonexistent;  // ❌ Module not found
  }
  ```
- ❌ **TRAP:** Only public and protected types are accessible (package-private still hidden)
  ```java
  package com.myapp.api;
  
  public class PublicAPI { }        // ✅ Accessible when exported
  protected class ProtectedAPI { }  // ✅ Accessible when exported
  class PackagePrivate { }          // ❌ Still hidden even with export!
  ```

---

## 5. opens Directive and Reflection

**Exam Objective:** Expose module content by reflection

### What is opens?
Directive that allows runtime-only reflective access to packages. Unlike exports (compile + runtime), opens is runtime only.

### When to Use
| Type | Syntax | Purpose |
|------|--------|---------|
| **opens** | `opens com.pkg;` | Allow reflection on package |
| **opens...to** | `opens com.pkg to module;` | Allow reflection for specific module |
| **open module** | `open module com.m { }` | Open ALL packages for reflection |

### Why Use opens
- Frameworks need reflection (Hibernate, Spring, JSON libraries)
- Dependency injection frameworks
- Testing frameworks (JUnit, Mockito)
- Serialization libraries (Jackson, Gson)

### Real-World Example
```java
// Entity module with Hibernate/JPA entities
module com.mystore.entity {
    requires java.persistence;
    
    // API exported for normal access
    exports com.mystore.entity.dto;
    
    // Opens for Hibernate reflection (private fields)
    opens com.mystore.entity.model to org.hibernate.orm.core;
    
    // Opens for Jackson JSON serialization
    opens com.mystore.entity.dto to com.fasterxml.jackson.databind;
}

// Entity class
package com.mystore.entity.model;

@Entity
public class User {
    @Id
    private Long id;  // Hibernate needs reflection to access private field
    private String name;
}
```
**Why:** Hibernate uses reflection to access private fields. opens grants this without compile-time visibility.

### Exam Traps
- ❌ **TRAP:** opens does NOT allow compile-time access
  ```java
  // Module A
  module com.a {
      opens com.a.internal;  // Only reflection access!
  }
  
  // Module B
  module com.b {
      requires com.a;
  }
  
  // In Module B code:
  import com.a.internal.Util;  // ❌ Compilation error! opens != exports
  ```
- ❌ **TRAP:** open module exports NOTHING by default
  ```java
  open module com.myapp {
      // All packages open for reflection
      // But NOTHING is exported for compile-time access!
  }
  
  // Other modules can't compile against it without explicit exports
  ```
- ❌ **TRAP:** Can't open and export to different modules
  ```java
  module com.myapp {
      exports com.myapp.api to com.module1;
      opens com.myapp.api to com.module2;  // ✅ Allowed! Different purposes
  }
  ```
- ❌ **TRAP:** opens doesn't affect setAccessible() for exported packages
  ```java
  // Already exported package
  module com.myapp {
      exports com.myapp.api;
      opens com.myapp.api;  // ❌ Redundant if already exported!
  }
  ```

---

## 6. Module Types (Named, Automatic, Unnamed)

**Exam Objective:** Implement migration to modules using unnamed and automatic modules

### What are Module Types?
Three types of modules for different migration scenarios: named (full module), automatic (JAR on module-path), unnamed (JAR on classpath).

### When to Use
| Type | Location | Has module-info.java? | When |
|------|----------|-----------------------|------|
| **Named** | Module path | Yes | New modular code |
| **Automatic** | Module path | No | Migrating JARs (gradual) |
| **Unnamed** | Classpath | No | Legacy code |

### Why Use Different Types
- **Named:** Full benefits of JPMS
- **Automatic:** Gradual migration without rewriting
- **Unnamed:** Backward compatibility

### Real-World Example
```java
// NAMED MODULE (full module with module-info.java)
module com.myapp {
    requires java.sql;
    exports com.myapp.api;
}

// AUTOMATIC MODULE (JAR without module-info on module-path)
// File: commons-lang3-3.12.jar on --module-path
// Becomes automatic module named: commons.lang3
// - Exports ALL packages
// - Requires ALL other modules
// - Readable by all named modules

// UNNAMED MODULE (JAR on classpath)
// File: legacy-lib.jar on --classpath
// - Part of single "unnamed module"
// - Can read ALL named and automatic modules
// - Exports ALL packages
// - NOT readable by named modules!

// Real migration scenario
module com.myapp {
    // ✅ Can require automatic modules
    requires commons.lang3;  // Automatic module
    
    // ❌ CANNOT require unnamed modules
    // requires legacy.lib;  // Not possible!
}
```
**Why:** Gradual migration strategy - start with automatic modules before full module conversion.

### Exam Traps
- ❌ **TRAP:** Named modules CANNOT read unnamed modules
  ```java
  // Module A (named)
  module com.a {
      // ❌ Can't depend on anything from classpath (unnamed module)
  }
  
  // But unnamed module CAN read named modules! (one-way street)
  ```
- ❌ **TRAP:** Automatic module name derived from JAR filename
  ```java
  // commons-lang3-3.12.jar → module name: commons.lang3
  // (version and extensions removed, hyphens → dots)
  
  // my-cool-lib-1.0-SNAPSHOT.jar → module name: my.cool.lib
  ```
- ❌ **TRAP:** Automatic modules export EVERYTHING
  ```java
  // Automatic module exports ALL packages automatically
  // No control over what's exported!
  ```
- ❌ **TRAP:** Can't have same module on both classpath and module-path
  ```java
  java --module-path libs/mylib.jar \
       --classpath libs/mylib.jar \  // ❌ Conflict!
       --module com.myapp
  ```

---

## 7. provides and uses (Services)

**Exam Objective:** Define services, providers, and consumers

### What are Services?
Service provider mechanism for loose coupling. A module declares a service interface (uses), and other modules provide implementations (provides).

### When to Use
- Plugin architecture
- Multiple implementations of same interface
- Decoupling interface from implementation
- Runtime discovery of implementations

### Why Use Services
- Loose coupling (consumer doesn't depend on provider)
- Runtime selection of implementation
- Extensible architecture
- No hard-coded dependencies

### Real-World Example
```java
// --- SERVICE INTERFACE MODULE ---
module com.myapp.logger.api {
    exports com.myapp.logger;
}

package com.myapp.logger;
public interface Logger {
    void log(String message);
}

// --- SERVICE PROVIDER MODULE 1 (File logger) ---
module com.myapp.logger.file {
    requires com.myapp.logger.api;
    provides com.myapp.logger.Logger 
        with com.myapp.logger.file.FileLogger;
}

package com.myapp.logger.file;
public class FileLogger implements Logger {
    public void log(String message) {
        // Write to file
    }
}

// --- SERVICE PROVIDER MODULE 2 (Console logger) ---
module com.myapp.logger.console {
    requires com.myapp.logger.api;
    provides com.myapp.logger.Logger 
        with com.myapp.logger.console.ConsoleLogger;
}

// --- SERVICE CONSUMER MODULE ---
module com.myapp {
    requires com.myapp.logger.api;
    uses com.myapp.logger.Logger;  // Declare usage
}

// Consumer code
import java.util.ServiceLoader;
import com.myapp.logger.Logger;

ServiceLoader<Logger> loader = ServiceLoader.load(Logger.class);
for (Logger logger : loader) {
    logger.log("Hello");  // Finds all implementations at runtime
}
```
**Why:** Plugin-based logging system where implementations are discovered at runtime.

### Exam Traps
- ❌ **TRAP:** provides requires both interface AND implementation class
  ```java
  provides Logger;  // ❌ Missing implementation!
  provides Logger with FileLogger;  // ✅ Correct
  ```
- ❌ **TRAP:** Implementation class must have public no-arg constructor
  ```java
  public class FileLogger implements Logger {
      private FileLogger() { }  // ❌ Private constructor won't work!
      
      public FileLogger(String path) { }  // ❌ No no-arg constructor!
  }
  ```
- ❌ **TRAP:** Implementation class must be in provider module (not exported)
  ```java
  module com.myapp.logger.file {
      provides Logger with FileLogger;
      exports com.myapp.logger.file;  // ❌ NOT recommended! Implementation should be hidden
  }
  ```
- ❌ **TRAP:** uses without requires won't compile
  ```java
  module com.myapp {
      uses Logger;  // ❌ Need: requires com.myapp.logger.api;
  }
  ```

---

## 8. Compilation and Running with Modules

**Exam Objective:** Compile Java code, create modular and non-modular jars

### What is Module Compilation?
Compile and run modular Java applications using --module-path and --module options.

### When to Use
| Option | Purpose | Example |
|--------|---------|---------|
| `--module-path` or `-p` | Specify module path | `java -p mods -m com.app/com.app.Main` |
| `--module` or `-m` | Specify main module | `-m com.app/com.app.Main` |
| `-d` | Output directory | `javac -d out module-info.java` |

### Why Use Module Path
- Separate modules from regular classpath
- Enable module system features
- Better dependency management

### Real-World Example
```bash
# Project structure
myapp/
├── src/
│   ├── module-info.java
│   └── com/
│       └── myapp/
│           └── Main.java
└── lib/
    └── commons-lang.jar

# 1. COMPILE
javac -d out \
      --module-path lib \
      src/module-info.java \
      src/com/myapp/*.java

# 2. RUN (two ways)
# Way 1: Specify main class
java --module-path out:lib \
     --module com.myapp/com.myapp.Main

# Way 2: If Main-Class in module-info
java --module-path out:lib \
     --module com.myapp

# 3. CREATE MODULAR JAR
jar --create \
    --file myapp.jar \
    --main-class com.myapp.Main \
    -C out .

# 4. RUN FROM JAR
java --module-path myapp.jar:lib \
     --module com.myapp
```
**Why:** Proper module compilation and execution for production deployment.

### Exam Traps
- ❌ **TRAP:** --module-path uses : (Unix) or ; (Windows) separator
  ```bash
  # Unix/Mac
  java --module-path lib1:lib2 -m com.app
  
  # Windows
  java --module-path lib1;lib2 -m com.app
  ```
- ❌ **TRAP:** -m requires module-name/main-class format (with /)
  ```bash
  java -m com.myapp                    # ❌ Need main class!
  java -m com.myapp/com.myapp.Main     # ✅ Correct
  
  # OR specify Main-Class in module-info/jar manifest
  ```
- ❌ **TRAP:** Can't mix --module-path and -jar
  ```bash
  java -jar myapp.jar --module-path lib  # ❌ -jar ignores module path!
  ```
- ❌ **TRAP:** Module path directory structure
  ```bash
  # Exploded modules
  mods/
    com.myapp/  # Module name directory
      module-info.class
      com/myapp/Main.class
  
  # OR JAR files directly
  mods/
    myapp.jar
    commons-lang.jar
  ```

---

## 9. jdeps and Module Analysis

**Exam Objective:** Compile Java code and implement migration

### What is jdeps?
Command-line tool to analyze module dependencies. Helps identify dependencies for migration to modules.

### When to Use
- Migrating legacy code to modules
- Analyzing JAR dependencies
- Finding circular dependencies
- Identifying missing module dependencies

### Why Use jdeps
- Automated dependency analysis
- Generates module-info suggestions
- Identifies JDK internal API usage
- Migration planning

### Real-World Example
```bash
# Analyze JAR dependencies
jdeps myapp.jar

# Output:
# myapp.jar -> java.base
# myapp.jar -> java.sql
#    com.myapp.Main -> java.lang (java.base)
#    com.myapp.DB   -> java.sql.Connection (java.sql)

# Generate module-info.java suggestion
jdeps --generate-module-info . myapp.jar

# Creates: module-info.java
module myapp {
    requires java.sql;
    exports com.myapp;
}

# Check for JDK internal API usage
jdeps --jdk-internals myapp.jar

# Find missing dependencies
jdeps --module-path mods -m com.myapp

# Summary report
jdeps -s myapp.jar
# Output: myapp.jar -> java.base
#         myapp.jar -> java.sql
```
**Why:** Automated migration analysis saves time and catches missing dependencies early.

### Exam Traps
- ❌ **TRAP:** jdeps doesn't verify runtime dependencies
  ```bash
  # jdeps only shows compile-time dependencies
  # Runtime-only dependencies (reflection, Class.forName) won't show!
  ```
- ❌ **TRAP:** jdeps shows internal JDK usage (should be avoided)
  ```bash
  jdeps --jdk-internals myapp.jar
  # Warning: Uses sun.misc.Unsafe (internal API)
  # Should refactor before Java 9+
  ```
- ❌ **TRAP:** Generated module-info may need manual adjustment
  ```bash
  # jdeps can't detect:
  # - Transitive dependencies
  # - Reflection-based dependencies
  # - Runtime-only requirements
  ```

---

## 10. jlink and Custom Runtime Images

**Exam Objective:** Create runtime images

### What is jlink?
Tool to create custom Java runtime images with only required modules. Reduces distribution size and improves startup time.

### When to Use
- Distributing applications
- Reducing deployment size
- Creating standalone runtime
- Docker containers

### Why Use jlink
- Smaller runtime (vs full JDK)
- Faster startup time
- Self-contained application
- Improved security (fewer modules = smaller attack surface)

### Real-World Example
```bash
# 1. Create modular JAR
jar --create \
    --file myapp.jar \
    --main-class com.myapp.Main \
    -C out .

# 2. Create custom runtime image
jlink --module-path $JAVA_HOME/jmods:myapp.jar \
      --add-modules com.myapp \
      --output myappimage \
      --launcher myapp=com.myapp/com.myapp.Main \
      --compress 2 \
      --strip-debug

# Result: myappimage/
#   bin/
#     myapp  (launcher script)
#     java
#   lib/
#     modules  (combined module image)

# 3. Run from custom runtime
./myappimage/bin/myapp

# Size comparison:
# Full JDK: ~300MB
# Custom runtime: ~50MB (only needed modules!)
```
**Why:** Microservice deployment with minimal footprint - only includes java.base, java.sql, and app module.

### Exam Traps
- ❌ **TRAP:** jlink requires ALL modules to be modular
  ```bash
  jlink --module-path myapp.jar:legacy.jar  # ❌ If legacy.jar not modular
  # Use jdeps to migrate first!
  ```
- ❌ **TRAP:** --add-modules requires transitive dependencies
  ```bash
  # If com.myapp requires java.sql
  jlink --add-modules com.myapp  # ✅ Includes java.sql automatically
  ```
- ❌ **TRAP:** Can't use jlink with automatic or unnamed modules
  ```bash
  # All modules must be named (have module-info.java)
  ```
- ❌ **TRAP:** jlink creates platform-specific runtime
  ```bash
  # Linux jlink → Linux runtime only
  # Windows jlink → Windows runtime only
  # Cross-platform build requires build on each platform
  ```

---

## Real Exam Questions (From Shared Sources)

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes** (2.4 min/question)
- **Passing score:** 68% (34 correct minimum)
- **Question types:** Multiple choice, Multi-select
- **Difficulty:** Very Hard - modules are heavily tested

---

### Question 1: Module Declaration Basics
Which statements are valid in a module-info.java file? (Choose all that apply)

A. `module com.myapp { }`  
B. `public module com.myapp { }`  
C. `module com.myapp.v1 { }`  
D. `package com.myapp;`  
E. `import java.util.*;`

**Answer:** A, C

**Explanation:** Module declarations don't use access modifiers (B is wrong). Module names can contain dots and numbers (C is valid). Package and import statements are NOT allowed in module-info.java (D, E wrong). Covered in official exam objective: "Define modules and expose module content."

---

### Question 2: requires Directives
```java
// Module A
module com.a {
    requires java.sql;
}

// Module B  
module com.b {
    requires com.a;
}
```
Can module B use classes from java.sql?

A. Yes, requires are transitive by default  
B. No, module A must use `requires transitive`  
C. Yes, all JDK modules are automatically available  
D. No, module B must explicitly require java.sql

**Answer:** D

**Explanation:** requires is NOT transitive by default. Module B needs `requires java.sql` explicitly. Transitive requires need `requires transitive` keyword. Maps to objective: "Declare module dependencies."

---

### Question 3: exports vs opens
```java
module com.myapp {
    exports com.myapp.api;
    opens com.myapp.model;
}
```
Which statements are true? (Choose all that apply)

A. Other modules can compile against com.myapp.api classes  
B. Other modules can compile against com.myapp.model classes  
C. Reflection can access com.myapp.api private members  
D. Reflection can access com.myapp.model private members  
E. com.myapp.internal is accessible via reflection

**Answer:** A, D

**Explanation:** `exports` = compile-time and runtime access (A true, B false). `opens` = runtime reflection only (D true). Packages not exported or opened are completely inaccessible (E false). C is false - exports doesn't grant reflection to private members without opens.

---

### Question 4: Automatic Module Naming
A JAR named `commons-lang3-3.12.0.jar` is placed on the module path. What is its automatic module name?

A. `commons-lang3-3.12.0`  
B. `commons.lang3.3.12.0`  
C. `commons.lang3`  
D. `commons-lang3`  
E. Compilation error - no module-info.java

**Answer:** C

**Explanation:** Automatic module name: remove version (3.12.0), remove extension (.jar), convert hyphens to dots. Result: commons.lang3. Covers: "Implement migration using automatic modules."

---

### Question 5: Module Dependency Graph
```java
// Module A
module com.a {
    exports com.a.api;
}

// Module B
module com.b {
    requires com.a;
    exports com.b.api;
}

// Module C
module com.c {
    requires com.b;
}
```
Can module C access classes in com.a.api?

A. Yes, transitive dependencies are automatic  
B. No, C must require A explicitly  
C. Yes, but only via reflection  
D. No, circular dependency prevents access

**Answer:** B

**Explanation:** Module B must use `requires transitive com.a` for C to access A. Normal requires doesn't grant transitive access. No circular dependency here.

---

### Question 6: split packages
```java
// Module A
module com.a {
    exports com.shared;
}

// Module B
module com.b {
    exports com.shared;
}
```
What happens when both modules are on the module path?

A. Compiles successfully  
B. Runtime error only  
C. Compilation error - split package  
D. Module B's package shadows module A's

**Answer:** C

**Explanation:** Split packages (same package in multiple modules) are forbidden in JPMS. This is a compilation error. Strong encapsulation prevents package splitting.

---

### Question 7: requires static
```java
module com.myapp {
    requires static org.junit;
}
```
Which statements are true? (Choose all that apply)

A. JUnit must be present at compile-time  
B. JUnit must be present at runtime  
C. JUnit is optional at runtime  
D. This is used for test-only dependencies  
E. Code using JUnit classes will always run

**Answer:** A, C, D

**Explanation:** `requires static` = compile-time dependency, optional at runtime. Useful for test dependencies, annotation processors. If JUnit classes actually used at runtime and JUnit missing → ClassNotFoundException (E false).

---

### Question 8: Service Provider
```java
// Service interface
module com.api {
    exports com.api;
}

// Provider module
module com.provider {
    requires com.api;
    provides com.api.Service with com.provider.ServiceImpl;
}
```
What must be true about ServiceImpl? (Choose all that apply)

A. Must be public  
B. Must have public no-arg constructor  
C. Must be in an exported package  
D. Must implement Service interface  
E. Can be abstract

**Answer:** A, B, D

**Explanation:** Provider implementation must be: public class, public no-arg constructor, implement interface. Should NOT be exported (C false) - keep implementation hidden. Can't be abstract (E false).

---

### Question 9: Unnamed Module Access
```java
// Named module
module com.myapp {
    requires com.library;
}
```
A JAR `legacy.jar` (no module-info) is on the classpath. Can com.myapp access legacy.jar classes?

A. Yes, classpath is always accessible  
B. No, named modules can't read unnamed modules  
C. Yes, with `requires unnamed`  
D. Yes, with `opens` directive

**Answer:** B

**Explanation:** Named modules CANNOT read unnamed modules (classpath). This is one-way: unnamed modules CAN read named modules. Migration strategy: use automatic modules (module-path) or migrate to named module.

---

### Question 10: opens vs exports
```java
module com.myapp {
    exports com.myapp.api;
}

// Other module trying to use reflection
Class<?> clazz = Class.forName("com.myapp.api.Service");
Method m = clazz.getDeclaredMethod("privateMethod");
m.setAccessible(true);  // Will this work?
```

A. Yes, exports allows reflection  
B. No, needs `opens com.myapp.api`  
C. Yes, but only for public methods  
D. No, reflection is never allowed

**Answer:** B

**Explanation:** `exports` allows compile-time and runtime access to public members only. Reflection on private members requires `opens`. Even with setAccessible(), JPMS blocks deep reflection without opens.

---

### Question 11: Qualified Exports
```java
module com.myapp {
    exports com.myapp.internal to com.myapp.test;
}
```
Which modules can access com.myapp.internal? (Choose all that apply)

A. com.myapp.test  
B. Any module on module-path  
C. Any module with requires com.myapp  
D. Only modules also using qualified exports

**Answer:** A

**Explanation:** Qualified export restricts access to ONLY specified modules. Only com.myapp.test can access com.myapp.internal. Other modules can't access it even with requires.

---

### Question 12: jlink Requirements
Which statements about jlink are true? (Choose all that apply)

A. Works with automatic modules  
B. Requires all modules to have module-info.java  
C. Creates platform-specific runtime  
D. Can include classpath JARs  
E. Reduces runtime size

**Answer:** B, C, E

**Explanation:** jlink requires all modules to be named (have module-info). Creates platform-specific runtime image. Significantly reduces size by including only needed modules. Can't work with automatic/unnamed modules (A, D false).

---

### Question 13: Module Path vs Classpath
```bash
java --module-path mods \
     --classpath legacy \
     --module com.myapp/com.myapp.Main
```
Which are on the module-path? (Choose all that apply)

A. mods directory contents  
B. legacy directory contents  
C. com.myapp module  
D. java.base module  
E. All automatic modules

**Answer:** A, C, D

**Explanation:** --module-path contains: mods directory (A), com.myapp (C), and java.base (always implicit, D). Classpath contents (B) are unnamed modules. Automatic modules are JARs without module-info on module-path - depends if any in mods directory (E may or may not be present).

---

### Question 14: requires transitive Usage
```java
// Module A
module com.a {
    exports com.a.dto;
}

// Module B (facade)
module com.b {
    requires transitive com.a;
    exports com.b.api;
}

// Module C
module com.c {
    requires com.b;
}
```
What can module C access? (Choose all that apply)

A. com.b.api classes  
B. com.a.dto classes  
C. com.b internal classes  
D. Needs explicit `requires com.a`

**Answer:** A, B

**Explanation:** Module C gets: com.b.api (exported by B) and com.a.dto (transitive from A). No need for explicit requires com.a (D false). Can't access non-exported packages (C false).

---

### Question 15: open module
```java
open module com.myapp {
    exports com.myapp.api;
}
```
Which statements are true? (Choose all that apply)

A. All packages open for reflection  
B. All packages exported for compile-time access  
C. com.myapp.api exported for compile-time  
D. Equivalent to opens on every package  
E. Should be used sparingly

**Answer:** A, C, D, E

**Explanation:** `open module` opens ALL packages for reflection (A, D). But only explicitly exported packages (com.myapp.api) are accessible at compile-time (C true, B false). open module reduces strong encapsulation - use sparingly (E).

---

### Question 16: Circular Dependencies
```java
// Module A
module com.a {
    requires com.b;
}

// Module B
module com.b {
    requires com.a;
}
```
What happens?

A. Compiles successfully  
B. Runtime error  
C. Compilation error - circular dependency  
D. Works if both use requires static

**Answer:** C

**Explanation:** Module system forbids circular dependencies. This is a compilation error. Even requires static doesn't help (D false) - need to refactor module structure.

---

### Question 17: Service Consumer
```java
module com.myapp {
    uses com.api.Logger;
}

import java.util.ServiceLoader;
ServiceLoader<Logger> loader = ServiceLoader.load(Logger.class);
```
What's required for this to compile? (Choose all that apply)

A. `requires com.api` where Logger is defined  
B. `provides Logger with Implementation`  
C. `exports com.api` in the com.api module  
D. At least one provider module at runtime  
E. Logger must be an interface

**Answer:** A, C

**Explanation:** Consumer needs: requires (A) to access Logger interface, and Logger must be exported (C). Provider (B) not required for compilation. Runtime needs provider but not for compilation (D). Logger can be interface or abstract class (E false).

---

### Question 18: Automatic Module Behavior
An automatic module is created from `mylib-1.0.jar` on the module-path. Which are true? (Choose all that apply)

A. Exports all packages  
B. Requires all observable modules  
C. Can be read by named modules  
D. Can read unnamed modules  
E. Requires explicit exports in code

**Answer:** A, B, C

**Explanation:** Automatic modules: export ALL packages (A), require ALL modules (B), readable by named modules (C). Can't read unnamed/classpath (D false). No code changes needed (E false) - behavior is automatic.

---

### Question 19: Module Compilation Command
```bash
javac -d out \
      --module-path lib \
      src/module-info.java \
      src/com/myapp/*.java
```
What does --module-path specify?

A. Output directory for compiled modules  
B. Location of dependent modules  
C. Source code location  
D. Runtime module location  
E. Classpath equivalent

**Answer:** B

**Explanation:** --module-path (or -p) specifies location of dependent modules for compilation. -d is output (A false), src paths are source (C false), runtime uses same option but for java command (D false). Not same as classpath (E false) - different purposes.

---

### Question 20: jdeps Analysis
```bash
jdeps --generate-module-info . myapp.jar
```
What does this command do?

A. Creates module-info.class  
B. Suggests module-info.java based on dependencies  
C. Compiles the JAR to modular format  
D. Analyzes runtime dependencies  
E. Lists all classes in the JAR

**Answer:** B

**Explanation:** jdeps --generate-module-info analyzes JAR and generates suggested module-info.java file. Doesn't compile (C false), doesn't create .class (A false). Analyzes compile-time dependencies, not runtime (D false). Doesn't list classes (E false) - shows dependencies.

---

## Summary

### Exam Objectives Checklist
- ✅ **Define modules and expose module content:** Covered in Topics 1-5 (module-info.java, requires, exports, opens)
- ✅ **Declare module dependencies:** Covered in Topic 3 (requires, requires transitive, requires static)
- ✅ **Define services, providers, and consumers:** Covered in Topic 7 (provides, uses)
- ✅ **Compile Java code and create modular jars:** Covered in Topics 8, 10 (javac, jar, jlink)
- ✅ **Implement migration using unnamed and automatic modules:** Covered in Topic 6 (module types)

### Key Points for Exam (Most Tested)

1. **Module types** - Named (full module), Automatic (JAR on module-path), Unnamed (classpath)
2. **Named can't read Unnamed** - One-way street: unnamed CAN read named
3. **requires NOT transitive by default** - Need explicit `requires transitive`
4. **exports vs opens** - exports = compile+runtime, opens = reflection only
5. **Automatic module naming** - Strip version, convert hyphens to dots
6. **No split packages** - Same package can't be in multiple modules
7. **No circular dependencies** - Module graph must be acyclic  
8. **java.base always implicit** - Don't need to declare requires java.base
9. **Service implementation** - Must have public no-arg constructor
10. **Qualified exports** - exports...to restricts access to specific modules
11. **open module opens ALL packages** - But doesn't export them
12. **jlink requires named modules** - Can't use automatic/unnamed
13. **module-info.java location** - Must be at source root with packages
14. **Sub-packages NOT auto-exported** - Each package needs explicit export
15. **Provider implementation hidden** - Should NOT export implementation package

### Common Exam Patterns

**Pattern 1: Module Type Identification**
- Given JAR location (classpath vs module-path) and module-info presence
- Determine: named, automatic, or unnamed
- Check what can read what

**Pattern 2: Dependency Resolution**
- Given module graph with requires statements
- Determine what modules can access what packages
- Watch for missing transitive keywords

**Pattern 3: exports vs opens Confusion**
- Question shows reflection code
- Identify if exports or opens needed
- Remember: exports alone doesn't allow deep reflection

**Pattern 4: Service Provider Requirements**
- Given service interface and provider
- Check: public class, public no-arg constructor, correct directive
- Verify interface in requires

**Pattern 5: Automatic Module Naming**
- Given JAR filename
- Apply rules: remove version, extension, convert hyphens
- Common trap: forgetting to convert hyphens to dots

**Pattern 6: Module Migration Strategy**
- Legacy code on classpath (unnamed module)
- Move to module-path (automatic module)
- Add module-info.java (named module)
- Remember: named can't read unnamed!

**Pattern 7: Compilation Commands**
- javac with --module-path vs --classpath
- java with --module-path and --module
- jar creation with --main-class
- jlink for custom runtime

### Study Tips

1. **Draw module graphs** - Visual understanding helps with requires transitive
2. **Practice commands** - javac, java, jar, jlink, jdeps syntax
3. **Memorize module types table** - Named, Automatic, Unnamed characteristics
4. **Service pattern** - Remember: uses (consumer), provides...with (provider)
5. **Migration order** - Unnamed → Automatic → Named (can't skip)
6. **Compile vs Runtime** - exports (both), opens (runtime), requires static (compile-time)
7. **Name derivation** - Practice converting JAR names to module names
8. **Know restrictions** - No split packages, no circular dependencies, no unnamed reading from named
9. **Commands practice** - Module path syntax different on Unix (:) vs Windows (;)
10. **Service requirements** - Public class, public no-arg constructor, implements interface

### Quick Reference Card

```
MODULE TYPES:
┌──────────┬───────────┬────────────┬────────────┐
│ Type     │ Location  │ module-inf │ Exports    │
├──────────┼───────────┼────────────┼────────────┤
│ Named    │ Module-pt │ YES        │ Explicit   │
│ Automatic│ Module-pt │ NO         │ ALL        │
│ Unnamed  │ Classpath │ NO         │ ALL        │
└──────────┴───────────┴────────────┴────────────┘

READABILITY RULES:
Named     → Named (with requires) ✅
Named     → Automatic ✅
Named     → Unnamed ❌
Automatic → All ✅
Unnamed   → All ✅

DIRECTIVES:
requires           → Normal dependency
requires transitive → Re-export (implied readability)
requires static    → Compile-time only
exports            → Make package accessible
exports...to       → Qualified (specific modules)
opens              → Reflection access
opens...to         → Qualified reflection
uses               → Service consumer
provides...with    → Service provider

COMMANDS:
Compile:   javac -p <modulepath> -d <out> <sources>
Run:       java -p <modulepath> -m <module>/<mainclass>
JAR:       jar --create --file <jar> --main-class <class> -C <dir> .
jdeps:     jdeps --generate-module-info <dir> <jar>
jlink:     jlink -p <modulepath> --add-modules <module> --output <dir>
```

---

## Next Steps

✅ **Completed:** Module system basics, module-info.java, requires/exports/opens directives, services, compilation, migration strategies

➡️ **Next:** [Chapter 8: Concurrency & Virtual Threads](CHAPTER-08.md) - Platform threads, virtual threads (Java 21), ExecutorService, thread safety, concurrent collections

**Practice Tasks:**
1. Create a simple modular application with 3 modules
2. Practice javac, java, jar commands with modules
3. Migrate a non-modular JAR to automatic module
4. Use jdeps to analyze a JAR's dependencies
5. Create a service with provider and consumer
6. Build a custom runtime with jlink
7. Try all 20 exam questions without looking at answers
8. Draw module graphs for practice scenarios

---

[← Previous Chapter](CHAPTER-06.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-08.md)

---

*Chapter 7 - Modules (Java Platform Module System)*  
*Java SE 21 Certification Study Guide*  
*Topics: 10 | Examples: 30+ | Questions: 20 | Traps: 35+*

