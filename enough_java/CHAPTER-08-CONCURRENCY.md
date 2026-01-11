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

## 6. Virtual Threads (Java 21 - Project Loom)

**Exam Objective:** Create both platform and virtual threads

### What are Virtual Threads?
Lightweight threads managed by the JVM (not OS). Virtual threads are scheduled on a pool of platform threads (carrier threads). Each virtual thread uses only ~1KB of memory vs 2MB for platform threads.

### When to Use
- High-concurrency I/O-bound tasks (thousands to millions of threads)
- Blocking I/O operations (network, database, file)
- Simple concurrent code (avoid reactive programming)
- Microservices with many concurrent requests

### Why Use Virtual Threads
- **Scalability:** Run millions of threads (vs thousands of platform threads)
- **Memory efficient:** 1KB vs 2MB per thread
- **Simpler code:** Write blocking code that scales like async
- **No code changes:** Drop-in replacement for platform threads

### Real-World Example
```java
import java.util.concurrent.*;
import java.time.*;

public class VirtualThreadsExample {
    public static void main(String[] args) throws Exception {
        // 1. Creating virtual threads (Java 21)
        
        // Method 1: Thread.ofVirtual()
        Thread vt1 = Thread.ofVirtual()
            .name("virtual-1")
            .start(() -> System.out.println("Virtual thread: " + Thread.currentThread()));
        
        vt1.join();
        System.out.println("Is virtual: " + vt1.isVirtual()); // true
        
        // Method 2: Thread.startVirtualThread()
        Thread vt2 = Thread.startVirtualThread(() -> {
            System.out.println("Running in: " + Thread.currentThread().getName());
        });
        
        // Method 3: Executors.newVirtualThreadPerTaskExecutor()
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Simulate 10,000 concurrent HTTP requests
            Instant start = Instant.now();
            
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                final int requestId = i;
                Future<String> future = executor.submit(() -> {
                    // Simulate HTTP call (blocking I/O)
                    Thread.sleep(1000); // 1 second delay
                    return "Response " + requestId;
                });
                futures.add(future);
            }
            
            // Get all results
            for (Future<String> future : futures) {
                future.get();
            }
            
            Duration duration = Duration.between(start, Instant.now());
            System.out.println("10,000 requests completed in: " + duration.toSeconds() + " seconds");
            // With platform threads: ~2-3 minutes (limited by thread pool)
            // With virtual threads: ~1 second (all run concurrently!)
        }
        
        // 4. Comparison: Platform vs Virtual
        comparePlatformVsVirtual();
    }
    
    static void comparePlatformVsVirtual() throws Exception {
        int tasks = 100_000;
        
        // Platform threads - FAILS with OutOfMemoryError
        try {
            for (int i = 0; i < tasks; i++) {
                Thread.ofPlatform().start(() -> {
                    try { Thread.sleep(1000); } catch (Exception e) {}
                });
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Platform threads: OutOfMemoryError!");
        }
        
        // Virtual threads - SUCCESS
        for (int i = 0; i < tasks; i++) {
            Thread.startVirtualThread(() -> {
                try { Thread.sleep(1000); } catch (Exception e) {}
            });
        }
        System.out.println("Virtual threads: 100,000 threads created successfully!");
    }
}
```
**Why:** Web server handling 100,000 concurrent connections - platform threads fail, virtual threads succeed.

### Exam Traps
- ❌ **TRAP:** Virtual threads are NOT faster for CPU-intensive tasks
  ```java
  // CPU-bound task (calculating prime numbers)
  Thread.startVirtualThread(() -> {
      for (int i = 0; i < 1_000_000; i++) {
          isPrime(i); // Pure computation, no blocking
      }
  });
  // ❌ No benefit! Virtual threads shine for I/O-bound tasks
  ```
- ❌ **TRAP:** Thread.isVirtual() only in Java 21
  ```java
  Thread vt = Thread.startVirtualThread(() -> {});
  boolean isVirtual = vt.isVirtual(); // ✅ Java 21
  // Method doesn't exist in Java 17 or earlier
  ```
- ❌ **TRAP:** Virtual threads use ForkJoinPool as carrier threads
  ```java
  Thread vt = Thread.startVirtualThread(() -> {
      System.out.println(Thread.currentThread().getName());
      // Prints something like: "ForkJoinPool-1-worker-3"
      // Virtual thread runs ON a platform carrier thread
  });
  ```
- ❌ **TRAP:** Thread pinning - virtual thread can't unmount
  ```java
  // Pinning occurs with synchronized blocks
  Thread.startVirtualThread(() -> {
      synchronized(lock) {
          Thread.sleep(1000); // ❌ Virtual thread PINNED to carrier!
          // Can't be unmounted during synchronized block
      }
  });
  
  // Use ReentrantLock instead (doesn't pin)
  ReentrantLock lock = new ReentrantLock();
  Thread.startVirtualThread(() -> {
      lock.lock();
      try {
          Thread.sleep(1000); // ✅ Can unmount, no pinning
      } finally {
          lock.unlock();
      }
  });
  ```
- ❌ **TRAP:** Executors.newVirtualThreadPerTaskExecutor() vs newFixedThreadPool()
  ```java
  // Wrong - limited to 10 threads
  ExecutorService platform = Executors.newFixedThreadPool(10);
  
  // Right - unlimited virtual threads
  ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor(); // ✅
  ```

---

## 7. Locks and ReentrantLock

**Exam Objective:** Develop thread-safe code using locking mechanisms

### What is ReentrantLock?
Explicit lock from java.util.concurrent.locks with more features than synchronized. Allows fair scheduling, interruptible locking, and try-lock operations.

### When to Use
| Feature | synchronized | ReentrantLock |
|---------|--------------|---------------|
| **Fairness** | No | Optional |
| **Interruptible** | No | Yes (lockInterruptibly) |
| **Try-lock** | No | Yes (tryLock) |
| **Multiple conditions** | 1 (wait/notify) | Many (Condition) |
| **Performance** | Good | Slightly better |

### Why Use ReentrantLock
- Need fairness (FIFO ordering)
- Need timeout on lock acquisition
- Need to interrupt waiting threads
- Multiple condition variables
- Doesn't pin virtual threads

### Real-World Example
```java
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock(true); // Fair lock
    private int count = 0;
    
    // Basic lock usage
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock(); // MUST be in finally!
        }
    }
    
    // Try-lock with timeout
    public boolean tryIncrement() throws InterruptedException {
        if (lock.tryLock(1, TimeUnit.SECONDS)) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false; // Couldn't acquire lock
    }
    
    // Interruptible lock
    public void interruptibleIncrement() throws InterruptedException {
        lock.lockInterruptibly(); // Can be interrupted while waiting
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    // Condition variables (producer-consumer)
    private final LinkedList<String> queue = new LinkedList<>();
    private final int capacity = 10;
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    public void produce(String item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await(); // Wait until not full
            }
            queue.add(item);
            notEmpty.signal(); // Signal consumers
        } finally {
            lock.unlock();
        }
    }
    
    public String consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await(); // Wait until not empty
            }
            String item = queue.remove();
            notFull.signal(); // Signal producers
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    // Check lock status
    public void checkStatus() {
        System.out.println("Lock held by current thread: " + lock.isHeldByCurrentThread());
        System.out.println("Queue length: " + lock.getQueueLength());
        System.out.println("Fair lock: " + lock.isFair());
    }
}
```
**Why:** Producer-consumer with multiple conditions; timeout for database connection acquisition.

### Exam Traps
- ❌ **TRAP:** Forgetting unlock() leads to deadlock
  ```java
  lock.lock();
  count++; // Exception here!
  lock.unlock(); // ❌ Never reached! Always use try-finally
  
  lock.lock();
  try {
      count++;
  } finally {
      lock.unlock(); // ✅ Always executes
  }
  ```
- ❌ **TRAP:** unlock() must be called by same thread that locked
  ```java
  ReentrantLock lock = new ReentrantLock();
  
  Thread t1 = new Thread(() -> lock.lock());
  Thread t2 = new Thread(() -> lock.unlock());
  
  t1.start();
  t2.start();
  // ❌ IllegalMonitorStateException! t2 didn't acquire the lock
  ```
- ❌ **TRAP:** tryLock() doesn't honor fairness
  ```java
  ReentrantLock lock = new ReentrantLock(true); // Fair
  lock.lock(); // Thread 1
  
  Thread t2 = new Thread(() -> lock.lock()); // Waits fairly
  Thread t3 = new Thread(() -> lock.tryLock()); // ❌ Can jump queue!
  ```
- ❌ **TRAP:** Can't use ReentrantLock with try-with-resources
  ```java
  try (ReentrantLock lock = new ReentrantLock()) { // ❌ Doesn't implement AutoCloseable!
      lock.lock();
  }
  ```

---

## 8. Atomic Variables

**Exam Objective:** Develop thread-safe code using concurrent API

### What are Atomic Variables?
Thread-safe variables from java.util.concurrent.atomic using lock-free Compare-And-Swap (CAS) operations. More efficient than synchronization for single variables.

### When to Use
- Simple counters or flags
- Need atomicity without locks
- High contention on single variable
- Better performance than synchronized

### Why Use Atomic Variables
- Lock-free (no blocking)
- Better performance than synchronized
- Atomic compound operations (incrementAndGet, compareAndSet)
- No deadlock risk

### Real-World Example
```java
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

public class AtomicVariablesExample {
    // 1. AtomicInteger - most common
    private AtomicInteger counter = new AtomicInteger(0);
    
    public void incrementCounter() {
        int newValue = counter.incrementAndGet(); // Atomic i++
        int oldValue = counter.getAndIncrement(); // Atomic i++, returns old
        counter.addAndGet(5); // Atomic i += 5
    }
    
    // 2. AtomicBoolean - flags
    private AtomicBoolean initialized = new AtomicBoolean(false);
    
    public void initialize() {
        if (initialized.compareAndSet(false, true)) {
            // Only one thread initializes
            System.out.println("Initializing...");
        }
    }
    
    // 3. AtomicReference - object references
    private static class User {
        String name;
        User(String name) { this.name = name; }
    }
    
    private AtomicReference<User> currentUser = new AtomicReference<>(new User("Guest"));
    
    public void updateUser(User newUser) {
        User oldUser = currentUser.getAndSet(newUser); // Atomic swap
        System.out.println("Updated from: " + oldUser.name);
    }
    
    public void compareAndSetUser() {
        User expected = currentUser.get();
        User newUser = new User("Admin");
        
        boolean updated = currentUser.compareAndSet(expected, newUser);
        if (updated) {
            System.out.println("User updated successfully");
        }
    }
    
    // 4. AtomicLong - counters
    private AtomicLong requestCount = new AtomicLong(0);
    
    public void handleRequest() {
        long count = requestCount.incrementAndGet();
        System.out.println("Request #" + count);
    }
    
    // 5. Performance comparison
    public static void main(String[] args) throws Exception {
        int threads = 10;
        int iterations = 100_000;
        
        // Synchronized version
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        long start1 = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    syncCounter.increment();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        long time1 = System.currentTimeMillis() - start1;
        System.out.println("Synchronized: " + time1 + "ms");
        
        // Atomic version
        AtomicInteger atomicCounter = new AtomicInteger(0);
        long start2 = System.currentTimeMillis();
        
        ExecutorService executor2 = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor2.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    atomicCounter.incrementAndGet();
                }
            });
        }
        executor2.shutdown();
        executor2.awaitTermination(10, TimeUnit.SECONDS);
        
        long time2 = System.currentTimeMillis() - start2;
        System.out.println("Atomic: " + time2 + "ms (faster!)");
    }
}

class SynchronizedCounter {
    private int count = 0;
    public synchronized void increment() { count++; }
}
```
**Why:** Web analytics counter - millions of updates per second, atomic is faster than synchronized.

### Exam Traps
- ❌ **TRAP:** getAndIncrement() vs incrementAndGet()
  ```java
  AtomicInteger counter = new AtomicInteger(0);
  int old = counter.getAndIncrement(); // Returns 0, then increments to 1
  int new = counter.incrementAndGet();  // Increments to 2, returns 2
  ```
- ❌ **TRAP:** compareAndSet() fails if value changed
  ```java
  AtomicInteger counter = new AtomicInteger(5);
  
  // Thread 1
  counter.compareAndSet(5, 10); // ✅ Success
  
  // Thread 2 (after Thread 1)
  counter.compareAndSet(5, 20); // ❌ Fails! Current value is 10, not 5
  ```
- ❌ **TRAP:** Atomic classes don't prevent race conditions in compound operations
  ```java
  AtomicInteger balance = new AtomicInteger(100);
  
  // Race condition!
  if (balance.get() >= 50) {
      balance.addAndGet(-50); // ❌ Another thread may withdraw in between!
  }
  
  // Fix with loop
  int current, updated;
  do {
      current = balance.get();
      if (current < 50) break;
      updated = current - 50;
  } while (!balance.compareAndSet(current, updated));
  ```
- ❌ **TRAP:** AtomicReference doesn't guarantee object immutability
  ```java
  AtomicReference<List<String>> list = new AtomicReference<>(new ArrayList<>());
  
  List<String> ref = list.get();
  ref.add("Item"); // ❌ Modifies object without atomic operation!
  ```

---

## 9. Thread Coordination (wait/notify, CountDownLatch, CyclicBarrier)

**Exam Objective:** Develop thread-safe code using concurrent API

### What is Thread Coordination?
Mechanisms to synchronize thread execution - one thread waits for events from other threads.

### When to Use
| Tool | When to Use |
|------|-------------|
| **wait/notify** | One thread waits for condition from another (with synchronized) |
| **CountDownLatch** | Wait for N events (one-time use) |
| **CyclicBarrier** | Wait for N threads to reach point (reusable) |
| **Phaser** | Dynamic thread registration (advanced) |

### Why Use Coordination
- Threads need to wait for each other
- Start multiple threads simultaneously
- Wait for multiple tasks to complete
- Producer-consumer synchronization

### Real-World Example
```java
import java.util.concurrent.*;

public class ThreadCoordinationExample {
    // 1. wait/notify - classic producer-consumer
    static class WaitNotifyQueue {
        private String message;
        private boolean hasMessage = false;
        
        public synchronized void produce(String msg) throws InterruptedException {
            while (hasMessage) {
                wait(); // Release lock and wait
            }
            message = msg;
            hasMessage = true;
            notify(); // Wake up waiting consumer
        }
        
        public synchronized String consume() throws InterruptedException {
            while (!hasMessage) {
                wait(); // Release lock and wait
            }
            hasMessage = false;
            notify(); // Wake up waiting producer
            return message;
        }
    }
    
    // 2. CountDownLatch - wait for multiple services to start
    static void countDownLatchExample() throws InterruptedException {
        int services = 3;
        CountDownLatch latch = new CountDownLatch(services);
        
        // Start 3 services
        for (int i = 0; i < services; i++) {
            final int serviceId = i;
            new Thread(() -> {
                System.out.println("Service " + serviceId + " starting...");
                try {
                    Thread.sleep(1000); // Simulate startup
                } catch (InterruptedException e) {}
                System.out.println("Service " + serviceId + " ready");
                latch.countDown(); // Signal completion
            }).start();
        }
        
        // Main thread waits for all services
        System.out.println("Waiting for services...");
        latch.await(); // Blocks until count reaches 0
        System.out.println("All services started!");
    }
    
    // 3. CyclicBarrier - racing game (wait for all players)
    static void cyclicBarrierExample() throws Exception {
        int players = 4;
        CyclicBarrier barrier = new CyclicBarrier(players, () -> {
            System.out.println("All players ready - START RACE!");
        });
        
        ExecutorService executor = Executors.newFixedThreadPool(players);
        
        for (int i = 0; i < players; i++) {
            final int playerId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Player " + playerId + " ready");
                    barrier.await(); // Wait for all players
                    System.out.println("Player " + playerId + " racing!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    // 4. Practical example: Parallel data processing
    static void parallelProcessing() throws Exception {
        int chunks = 10;
        CountDownLatch latch = new CountDownLatch(chunks);
        
        ExecutorService executor = Executors.newFixedThreadPool(chunks);
        
        for (int i = 0; i < chunks; i++) {
            final int chunkId = i;
            executor.submit(() -> {
                try {
                    // Process data chunk
                    Thread.sleep(100);
                    System.out.println("Chunk " + chunkId + " processed");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(); // Wait for all chunks
        System.out.println("All data processed!");
        
        executor.shutdown();
    }
    
    public static void main(String[] args) throws Exception {
        countDownLatchExample();
        cyclicBarrierExample();
        parallelProcessing();
    }
}
```
**Why:** Microservice startup - wait for database, cache, message queue before accepting requests.

### Exam Traps
- ❌ **TRAP:** wait() must be inside synchronized block
  ```java
  Object lock = new Object();
  lock.wait(); // ❌ IllegalMonitorStateException!
  
  synchronized(lock) {
      lock.wait(); // ✅ Correct
  }
  ```
- ❌ **TRAP:** notify() wakes ONE thread, notifyAll() wakes ALL
  ```java
  synchronized(lock) {
      lock.notify();    // Wakes 1 waiting thread (random)
      lock.notifyAll(); // Wakes ALL waiting threads
  }
  ```
- ❌ **TRAP:** CountDownLatch can't be reset
  ```java
  CountDownLatch latch = new CountDownLatch(3);
  latch.countDown(); // 3 -> 2
  latch.countDown(); // 2 -> 1
  latch.countDown(); // 1 -> 0
  latch.countDown(); // Still 0 (can't go negative)
  // ❌ Can't reuse! Use CyclicBarrier for reusable barrier
  ```
- ❌ **TRAP:** CyclicBarrier count includes calling thread
  ```java
  CyclicBarrier barrier = new CyclicBarrier(3);
  
  // Start 2 threads
  new Thread(() -> barrier.await()).start();
  new Thread(() -> barrier.await()).start();
  
  // Main thread MUST also await (3rd thread)
  barrier.await(); // ✅ All 3 threads now proceed
  ```

---

## 10. CompletableFuture (Async Programming)

**Exam Objective:** Use concurrent API to run tasks

### What is CompletableFuture?
Advanced Future (Java 8+) that supports asynchronous, non-blocking programming with composition, chaining, and exception handling.

### When to Use
- Asynchronous operations
- Combining multiple async tasks
- Non-blocking I/O
- Parallel task execution with dependencies

### Why Use CompletableFuture
- Non-blocking (doesn't block threads)
- Composable (chain operations)
- Exception handling (handle, exceptionally)
- Combine multiple futures (thenCombine, allOf)

### Real-World Example
```java
import java.util.concurrent.*;

public class CompletableFutureExample {
    // 1. Basic async execution
    static void basicExample() throws Exception {
        // supplyAsync - returns value
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Hello";
        });
        
        // runAsync - no return value
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("Running async task");
        });
        
        String result = future.get(); // Blocks until complete
        System.out.println(result);
    }
    
    // 2. Chaining operations
    static void chainingExample() {
        CompletableFuture.supplyAsync(() -> "Hello")
            .thenApply(s -> s + " World")           // Transform
            .thenApply(String::toUpperCase)         // Transform again
            .thenAccept(System.out::println)        // Consume
            .thenRun(() -> System.out.println("Done")); // Finally
        
        sleep(2000); // Wait for async completion
    }
    
    // 3. Combining multiple futures
    static void combiningExample() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Result1";
        });
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Result2";
        });
        
        // Combine two futures
        CompletableFuture<String> combined = future1.thenCombine(future2, (r1, r2) -> {
            return r1 + " + " + r2;
        });
        
        System.out.println(combined.join()); // "Result1 + Result2"
    }
    
    // 4. Exception handling
    static void exceptionHandling() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Error!");
            }
            return "Success";
        }).exceptionally(ex -> {
            System.out.println("Handled: " + ex.getMessage());
            return "Default value";
        }).handle((result, ex) -> {
            if (ex != null) {
                return "Error handled";
            }
            return result;
        });
        
        System.out.println(future.join());
    }
    
    // 5. Real-world: Parallel API calls
    static void parallelApiCalls() {
        // Fetch user, orders, and recommendations in parallel
        CompletableFuture<String> userFuture = fetchUser(123);
        CompletableFuture<String> ordersFuture = fetchOrders(123);
        CompletableFuture<String> recoFuture = fetchRecommendations(123);
        
        // Wait for all
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            userFuture, ordersFuture, recoFuture
        );
        
        allFutures.thenRun(() -> {
            String user = userFuture.join();
            String orders = ordersFuture.join();
            String reco = recoFuture.join();
            System.out.println("All data fetched: " + user + ", " + orders + ", " + reco);
        }).join();
    }
    
    static CompletableFuture<String> fetchUser(int id) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "User" + id;
        });
    }
    
    static CompletableFuture<String> fetchOrders(int id) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "Orders" + id;
        });
    }
    
    static CompletableFuture<String> fetchRecommendations(int id) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "Reco" + id;
        });
    }
    
    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
    
    public static void main(String[] args) {
        basicExample();
        chainingExample();
        combiningExample();
        exceptionHandling();
        parallelApiCalls();
    }
}
```
**Why:** E-commerce checkout - fetch user, validate payment, check inventory, send email - all in parallel.

### Exam Traps
- ❌ **TRAP:** get() vs join() difference
  ```java
  CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
  
  String result1 = future.get();  // Throws checked Exception
  String result2 = future.join(); // Throws unchecked CompletionException
  ```
- ❌ **TRAP:** thenApply() vs thenCompose()
  ```java
  // thenApply - transform value
  future.thenApply(s -> s.toUpperCase()); // Returns CompletableFuture<String>
  
  // thenCompose - flatten nested futures
  future.thenCompose(s -> CompletableFuture.supplyAsync(() -> s.toUpperCase()));
  // Returns CompletableFuture<String> (not CompletableFuture<CompletableFuture<String>>)
  ```
- ❌ **TRAP:** allOf() returns CompletableFuture<Void>
  ```java
  CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
  CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
  
  CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
  String result = all.join(); // ❌ join() returns void (null)
  
  // Must get results from individual futures
  all.thenRun(() -> {
      String r1 = f1.join(); // ✅ Get individual results
      String r2 = f2.join();
  });
  ```
- ❌ **TRAP:** Exception in supplyAsync requires exceptionally()
  ```java
  CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      throw new RuntimeException("Error");
  });
  
  String result = future.join(); // ❌ Throws CompletionException!
  
  future.exceptionally(ex -> "Default").join(); // ✅ Handles exception
  ```

---

---

## Exam Questions

**Exam Format:**
- 50 questions total (6-10 on concurrency)
- 120 minutes
- 68% passing score (34/50)
- Multiple choice, multiple select, code analysis

---

### Question 1
```java
Thread t1 = new Thread(() -> System.out.println("A"));
Thread t2 = Thread.startVirtualThread(() -> System.out.println("B"));

System.out.println(t1.isVirtual());
System.out.println(t2.isVirtual());
```

What is the output?

A. true, true  
B. false, false  
C. false, true  
D. true, false  
E. Compilation error

**Answer:** C

**Explanation:** t1 is a platform thread created with new Thread(), so isVirtual() returns false. t2 is a virtual thread created with startVirtualThread(), so isVirtual() returns true. (Exam Objective: Create both platform and virtual threads)

---

### Question 2
```java
Callable<Integer> task = () -> {
    Thread.sleep(1000);
    return 42;
};

Thread t = new Thread(task);
t.start();
```

What is the result?

A. Prints 42 after 1 second  
B. Returns 42 immediately  
C. Compilation error  
D. Runtime exception  
E. No output

**Answer:** C

**Explanation:** Thread constructor does not accept Callable, only Runnable. Callable must be used with ExecutorService. (Exam Objective: Use both Runnable and Callable objects)

---

### Question 3
```java
ExecutorService executor = Executors.newFixedThreadPool(2);

executor.submit(() -> System.out.println("Task 1"));
executor.submit(() -> System.out.println("Task 2"));
executor.shutdown();
executor.submit(() -> System.out.println("Task 3"));
```

What happens?

A. All three tasks execute  
B. Only Task 1 and Task 2 execute  
C. RejectedExecutionException for Task 3  
D. Program hangs  
E. Compilation error

**Answer:** C

**Explanation:** After shutdown(), executor does not accept new tasks. Submitting Task 3 throws RejectedExecutionException. (Exam Objective: Use different Executor services)

---

### Question 4
```java
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;
    }
}

Counter counter = new Counter();

ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100; i++) {
    executor.submit(() -> counter.increment());
}

executor.shutdown();
executor.awaitTermination(5, TimeUnit.SECONDS);

System.out.println(counter.count);
```

What is the most likely output?

A. Always 100  
B. Less than 100  
C. More than 100  
D. Compilation error  
E. Runtime exception

**Answer:** B

**Explanation:** Race condition - count++ is not atomic. Multiple threads can read the same value, increment, and write back, losing updates. Final count is less than 100. (Exam Objective: Develop thread-safe code)

---

### Question 5
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

map.put("A", 1);
map.put(null, 2);
map.put("B", null);

System.out.println(map.size());
```

What is the output?

A. 3  
B. 2  
C. 1  
D. NullPointerException on line 4  
E. NullPointerException on line 5

**Answer:** D

**Explanation:** ConcurrentHashMap does not allow null keys or values. Line 4 throws NullPointerException when attempting to put null key. (Exam Objective: Process collections concurrently)

---

### Question 6
```java
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            Thread.sleep(1000);
            return "Done";
        });
    }
}

System.out.println("Completed");
```

What happens?

A. OutOfMemoryError  
B. Program completes in ~1 second  
C. Program completes in ~100 seconds  
D. Compilation error  
E. RejectedExecutionException

**Answer:** B

**Explanation:** Virtual threads can handle 100,000 concurrent tasks easily. All tasks sleep for 1 second concurrently, so total time is ~1 second. Platform threads would fail. (Exam Objective: Create virtual threads)

---

### Question 7
```java
ReentrantLock lock = new ReentrantLock();

lock.lock();
lock.lock();
System.out.println("Locked");
lock.unlock();
```

What happens?

A. Prints "Locked" and thread continues  
B. Deadlock  
C. IllegalMonitorStateException  
D. Compilation error  
E. Program hangs

**Answer:** E

**Explanation:** ReentrantLock allows same thread to acquire lock multiple times (reentrant). However, must unlock() same number of times. Only one unlock() is called but lock acquired twice, so lock is never fully released and program hangs if another thread tries to acquire. (Exam Objective: Develop thread-safe code using locking mechanisms)

---

### Question 8
```java
AtomicInteger counter = new AtomicInteger(10);

int value1 = counter.getAndIncrement();
int value2 = counter.incrementAndGet();

System.out.println(value1 + " " + value2);
```

What is the output?

A. 10 11  
B. 10 12  
C. 11 12  
D. 11 11  
E. 12 12

**Answer:** B

**Explanation:** getAndIncrement() returns current value (10) then increments to 11. incrementAndGet() increments from 11 to 12 and returns new value (12). (Exam Objective: Develop thread-safe code)

---

### Question 9
```java
CountDownLatch latch = new CountDownLatch(3);

for (int i = 0; i < 5; i++) {
    new Thread(() -> latch.countDown()).start();
}

latch.await();
System.out.println("Done");
```

What happens?

A. Prints "Done" immediately  
B. Prints "Done" after all 5 threads complete  
C. Prints "Done" after 3 threads complete  
D. Program hangs forever  
E. Compilation error

**Answer:** C

**Explanation:** CountDownLatch initialized with count of 3. After 3 countDown() calls, await() unblocks. Remaining 2 countDown() calls have no effect. (Exam Objective: Use concurrent API)

---

### Question 10
```java
List<Integer> list = Collections.synchronizedList(new ArrayList<>());

Callable<String> task = () -> {
    for (int i = 0; i < 5; i++) {
        list.add(i);
    }
    return null;
};

ExecutorService executor = Executors.newFixedThreadPool(3);
List<Future<String>> futures = executor.invokeAll(List.of(task, task, task));

executor.shutdown();
list.forEach(System.out::print);
```

What is the most likely output?

A. 012340123401234  
B. 000111222333444  
C. 001122334401234  
D. 012341234  
E. Compilation error

**Answer:** A

**Explanation:** Three tasks each add 0-4 to synchronized list. invokeAll() waits for all to complete. Final list contains 15 elements in order added: all 0-4 three times, but interleaved based on thread scheduling. Most likely shows all values 0-4 repeated 3 times. (Exam Objective: Process collections concurrently)

---

### Question 11
```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Error");
});

String result = future.join();
System.out.println(result);
```

What happens?

A. Prints "Error"  
B. Prints null  
C. Throws RuntimeException  
D. Throws CompletionException  
E. Compilation error

**Answer:** D

**Explanation:** Exceptions in CompletableFuture are wrapped in CompletionException. join() throws CompletionException (unchecked) containing the original RuntimeException. (Exam Objective: Use concurrent API)

---

### Question 12
```java
synchronized(this) {
    Thread t = Thread.startVirtualThread(() -> {
        synchronized(this) {
            System.out.println("Virtual");
        }
    });
    t.join();
}
```

What is a potential issue?

A. Deadlock  
B. Thread pinning  
C. Compilation error  
D. NullPointerException  
E. No issues

**Answer:** B

**Explanation:** Virtual thread inside synchronized block causes thread pinning - virtual thread cannot unmount from carrier thread, reducing scalability. Use ReentrantLock instead for virtual threads. (Exam Objective: Create virtual threads)

---

### Question 13
```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("A");
list.add("B");

Iterator<String> it = list.iterator();
list.add("C");

while (it.hasNext()) {
    System.out.print(it.next());
}
```

What is the output?

A. ABC  
B. AB  
C. ConcurrentModificationException  
D. AC  
E. BC

**Answer:** B

**Explanation:** CopyOnWriteArrayList iterator sees snapshot at time of creation. Adding "C" after iterator creation doesn't affect iteration. Only prints AB. (Exam Objective: Process collections concurrently)

---

### Question 14
```java
Object lock = new Object();

new Thread(() -> {
    synchronized(lock) {
        try {
            lock.wait();
        } catch (InterruptedException e) {}
    }
}).start();

Thread.sleep(100);

synchronized(lock) {
    lock.notify();
}
```

What happens?

A. First thread terminates immediately  
B. First thread waits forever  
C. First thread wakes up and terminates  
D. IllegalMonitorStateException  
E. Compilation error

**Answer:** C

**Explanation:** First thread enters synchronized block, calls wait() and releases lock. Main thread acquires lock, calls notify() to wake first thread. First thread reacquires lock and terminates. (Exam Objective: Develop thread-safe code)

---

### Question 15
```java
ExecutorService executor = Executors.newCachedThreadPool();

for (int i = 0; i < 1_000_000; i++) {
    executor.submit(() -> Thread.sleep(60_000));
}
```

What is the likely result?

A. OutOfMemoryError  
B. Program completes successfully  
C. RejectedExecutionException  
D. Compilation error  
E. 1 million threads sleep for 1 minute

**Answer:** A

**Explanation:** newCachedThreadPool() creates unlimited threads. Submitting 1 million long-running tasks creates 1 million platform threads (~2GB memory each), causing OutOfMemoryError. (Exam Objective: Use different Executor services)

---

### Question 16
```java
ReentrantLock lock = new ReentrantLock(true);

Thread t1 = new Thread(() -> {
    lock.lock();
    try { Thread.sleep(100); }
    catch (InterruptedException e) {}
    lock.unlock();
});

Thread t2 = new Thread(() -> {
    if (lock.tryLock()) {
        System.out.println("Acquired");
        lock.unlock();
    }
});

t1.start();
Thread.sleep(10);
t2.start();
```

What is the behavior?

A. t2 always prints "Acquired"  
B. t2 never prints "Acquired"  
C. t2 may or may not print "Acquired"  
D. Compilation error  
E. Deadlock

**Answer:** C

**Explanation:** ReentrantLock(true) creates fair lock, but tryLock() does NOT honor fairness - it can acquire lock even if other threads are waiting. Whether t2 acquires lock depends on timing. (Exam Objective: Develop thread-safe code using locking mechanisms)

---

### Question 17
```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");

CompletableFuture<Void> combined = CompletableFuture.allOf(f1, f2);

String result = combined.join();
System.out.println(result);
```

What is the output?

A. AB  
B. null  
C. A  
D. B  
E. Compilation error

**Answer:** B

**Explanation:** allOf() returns CompletableFuture<Void>. join() on Void future returns null. Must get results from individual futures (f1.join(), f2.join()). (Exam Objective: Use concurrent API)

---

### Question 18
```java
class Data {
    private static int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

Data d1 = new Data();
Data d2 = new Data();

new Thread(() -> d1.increment()).start();
new Thread(() -> d2.increment()).start();
```

Is this thread-safe?

A. Yes, synchronized prevents race conditions  
B. No, count is static but synchronized on instance  
C. Yes, because count is static  
D. No, need volatile keyword  
E. Compilation error

**Answer:** B

**Explanation:** count is static (shared across instances) but synchronized locks instance (this). Two threads lock different instances (d1, d2) and can modify count simultaneously causing race condition. Need static synchronized or lock on class object. (Exam Objective: Develop thread-safe code)

---

### Question 19
```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("Go!"));

for (int i = 0; i < 2; i++) {
    new Thread(() -> {
        try {
            barrier.await();
        } catch (Exception e) {}
    }).start();
}
```

What happens?

A. Prints "Go!" and threads continue  
B. Threads wait forever  
C. Prints "Go!" after 2 threads  
D. BrokenBarrierException  
E. Compilation error

**Answer:** B

**Explanation:** CyclicBarrier initialized with parties=3. Only 2 threads call await(). Barrier waits for 3rd thread forever. Main thread doesn't call await(), so barrier never trips. (Exam Objective: Use concurrent API)

---

### Question 20
```java
AtomicReference<String> ref = new AtomicReference<>("Initial");

String current = ref.get();
ref.set("Updated");

boolean success = ref.compareAndSet("Initial", "New");
System.out.println(success + " " + ref.get());
```

What is the output?

A. true New  
B. false Updated  
C. true Updated  
D. false New  
E. Compilation error

**Answer:** B

**Explanation:** After ref.set("Updated"), current value is "Updated". compareAndSet("Initial", "New") expects current to be "Initial" but it's "Updated", so CAS fails (returns false) and value remains "Updated". (Exam Objective: Develop thread-safe code using concurrent API)

---

---

## Chapter Summary

### Official 1Z0-830 Exam Objectives Checklist

✅ **Create both platform and virtual threads**
- Platform threads (Thread class, Runnable)
- Virtual threads (Thread.startVirtualThread(), Thread.ofVirtual())
- Thread lifecycle and isVirtual()

✅ **Use both Runnable and Callable objects, manage thread lifecycle, use Executor services**
- Runnable (void return, no exceptions)
- Callable (generic return, checked exceptions)
- ExecutorService types (fixed, cached, scheduled, virtual)
- shutdown() vs shutdownNow()

✅ **Develop thread-safe code using locking mechanisms and concurrent API**
- synchronized keyword (methods, blocks)
- ReentrantLock (fair, tryLock, lockInterruptibly)
- Atomic variables (AtomicInteger, AtomicReference, CAS)
- Thread coordination (wait/notify, CountDownLatch, CyclicBarrier)

✅ **Process Java collections concurrently**
- ConcurrentHashMap (no null keys/values)
- CopyOnWriteArrayList (snapshot iterators)
- ConcurrentLinkedQueue (non-blocking)
- BlockingQueue (producer-consumer)
- Collections.synchronizedList/Map/Set

---

### Key Points for Exam

1. **Platform vs Virtual Threads**
   - Platform: 1:1 OS mapping, 2MB each, limited to thousands
   - Virtual: JVM-managed, 1KB each, millions possible
   - Virtual threads shine for I/O-bound tasks, not CPU-bound

2. **Thread Creation**
   - run() = executes in current thread
   - start() = creates new thread
   - Can't call start() twice on same thread

3. **Runnable vs Callable**
   - Runnable: void return, use with Thread or ExecutorService
   - Callable: generic return, checked exceptions, ExecutorService only

4. **ExecutorService Shutdown**
   - shutdown() = wait for tasks to complete
   - shutdownNow() = interrupt running tasks
   - Must call shutdown() or program never exits

5. **Thread Safety**
   - synchronized locks on instance (this) or class (static)
   - Race condition = multiple threads modifying shared data
   - Always use try-finally with locks

6. **Concurrent Collections**
   - ConcurrentHashMap: no null keys/values, size() approximate
   - CopyOnWriteArrayList: expensive writes, cheap reads
   - Collections.synchronizedList: external synchronization for iteration

7. **Virtual Thread Pinning**
   - Occurs with synchronized blocks (can't unmount)
   - Occurs with native methods
   - Solution: Use ReentrantLock instead

8. **Locks**
   - ReentrantLock: must unlock() in finally block
   - tryLock(): doesn't honor fairness
   - unlock() must be by same thread that locked

9. **Atomic Variables**
   - Lock-free, CAS-based
   - getAndIncrement() returns old value
   - incrementAndGet() returns new value
   - compareAndSet() fails if value changed

10. **Thread Coordination**
    - wait/notify: must be inside synchronized
    - CountDownLatch: one-time use, can't reset
    - CyclicBarrier: reusable, count includes calling thread

11. **CompletableFuture**
    - get() throws checked exceptions
    - join() throws unchecked CompletionException
    - allOf() returns CompletableFuture<Void>
    - thenApply() = transform, thenCompose() = flatten

12. **Executor Types**
    - newFixedThreadPool(n): fixed size, good for known workload
    - newCachedThreadPool(): unlimited threads, risk of OOM
    - newSingleThreadExecutor(): sequential execution
    - newVirtualThreadPerTaskExecutor(): millions of virtual threads

13. **Common Mistakes**
    - Forgetting shutdown() on ExecutorService
    - Using synchronized on String/Integer literals
    - Calling run() instead of start()
    - Not using try-finally with lock.unlock()
    - Expecting allOf() to return combined results

14. **Exception Handling**
    - Callable.call() can throw checked exceptions
    - Runnable.run() cannot throw checked exceptions
    - CompletableFuture wraps exceptions in CompletionException
    - ExecutionException wraps task exceptions in Future.get()

15. **Memory and Performance**
    - Platform thread: ~2MB stack
    - Virtual thread: ~1KB
    - CachedThreadPool can create unlimited threads
    - Atomic operations faster than synchronized

---

### Common Exam Patterns

1. **Thread Creation Traps**
   - run() vs start()
   - Thread(Callable) doesn't compile
   - Can't start() same thread twice

2. **Null Value Traps**
   - ConcurrentHashMap rejects null keys/values
   - HashMap allows null (not thread-safe)
   - Collections.synchronizedMap allows null

3. **Shutdown Patterns**
   - Executor after shutdown() throws RejectedExecutionException
   - Virtual thread executor auto-closes with try-with-resources
   - Must wait for tasks with awaitTermination()

4. **Synchronization Scope**
   - synchronized method on instance vs static
   - Static variable needs static synchronized or class lock
   - Different instances = different locks

5. **Atomic Operation Patterns**
   - Read-modify-write needs synchronization
   - getAndXxx() returns old value
   - xxxAndGet() returns new value

6. **CountDownLatch vs CyclicBarrier**
   - CountDownLatch: one-time countdown
   - CyclicBarrier: reusable barrier for threads
   - Barrier count includes calling thread

7. **Virtual Thread Scenarios**
   - I/O-bound = virtual threads win
   - CPU-bound = no benefit
   - Synchronized blocks cause pinning

---

### Study Tips

1. **Practice Thread Creation**
   - Write code using all 3 methods for platform threads
   - Write code using all 3 methods for virtual threads
   - Understand when isVirtual() returns true/false

2. **Master ExecutorService**
   - Know difference between execute() and submit()
   - Practice shutdown() and awaitTermination()
   - Understand when to use each executor type

3. **Thread Safety Exercises**
   - Identify race conditions in code
   - Fix with synchronized, locks, or atomic variables
   - Understand instance vs class locking

4. **Concurrent Collections**
   - Know which collections allow null
   - Understand fail-fast vs fail-safe iterators
   - Practice ConcurrentHashMap atomic operations

5. **Virtual Threads Focus** ⭐ HIGH EXAM WEIGHT
   - Create virtual threads 3 different ways
   - Understand thread pinning scenarios
   - Compare memory usage: platform vs virtual

6. **Lock Mechanisms**
   - Always use try-finally with ReentrantLock
   - Practice tryLock() with timeout
   - Understand fairness parameter

7. **Coordination Primitives**
   - Practice wait/notify patterns
   - Use CountDownLatch for one-time events
   - Use CyclicBarrier for repeating synchronization

8. **CompletableFuture Chains**
   - Chain operations with thenApply/thenCompose
   - Handle exceptions with exceptionally/handle
   - Combine futures with allOf/anyOf

9. **Common Pitfalls**
   - Memorize: ConcurrentHashMap no nulls
   - Memorize: CountDownLatch not reusable
   - Memorize: allOf() returns Void

10. **Code Reading**
    - Identify thread safety issues
    - Spot deadlock potential
    - Recognize race conditions

---

### Quick Reference Card

#### Thread Creation
```java
// Platform
Thread t = new Thread(() -> {});
t.start();

// Virtual
Thread vt = Thread.startVirtualThread(() -> {});
Thread vt2 = Thread.ofVirtual().start(() -> {});
```

#### Executors
```java
// Fixed pool
ExecutorService e = Executors.newFixedThreadPool(10);

// Virtual threads
ExecutorService ve = Executors.newVirtualThreadPerTaskExecutor();

// Always shutdown
e.shutdown();
e.awaitTermination(10, TimeUnit.SECONDS);
```

#### Thread Safety
```java
// Synchronized
public synchronized void method() { }

synchronized(lock) { }

// Lock
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}

// Atomic
AtomicInteger counter = new AtomicInteger();
counter.incrementAndGet();
```

#### Concurrent Collections
```java
// Map (no nulls)
ConcurrentHashMap<K,V> map = new ConcurrentHashMap<>();

// List (copy on write)
CopyOnWriteArrayList<E> list = new CopyOnWriteArrayList<>();

// Synchronized wrapper
List<E> syncList = Collections.synchronizedList(new ArrayList<>());
```

#### Coordination
```java
// CountDownLatch (one-time)
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();
latch.await();

// CyclicBarrier (reusable)
CyclicBarrier barrier = new CyclicBarrier(3);
barrier.await();

// Wait/Notify
synchronized(lock) {
    lock.wait();
    lock.notify();
}
```

#### CompletableFuture
```java
CompletableFuture.supplyAsync(() -> "result")
    .thenApply(String::toUpperCase)
    .thenAccept(System.out::println)
    .exceptionally(ex -> null);
```

---

### Next Steps

**Practice Tasks:**
1. Create program with 100,000 virtual threads
2. Implement thread-safe counter using 3 different approaches
3. Build producer-consumer with BlockingQueue
4. Chain 5 CompletableFutures with error handling
5. Compare performance: synchronized vs ReentrantLock vs Atomic

**Review Before Exam:**
- Virtual threads are Java 21's biggest feature
- ConcurrentHashMap never accepts null
- CountDownLatch is not reusable
- Thread pinning occurs with synchronized
- allOf() returns CompletableFuture<Void>

**Common Exam Traps:**
- run() vs start()
- execute() vs submit()
- shutdown() vs shutdownNow()
- getAndIncrement() vs incrementAndGet()
- synchronized on instance vs static

---

[← Previous Chapter](CHAPTER-07.md) | [Back to Main Guide](README.md) | [Next Chapter →](CHAPTER-09.md)

---

**Chapter 8 Complete!** ✅  
Concurrency & Virtual Threads - The most heavily weighted Java 21 topic.

