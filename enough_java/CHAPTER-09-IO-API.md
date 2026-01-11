# Chapter 9: I/O API (Java I/O and NIO.2)

[← Previous Chapter](CHAPTER-08.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-10.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | I/O Fundamentals and File System Operations |
| **Java Versions** | 1.0 (Classic I/O), 7 (NIO.2) |
| **Code Examples** | 70+ |
| **Study Time** | 8-10 hours |
| **Exam Weight** | ~10-12% |
| **Difficulty** | ⭐⭐⭐ Hard |

**Official 1Z0-830 Exam Objectives Covered:**
- Read and write console and file data using I/O Streams
- Serialize and deserialize Java objects
- Construct, traverse, create, read, and write Path objects and their properties using java.nio.file API
- Use Files class to manipulate files and directories

---

## 1. Java I/O Streams Fundamentals

**Exam Objective:** Read and write console and file data using I/O Streams

### What are I/O Streams?
Streams represent a flow of data from source to destination. Java has two types: byte streams (InputStream/OutputStream) and character streams (Reader/Writer).

### When to Use
| Stream Type | When to Use | Example |
|------------|-------------|---------|
| **Byte Streams** | Binary data, images, audio | FileInputStream, FileOutputStream |
| **Character Streams** | Text files, Unicode | FileReader, FileWriter |
| **Buffered Streams** | Performance improvement | BufferedReader, BufferedWriter |
| **Data Streams** | Primitive types | DataInputStream, DataOutputStream |
| **Object Streams** | Serialization | ObjectInputStream, ObjectOutputStream |

### Why Use I/O Streams
- Universal data access pattern
- Decorator pattern for flexibility
- Automatic resource management (try-with-resources)
- Platform-independent file handling

### Real-World Example
```java
import java.io.*;

public class IOStreamsExample {
    public static void main(String[] args) {
        // 1. Writing to file with byte stream
        try (FileOutputStream fos = new FileOutputStream("data.bin")) {
            fos.write(65); // Writes byte value (ASCII 'A')
            fos.write(new byte[]{66, 67, 68}); // BCD
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 2. Reading from file with byte stream
        try (FileInputStream fis = new FileInputStream("data.bin")) {
            int data;
            while ((data = fis.read()) != -1) {
                System.out.print((char) data); // Prints: ABCD
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 3. Writing text with character stream
        try (FileWriter writer = new FileWriter("text.txt")) {
            writer.write("Hello World!");
            writer.write("\nSecond line");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 4. Reading text with BufferedReader (efficient)
        try (BufferedReader reader = new BufferedReader(
                new FileReader("text.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 5. Console I/O
        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in))) {
            System.out.print("Enter name: ");
            String name = console.readLine();
            System.out.println("Hello, " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 6. PrintWriter for formatted output
        try (PrintWriter pw = new PrintWriter("output.txt")) {
            pw.println("Line 1");
            pw.printf("Formatted: %d %s%n", 42, "test");
            pw.print("No newline");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Log file processing - read application logs, parse entries, write filtered results.

### Exam Traps
- ❌ **TRAP:** read() returns -1 at end of stream, not 0
  ```java
  int data = fis.read();
  if (data == 0) { } // ❌ Wrong! Should check == -1
  if (data == -1) { } // ✅ Correct - end of stream
  ```
- ❌ **TRAP:** FileReader/FileWriter use platform default encoding
  ```java
  FileReader fr = new FileReader("file.txt"); // Platform encoding
  // Better: specify encoding explicitly
  Reader r = new InputStreamReader(new FileInputStream("file.txt"), 
                                   StandardCharsets.UTF_8);
  ```
- ❌ **TRAP:** Flush is required before closing for proper writes
  ```java
  Writer w = new FileWriter("file.txt");
  w.write("data");
  // w.close(); // May lose data if exception before close!
  w.flush(); // ✅ Ensure data written
  w.close();
  ```
- ❌ **TRAP:** BufferedReader.readLine() returns null at EOF, not empty string
  ```java
  String line = reader.readLine();
  if (line.isEmpty()) { } // ❌ NullPointerException at EOF!
  if (line == null) { } // ✅ Correct EOF check
  ```

---

## 2. File Class (Legacy java.io.File)

**Exam Objective:** Understand legacy File class and its limitations

### What is File Class?
Legacy class from Java 1.0 representing file/directory paths. Mostly replaced by NIO.2 Path but still appears on exam.

### When to Use
- ❌ **Avoid in new code** - use Path instead
- ✅ Legacy code maintenance
- ✅ Exam questions (must know for exam)

### Why Learn File Class
- Understanding legacy code
- Exam still tests it
- Comparison with modern Path API

### Real-World Example
```java
import java.io.*;

public class FileClassExample {
    public static void main(String[] args) {
        // 1. Creating File object (doesn't create physical file!)
        File file = new File("test.txt");
        System.out.println("Exists: " + file.exists()); // false initially
        
        // 2. Creating actual file
        try {
            boolean created = file.createNewFile();
            System.out.println("Created: " + created); // true if new
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 3. File properties
        System.out.println("Absolute path: " + file.getAbsolutePath());
        System.out.println("Name: " + file.getName());
        System.out.println("Parent: " + file.getParent());
        System.out.println("Is file: " + file.isFile());
        System.out.println("Is directory: " + file.isDirectory());
        System.out.println("Can read: " + file.canRead());
        System.out.println("Can write: " + file.canWrite());
        System.out.println("Length: " + file.length() + " bytes");
        System.out.println("Last modified: " + file.lastModified());
        
        // 4. Directory operations
        File dir = new File("mydir");
        dir.mkdir(); // Create single directory
        
        File nestedDir = new File("parent/child/grandchild");
        nestedDir.mkdirs(); // Create all parent directories
        
        // 5. Listing directory contents
        File currentDir = new File(".");
        String[] files = currentDir.list(); // Just names
        File[] fileObjects = currentDir.listFiles(); // File objects
        
        if (fileObjects != null) {
            for (File f : fileObjects) {
                System.out.println(f.getName() + " - " + 
                    (f.isDirectory() ? "DIR" : "FILE"));
            }
        }
        
        // 6. File operations
        File oldFile = new File("old.txt");
        File newFile = new File("new.txt");
        oldFile.renameTo(newFile); // Rename/move
        file.delete(); // Delete file
        
        // 7. File path separators
        System.out.println("Separator: " + File.separator); // \ on Windows, / on Unix
        System.out.println("Path separator: " + File.pathSeparator); // ; on Windows, : on Unix
    }
}
```
**Why:** Directory cleanup utility - find old files, check sizes, delete based on age.

### Exam Traps
- ❌ **TRAP:** File object creation doesn't create physical file
  ```java
  File f = new File("test.txt"); // ❌ File not created yet!
  f.createNewFile(); // ✅ Now file exists
  ```
- ❌ **TRAP:** delete() returns false on failure (no exception)
  ```java
  File f = new File("locked.txt");
  boolean deleted = f.delete(); // false if locked, no exception
  ```
- ❌ **TRAP:** mkdir() vs mkdirs() difference
  ```java
  new File("a/b/c").mkdir(); // ❌ Fails if a/b doesn't exist
  new File("a/b/c").mkdirs(); // ✅ Creates all parent dirs
  ```
- ❌ **TRAP:** listFiles() returns null if not a directory
  ```java
  File f = new File("test.txt");
  for (File file : f.listFiles()) { } // ❌ NullPointerException!
  
  File[] files = f.listFiles();
  if (files != null) { // ✅ Check for null
      for (File file : files) { }
  }
  ```

---

## 3. Path Interface (NIO.2)

**Exam Objective:** Construct, traverse Path objects

### What is Path?
Modern replacement for File (Java 7+). Represents file system path, not actual file. Immutable and platform-independent.

### When to Use
- ✅ All new file operations
- ✅ Path manipulation (resolve, relativize)
- ✅ Symbolic link handling
- ✅ Better exception handling than File

### Why Use Path
- Cleaner API than File
- Immutable and thread-safe
- Better error reporting
- Platform-independent path operations

### Real-World Example
```java
import java.nio.file.*;
import java.io.IOException;

public class PathExample {
    public static void main(String[] args) {
        // 1. Creating Path objects
        Path path1 = Paths.get("file.txt"); // Relative path
        Path path2 = Paths.get("/home", "user", "docs"); // Unix absolute
        Path path3 = Paths.get("C:\\", "Users", "Alice"); // Windows absolute
        Path path4 = Path.of("modern", "way.txt"); // Java 11+ method
        
        // 2. Path components
        Path path = Paths.get("/home/user/documents/file.txt");
        System.out.println("toString: " + path); // /home/user/documents/file.txt
        System.out.println("File name: " + path.getFileName()); // file.txt
        System.out.println("Parent: " + path.getParent()); // /home/user/documents
        System.out.println("Root: " + path.getRoot()); // /
        System.out.println("Name count: " + path.getNameCount()); // 4
        System.out.println("Name(0): " + path.getName(0)); // home
        System.out.println("Name(1): " + path.getName(1)); // user
        System.out.println("Subpath(1,3): " + path.subpath(1, 3)); // user/documents
        
        // 3. Path properties
        System.out.println("Is absolute: " + path.isAbsolute()); // true
        Path relative = Paths.get("docs/file.txt");
        System.out.println("Is absolute: " + relative.isAbsolute()); // false
        
        // 4. Resolving paths (combining)
        Path base = Paths.get("/home/user");
        Path resolved = base.resolve("documents/file.txt");
        System.out.println("Resolved: " + resolved); // /home/user/documents/file.txt
        
        Path absolute = Paths.get("/etc/config");
        Path result = base.resolve(absolute);
        System.out.println("Resolved absolute: " + result); // /etc/config (absolute wins!)
        
        // 5. Relativizing paths
        Path p1 = Paths.get("/home/user/documents");
        Path p2 = Paths.get("/home/user/downloads");
        Path relativePath = p1.relativize(p2);
        System.out.println("Relative: " + relativePath); // ../downloads
        
        // 6. Normalizing paths
        Path messyPath = Paths.get("/home/user/../user/./documents");
        Path normalized = messyPath.normalize();
        System.out.println("Normalized: " + normalized); // /home/user/documents
        
        // 7. Real path (resolves symbolic links)
        try {
            Path real = Paths.get(".").toRealPath();
            System.out.println("Real path: " + real);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 8. Converting to File (interoperability)
        Path p = Paths.get("test.txt");
        java.io.File file = p.toFile();
        Path backToPath = file.toPath();
        
        // 9. Path comparison
        Path p3 = Paths.get("file.txt");
        Path p4 = Paths.get("FILE.TXT");
        System.out.println("Equals: " + p3.equals(p4)); // false (case-sensitive)
        System.out.println("Starts with: " + path.startsWith("/home")); // true
        System.out.println("Ends with: " + path.endsWith("file.txt")); // true
    }
}
```
**Why:** Build system - resolve relative paths, normalize build paths, create output directories.

### Exam Traps
- ❌ **TRAP:** resolve() with absolute path ignores base
  ```java
  Path base = Paths.get("/home/user");
  Path result = base.resolve("/etc/config"); // "/etc/config" not "/home/user/etc/config"
  ```
- ❌ **TRAP:** getName() index doesn't include root
  ```java
  Path p = Paths.get("/home/user/file.txt");
  p.getName(0); // "home" not "/"!
  p.getRoot(); // "/" - use this for root
  ```
- ❌ **TRAP:** relativize() requires both absolute or both relative
  ```java
  Path abs = Paths.get("/home/user");
  Path rel = Paths.get("documents");
  abs.relativize(rel); // ❌ IllegalArgumentException!
  ```
- ❌ **TRAP:** normalize() doesn't check if path exists
  ```java
  Path p = Paths.get("/nonexistent/../test");
  p.normalize(); // Returns "/test" even though /nonexistent doesn't exist!
  ```
- ❌ **TRAP:** getNameCount() excludes root from count
  ```java
  Path p = Paths.get("/home/user/file.txt");
  System.out.println(p.getNameCount()); // 3, not 4! (root not counted)
  ```
- ❌ **TRAP:** endsWith() checks path elements, not strings
  ```java
  Path p = Paths.get("/home/user/documents");
  p.endsWith("uments"); // false! Must be complete element
  p.endsWith("documents"); // true
  ```
- ❌ **TRAP:** Path equality is case-sensitive on most systems
  ```java
  Path p1 = Paths.get("File.txt");
  Path p2 = Paths.get("file.txt");
  p1.equals(p2); // false on Unix/Linux, may be true on Windows
  ```

---

## 4. Files Class (NIO.2 Operations)

**Exam Objective:** Create, read, and write files using Files class

### What is Files Class?
Utility class with static methods for file operations. Works with Path objects. Throws IOException on errors.

### When to Use
- ✅ All file/directory operations
- ✅ Reading/writing file content
- ✅ File attributes and metadata
- ✅ Directory traversal

### Why Use Files Class
- Simple static methods
- Better exception handling
- Atomic operations
- Rich set of utilities

### Real-World Example
```java
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.util.*;

public class FilesExample {
    public static void main(String[] args) {
        try {
            // 1. Checking file existence
            Path file = Paths.get("test.txt");
            System.out.println("Exists: " + Files.exists(file));
            System.out.println("Not exists: " + Files.notExists(file));
            System.out.println("Is regular file: " + Files.isRegularFile(file));
            System.out.println("Is directory: " + Files.isDirectory(file));
            System.out.println("Is symbolic link: " + Files.isSymbolicLink(file));
            System.out.println("Is readable: " + Files.isReadable(file));
            System.out.println("Is writable: " + Files.isWritable(file));
            System.out.println("Is executable: " + Files.isExecutable(file));
            System.out.println("Is hidden: " + Files.isHidden(file));
            
            // 2. Creating files and directories
            Path newFile = Paths.get("newfile.txt");
            Files.createFile(newFile); // Fails if exists
            
            Path dir = Paths.get("newdir");
            Files.createDirectory(dir); // Single directory
            
            Path nestedDirs = Paths.get("a/b/c");
            Files.createDirectories(nestedDirs); // All parent dirs
            
            // 3. Deleting files
            Files.delete(newFile); // Throws exception if doesn't exist
            boolean deleted = Files.deleteIfExists(newFile); // Returns false if doesn't exist
            
            // 4. Copying files
            Path source = Paths.get("source.txt");
            Path target = Paths.get("target.txt");
            Files.copy(source, target); // Fails if target exists
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
            
            // 5. Moving files
            Path oldPath = Paths.get("old.txt");
            Path newPath = Paths.get("new.txt");
            Files.move(oldPath, newPath); // Rename/move
            Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE);
            
            // 6. Reading file content
            List<String> lines = Files.readAllLines(Paths.get("file.txt"));
            for (String line : lines) {
                System.out.println(line);
            }
            
            byte[] bytes = Files.readAllBytes(Paths.get("file.txt"));
            String content = new String(bytes);
            
            // 7. Writing file content
            String text = "Hello World!";
            Files.writeString(Paths.get("output.txt"), text); // Java 11+
            
            List<String> linesToWrite = Arrays.asList("Line 1", "Line 2", "Line 3");
            Files.write(Paths.get("lines.txt"), linesToWrite);
            
            // 8. File attributes
            Path p = Paths.get("file.txt");
            long size = Files.size(p);
            FileTime lastModified = Files.getLastModifiedTime(p);
            Object owner = Files.getOwner(p);
            
            BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
            System.out.println("Creation time: " + attrs.creationTime());
            System.out.println("Last access: " + attrs.lastAccessTime());
            System.out.println("Last modified: " + attrs.lastModifiedTime());
            System.out.println("Size: " + attrs.size());
            System.out.println("Is regular file: " + attrs.isRegularFile());
            System.out.println("Is directory: " + attrs.isDirectory());
            System.out.println("Is symbolic link: " + attrs.isSymbolicLink());
            
            // 9. Setting attributes
            Files.setLastModifiedTime(p, FileTime.fromMillis(System.currentTimeMillis()));
            
            // 10. Listing directory contents
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."))) {
                for (Path entry : stream) {
                    System.out.println(entry.getFileName());
                }
            }
            
            // With glob pattern filter
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), "*.txt")) {
                for (Path entry : stream) {
                    System.out.println(entry);
                }
            }
            
            // 11. Walking directory tree (Java 8+)
            Files.walk(Paths.get("."), 2) // Max depth 2
                .filter(Files::isRegularFile)
                .forEach(System.out::println);
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Backup utility - walk directory tree, copy files preserving attributes, skip certain file types.

### Exam Traps
- ❌ **TRAP:** createFile() throws FileAlreadyExistsException if exists
  ```java
  Files.createFile(Paths.get("existing.txt")); // ❌ Exception if exists
  if (!Files.exists(path)) Files.createFile(path); // ✅ Check first
  ```
- ❌ **TRAP:** delete() throws exception, deleteIfExists() returns boolean
  ```java
  Files.delete(nonExistent); // ❌ NoSuchFileException
  Files.deleteIfExists(nonExistent); // ✅ Returns false, no exception
  ```
- ❌ **TRAP:** copy() doesn't overwrite by default
  ```java
  Files.copy(src, dest); // ❌ FileAlreadyExistsException if dest exists
  Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING); // ✅ Overwrites
  ```
- ❌ **TRAP:** readAllLines() loads entire file into memory
  ```java
  List<String> lines = Files.readAllLines(hugeFile); // ❌ OutOfMemoryError!
  Files.lines(hugeFile).forEach(System.out::println); // ✅ Streams lazily
  ```

---

## 5. Serialization

**Exam Objective:** Serialize and deserialize Java objects

### What is Serialization?
Converting object to byte stream for storage/transmission. Object must implement Serializable interface.

### When to Use
- Saving object state to disk
- Sending objects over network
- Caching objects
- Deep copying objects

### Why Use Serialization
- Persist object state
- Distribute objects
- Session management
- Object versioning

### Real-World Example
```java
import java.io.*;

// Serializable class
class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
    private transient String password; // transient = not serialized
    private static String country = "USA"; // static = not serialized
    
    public Person(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + 
               ", password='" + password + "', country='" + country + "'}";
    }
}

public class SerializationExample {
    public static void main(String[] args) {
        // 1. Serializing object
        Person person = new Person("Alice", 30, "secret123");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("person.ser"))) {
            oos.writeObject(person);
            System.out.println("Object serialized: " + person);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 2. Deserializing object
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("person.ser"))) {
            Person loaded = (Person) ois.readObject();
            System.out.println("Object deserialized: " + loaded);
            // Note: password is null (transient), country retains static value
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        // 3. Serializing multiple objects
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("people.ser"))) {
            oos.writeObject(new Person("Bob", 25, "pass1"));
            oos.writeObject(new Person("Charlie", 35, "pass2"));
            oos.writeInt(42); // Can mix object and primitive types
            oos.writeUTF("Some text");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 4. Deserializing multiple objects (order matters!)
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("people.ser"))) {
            Person p1 = (Person) ois.readObject();
            Person p2 = (Person) ois.readObject();
            int number = ois.readInt();
            String text = ois.readUTF();
            System.out.println("Read: " + p1 + ", " + p2 + ", " + number + ", " + text);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Session management - serialize user session objects, store in Redis/file, restore on server restart.

### Exam Traps
- ❌ **TRAP:** Class must implement Serializable (marker interface)
  ```java
  class MyClass { } // ❌ NotSerializableException
  class MyClass implements Serializable { } // ✅ Can serialize
  ```
- ❌ **TRAP:** transient fields not serialized (become null/0/false)
  ```java
  class User implements Serializable {
      transient String password; // Not serialized
      // After deserialization: password = null
  }
  ```
- ❌ **TRAP:** static fields not serialized (class-level, not instance)
  ```java
  class Config implements Serializable {
      static String setting = "value";
      // Static fields are NOT serialized
  }
  ```
- ❌ **TRAP:** serialVersionUID mismatch causes InvalidClassException
  ```java
  class Data implements Serializable {
      private static final long serialVersionUID = 1L; // Version ID
      // Changing class structure without updating UID = exception on deserialize
  }
  ```
- ❌ **TRAP:** Deserialization order must match serialization order
  ```java
  oos.writeObject(obj1);
  oos.writeInt(42);
  // Must read in same order:
  Object o = ois.readObject(); // obj1
  int i = ois.readInt(); // 42
  ```

---

## 6. Custom Serialization

**Exam Objective:** Customize serialization behavior

### What is Custom Serialization?
Implementing writeObject() and readObject() methods to control serialization process.

### When to Use
- Encrypt sensitive data during serialization
- Compute derived fields on deserialization
- Maintain object invariants
- Handle version changes

### Why Use Custom Serialization
- Security (encrypt passwords)
- Performance (skip redundant data)
- Compatibility (handle class evolution)
- Validation (enforce constraints)

### Real-World Example
```java
import java.io.*;

class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String accountNumber;
    private double balance;
    private transient String pin; // Don't serialize PIN directly
    
    public BankAccount(String accountNumber, double balance, String pin) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.pin = pin;
    }
    
    // Custom serialization
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Serialize non-transient fields
        
        // Encrypt PIN before serializing
        String encrypted = encrypt(pin);
        oos.writeObject(encrypted);
    }
    
    // Custom deserialization
    private void readObject(ObjectInputStream ois) 
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-transient fields
        
        // Decrypt PIN after deserializing
        String encrypted = (String) ois.readObject();
        this.pin = decrypt(encrypted);
    }
    
    private String encrypt(String data) {
        // Simple encryption (use proper encryption in production!)
        return new StringBuilder(data).reverse().toString();
    }
    
    private String decrypt(String data) {
        return new StringBuilder(data).reverse().toString();
    }
    
    @Override
    public String toString() {
        return "BankAccount{account='" + accountNumber + 
               "', balance=" + balance + ", pin='" + pin + "'}";
    }
}

public class CustomSerializationExample {
    public static void main(String[] args) {
        // Serialize with custom logic
        BankAccount account = new BankAccount("123-456", 1000.50, "1234");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("account.ser"))) {
            oos.writeObject(account);
            System.out.println("Serialized: " + account);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Deserialize with custom logic
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("account.ser"))) {
            BankAccount loaded = (BankAccount) ois.readObject();
            System.out.println("Deserialized: " + loaded);
            // PIN is restored via custom readObject()
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Secure user data - encrypt passwords/tokens during serialization, validate constraints on deserialization.

### Exam Traps
- ❌ **TRAP:** Must call defaultWriteObject() first in writeObject()
  ```java
  private void writeObject(ObjectOutputStream oos) throws IOException {
      oos.writeObject(customField); // ❌ Wrong order!
      oos.defaultWriteObject();
      
      oos.defaultWriteObject(); // ✅ Call first
      oos.writeObject(customField);
  }
  ```
- ❌ **TRAP:** readObject() must read in same order as writeObject()
  ```java
  private void writeObject(ObjectOutputStream oos) throws IOException {
      oos.defaultWriteObject();
      oos.writeInt(x);
      oos.writeObject(y);
  }
  
  private void readObject(ObjectInputStream ois) throws ... {
      ois.defaultReadObject();
      this.x = ois.readInt(); // Must match write order
      this.y = ois.readObject();
  }
  ```
- ❌ **TRAP:** Signature must be exact (private void, correct parameters)
  ```java
  public void writeObject(ObjectOutputStream oos) { } // ❌ Must be private!
  private void writeObject(OutputStream os) { } // ❌ Wrong parameter type!
  private void writeObject(ObjectOutputStream oos) throws IOException { } // ✅ Correct
  ```

---

## 7. NIO.2 File Attributes

**Exam Objective:** Read and modify file attributes

### What are File Attributes?
Metadata about files: size, timestamps, permissions, ownership. Accessed via BasicFileAttributes and related views.

### When to Use
- Checking file metadata
- Setting permissions
- Managing timestamps
- Platform-specific attributes

### Why Use File Attributes
- File management operations
- Security and permissions
- Backup/restore metadata
- File comparison

### Real-World Example
```java
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.time.Instant;

public class FileAttributesExample {
    public static void main(String[] args) {
        try {
            Path file = Paths.get("test.txt");
            
            // 1. Basic file attributes
            BasicFileAttributes attrs = Files.readAttributes(
                file, BasicFileAttributes.class);
            
            System.out.println("Creation time: " + attrs.creationTime());
            System.out.println("Last modified: " + attrs.lastModifiedTime());
            System.out.println("Last access: " + attrs.lastAccessTime());
            System.out.println("Size: " + attrs.size() + " bytes");
            System.out.println("Is regular file: " + attrs.isRegularFile());
            System.out.println("Is directory: " + attrs.isDirectory());
            System.out.println("Is symbolic link: " + attrs.isSymbolicLink());
            System.out.println("Is other: " + attrs.isOther());
            System.out.println("File key: " + attrs.fileKey());
            
            // 2. Modifying timestamps
            FileTime now = FileTime.from(Instant.now());
            Files.setLastModifiedTime(file, now);
            
            // 3. DOS attributes (Windows-specific)
            try {
                DosFileAttributes dosAttrs = Files.readAttributes(
                    file, DosFileAttributes.class);
                System.out.println("Is hidden: " + dosAttrs.isHidden());
                System.out.println("Is read-only: " + dosAttrs.isReadOnly());
                System.out.println("Is system: " + dosAttrs.isSystem());
                System.out.println("Is archive: " + dosAttrs.isArchive());
            } catch (UnsupportedOperationException e) {
                System.out.println("DOS attributes not supported");
            }
            
            // 4. POSIX attributes (Unix/Linux-specific)
            try {
                PosixFileAttributes posixAttrs = Files.readAttributes(
                    file, PosixFileAttributes.class);
                System.out.println("Owner: " + posixAttrs.owner().getName());
                System.out.println("Group: " + posixAttrs.group().getName());
                System.out.println("Permissions: " + 
                    PosixFilePermissions.toString(posixAttrs.permissions()));
            } catch (UnsupportedOperationException e) {
                System.out.println("POSIX attributes not supported");
            }
            
            // 5. Attribute views for bulk operations
            BasicFileAttributeView view = Files.getFileAttributeView(
                file, BasicFileAttributeView.class);
            
            BasicFileAttributes readAttrs = view.readAttributes();
            
            // Update multiple timestamps at once
            FileTime time = FileTime.fromMillis(System.currentTimeMillis());
            view.setTimes(time, time, time); // modified, access, create
            
            // 6. User-defined attributes (extended attributes)
            UserDefinedFileAttributeView userView = 
                Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
            
            if (userView != null) {
                // Set custom attribute
                userView.write("author", 
                    java.nio.ByteBuffer.wrap("Alice".getBytes()));
                
                // Read custom attribute
                java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(
                    userView.size("author"));
                userView.read("author", buffer);
                buffer.flip();
                System.out.println("Author: " + 
                    new String(buffer.array()));
                
                // List all custom attributes
                for (String name : userView.list()) {
                    System.out.println("Attribute: " + name);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** File synchronization tool - compare file timestamps, sizes, check if modified, preserve attributes during copy.

### Exam Traps
- ❌ **TRAP:** BasicFileAttributes is read-only, use view to modify
  ```java
  BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
  attrs.setLastModifiedTime(time); // ❌ No setter methods!
  
  Files.setLastModifiedTime(path, time); // ✅ Use Files class
  ```
- ❌ **TRAP:** Platform-specific attributes throw UnsupportedOperationException
  ```java
  DosFileAttributes attrs = Files.readAttributes(path, DosFileAttributes.class);
  // ❌ Throws exception on Unix/Linux!
  ```
- ❌ **TRAP:** fileKey() can be null
  ```java
  Object key = attrs.fileKey();
  key.toString(); // ❌ NullPointerException if null!
  ```

---

## 8. Directory Walking and Searching

**Exam Objective:** Traverse directory structures

### What is Directory Walking?
Recursively visiting all files and subdirectories in a directory tree using Files.walk() or FileVisitor.

### When to Use
- Finding files matching criteria
- Directory cleanup operations
- Calculating total disk usage
- File indexing/searching

### Why Use Directory Walking
- Process all files in tree
- Depth-first traversal
- Stream-based processing
- Custom visit logic

### Real-World Example
```java
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.*;

public class DirectoryWalkingExample {
    public static void main(String[] args) {
        try {
            // 1. Walk entire directory tree (unlimited depth)
            System.out.println("=== All files ===");
            Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .forEach(System.out::println);
            
            // 2. Walk with max depth
            System.out.println("\n=== Files (depth 2) ===");
            Files.walk(Paths.get("."), 2)
                .filter(Files::isRegularFile)
                .forEach(System.out::println);
            
            // 3. Find files by name pattern
            System.out.println("\n=== .txt files ===");
            Files.walk(Paths.get("."))
                .filter(p -> p.toString().endsWith(".txt"))
                .forEach(System.out::println);
            
            // 4. Calculate total size
            long totalSize = Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .mapToLong(p -> {
                    try { return Files.size(p); }
                    catch (IOException e) { return 0; }
                })
                .sum();
            System.out.println("\nTotal size: " + totalSize + " bytes");
            
            // 5. Find files modified recently
            long cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24 hours
            System.out.println("\n=== Recently modified ===");
            Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return Files.getLastModifiedTime(p).toMillis() > cutoff;
                    } catch (IOException e) { return false; }
                })
                .forEach(System.out::println);
            
            // 6. Using Files.find() with BiPredicate
            System.out.println("\n=== Large files (>1MB) ===");
            Files.find(Paths.get("."), Integer.MAX_VALUE,
                (path, attrs) -> attrs.isRegularFile() && attrs.size() > 1_000_000)
                .forEach(System.out::println);
            
            // 7. Custom FileVisitor
            Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
                private int fileCount = 0;
                private int dirCount = 0;
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileCount++;
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    dirCount++;
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc == null) {
                        System.out.println("\nFiles: " + fileCount + ", Dirs: " + dirCount);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.println("Failed to visit: " + file);
                    return FileVisitResult.CONTINUE;
                }
            });
            
            // 8. Skip directories
            Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().equals(".git")) {
                        return FileVisitResult.SKIP_SUBTREE; // Skip .git directories
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Source code indexer - walk project directory, find all .java files, extract class/method names, build search index.

### Exam Traps
- ❌ **TRAP:** Files.walk() must be closed (use try-with-resources)
  ```java
  Files.walk(path).forEach(System.out::println); // ❌ Resource leak!
  
  try (Stream<Path> stream = Files.walk(path)) { // ✅ Properly closed
      stream.forEach(System.out::println);
  }
  ```
- ❌ **TRAP:** SKIP_SUBTREE only works in preVisitDirectory
  ```java
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      return FileVisitResult.SKIP_SUBTREE; // ❌ Wrong place! No effect
  }
  
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
      return FileVisitResult.SKIP_SUBTREE; // ✅ Skips entire directory
  }
  ```
- ❌ **TRAP:** TERMINATE stops entire walk immediately
  ```java
  return FileVisitResult.TERMINATE; // Stops walking completely
  return FileVisitResult.CONTINUE; // Continues to next file
  ```

---

## 9. Path Matchers and Glob Patterns

**Exam Objective:** Filter paths using patterns

### What are Path Matchers?
Pattern-based path matching using glob or regex syntax. Used for filtering files.

### When to Use
- Filtering directory listings
- Finding files by extension
- Pattern-based searches
- Build tool file selection

### Why Use Path Matchers
- Simple pattern syntax (glob)
- Flexible matching
- Platform-independent
- Integration with Files.walk()

### Real-World Example
```java
import java.nio.file.*;
import java.io.IOException;

public class PathMatcherExample {
    public static void main(String[] args) {
        try {
            FileSystem fs = FileSystems.getDefault();
            
            // 1. Glob patterns
            PathMatcher txtMatcher = fs.getPathMatcher("glob:*.txt");
            System.out.println(txtMatcher.matches(Paths.get("file.txt"))); // true
            System.out.println(txtMatcher.matches(Paths.get("file.java"))); // false
            
            // 2. Match with path separators
            PathMatcher javaMatcher = fs.getPathMatcher("glob:**/*.java");
            System.out.println(javaMatcher.matches(Paths.get("src/Main.java"))); // true
            System.out.println(javaMatcher.matches(Paths.get("src/util/Helper.java"))); // true
            
            // 3. Character classes
            PathMatcher digitMatcher = fs.getPathMatcher("glob:file[0-9].txt");
            System.out.println(digitMatcher.matches(Paths.get("file5.txt"))); // true
            System.out.println(digitMatcher.matches(Paths.get("fileA.txt"))); // false
            
            // 4. Negation
            PathMatcher notTestMatcher = fs.getPathMatcher("glob:*[!t][!e][!s][!t].txt");
            
            // 5. Multiple extensions
            PathMatcher sourceMatcher = fs.getPathMatcher("glob:*.{java,class,jar}");
            System.out.println(sourceMatcher.matches(Paths.get("Main.java"))); // true
            System.out.println(sourceMatcher.matches(Paths.get("Main.class"))); // true
            System.out.println(sourceMatcher.matches(Paths.get("lib.jar"))); // true
            System.out.println(sourceMatcher.matches(Paths.get("readme.txt"))); // false
            
            // 6. Regex patterns
            PathMatcher regexMatcher = fs.getPathMatcher("regex:.*\\.java");
            System.out.println(regexMatcher.matches(Paths.get("Test.java"))); // true
            
            // 7. Using with Files.walk()
            System.out.println("\n=== Java files ===");
            PathMatcher javaFiles = fs.getPathMatcher("glob:**.java");
            try (var stream = Files.walk(Paths.get("."))) {
                stream.filter(javaFiles::matches)
                    .forEach(System.out::println);
            }
            
            // 8. DirectoryStream with glob
            System.out.println("\n=== Text files in current dir ===");
            try (DirectoryStream<Path> stream = 
                    Files.newDirectoryStream(Paths.get("."), "*.txt")) {
                for (Path entry : stream) {
                    System.out.println(entry.getFileName());
                }
            }
            
            // 9. Complex patterns
            PathMatcher testFiles = fs.getPathMatcher("glob:**/test/**/*.java");
            PathMatcher srcFiles = fs.getPathMatcher("glob:**/src/**/*.java");
            
            Path p = Paths.get("project/src/main/Main.java");
            System.out.println("Is test file: " + testFiles.matches(p)); // false
            System.out.println("Is src file: " + srcFiles.matches(p)); // true
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Build system - find all source files matching pattern, exclude test files, filter by extension.

### Exam Traps
- ❌ **TRAP:** * matches file name only, ** matches across directories
  ```java
  PathMatcher m1 = fs.getPathMatcher("glob:*.java");
  m1.matches(Paths.get("src/Main.java")); // ❌ false! * doesn't cross /
  
  PathMatcher m2 = fs.getPathMatcher("glob:**.java");
  m2.matches(Paths.get("src/Main.java")); // ✅ true! ** crosses directories
  ```
- ❌ **TRAP:** Glob patterns are platform-dependent for separators
  ```java
  // On Windows:
  "glob:src\\*.java" // Windows separator
  
  // On Unix:
  "glob:src/*.java" // Unix separator
  
  // Cross-platform:
  "glob:**/*.java" // Use ** and / for cross-platform
  ```
- ❌ **TRAP:** Character classes use [], not ()
  ```java
  "glob:file(0-9).txt" // ❌ Wrong! Literal parentheses
  "glob:file[0-9].txt" // ✅ Correct! Matches file0.txt to file9.txt
  ```

---

## 10. Watch Service (Monitoring File Changes)

**Exam Objective:** Monitor file system changes

### What is Watch Service?
API for monitoring directory changes: file creation, modification, deletion. Registers directory for events.

### When to Use
- Hot reload configurations
- File synchronization
- Build tool file watching
- Real-time monitoring

### Why Use Watch Service
- Event-driven file monitoring
- Efficient (OS-level notifications)
- Asynchronous monitoring
- Multiple directory watching

### Real-World Example
```java
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.IOException;

public class WatchServiceExample {
    public static void main(String[] args) {
        try {
            // 1. Create WatchService
            WatchService watchService = FileSystems.getDefault().newWatchService();
            
            // 2. Register directory for events
            Path dir = Paths.get(".");
            WatchKey key = dir.register(watchService,
                ENTRY_CREATE,
                ENTRY_MODIFY,
                ENTRY_DELETE);
            
            System.out.println("Watching directory: " + dir.toAbsolutePath());
            System.out.println("Create, modify, or delete files to see events...");
            
            // 3. Event loop
            while (true) {
                // Wait for events (blocks until event occurs)
                WatchKey watchKey = watchService.take();
                
                // Process all events
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    // Overflow event
                    if (kind == OVERFLOW) {
                        System.out.println("Event overflow!");
                        continue;
                    }
                    
                    // Get the file name
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    
                    // Print event
                    System.out.println(kind.name() + ": " + fileName);
                    
                    // Example: reload config on modification
                    if (kind == ENTRY_MODIFY && fileName.toString().equals("config.txt")) {
                        System.out.println("Config file changed! Reloading...");
                        // reloadConfig();
                    }
                }
                
                // Reset key (must do this!)
                boolean valid = watchKey.reset();
                if (!valid) {
                    System.out.println("Watch key no longer valid!");
                    break;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Example with timeout
    public static void watchWithTimeout() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(".");
            dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            
            // Poll with timeout (doesn't block forever)
            WatchKey key = watchService.poll(5, java.util.concurrent.TimeUnit.SECONDS);
            
            if (key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event: " + event.kind());
                }
                key.reset();
            } else {
                System.out.println("No events in 5 seconds");
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
**Why:** Configuration hot-reload - watch config directory, reload settings when files change, no restart needed.

### Exam Traps
- ❌ **TRAP:** Must call reset() after processing events
  ```java
  WatchKey key = watchService.take();
  for (WatchEvent<?> event : key.pollEvents()) {
      // Process event
  }
  // key.reset(); // ❌ Forgot to reset! Won't receive more events
  boolean valid = key.reset(); // ✅ Must reset key
  ```
- ❌ **TRAP:** context() returns relative path, not absolute
  ```java
  Path fileName = event.context(); // Relative to watched directory
  Path fullPath = dir.resolve(fileName); // ✅ Resolve to get full path
  ```
- ❌ **TRAP:** Watch service doesn't monitor subdirectories
  ```java
  dir.register(watchService, ENTRY_CREATE);
  // Only monitors "dir", NOT subdirectories!
  // Must register each subdirectory separately
  ```
- ❌ **TRAP:** OVERFLOW event means events were lost
  ```java
  if (event.kind() == OVERFLOW) {
      // Too many events! Some were lost
      // Cannot know which events were missed
  }
  ```

---

## Exam Questions

**Exam Format:**
- 50 questions total (5-6 on I/O)
- 120 minutes
- 68% passing score (34/50)
- Multiple choice, code analysis

---

### Question 1
```java
Path p1 = Paths.get("/home/user/documents");
Path p2 = Paths.get("/home/user/downloads");
Path result = p1.relativize(p2);
System.out.println(result);
```

What is printed?

A. downloads  
B. ../downloads  
C. /home/user/downloads  
D. documents/downloads  
E. Compilation error

**Answer:** B

**Explanation:** relativize() creates a relative path from p1 to p2. From /home/user/documents to /home/user/downloads, you go up one level (..) then into downloads. Result: ../downloads. (Exam Objective: Construct, traverse Path objects)

---

### Question 2
```java
Path path = Paths.get("/home/user/file.txt");
System.out.println(path.getName(0));
```

What is printed?

A. /  
B. home  
C. user  
D. file.txt  
E. IndexOutOfBoundsException

**Answer:** B

**Explanation:** getName(index) returns path element at index. Index 0 is first element AFTER root. Root (/) is accessed via getRoot(), not getName(). Elements: 0=home, 1=user, 2=file.txt. (Exam Objective: Construct, traverse Path objects)

---

### Question 3
```java
Path source = Paths.get("source.txt");
Path target = Paths.get("target.txt");
Files.copy(source, target);
Files.copy(source, target);
```

What happens?

A. Both copy operations succeed  
B. Second copy fails silently  
C. FileAlreadyExistsException on second copy  
D. First copy throws exception  
E. target.txt is overwritten

**Answer:** C

**Explanation:** Files.copy() throws FileAlreadyExistsException if target exists. Must use StandardCopyOption.REPLACE_EXISTING to overwrite. (Exam Objective: Use Files class)

---

### Question 4
```java
class Data {
    int value = 42;
}

Data data = new Data();
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("data.ser"))) {
    oos.writeObject(data);
}
```

What happens?

A. Compiles and runs successfully  
B. Compilation error  
C. NotSerializableException at runtime  
D. FileNotFoundException  
E. ClassCastException

**Answer:** C

**Explanation:** Data class doesn't implement Serializable interface. Attempting to serialize throws NotSerializableException. Must add "implements Serializable". (Exam Objective: Serialize Java objects)

---

### Question 5
```java
class User implements Serializable {
    String name = "Alice";
    transient String password = "secret";
    static String role = "admin";
}

// Serialize and deserialize
User original = new User();
// ... serialize to file ...
User loaded = // ... deserialize from file ...

System.out.println(loaded.password);
System.out.println(loaded.role);
```

What is printed?

A. secret admin  
B. null admin  
C. secret null  
D. null null  
E. Compilation error

**Answer:** B

**Explanation:** transient fields (password) are not serialized, become null. static fields (role) are not serialized but retain class value "admin". (Exam Objective: Serialize Java objects)

---

### Question 6
```java
Path base = Paths.get("/home/user");
Path absolute = Paths.get("/etc/config");
Path result = base.resolve(absolute);
System.out.println(result);
```

What is printed?

A. /home/user/etc/config  
B. /etc/config  
C. /home/user  
D. etc/config  
E. IllegalArgumentException

**Answer:** B

**Explanation:** resolve() with absolute path ignores base and returns the absolute path. Only relative paths are resolved against base. (Exam Objective: Construct Path objects)

---

### Question 7
```java
File f = new File("test.txt");
System.out.println(f.exists());
```

Assuming test.txt doesn't exist, what is printed?

A. true  
B. false  
C. FileNotFoundException  
D. Compilation error  
E. null

**Answer:** B

**Explanation:** Creating File object doesn't create physical file. exists() returns false if file doesn't exist. No exception thrown. (Exam Objective: Understand File class)

---

### Question 8
```java
Files.delete(Paths.get("nonexistent.txt"));
```

What happens?

A. Returns false  
B. Returns true  
C. Does nothing  
D. NoSuchFileException  
E. Compilation error

**Answer:** D

**Explanation:** Files.delete() throws NoSuchFileException if file doesn't exist. Use Files.deleteIfExists() to avoid exception. (Exam Objective: Use Files class)

---

### Question 9
```java
Path path = Paths.get("/home/user/../user/./docs");
Path result = path.normalize();
System.out.println(result);
```

What is printed?

A. /home/user/docs  
B. /home/user/../user/./docs  
C. /home/docs  
D. /user/docs  
E. Compilation error

**Answer:** A

**Explanation:** normalize() removes redundant elements: .. (parent), . (current). /home/user/../user = /home/user, then ./docs = docs. Result: /home/user/docs. (Exam Objective: Construct Path objects)

---

### Question 10
```java
Path p1 = Paths.get("/home/user");
Path p2 = Paths.get("documents");
Path result = p1.relativize(p2);
```

What happens?

A. Prints ../documents  
B. Prints documents  
C. Prints /home/user/documents  
D. IllegalArgumentException  
E. Compilation error

**Answer:** D

**Explanation:** relativize() requires both paths to be absolute or both relative. Cannot relativize absolute (/home/user) with relative (documents). (Exam Objective: Construct Path objects)

---

### Question 11
```java
try (FileInputStream fis = new FileInputStream("data.txt")) {
    int data = fis.read();
    if (data == 0) {
        System.out.println("End of file");
    }
}
```

What's wrong with this code?

A. Nothing, it's correct  
B. Should check data == -1 for EOF  
C. Should check data == null for EOF  
D. FileInputStream doesn't have read()  
E. try-with-resources can't be used with FileInputStream

**Answer:** B

**Explanation:** read() returns -1 at end of file, not 0. Checking for 0 is incorrect. (Exam Objective: Read file data using I/O Streams)

---

### Question 12
```java
File dir = new File("a/b/c");
dir.mkdir();
```

Assuming directories a and b don't exist, what happens?

A. Creates all directories a, b, c  
B. Creates only directory c  
C. Returns false, no directories created  
D. FileNotFoundException  
E. Compilation error

**Answer:** C

**Explanation:** mkdir() creates only single directory, fails if parent doesn't exist. Use mkdirs() to create parent directories. (Exam Objective: Understand File class)

---

### Question 13
```java
Path file = Paths.get("huge.txt"); // 10 GB file
List<String> lines = Files.readAllLines(file);
```

What's the potential problem?

A. Nothing, this is correct  
B. OutOfMemoryError - entire file loaded into memory  
C. File too large to read  
D. Compilation error  
E. FileNotFoundException

**Answer:** B

**Explanation:** readAllLines() loads entire file into memory. For large files, use Files.lines() which streams lazily. (Exam Objective: Use Files class)

---

### Question 14
```java
try (Stream<Path> stream = Files.walk(Paths.get("."))) {
    stream.forEach(System.out::println);
}
// Later...
stream.forEach(System.out::println);
```

What happens at the second forEach?

A. Prints all paths again  
B. IllegalStateException  
C. Compilation error - stream out of scope  
D. NullPointerException  
E. Does nothing

**Answer:** C

**Explanation:** stream variable is declared inside try-with-resources, out of scope after try block. Compilation error at second reference. (Exam Objective: Traverse directory structures)

---

### Question 15
```java
class Config implements Serializable {
    private static final long serialVersionUID = 1L;
    transient String tempData = "temp";
    static String appName = "MyApp";
}

// After serialize and deserialize
System.out.println(config.tempData);
System.out.println(config.appName);
```

What is printed?

A. temp MyApp  
B. null MyApp  
C. temp null  
D. null null  
E. Compilation error

**Answer:** B

**Explanation:** transient fields not serialized (null after deserialization). static fields retain class value (not serialized but available as class member). (Exam Objective: Serialize Java objects)

---

### Question 16
```java
PathMatcher matcher = FileSystems.getDefault()
    .getPathMatcher("glob:*.txt");

System.out.println(matcher.matches(Paths.get("src/file.txt")));
```

What is printed?

A. true  
B. false  
C. Compilation error  
D. IllegalArgumentException  
E. NullPointerException

**Answer:** B

**Explanation:** * matches filename only, doesn't cross directory separators. src/file.txt has path separator, doesn't match *.txt. Use **/*.txt to match across directories. (Exam Objective: Filter paths using patterns)

---

### Question 17
```java
BasicFileAttributes attrs = Files.readAttributes(
    Paths.get("file.txt"), BasicFileAttributes.class);

attrs.setLastModifiedTime(FileTime.fromMillis(System.currentTimeMillis()));
```

What happens?

A. Modifies last modified time  
B. Compilation error  
C. UnsupportedOperationException  
D. IOException  
E. Does nothing

**Answer:** B

**Explanation:** BasicFileAttributes is read-only, no setter methods. Use Files.setLastModifiedTime() or FileAttributeView to modify. (Exam Objective: Read file attributes)

---

### Question 18
```java
WatchKey key = watchService.take();
for (WatchEvent<?> event : key.pollEvents()) {
    System.out.println(event.kind());
}
// Continue watching...
```

What's missing?

A. Nothing, code is complete  
B. key.reset() call  
C. watchService.close() call  
D. event.reset() call  
E. key.cancel() call

**Answer:** B

**Explanation:** Must call key.reset() after processing events to continue receiving events. Without reset(), no more events are reported. (Exam Objective: Monitor file changes)

---

### Question 19
```java
Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(".tmp")) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }
});
```

What does SKIP_SUBTREE do here?

A. Skips all .tmp files  
B. Skips directory containing .tmp file  
C. Nothing - wrong place for SKIP_SUBTREE  
D. Stops entire walk  
E. Compilation error

**Answer:** C

**Explanation:** SKIP_SUBTREE only works in preVisitDirectory(), has no effect in visitFile(). Use CONTINUE or TERMINATE in visitFile(). (Exam Objective: Traverse directory structures)

---

### Question 20
```java
class Employee implements Serializable {
    String name;
    Manager manager; // Manager doesn't implement Serializable
}

Employee emp = new Employee();
emp.name = "Bob";
emp.manager = new Manager();

try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("emp.ser"))) {
    oos.writeObject(emp);
}
```

What happens?

A. Serializes successfully  
B. Compilation error  
C. NotSerializableException for Manager  
D. NullPointerException  
E. Only name is serialized

**Answer:** C

**Explanation:** All non-transient fields must be serializable. Manager doesn't implement Serializable, throws NotSerializableException. Mark manager as transient or make Manager serializable. (Exam Objective: Serialize Java objects)

---

## Chapter Summary

### Official 1Z0-830 Exam Objectives Checklist

✅ **Read and write console and file data using I/O Streams**
- Byte streams (InputStream/OutputStream)
- Character streams (Reader/Writer)
- Buffered streams for performance
- Console I/O

✅ **Serialize and deserialize Java objects**
- Serializable interface
- transient and static fields
- serialVersionUID
- Custom serialization (writeObject/readObject)

✅ **Construct, traverse, create, read, and write Path objects**
- Creating Path objects (Paths.get, Path.of)
- Path components (getFileName, getParent, getRoot)
- resolve, relativize, normalize
- Path comparison and properties

✅ **Use Files class to manipulate files and directories**
- File existence and properties
- Creating, deleting, copying, moving files
- Reading and writing file content
- File attributes and metadata
- Directory operations and walking

---

### Key Points for Exam

1. **I/O Streams**
   - Byte streams for binary, character streams for text
   - read() returns -1 at EOF (not 0!)
   - BufferedReader.readLine() returns null at EOF
   - Always use try-with-resources

2. **File vs Path**
   - File is legacy, Path is modern
   - File object creation doesn't create physical file
   - Path is immutable and thread-safe
   - Use Path for new code

3. **Path Operations**
   - getName(index) doesn't include root
   - resolve() with absolute path ignores base
   - relativize() requires both absolute or both relative
   - normalize() doesn't check existence

4. **Files Class**
   - copy() doesn't overwrite by default
   - delete() throws exception, deleteIfExists() returns boolean
   - createFile() fails if exists
   - readAllLines() loads entire file into memory

5. **Serialization**
   - Must implement Serializable
   - transient fields not serialized (null after)
   - static fields not serialized (retain class value)
   - serialVersionUID for version compatibility

6. **Custom Serialization**
   - writeObject() signature must be exact
   - Must call defaultWriteObject() first
   - Read in same order as write
   - Methods must be private

7. **File Attributes**
   - BasicFileAttributes is read-only
   - Use Files class or views to modify
   - Platform-specific attributes may throw exceptions
   - fileKey() can be null

8. **Directory Walking**
   - Files.walk() must be closed (try-with-resources)
   - SKIP_SUBTREE only in preVisitDirectory()
   - TERMINATE stops entire walk
   - Use filters to control what's processed

9. **Path Matchers**
   - * matches name only, ** crosses directories
   - Glob is simpler, regex more powerful
   - Platform-dependent separators
   - Use with Files.walk() for filtering

10. **Watch Service**
    - Must call reset() after processing
    - Only monitors registered directory (not subdirectories)
    - context() returns relative path
    - OVERFLOW means events were lost

---

### Common Exam Patterns

1. **Path Manipulation Traps**
   - resolve() behavior with absolute paths
   - relativize() absolute/relative mixing
   - getName() index confusion

2. **Files Class Exceptions**
   - When methods throw vs return boolean
   - FileAlreadyExistsException scenarios
   - NoSuchFileException vs returns false

3. **Serialization Rules**
   - transient vs static behavior
   - Serializable requirement cascade
   - Custom serialization method signatures

4. **Stream EOF Detection**
   - -1 for read() methods
   - null for readLine()
   - Don't confuse with 0 or empty string

5. **File Operations**
   - mkdir() vs mkdirs()
   - copy() default behavior
   - delete() vs deleteIfExists()

6. **Path Matcher Patterns**
   - * vs ** difference
   - Character class syntax []
   - Cross-platform considerations

7. **Watch Service Usage**
   - reset() requirement
   - Relative vs absolute paths in events
   - SKIP_SUBTREE placement

---

### Study Tips

1. **Practice Path Operations**
   - Write code for resolve, relativize, normalize
   - Understand index-based getName()
   - Compare absolute and relative paths

2. **Memorize Stream EOF**
   - read() returns -1 (NOT 0!)
   - readLine() returns null (NOT empty)
   - Practice reading files correctly

3. **Understand Serialization**
   - Know what gets serialized (instance fields)
   - Know what doesn't (transient, static)
   - Practice custom serialization

4. **Know Files Class Methods**
   - Which throw exceptions vs return boolean
   - Default copy/move behavior
   - Memory implications (readAllLines)

5. **Directory Walking Patterns**
   - Always use try-with-resources
   - Know FileVisitor return values
   - Practice with filters

6. **Path Matcher Syntax**
   - Write glob patterns for common cases
   - Understand * vs ** difference
   - Test regex vs glob

7. **File Attributes**
   - Read-only vs modifiable
   - Platform-specific attributes
   - Null handling

8. **Common Mistakes**
   - Don't forget reset() in WatchService
   - Check for null from listFiles()
   - Use correct EOF detection

---

### Quick Reference Card

#### Path Creation
```java
Paths.get("file.txt")           // Relative
Paths.get("/home/user/file")    // Absolute
Path.of("file.txt")             // Java 11+
```

#### Path Operations
```java
path.getFileName()              // Last element
path.getParent()                // Parent directory
path.getRoot()                  // Root (/)
path.getName(index)             // Element at index (excludes root)
path.resolve(other)             // Combine paths
path.relativize(other)          // Relative path between
path.normalize()                // Remove . and ..
```

#### Files Operations
```java
Files.exists(path)              // Check existence
Files.createFile(path)          // Create file
Files.createDirectory(path)     // Create dir
Files.createDirectories(path)   // Create with parents
Files.delete(path)              // Delete (throws exception)
Files.deleteIfExists(path)      // Delete (returns boolean)
Files.copy(src, dest)           // Copy file
Files.move(src, dest)           // Move/rename
Files.readAllLines(path)        // Read entire file
Files.write(path, lines)        // Write lines
```

#### Serialization
```java
class Data implements Serializable {
    private static final long serialVersionUID = 1L;
    transient String notSerialized; // Not serialized
    static String classLevel;       // Not serialized
}
```

#### I/O Streams
```java
read() returns -1 at EOF        // Not 0!
readLine() returns null at EOF  // Not empty string
```

---

### Next Steps

**Practice Tasks:**
1. Write program to walk directory and find large files
2. Serialize/deserialize complex object graph
3. Implement custom FileVisitor for specific task
4. Create file backup utility with attributes
5. Write path matcher for various patterns

**Review Before Exam:**
- Path operations (resolve, relativize)
- Serialization rules (transient, static)
- Files class exception handling
- EOF detection methods
- Directory walking patterns

**Common Exam Traps:**
- resolve() with absolute path
- relativize() absolute/relative mixing
- Serialization what's included/excluded
- read() returns -1 not 0
- Files.walk() resource leak

---

[← Previous Chapter](CHAPTER-08.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-10.md)

---

**Chapter 9 Complete!** ✅  
I/O API (Classic I/O and NIO.2) - Essential for file operations and exam success.
