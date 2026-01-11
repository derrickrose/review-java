# Chapter 5: Working with Arrays and Collections - PART 5: Queue/Deque + Summary

[← Part 4: Map](CHAPTER-05-PART4-MAP.md) | [Back to Main Guide](README.md)

---

## 5. Queue and Deque Interfaces

**Exam Objective:** Use Queue and Deque interfaces and implementations

### What is Queue?
First-In-First-Out (FIFO) collection designed for holding elements before processing. Adds at tail, removes from head. Part of Collection hierarchy. Main implementations: LinkedList, ArrayDeque, PriorityQueue.

### What is Deque?
Double-Ended Queue - can add/remove from both ends. Supports both FIFO (queue) and LIFO (stack) operations. Pronounced "deck". Main implementations: ArrayDeque, LinkedList.

### When to Use

| Implementation | When to Use | Ordering | Performance |
|----------------|-------------|----------|-------------|
| **ArrayDeque** | General-purpose queue/deque, stack | Insertion order | O(1) amortized |
| **LinkedList** | Deque with frequent mid-list operations | Insertion order | O(1) guaranteed |
| **PriorityQueue** | Need natural/custom ordering, priority processing | Priority order (heap) | O(log n) |
| **LinkedBlockingQueue** | Thread-safe, producer-consumer | FIFO | O(1) |
| **ConcurrentLinkedQueue** | Thread-safe, non-blocking | FIFO | O(1) |

### Why Use Queue/Deque
- **Natural FIFO/LIFO modeling:** Task queues, undo/redo, BFS/DFS
- **Explicit operations:** offer/poll (no exceptions) vs add/remove (exceptions)
- **Deque flexibility:** Can act as queue OR stack
- **PriorityQueue:** Automatic ordering by priority
- **ArrayDeque performance:** Faster than Stack and LinkedList for stack operations

### Performance Characteristics

#### ArrayDeque vs LinkedList vs PriorityQueue - Complete Comparison

| Operation | ArrayDeque | LinkedList | PriorityQueue | Winner |
|-----------|------------|------------|---------------|--------|
| **offer(E) / add(E)** | O(1) amortized | O(1) | O(log n) | ArrayDeque/LinkedList |
| **poll() / remove()** | O(1) | O(1) | O(log n) | ArrayDeque/LinkedList |
| **peek() / element()** | O(1) | O(1) | O(1) | Tie |
| **offerFirst(E)** | O(1) amortized | O(1) | N/A | LinkedList guaranteed |
| **offerLast(E)** | O(1) amortized | O(1) | N/A | LinkedList guaranteed |
| **pollFirst()** | O(1) | O(1) | N/A | Tie |
| **pollLast()** | O(1) | O(1) | N/A | Tie |
| **size()** | O(1) | O(1) | O(1) | Tie |
| **contains(Object)** | O(n) | O(n) | O(n) | Tie |
| **remove(Object)** | O(n) | O(n) | O(n) | Tie |
| **Iterator** | O(n) | O(n) | O(n) | Tie |
| **Memory per element** | ~8 bytes + array | ~24 bytes | ~8 bytes + array | ArrayDeque |
| **Null elements** | No | Yes | No | LinkedList only |
| **Random access** | No | No | No | N/A |
| **Thread-safe** | No | No | No | Use Concurrent versions |

**Key differences:**
- **ArrayDeque:** Fastest for most queue/stack operations, resizable circular array
- **LinkedList:** Only one allowing null elements, guaranteed O(1) for all operations
- **PriorityQueue:** Automatically maintains ordering, not a true queue (no FIFO guarantee)

#### Space Complexity

| Implementation | Space per element | Total Space | Notes |
|----------------|------------------|-------------|-------|
| **ArrayDeque** | ~8 bytes | O(n) + unused capacity | Circular array, resizes by 2× |
| **LinkedList** | ~24 bytes (node overhead) | O(n) | No unused space |
| **PriorityQueue** | ~8 bytes | O(n) + unused capacity | Binary heap, resizes by ~1.5× |

### Real-World Examples

#### Example 1: Task Scheduler (ArrayDeque - FIFO Queue)
```java
public class TaskScheduler {
    // ArrayDeque: efficient FIFO queue for task processing
    private Deque<Task> taskQueue = new ArrayDeque<>();
    
    public static class Task {
        String id;
        String description;
        int priority;
        
        public Task(String id, String description, int priority) {
            this.id = id;
            this.description = description;
            this.priority = priority;
        }
    }
    
    // Add task to end of queue - O(1) amortized
    public void scheduleTask(Task task) {
        taskQueue.offer(task);                   // O(1)
        System.out.println("Scheduled: " + task.id);
    }
    
    // Process next task (FIFO) - O(1)
    public Task processNextTask() {
        Task task = taskQueue.poll();            // O(1)
        if (task != null) {
            System.out.println("Processing: " + task.id);
        }
        return task;
    }
    
    // Peek at next task without removing - O(1)
    public Task peekNextTask() {
        return taskQueue.peek();                 // O(1)
    }
    
    // Check if queue is empty - O(1)
    public boolean hasMoreTasks() {
        return !taskQueue.isEmpty();
    }
    
    // Get queue size - O(1)
    public int getPendingTaskCount() {
        return taskQueue.size();
    }
    
    // Process all tasks
    public void processAllTasks() {
        while (!taskQueue.isEmpty()) {
            processNextTask();
        }
    }
}
```
**Why ArrayDeque:** Task scheduling needs efficient FIFO queue. ArrayDeque provides O(1) add/remove with better performance than LinkedList due to cache locality.

#### Example 2: Priority Task Queue (PriorityQueue - Priority Ordering)
```java
public class PriorityTaskQueue {
    // PriorityQueue: automatically orders by priority
    private PriorityQueue<PriorityTask> queue;
    
    public static class PriorityTask implements Comparable<PriorityTask> {
        String id;
        String description;
        int priority;  // Lower number = higher priority
        Instant created;
        
        public PriorityTask(String id, String description, int priority) {
            this.id = id;
            this.description = description;
            this.priority = priority;
            this.created = Instant.now();
        }
        
        @Override
        public int compareTo(PriorityTask other) {
            // Primary: by priority (lower first)
            int priorityCompare = Integer.compare(this.priority, other.priority);
            if (priorityCompare != 0) return priorityCompare;
            
            // Secondary: by creation time (older first)
            return this.created.compareTo(other.created);
        }
    }
    
    public PriorityTaskQueue() {
        queue = new PriorityQueue<>();
    }
    
    // Add task - O(log n)
    public void addTask(PriorityTask task) {
        queue.offer(task);                       // O(log n) - maintains heap
        System.out.println("Added task: " + task.id + " (priority: " + task.priority + ")");
    }
    
    // Get highest priority task - O(log n)
    public PriorityTask getHighestPriorityTask() {
        return queue.poll();                     // O(log n) - removes root, reheapifies
    }
    
    // Peek highest priority - O(1)
    public PriorityTask peekHighestPriority() {
        return queue.peek();                     // O(1) - just root
    }
    
    // Get count - O(1)
    public int getTaskCount() {
        return queue.size();
    }
    
    // Custom comparator example - by priority DESC
    public PriorityTaskQueue createReversePriorityQueue() {
        PriorityQueue<PriorityTask> reversed = new PriorityQueue<>(
            Comparator.comparingInt((PriorityTask t) -> t.priority).reversed()
        );
        return new PriorityTaskQueue();  // Would need constructor accepting PriorityQueue
    }
}
```
**Why PriorityQueue:** Priority-based task processing needs automatic ordering. PriorityQueue (min-heap) always provides highest priority task in O(log n) time.

#### Example 3: Undo/Redo System (ArrayDeque - Stack/LIFO)
```java
public class UndoRedoManager<T> {
    // ArrayDeque as stack: better than Stack class
    private Deque<T> undoStack = new ArrayDeque<>();
    private Deque<T> redoStack = new ArrayDeque<>();
    private static final int MAX_HISTORY = 100;
    
    // Execute action and add to undo stack - O(1)
    public void executeAction(T action) {
        undoStack.push(action);                  // O(1) - add to front
        redoStack.clear();                       // Clear redo when new action
        
        // Limit history size
        if (undoStack.size() > MAX_HISTORY) {
            undoStack.removeLast();              // O(1) - remove from back
        }
    }
    
    // Undo last action - O(1)
    public T undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return null;
        }
        
        T action = undoStack.pop();              // O(1) - remove from front
        redoStack.push(action);                  // O(1) - add to redo
        return action;
    }
    
    // Redo last undone action - O(1)
    public T redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return null;
        }
        
        T action = redoStack.pop();              // O(1)
        undoStack.push(action);                  // O(1)
        return action;
    }
    
    // Peek at next undo without removing - O(1)
    public T peekUndo() {
        return undoStack.peek();                 // O(1)
    }
    
    // Can undo? - O(1)
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    // Can redo? - O(1)
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    // Get undo history size - O(1)
    public int getUndoCount() {
        return undoStack.size();
    }
    
    // Clear all history
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
}
```
**Why ArrayDeque (as Stack):** Undo/redo needs LIFO (stack) operations. ArrayDeque is faster than legacy Stack class and provides consistent O(1) operations.

#### Example 4: BFS Graph Traversal (ArrayDeque - Queue)
```java
public class GraphBFS {
    private Map<Integer, List<Integer>> adjacencyList;
    
    public GraphBFS() {
        adjacencyList = new HashMap<>();
    }
    
    public void addEdge(int from, int to) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
    }
    
    // Breadth-First Search using ArrayDeque - O(V + E)
    public List<Integer> bfs(int start) {
        List<Integer> visited = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();   // FIFO for BFS
        
        queue.offer(start);                          // O(1)
        seen.add(start);
        
        while (!queue.isEmpty()) {
            int node = queue.poll();                 // O(1) - FIFO
            visited.add(node);
            
            // Visit neighbors
            List<Integer> neighbors = adjacencyList.getOrDefault(node, new ArrayList<>());
            for (int neighbor : neighbors) {
                if (!seen.contains(neighbor)) {
                    queue.offer(neighbor);           // O(1)
                    seen.add(neighbor);
                }
            }
        }
        
        return visited;
    }
    
    // Find shortest path using BFS - O(V + E)
    public List<Integer> shortestPath(int start, int end) {
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            
            if (node == end) {
                // Reconstruct path
                List<Integer> path = new ArrayList<>();
                Integer current = end;
                while (current != null) {
                    path.add(0, current);
                    current = parent.get(current);
                }
                return path;
            }
            
            for (int neighbor : adjacencyList.getOrDefault(node, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, node);
                }
            }
        }
        
        return new ArrayList<>();  // No path found
    }
}
```
**Why ArrayDeque:** BFS requires FIFO queue. ArrayDeque provides efficient O(1) enqueue/dequeue with better cache performance than LinkedList.

### Queue Interface - Complete Reference

#### Queue Methods

```java
Queue<String> queue = new ArrayDeque<>();

// ADDING elements - two methods with different failure behavior

// offer(E) - adds element, returns false if fails (capacity-restricted queues)
boolean added1 = queue.offer("A");           // true, queue=[A]
boolean added2 = queue.offer("B");           // true, queue=[A, B]

// add(E) - adds element, throws IllegalStateException if fails
queue.add("C");                              // queue=[A, B, C]
// For unbounded queues like ArrayDeque, add() never fails

// REMOVING elements - three methods with different failure behavior

// poll() - removes and returns head, returns null if empty
String removed1 = queue.poll();              // "A", queue=[B, C]

// remove() - removes and returns head, throws NoSuchElementException if empty
String removed2 = queue.remove();            // "B", queue=[C]

// EXAMINING elements - two methods with different failure behavior

// peek() - returns head without removing, returns null if empty
queue.offer("D");                            // queue=[C, D]
String peeked1 = queue.peek();               // "C", queue=[C, D]

// element() - returns head without removing, throws NoSuchElementException if empty
String peeked2 = queue.element();            // "C", queue=[C, D]

// OTHER methods
int size = queue.size();                     // 2
boolean empty = queue.isEmpty();             // false
queue.clear();                               // []

// When to use which?
// - Use offer/poll/peek (no exceptions) for most cases
// - Use add/remove/element (exceptions) when empty queue is truly exceptional
```

### Deque Interface - Complete Reference

#### Deque Methods (Double-Ended)

```java
Deque<String> deque = new ArrayDeque<>();

// ADD TO FRONT (head)
deque.offerFirst("A");                       // [A]
deque.offerFirst("Z");                       // [Z, A]
deque.addFirst("Y");                         // [Y, Z, A] - throws exception on failure

// ADD TO BACK (tail)
deque.offerLast("B");                        // [Y, Z, A, B]
deque.offerLast("C");                        // [Y, Z, A, B, C]
deque.addLast("D");                          // [Y, Z, A, B, C, D]

// REMOVE FROM FRONT
String first1 = deque.pollFirst();           // "Y", [Z, A, B, C, D]
String first2 = deque.removeFirst();         // "Z", [A, B, C, D] - throws if empty

// REMOVE FROM BACK
String last1 = deque.pollLast();             // "D", [A, B, C]
String last2 = deque.removeLast();           // "C", [A, B] - throws if empty

// EXAMINE FRONT
deque.offerFirst("X");                       // [X, A, B]
String peekFirst1 = deque.peekFirst();       // "X", no removal
String peekFirst2 = deque.getFirst();        // "X", throws if empty

// EXAMINE BACK
String peekLast1 = deque.peekLast();         // "B", no removal
String peekLast2 = deque.getLast();          // "B", throws if empty

// STACK OPERATIONS (LIFO)
deque.clear();
deque.push("A");                             // [A] - same as addFirst
deque.push("B");                             // [B, A]
deque.push("C");                             // [C, B, A]
String popped = deque.pop();                 // "C", [B, A] - same as removeFirst
String top = deque.peek();                   // "B" - same as peekFirst

// QUEUE OPERATIONS (FIFO) - inherited from Queue
deque.clear();
deque.offer("A");                            // [A] - same as offerLast
deque.offer("B");                            // [A, B]
String removed = deque.poll();               // "A", [B] - same as pollFirst
```

**Method equivalences:**
- **Queue operations:** offer() = offerLast(), poll() = pollFirst(), peek() = peekFirst()
- **Stack operations:** push() = addFirst(), pop() = removeFirst(), peek() = peekFirst()

### ArrayDeque - Complete Reference

#### Internal Structure
```java
// ArrayDeque uses circular array (resizable)
public class ArrayDeque<E> extends AbstractCollection<E>
        implements Deque<E> {
    transient Object[] elements;             // Circular array
    transient int head;                      // Index of first element
    transient int tail;                      // Index of next available slot
    
    // Default capacity: 16
    // Resize: doubles when full (16 → 32 → 64...)
    // Circular: head and tail wrap around
}
```

#### Constructors and Usage

```java
// 1. Default constructor - capacity 16
Deque<String> deque1 = new ArrayDeque<>();

// 2. With initial capacity
Deque<String> deque2 = new ArrayDeque<>(100);

// 3. From collection
Deque<String> deque3 = new ArrayDeque<>(List.of("A", "B", "C"));

// All deque operations are O(1) amortized
deque1.offerFirst("X");                      // O(1)
deque1.offerLast("Y");                       // O(1)
deque1.pollFirst();                          // O(1)
deque1.pollLast();                           // O(1)

// Null elements NOT allowed
// deque1.offer(null);                       // ❌ NullPointerException

// Better than Stack for LIFO
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);
stack.push(2);
stack.push(3);
System.out.println(stack.pop());             // 3

// Better than LinkedList for Queue
Queue<String> queue = new ArrayDeque<>();
queue.offer("A");
queue.offer("B");
System.out.println(queue.poll());            // "A"
```

### PriorityQueue - Complete Reference

#### Internal Structure
```java
// PriorityQueue uses binary min-heap
public class PriorityQueue<E> extends AbstractQueue<E> {
    transient Object[] queue;                // Heap array
    int size;
    Comparator<? super E> comparator;        // Optional
    
    // Default capacity: 11
    // Resize: grows by ~50% when full
    // Heap property: parent ≤ children (min-heap)
}

// Binary heap:
//        1
//      /   \
//     2     3
//    / \   / \
//   4   5 6   7
// Array: [1, 2, 3, 4, 5, 6, 7]
// Parent of i: (i-1)/2
// Left child: 2*i+1
// Right child: 2*i+2
```

#### Constructors and Usage

```java
// 1. Default - natural ordering (min-heap)
PriorityQueue<Integer> pq1 = new PriorityQueue<>();
pq1.offer(5);
pq1.offer(2);
pq1.offer(8);
pq1.offer(1);
System.out.println(pq1.poll());              // 1 (smallest first)
System.out.println(pq1.poll());              // 2

// 2. With initial capacity
PriorityQueue<Integer> pq2 = new PriorityQueue<>(100);

// 3. With Comparator - custom ordering (max-heap)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(2);
maxHeap.offer(8);
System.out.println(maxHeap.poll());          // 8 (largest first)

// 4. From collection
PriorityQueue<Integer> pq4 = new PriorityQueue<>(List.of(5, 2, 8, 1));
// Heapifies in O(n)

// Operations
PriorityQueue<String> pq = new PriorityQueue<>();

// offer(E) / add(E) - O(log n)
pq.offer("banana");
pq.offer("apple");
pq.offer("cherry");

// peek() - O(1) - returns smallest (natural order)
System.out.println(pq.peek());               // "apple"

// poll() / remove() - O(log n)
System.out.println(pq.poll());               // "apple"
System.out.println(pq.poll());               // "banana"

// size(), isEmpty() - O(1)
System.out.println(pq.size());               // 1

// Null elements NOT allowed
// pq.offer(null);                           // ❌ NullPointerException

// NOT a true Queue - iteration order is undefined
for (String s : pq) {
    System.out.println(s);                   // Not guaranteed sorted!
}

// Custom objects must be Comparable or provide Comparator
class Task implements Comparable<Task> {
    String name;
    int priority;
    
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}
```


### Exam Traps

- ❌ **TRAP 1:** Queue.poll() vs Queue.remove() - null vs exception
  ```java
  Queue<String> queue = new ArrayDeque<>();
  
  String val1 = queue.poll();                // null (empty, no exception)
  // String val2 = queue.remove();           // ❌ NoSuchElementException
  
  // Similarly: peek() vs element()
  String val3 = queue.peek();                // null
  // String val4 = queue.element();          // ❌ NoSuchElementException
  ```

- ❌ **TRAP 2:** PriorityQueue iteration order is undefined
  ```java
  PriorityQueue<Integer> pq = new PriorityQueue<>();
  pq.addAll(List.of(5, 2, 8, 1, 9));
  
  // poll() returns in order
  System.out.println(pq.poll());             // 1
  System.out.println(pq.poll());             // 2
  
  // BUT iterator() is NOT ordered!
  pq.addAll(List.of(5, 2, 8, 1, 9));
  for (Integer i : pq) {
      System.out.print(i + " ");             // Could be: 1 2 5 8 9, or 1 5 2 9 8, etc.
  }
  // Only poll() guarantees order!
  ```

- ❌ **TRAP 3:** ArrayDeque and PriorityQueue don't allow null
  ```java
  Deque<String> deque = new ArrayDeque<>();
  deque.offer(null);                         // ❌ NullPointerException
  
  PriorityQueue<String> pq = new PriorityQueue<>();
  pq.offer(null);                            // ❌ NullPointerException
  
  // LinkedList allows null
  Deque<String> linked = new LinkedList<>();
  linked.offer(null);                        // ✅ OK
  ```

- ❌ **TRAP 4:** PriorityQueue requires Comparable or Comparator
  ```java
  class Task { String name; }
  
  PriorityQueue<Task> pq = new PriorityQueue<>();
  pq.offer(new Task());
  pq.offer(new Task());                      // ❌ ClassCastException on second add
  // Task doesn't implement Comparable!
  
  // Fix: provide Comparator
  PriorityQueue<Task> pq2 = new PriorityQueue<>((t1, t2) -> 
      t1.name.compareTo(t2.name)
  );
  ```

- ❌ **TRAP 5:** Deque.push() adds to FRONT, not back
  ```java
  Deque<Integer> deque = new ArrayDeque<>();
  deque.push(1);                             // [1]
  deque.push(2);                             // [2, 1] - adds to front!
  deque.push(3);                             // [3, 2, 1]
  
  System.out.println(deque.pop());           // 3 (LIFO)
  
  // For FIFO, use offer() and poll()
  ```

- ❌ **TRAP 6:** ArrayDeque is NOT thread-safe
  ```java
  Deque<String> deque = new ArrayDeque<>();
  
  // Multiple threads
  thread1: deque.offer("A");
  thread2: deque.offer("B");                 // ❌ Race condition!
  
  // Use ConcurrentLinkedDeque for thread safety
  Deque<String> concurrent = new ConcurrentLinkedDeque<>();
  ```

- ❌ **TRAP 7:** Queue.offer() vs Queue.add() with capacity
  ```java
  // For capacity-restricted queues (like ArrayBlockingQueue)
  BlockingQueue<String> bounded = new ArrayBlockingQueue<>(2);
  
  bounded.add("A");                          // OK
  bounded.add("B");                          // OK
  // bounded.add("C");                       // ❌ IllegalStateException - full!
  
  boolean added = bounded.offer("C");        // false - returns false instead
  ```

- ❌ **TRAP 8:** PriorityQueue peek() is O(1), poll() is O(log n)
  ```java
  PriorityQueue<Integer> pq = new PriorityQueue<>();
  pq.addAll(List.of(5, 2, 8, 1, 9));
  
  Integer min = pq.peek();                   // O(1) - just root
  // pq still = [1, 2, 8, 5, 9]
  
  Integer removed = pq.poll();               // O(log n) - remove root, reheapify
  // pq = [2, 5, 8, 9] - internal restructuring
  ```

- ❌ **TRAP 9:** ArrayDeque faster than Stack class
  ```java
  // DON'T use legacy Stack class (synchronized, slow)
  Stack<Integer> oldStack = new Stack<>();   // ❌ Avoid
  
  // USE ArrayDeque instead
  Deque<Integer> newStack = new ArrayDeque<>(); // ✅ Faster
  newStack.push(1);
  newStack.pop();
  ```

- ❌ **TRAP 10:** PriorityQueue remove(Object) is O(n), not O(log n)
  ```java
  PriorityQueue<Integer> pq = new PriorityQueue<>();
  pq.addAll(List.of(1, 2, 3, 4, 5));
  
  pq.poll();                                 // O(log n) - efficient
  pq.remove(3);                              // O(n) - must find element first!
  // Scans array to find element, then reheapifies
  ```

---

## Exam Questions - Queue/Deque

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **Queue/Deque questions:** ~2-3 questions (4-6% of exam)

---

### Question 1: Queue poll() vs remove()
```java
Queue<String> queue = new ArrayDeque<>();
String val = queue.poll();
System.out.println(val);
```
**What is the output?**  
A. ""  
B. null  
C. NoSuchElementException  
D. Compilation error  

**Answer:** B  
**Explanation:** `poll()` returns null when the queue is empty (doesn't throw exception). `remove()` would throw NoSuchElementException on empty queue.

---

### Question 2: PriorityQueue ordering
```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.addAll(List.of(5, 2, 8, 1));
System.out.println(pq.poll());
System.out.println(pq.poll());
```
**What is the output?**  
A. 5<br>2  
B. 1<br>2  
C. 2<br>5  
D. 8<br>5  

**Answer:** B  
**Explanation:** PriorityQueue is a min-heap by default (natural ordering). `poll()` removes and returns the smallest element each time: 1, then 2.

---

### Question 3: Deque push() behavior
```java
Deque<Integer> deque = new ArrayDeque<>();
deque.push(1);
deque.push(2);
deque.push(3);
System.out.println(deque.poll());
```
**What is the output?**  
A. 1  
B. 2  
C. 3  
D. Compilation error  

**Answer:** C  
**Explanation:** `push()` adds to the FRONT of deque (LIFO/stack behavior). After pushes: [3, 2, 1]. `poll()` removes from front: 3.

---

### Question 4: ArrayDeque null elements
```java
Deque<String> deque = new ArrayDeque<>();
deque.offer("A");
deque.offer(null);
deque.offer("B");
```
**What is the result?**  
A. deque = [A, null, B]  
B. deque = [A, B]  
C. NullPointerException  
D. Compilation error  

**Answer:** C  
**Explanation:** ArrayDeque doesn't allow null elements and throws NullPointerException when attempting to add null. Use LinkedList if you need null elements.

---

### Question 5: PriorityQueue iteration
```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.addAll(List.of(5, 2, 8, 1));
for (Integer i : pq) {
    System.out.print(i + " ");
}
```
**What is the output?**  
A. 1 2 5 8  
B. 5 2 8 1  
C. Undefined order  
D. 8 5 2 1  

**Answer:** C  
**Explanation:** PriorityQueue iterator doesn't guarantee any particular order. Only `poll()` guarantees removal in priority order. Iteration order depends on internal heap structure.

---

### Question 6: Queue offer() vs add()
```java
Queue<String> queue = new ArrayDeque<>();
boolean result1 = queue.offer("A");
queue.add("B");
System.out.println(result1);
```
**What is the output?**  
A. true  
B. false  
C. Compilation error  
D. Exception  

**Answer:** A  
**Explanation:** For unbounded queues like ArrayDeque, both `offer()` and `add()` succeed. `offer()` returns true when successful. The difference matters for capacity-restricted queues.

---

### Question 7: PriorityQueue Comparator
```java
PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
pq.addAll(List.of(5, 2, 8, 1));
System.out.println(pq.poll());
```
**What is the output?**  
A. 1  
B. 2  
C. 5  
D. 8  

**Answer:** D  
**Explanation:** `Comparator.reverseOrder()` creates a max-heap (largest first). `poll()` returns the largest element: 8.

---

### Question 8: Deque as Stack
```java
Deque<String> stack = new ArrayDeque<>();
stack.push("A");
stack.push("B");
System.out.println(stack.pop());
System.out.println(stack.peek());
```
**What is the output?**  
A. A<br>B  
B. B<br>A  
C. A<br>A  
D. B<br>B  

**Answer:** B  
**Explanation:** `push()` adds to front (stack behavior). After pushes: [B, A]. `pop()` removes and returns "B". `peek()` returns "A" without removing.

---

### Question 9: LinkedList as Queue with null
```java
Queue<String> queue = new LinkedList<>();
queue.offer("A");
queue.offer(null);
queue.offer("B");
System.out.println(queue.size());
```
**What is the output?**  
A. 2  
B. 3  
C. NullPointerException  
D. Compilation error  

**Answer:** B  
**Explanation:** LinkedList (unlike ArrayDeque) allows null elements. Queue contains three elements: ["A", null, "B"], size = 3.

---

### Question 10: PriorityQueue requires Comparable
```java
class Task { String name; }

PriorityQueue<Task> pq = new PriorityQueue<>();
pq.offer(new Task());
pq.offer(new Task());
```
**What is the result?**  
A. pq contains 2 tasks  
B. Compilation error  
C. ClassCastException  
D. NullPointerException  

**Answer:** C  
**Explanation:** PriorityQueue requires elements to be Comparable or provide a Comparator. Task doesn't implement Comparable, so ClassCastException is thrown when trying to compare during the second offer.

---

## Complete Chapter Summary

### Collections Framework Hierarchy

```
Collection (interface)
├── List (interface) - Ordered, allows duplicates, indexed
│   ├── ArrayList - Resizable array, O(1) access
│   ├── LinkedList - Doubly-linked list, O(1) add/remove at ends
│   └── Vector - Legacy, synchronized (use Collections.synchronizedList instead)
│
├── Set (interface) - No duplicates, based on equals()/hashCode()
│   ├── HashSet - Hash table, O(1) operations, no order
│   ├── LinkedHashSet - Hash table + linked list, O(1) operations, insertion order
│   └── TreeSet - Red-Black tree, O(log n) operations, sorted order
│
└── Queue (interface) - FIFO (usually)
    ├── Deque (interface) - Double-ended queue
    │   ├── ArrayDeque - Circular array, O(1) operations, no null
    │   └── LinkedList - Doubly-linked list, O(1) operations, allows null
    └── PriorityQueue - Binary heap, O(log n) add/poll, priority order

Map (interface) - NOT part of Collection, key-value pairs
├── HashMap - Hash table, O(1) operations, no order, one null key
├── LinkedHashMap - Hash table + linked list, O(1) operations, insertion/access order
└── TreeMap - Red-Black tree, O(log n) operations, sorted by keys, no null keys
```

### Performance Comparison - All Collections

| Collection | Add | Remove | Get/Contains | Notes |
|------------|-----|--------|--------------|-------|
| **ArrayList** | O(1) amortized end<br>O(n) middle | O(n) | O(1) get by index<br>O(n) contains | Best for random access |
| **LinkedList** | O(1) ends<br>O(n) middle | O(1) ends<br>O(n) middle | O(n) all operations | Best for frequent add/remove at ends |
| **HashSet** | O(1) avg | O(1) avg | O(1) avg | Fast operations, no order |
| **LinkedHashSet** | O(1) avg | O(1) avg | O(1) avg | Fast + insertion order |
| **TreeSet** | O(log n) | O(log n) | O(log n) | Sorted, range operations |
| **HashMap** | O(1) avg | O(1) avg | O(1) avg | Fast key-value, no order |
| **LinkedHashMap** | O(1) avg | O(1) avg | O(1) avg | Fast + order, LRU cache |
| **TreeMap** | O(log n) | O(log n) | O(log n) | Sorted keys, range operations |
| **ArrayDeque** | O(1) amortized | O(1) | N/A | Best queue/stack |
| **PriorityQueue** | O(log n) | O(log n) poll<br>O(n) remove(obj) | O(1) peek | Priority-based |

### When to Use Which Collection - Decision Tree

**Need indexed access? → List**
- Random access frequent? → **ArrayList**
- Add/remove at ends frequent? → **LinkedList**

**Need unique elements? → Set**
- Fast operations, no order needed? → **HashSet**
- Need insertion order? → **LinkedHashSet**
- Need sorted order or range operations? → **TreeSet**

**Need key-value pairs? → Map**
- Fast operations, no order needed? → **HashMap**
- Need insertion/access order? → **LinkedHashMap**
- Need sorted keys or range operations? → **TreeMap**

**Need FIFO/LIFO? → Queue/Deque**
- General queue or stack? → **ArrayDeque**
- Need null elements? → **LinkedList**
- Need priority ordering? → **PriorityQueue**

### Common Patterns and Best Practices

#### Pattern 1: Frequency Counting
```java
// Best: HashMap with merge()
Map<String, Integer> freq = new HashMap<>();
for (String word : words) {
    freq.merge(word, 1, Integer::sum);       // O(1)
}
```

#### Pattern 2: Remove Duplicates
```java
// Best: Set constructor
List<String> list = Arrays.asList("A", "B", "A", "C");
List<String> unique = new ArrayList<>(new LinkedHashSet<>(list));
// Preserves order, O(n)
```

#### Pattern 3: Top K Elements
```java
// Best: PriorityQueue (min-heap of size K)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
for (int num : nums) {
    minHeap.offer(num);
    if (minHeap.size() > k) {
        minHeap.poll();                      // Remove smallest
    }
}
// minHeap contains top K largest, O(n log k)
```

#### Pattern 4: LRU Cache
```java
// Best: LinkedHashMap with access-order
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    
    public LRUCache(int maxSize) {
        super(16, 0.75f, true);              // access-order
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
```

#### Pattern 5: BFS/DFS
```java
// BFS: Queue (ArrayDeque)
Queue<Node> queue = new ArrayDeque<>();
queue.offer(root);
while (!queue.isEmpty()) {
    Node node = queue.poll();
    // Process node
    queue.addAll(node.children);
}

// DFS: Stack (ArrayDeque)
Deque<Node> stack = new ArrayDeque<>();
stack.push(root);
while (!stack.isEmpty()) {
    Node node = stack.pop();
    // Process node
    for (Node child : node.children) {
        stack.push(child);
    }
}
```

### Null Handling Summary

| Collection | Null Keys | Null Values | Notes |
|------------|-----------|-------------|-------|
| **ArrayList** | N/A | Multiple | ✅ Allows null |
| **LinkedList** | N/A | Multiple | ✅ Allows null |
| **HashSet** | N/A | One null | ✅ One null allowed |
| **TreeSet** | N/A | No null | ❌ NPE on add |
| **HashMap** | One null | Multiple | ✅ One null key |
| **TreeMap** | No null | Multiple | ❌ NPE on put null key |
| **ArrayDeque** | N/A | No null | ❌ NPE on offer |
| **PriorityQueue** | N/A | No null | ❌ NPE on offer |

### Immutable Collections (Java 9+)

```java
// List.of(), Set.of(), Map.of() - immutable
List<String> list = List.of("A", "B", "C");
Set<Integer> set = Set.of(1, 2, 3);
Map<String, Integer> map = Map.of("A", 1, "B", 2);

// Constraints:
// - Cannot add/remove/modify
// - No null elements
// - No duplicate keys (Map) or elements (Set)
// - Iteration order undefined (Set, Map)

// For mutable copy
List<String> mutable = new ArrayList<>(List.of("A", "B"));
```

### Key Exam Takeaways

1. **ArrayList** - O(1) get, O(n) add/remove middle
2. **LinkedList** - O(1) add/remove ends, O(n) get
3. **HashSet** - O(1) operations, no order
4. **TreeSet** - O(log n) operations, sorted, uses compareTo()
5. **HashMap** - O(1) operations, one null key
6. **TreeMap** - O(log n) operations, sorted keys, no null keys
7. **ArrayDeque** - Best queue/stack, no null
8. **PriorityQueue** - O(log n) add/poll, min-heap default
9. **Views are backed** - keySet(), subList(), subMap() modify original
10. **Iteration during modification** - Use iterator.remove() or removeIf()
11. **Modifying keys/elements** - Don't modify hashCode/compareTo fields after adding
12. **Map.of()/Set.of()/List.of()** - Immutable, no null, no duplicates

---

**End of Chapter 5 - Arrays and Collections**

**[← Back to Part 1: Arrays](CHAPTER-05-PART1-ARRAYS.md)**

