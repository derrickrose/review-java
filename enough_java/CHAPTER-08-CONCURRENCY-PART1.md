# Chapter 8: Concurrency & Virtual Threads

[← Previous Chapter](CHAPTER-07.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-09.md)

---

## Chapter Overview

| Attribute | Details |
|-----------|---------|
| **Exam Objectives Covered** | Managing Concurrent Code Execution |
| **Java Versions** | 1.0 (Threads), 5 (Executors), 21 (Virtual Threads) |
| **Code Examples** | 70+ |
| **Study Time** | 15-18 hours |
| **Exam Weight** | ~15-20% |
| **Difficulty** | ⭐⭐⭐⭐⭐ Extreme |

**Official 1Z0-830 Exam Objectives Covered:**
- Create both platform and virtual threads
- Use both Runnable and Callable objects, manage the thread lifecycle, and use different Executor services and concurrent API to run tasks
- Develop thread-safe code, using locking mechanisms and concurrent API
- Process Java collections concurrently and utilize parallel streams

---

## 1. Platform Threads (Traditional Threads)

**Exam Objective:** Create both platform and virtual threads

### What are Platform Threads?
Platform threads are traditional Java threads that map 1:1 to OS threads. Each platform thread consumes ~2MB of memory and is managed by the OS scheduler.

### When to Use
- Small number of concurrent tasks (< 1000 threads)
- CPU-intensive operations
- Long-running background tasks
- When you need thread priorities

### Why Use Platform Threads
- Direct OS thread mapping (predictable)
- Full control over thread lifecycle
- Supports thread priorities
- Compatible with all existing code

### Real-World Example
```java
// Creating platform threads (traditional way)
public class PlatformThreadExample {
    public static void main(String[] args) {
        // Method 1: Extend Thread class
        Thread t1 = new MyThread();
        t1.start();
        
        // Method 2: Implement Runnable (preferred)
        Thread t2 = new Thread(new MyRunnable());
        t2.start();
        
        // Method 3: Lambda (Java 8+)
        Thread t3 = new Thread(() -> {
            System.out.println("Running in: " + Thread.currentThread().getName());
        });
        t3.start();
        
        // Check thread type
        System.out.println("Is virtual: " + t3.isVirtual()); // false (Java 21)
    }
}

class MyThread extends Thread {
    public void run() {
        System.out.println("MyThread running");
    }
}

class MyRunnable implements Runnable {
    public void run() {
        System.out.println("MyRunnable running");
    }
}
```
**Why:** Traditional approach for thread creation. Limited to thousands of threads due to memory constraints.

### Exam Traps
- ❌ **TRAP:** Calling run() doesn't create new thread
  ```java
  Thread t = new Thread(() -> System.out.println("Hi"));
  t.run();  // ❌ Runs in CURRENT thread, not new thread!
  t.start(); // ✅ Starts NEW thread
  ```
- ❌ **TRAP:** Can't call start() twice on same thread
  ```java
  Thread t = new Thread(() -> System.out.println("Hi"));
  t.start();
  t.start(); // ❌ IllegalThreadStateException!
  ```
- ❌ **TRAP:** Thread stops when main() ends (if not daemon)
  ```java
  Thread t = new Thread(() -> {
      Thread.sleep(5000); // Long task
  });
  t.start();
  // Main ends → t continues (NOT daemon)
  
  t.setDaemon(true); // Must be before start()
  t.start();
  // Main ends → t terminates immediately
  ```
- ❌ **TRAP:** Platform threads are expensive (2MB each)
  ```java
  for (int i = 0; i < 100_000; i++) {
      new Thread(() -> doWork()).start(); // ❌ OutOfMemoryError!
  }
  ```

---

## 2. Runnable vs Callable

**Exam Objective:** Use both Runnable and Callable objects

### What are Runnable and Callable?
Runnable (Java 1.0) = task with no return value. Callable (Java 5) = task that returns value and can throw checked exceptions.

### When to Use
| Interface | Return Value | Checked Exceptions | When to Use |
|-----------|--------------|-------------------|-------------|
| **Runnable** | No (void) | No | Fire-and-forget tasks |
| **Callable** | Yes (generic) | Yes | Tasks needing result or error handling |

### Why Use Each
**Runnable:**
- Simple fire-and-forget tasks
- No result needed
- Compatible with Thread class

**Callable:**
- Need return value
- Need exception handling
- Used with ExecutorService

### Real-World Example
```java
import java.util.concurrent.*;

// Runnable - no return value
class DataProcessor implements Runnable {
    public void run() {
        System.out.println("Processing data...");
        // No return value
    }
}

// Callable - returns value
class DataFetcher implements Callable<String> {
    public String call() throws Exception {
        Thread.sleep(1000); // Simulate network delay
        if (Math.random() > 0.5) {
            throw new Exception("Network error");
        }
        return "Data fetched successfully";
    }
}

// Usage
public class RunnableCallableExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Submit Runnable
        Future<?> future1 = executor.submit(new DataProcessor());
        future1.get(); // Returns null
        
        // Submit Callable
        Future<String> future2 = executor.submit(new DataFetcher());
        try {
            String result = future2.get(); // Blocks until complete
            System.out.println(result);
        } catch (ExecutionException e) {
            System.out.println("Task failed: " + e.getCause());
        }
        
        executor.shutdown();
    }
}
```
**Why:** API call needs result (Callable), logging doesn't (Runnable).

### Exam Traps
- ❌ **TRAP:** Runnable with Thread class, Callable with ExecutorService only
  ```java
  Callable<String> task = () -> "Hello";
  new Thread(task).start(); // ❌ Compilation error! Thread doesn't accept Callable
  ```
- ❌ **TRAP:** Runnable.run() returns void, can't get result
  ```java
  Runnable r = () -> { return "Hello"; }; // ❌ Can't return from void method
  ```
- ❌ **TRAP:** Callable.call() can throw checked exceptions
  ```java
  Callable<String> c = () -> {
      throw new IOException(); // ✅ Allowed!
  };
  ```
- ❌ **TRAP:** Future.get() on Runnable returns null
  ```java
  Future<?> future = executor.submit(() -> System.out.println("Hi"));
  Object result = future.get(); // Returns null (not "Hi")
  ```

---

## 3. ExecutorService and Thread Pools

**Exam Objective:** Use different Executor services to run tasks

### What is ExecutorService?
High-level API for managing thread pools. Reuses threads to execute multiple tasks, avoiding the overhead of creating new threads.

### When to Use
| Executor Type | When to Use |
|---------------|-------------|
| **newSingleThreadExecutor()** | Sequential task execution |
| **newFixedThreadPool(n)** | Fixed number of concurrent tasks |
| **newCachedThreadPool()** | Variable load, short-lived tasks |
| **newScheduledThreadPool(n)** | Delayed or periodic tasks |

### Why Use ExecutorService
- Thread reuse (better performance)
- Automatic thread management
- Task queue management
- Graceful shutdown support

### Real-World Example
```java
import java.util.concurrent.*;
import java.util.*;

public class ExecutorServiceExample {
    public static void main(String[] args) throws Exception {
        // 1. Fixed thread pool - best for known workload
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        
        // Submit 10 tasks to pool of 3 threads
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            Future<Integer> future = fixedPool.submit(() -> {
                Thread.sleep(1000);
                return taskId * 2;
            });
            futures.add(future);
        }
        
        // Get results
        for (Future<Integer> future : futures) {
            System.out.println("Result: " + future.get());
        }
        
        // 2. Scheduled executor - periodic tasks
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
        
        // Execute once after delay
        scheduled.schedule(() -> System.out.println("Delayed task"), 2, TimeUnit.SECONDS);
        
        // Execute periodically
        scheduled.scheduleAtFixedRate(
            () -> System.out.println("Periodic: " + System.currentTimeMillis()),
            0, // Initial delay
            1, // Period
            TimeUnit.SECONDS
        );
        
        // 3. Proper shutdown
        fixedPool.shutdown(); // No new tasks accepted
        fixedPool.awaitTermination(10, TimeUnit.SECONDS); // Wait for completion
        
        scheduled.shutdownNow(); // Interrupt running tasks
    }
}
```
**Why:** Web server handling requests - thread pool handles 100 concurrent connections with only 10 threads.

### Exam Traps
- ❌ **TRAP:** Must call shutdown() or program hangs
  ```java
  ExecutorService executor = Executors.newFixedThreadPool(5);
  executor.submit(() -> System.out.println("Task"));
  // ❌ Program never exits! Executor threads keep running
  executor.shutdown(); // ✅ Required!
  ```
- ❌ **TRAP:** execute() vs submit() difference
  ```java
  executor.execute(runnable); // No return value
  Future<?> future = executor.submit(runnable); // Returns Future
  ```
- ❌ **TRAP:** shutdown() vs shutdownNow()
  ```java
  executor.shutdown();    // Waits for running tasks to complete
  executor.shutdownNow(); // Interrupts running tasks immediately
  ```
- ❌ **TRAP:** Can't submit tasks after shutdown()
  ```java
  executor.shutdown();
  executor.submit(() -> System.out.println("Hi")); // ❌ RejectedExecutionException
  ```
- ❌ **TRAP:** newCachedThreadPool() can create unlimited threads
  ```java
  ExecutorService executor = Executors.newCachedThreadPool();
  for (int i = 0; i < 1_000_000; i++) {
      executor.submit(() -> Thread.sleep(1000000));
  }
  // ❌ Creates 1 million threads → OutOfMemoryError!
  ```

---

## 4. Thread Safety and Synchronization

**Exam Objective:** Develop thread-safe code using locking mechanisms

### What is Thread Safety?
Code that correctly handles concurrent access by multiple threads without data corruption or unexpected behavior.

### When to Use
- Shared mutable state (fields, collections)
- Critical sections (one thread at a time)
- Coordinating threads (wait/notify)
- Atomic operations on primitives

### Why Use Synchronization
- Prevents race conditions
- Ensures visibility of changes
- Guarantees atomicity
- Coordinates thread execution

### Real-World Example
```java
// Race condition example (NOT thread-safe)
class BankAccount {
    private int balance = 100;
    
    public void withdraw(int amount) {
        if (balance >= amount) {
            // Context switch can happen here!
            balance -= amount; // ❌ Race condition!
        }
    }
}

// Fixed with synchronized (thread-safe)
class SafeBankAccount {
    private int balance = 100;
    
    // Method synchronization
    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount; // ✅ Thread-safe
        }
    }
    
    // Block synchronization (more granular)
    public void deposit(int amount) {
        synchronized(this) {
            balance += amount;
        }
    }
    
    // Synchronized on different object
    private final Object lock = new Object();
    
    public void transfer(SafeBankAccount other, int amount) {
        synchronized(lock) {
            this.withdraw(amount);
            other.deposit(amount);
        }
    }
}

// Demo
public class ThreadSafetyExample {
    public static void main(String[] args) throws Exception {
        SafeBankAccount account = new SafeBankAccount();
        
        // 10 threads trying to withdraw simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> account.withdraw(10));
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Final balance: " + account.balance); // Always 0 (thread-safe)
    }
}
```
**Why:** Banking system - multiple transactions must not corrupt balance.

### Exam Traps
- ❌ **TRAP:** synchronized on instance vs class
  ```java
  class Counter {
      private static int count = 0;
      
      public synchronized void increment() {
          count++; // ❌ Locks instance, but count is static!
      }
      
      public static synchronized void incrementStatic() {
          count++; // ✅ Locks class object
      }
  }
  ```
- ❌ **TRAP:** synchronized doesn't prevent visibility issues for long/double
  ```java
  class Data {
      private long value; // 64-bit
      
      public void setValue(long v) {
          value = v; // ❌ NOT atomic even with synchronized method!
      }
      
      public synchronized void setValueSafe(long v) {
          value = v; // ✅ Atomic with synchronized
      }
  }
  ```
- ❌ **TRAP:** Deadlock with multiple locks
  ```java
  Object lock1 = new Object();
  Object lock2 = new Object();
  
  // Thread 1
  synchronized(lock1) {
      synchronized(lock2) { } // Waits for lock2
  }
  
  // Thread 2
  synchronized(lock2) {
      synchronized(lock1) { } // Waits for lock1
  }
  // ❌ Deadlock! Always acquire locks in same order
  ```
- ❌ **TRAP:** Synchronized on String/Integer (bad practice)
  ```java
  String lock = "lock";
  synchronized(lock) { } // ❌ String is interned, other code may use same lock!
  
  Object lock = new Object();
  synchronized(lock) { } // ✅ Private lock object
  ```

---

## 5. Concurrent Collections

**Exam Objective:** Process Java collections concurrently

### What are Concurrent Collections?
Thread-safe collection implementations from java.util.concurrent designed for concurrent access without external synchronization.

### When to Use
| Collection | When to Use |
|------------|-------------|
| **ConcurrentHashMap** | High-concurrency map (better than Hashtable) |
| **CopyOnWriteArrayList** | Rarely modified, frequently iterated |
| **CopyOnWriteArraySet** | Small sets with rare modifications |
| **ConcurrentLinkedQueue** | Non-blocking FIFO queue |
| **BlockingQueue** | Producer-consumer pattern |

### Why Use Concurrent Collections
- Better performance than synchronized collections
- Fine-grained locking (ConcurrentHashMap)
- Fail-safe iterators (no ConcurrentModificationException)
- Atomic operations (putIfAbsent, remove)

### Real-World Example
```java
import java.util.concurrent.*;
import java.util.*;

public class ConcurrentCollectionsExample {
    public static void main(String[] args) throws Exception {
        // 1. ConcurrentHashMap - high concurrency
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // 10 threads updating map concurrently
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    map.put("key" + j, threadId);
                }
            });
        }
        
        // Atomic operations
        map.putIfAbsent("newKey", 42); // Only puts if absent
        map.computeIfAbsent("computed", k -> k.length()); // Compute if absent
        
        // 2. CopyOnWriteArrayList - read-heavy workloads
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        
        // Iterator sees snapshot, modifications don't affect it
        Iterator<String> it = list.iterator();
        list.add("C"); // Modification during iteration
        while (it.hasNext()) {
            System.out.println(it.next()); // Only prints A, B (no CME!)
        }
        
        // 3. BlockingQueue - producer-consumer
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
        
        // Producer
        executor.submit(() -> {
            for (int i = 0; i < 5; i++) {
                queue.put("Item " + i); // Blocks if queue full
            }
        });
        
        // Consumer
        executor.submit(() -> {
            for (int i = 0; i < 5; i++) {
                String item = queue.take(); // Blocks if queue empty
                System.out.println("Consumed: " + item);
            }
        });
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
```
**Why:** Cache system with multiple threads reading/writing simultaneously.

### Exam Traps
- ❌ **TRAP:** ConcurrentHashMap doesn't allow null keys/values
  ```java
  ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
  map.put(null, "value"); // ❌ NullPointerException!
  map.put("key", null);    // ❌ NullPointerException!
  ```
- ❌ **TRAP:** CopyOnWriteArrayList is expensive for writes
  ```java
  CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
  for (int i = 0; i < 1_000_000; i++) {
      list.add("Item " + i); // ❌ Very slow! Copies entire array each time
  }
  ```
- ❌ **TRAP:** Iterator on ConcurrentHashMap may not reflect recent changes
  ```java
  ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
  map.put("A", 1);
  Iterator<String> it = map.keySet().iterator();
  map.put("B", 2); // May or may not appear in iteration
  ```
- ❌ **TRAP:** size() on ConcurrentHashMap is approximate
  ```java
  ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
  // Multiple threads adding/removing...
  int size = map.size(); // ❌ May not be exact count at this instant
  ```

---

