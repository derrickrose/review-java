# Chapter 5: Working with Arrays and Collections - PART 2: List Interface

[← Part 1: Arrays](CHAPTER-05-PART1-ARRAYS.md) | [Back to Main Guide](README.md) | [Next: Part 3 - Set →](CHAPTER-05-PART3-SET.md)

---

## 2. List Interface

**Exam Objective:** Use List interface and implementations (ArrayList, LinkedList)

### What is List?
Ordered collection that allows duplicates with index-based access. Part of java.util.Collection framework. Maintains insertion order. Main implementations: ArrayList (resizable array), LinkedList (doubly-linked list).

### When to Use

| Implementation | When to Use | Best Use Case |
|----------------|-------------|---------------|
| **ArrayList** | Random access, iteration, infrequent modifications at end | 95% of use cases - default choice |
| **LinkedList** | Frequent insertions/deletions at both ends, queue/deque ops | Task queues, undo/redo stacks |
| **Vector** | Legacy code only (synchronized) | Use Collections.synchronizedList(new ArrayList<>()) instead |
| **CopyOnWriteArrayList** | Read-heavy, rare writes, thread-safe | Event listeners, observer patterns |

### Why Use List Over Array
- **Dynamic sizing:** Grows/shrinks automatically (no fixed size)
- **Rich API:** 25+ methods for manipulation
- **Maintains insertion order:** Predictable iteration
- **Allows duplicates:** Can store same element multiple times
- **Allows null:** Unlike some collections (Set, Map keys)
- **Type safety with generics:** List\<String\> prevents type errors
- **Framework integration:** Works with Collections utilities

### Performance Characteristics

#### ArrayList vs LinkedList - Complete Comparison

| Operation | ArrayList | LinkedList | Winner | Notes |
|-----------|-----------|------------|--------|-------|
| **get(index)** | O(1) | O(n) | ArrayList | Random access - ArrayList shines |
| **set(index, E)** | O(1) | O(n) | ArrayList | Must find index first in LinkedList |
| **add(E)** | O(1) amortized* | O(1) | LinkedList | LinkedList guaranteed O(1) |
| **add(0, E)** | O(n) | O(1) | LinkedList | ArrayList must shift all elements |
| **add(index, E)** | O(n) | O(n)** | Tie | Both need traversal |
| **remove(index)** | O(n) | O(n)** | Tie | ArrayList shifts, LinkedList traverses |
| **remove(Object)** | O(n) | O(n) | ArrayList | Better cache locality |
| **contains(Object)** | O(n) | O(n) | ArrayList | Better cache locality |
| **indexOf(Object)** | O(n) | O(n) | ArrayList | Sequential scan, better cache |
| **iterator.remove()** | O(n) | O(1) | LinkedList | LinkedList only updates pointers |
| **addFirst(E)** | O(n) | O(1) | LinkedList | ArrayList must shift |
| **addLast(E)** | O(1) | O(1) | Tie | Both constant time |
| **removeFirst()** | O(n) | O(1) | LinkedList | ArrayList must shift |
| **removeLast()** | O(1) | O(1) | Tie | Both constant time |
| **size()** | O(1) | O(1) | Tie | Both maintain size field |
| **clear()** | O(n) | O(n) | Tie | Must help GC |
| **Memory per element** | ~4 bytes | ~24 bytes | ArrayList | LinkedList has node overhead |
| **Cache locality** | Excellent | Poor | ArrayList | Contiguous vs scattered memory |
| **Initial allocation** | O(capacity) | O(1) | LinkedList | ArrayList pre-allocates array |

\* O(1) amortized - occasionally O(n) when array needs to resize (doubles capacity)
\*\* O(n) if index is in middle; O(1) if at ends (index 0 or size-1)

#### Space Complexity

| Implementation | Space per element | Total Space | Notes |
|----------------|------------------|-------------|-------|
| **ArrayList** | Element size + ~4 bytes overhead | O(n) + unused capacity | Capacity ≥ size |
| **LinkedList** | Element size + ~24 bytes (2 pointers + overhead) | O(n) | No unused space |

**Example:** ArrayList\<Integer\> with 100 elements
- ArrayList: ~400 bytes (if capacity=100)
- LinkedList: ~2,400 bytes (100 nodes × 24 bytes)

#### ArrayList Internal Behavior

```java
// ArrayList capacity growth
List<Integer> list = new ArrayList<>();      // Default capacity: 10
// Add 10 elements - no resize
// Add 11th element - resize to 15 (10 × 1.5)
// Add 16th element - resize to 22 (15 × 1.5)
// Pattern: newCapacity = oldCapacity + (oldCapacity >> 1)  // × 1.5

// Specify initial capacity to avoid resizes
List<String> optimized = new ArrayList<>(1000); // Pre-allocate for 1000
```

### Real-World Examples

#### Example 1: Shopping Cart (ArrayList - Read Heavy)
```java
public class ShoppingCart {
    // ArrayList: frequent reads/iteration, rare modifications
    private List<CartItem> items = new ArrayList<>();
    
    // Add item - O(1) amortized
    public void addItem(Product product, int quantity) {
        // Check if product already exists - O(n)
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);          // O(1) get
            if (item.getProduct().equals(product)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity)); // O(1) add at end
    }
    
    // Remove item by product - O(n)
    public void removeItem(Product product) {
        items.removeIf(item -> item.getProduct().equals(product)); // O(n)
    }
    
    // Get item by index - O(1)
    public CartItem getItem(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return items.get(index);
    }
    
    // Calculate total - O(n)
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {              // Enhanced for = iterator
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
    
    // Get items count - O(1)
    public int getItemCount() {
        return items.size();
    }
    
    // Get all items (defensive copy) - O(n)
    public List<CartItem> getItems() {
        return new ArrayList<>(items);             // Prevent external modification
    }
    
    // Apply discount to expensive items - O(n)
    public void applyBulkDiscount(double threshold, double discountPercent) {
        for (CartItem item : items) {
            if (item.getProduct().getPrice() > threshold) {
                double newPrice = item.getProduct().getPrice() * (1 - discountPercent);
                item.getProduct().setPrice(newPrice);
            }
        }
    }
    
    // Sort by price - O(n log n)
    public void sortByPrice() {
        items.sort((a, b) -> Double.compare(
            a.getProduct().getPrice(), 
            b.getProduct().getPrice()
        ));
    }
    
    // Clear cart - O(n)
    public void clear() {
        items.clear();
    }
}
```
**Why ArrayList:** Shopping cart needs fast random access for display, frequent iteration, and rare insertions/deletions (only at checkout).

#### Example 2: Task Queue (LinkedList - Insert/Delete Heavy)
```java
public class TaskQueue {
    // LinkedList: frequent add/remove at both ends
    private LinkedList<Task> queue = new LinkedList<>();
    
    // Add task to end - O(1) guaranteed
    public void addTask(Task task) {
        queue.addLast(task);                       // O(1)
    }
    
    // Add urgent task to front - O(1) guaranteed
    public void addUrgentTask(Task task) {
        queue.addFirst(task);                      // O(1)
    }
    
    // Get and remove next task - O(1)
    public Task getNextTask() {
        return queue.isEmpty() ? null : queue.removeFirst(); // O(1)
    }
    
    // Peek at next task without removing - O(1)
    public Task peekNextTask() {
        return queue.peekFirst();                  // O(1)
    }
    
    // Get task count - O(1)
    public int getPendingTaskCount() {
        return queue.size();
    }
    
    // Check if specific task exists - O(n)
    public boolean hasTask(Task task) {
        return queue.contains(task);
    }
    
    // Remove specific task - O(n) to find, O(1) to remove
    public boolean removeTask(Task task) {
        return queue.remove(task);
    }
    
    // Get all tasks as list - O(n)
    public List<Task> getAllTasks() {
        return new ArrayList<>(queue);             // Convert to ArrayList
    }
}
```
**Why LinkedList:** Task queue needs efficient add/remove at both ends (FIFO for normal, LIFO for urgent). No random access needed.

#### Example 3: Browser History (ArrayList with Capacity Planning)
```java
public class BrowserHistory {
    private static final int MAX_HISTORY = 100;
    private List<String> history;
    private int currentIndex = -1;
    
    public BrowserHistory() {
        // Pre-allocate capacity to avoid resizes - O(capacity)
        history = new ArrayList<>(MAX_HISTORY);
    }
    
    // Visit new URL - O(1) usually, O(n) if capacity exceeded
    public void visit(String url) {
        // Remove forward history when visiting new page
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);    // O(1) remove from end
        }
        
        history.add(url);                          // O(1) add at end
        currentIndex++;
        
        // Maintain max size
        if (history.size() > MAX_HISTORY) {
            history.remove(0);                     // O(n) - expensive!
            currentIndex--;
        }
    }
    
    // Go back - O(1)
    public String back() {
        if (currentIndex > 0) {
            currentIndex--;
            return history.get(currentIndex);      // O(1)
        }
        return history.isEmpty() ? null : history.get(0);
    }
    
    // Go forward - O(1)
    public String forward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return history.get(currentIndex);      // O(1)
        }
        return currentIndex >= 0 ? history.get(currentIndex) : null;
    }
    
    // Get current URL - O(1)
    public String getCurrentUrl() {
        return currentIndex >= 0 && currentIndex < history.size() 
            ? history.get(currentIndex) 
            : null;
    }
    
    // Search history - O(n)
    public List<String> searchHistory(String keyword) {
        List<String> results = new ArrayList<>();
        for (String url : history) {
            if (url.contains(keyword)) {
                results.add(url);
            }
        }
        return results;
    }
}
```
**Why ArrayList:** Browser history needs fast random access for back/forward navigation. Pre-allocation avoids resize overhead.

### ArrayList - Complete Reference

#### Internal Structure
```java
// Simplified ArrayList internal implementation
public class ArrayList<E> {
    private Object[] elementData;        // Internal array
    private int size;                    // Number of elements
    
    private static final int DEFAULT_CAPACITY = 10;
    
    public ArrayList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }
    
    public ArrayList(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }
    
    // Resize when needed
    private void grow() {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1); // × 1.5
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
}
```

#### Constructors

```java
// 1. Default constructor - capacity 10
List<String> list1 = new ArrayList<>();

// 2. With initial capacity - avoid resizes
List<String> list2 = new ArrayList<>(100);

// 3. From another collection - O(n)
List<String> source = List.of("A", "B", "C");
List<String> list3 = new ArrayList<>(source);

// 4. From array - O(n)
String[] arr = {"X", "Y", "Z"};
List<String> list4 = new ArrayList<>(Arrays.asList(arr));

// Capacity vs Size
List<Integer> nums = new ArrayList<>(100);  // capacity=100, size=0
nums.add(1);                                // capacity=100, size=1
```

#### Adding Elements

```java
List<String> list = new ArrayList<>();

// add(E) - add to end - O(1) amortized
list.add("A");                               // [A]
list.add("B");                               // [A, B]
list.add("C");                               // [A, B, C]

// add(index, E) - insert at position - O(n)
list.add(1, "X");                            // [A, X, B, C]
list.add(0, "Z");                            // [Z, A, X, B, C]

// addAll(Collection) - add all elements - O(m) where m=collection size
list.addAll(List.of("D", "E", "F"));         // [Z, A, X, B, C, D, E, F]

// addAll(index, Collection) - insert collection at position - O(n + m)
list.addAll(2, List.of("Y1", "Y2"));         // [Z, A, Y1, Y2, X, B, C, D, E, F]

// Returns boolean
boolean added = list.add("G");               // true (always for ArrayList)
```

#### Accessing Elements

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D", "E"));

// get(index) - O(1)
String first = list.get(0);                  // "A"
String third = list.get(2);                  // "C"
String last = list.get(list.size() - 1);     // "E"

// IndexOutOfBoundsException if invalid
try {
    String invalid = list.get(10);           // ❌ Exception
} catch (IndexOutOfBoundsException e) {
    System.out.println("Index out of bounds");
}

// set(index, E) - replace element - O(1)
String old = list.set(1, "X");               // Returns "B", list=[A, X, C, D, E]

// isEmpty() - O(1)
boolean empty = list.isEmpty();              // false

// size() - O(1)
int size = list.size();                      // 5
```

#### Removing Elements

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "B", "D"));

// remove(index) - O(n) - shifts elements
String removed1 = list.remove(0);            // "A", list=[B, C, B, D]

// remove(Object) - O(n) - removes first occurrence
boolean removed2 = list.remove("B");         // true, list=[C, B, D]
boolean removed3 = list.remove("X");         // false (not found)

// removeAll(Collection) - O(n*m)
list.addAll(List.of("A", "B", "C"));         // [C, B, D, A, B, C]
list.removeAll(List.of("B", "C"));           // [D, A]

// retainAll(Collection) - keep only these - O(n*m)
list.addAll(List.of("B", "C", "D"));         // [D, A, B, C, D]
list.retainAll(List.of("A", "D"));           // [D, A, D]

// clear() - O(n)
list.clear();                                // []

// removeIf(Predicate) - Java 8 - O(n)
list.addAll(List.of("A", "B", "C", "D"));
list.removeIf(s -> s.compareTo("C") < 0);    // [C, D]
```

#### Searching

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "B", "D"));

// contains(Object) - O(n)
boolean hasB = list.contains("B");           // true
boolean hasX = list.contains("X");           // false

// indexOf(Object) - first occurrence - O(n)
int index1 = list.indexOf("B");              // 1
int index2 = list.indexOf("X");              // -1 (not found)

// lastIndexOf(Object) - last occurrence - O(n)
int index3 = list.lastIndexOf("B");          // 3

// containsAll(Collection) - O(n*m)
boolean hasAll = list.containsAll(List.of("A", "B"));  // true
```

#### Bulk Operations

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));

// replaceAll(UnaryOperator) - Java 8 - O(n)
list.replaceAll(n -> n * 2);                 // [2, 4, 6, 8, 10]

// sort(Comparator) - Java 8 - O(n log n)
list.sort(Comparator.reverseOrder());        // [10, 8, 6, 4, 2]
list.sort(Comparator.naturalOrder());        // [2, 4, 6, 8, 10]

// subList(from, to) - view, not copy - O(1)
List<Integer> sub = list.subList(1, 4);      // [4, 6, 8] - backed by original
sub.set(0, 99);                              // Modifies original! [2, 99, 6, 8, 10]

// toArray() - O(n)
Object[] arr1 = list.toArray();
Integer[] arr2 = list.toArray(new Integer[0]);

// forEach(Consumer) - Java 8 - O(n)
list.forEach(n -> System.out.print(n + " "));
```

### LinkedList - Complete Reference

#### Internal Structure
```java
// Simplified LinkedList internal implementation
public class LinkedList<E> {
    private Node<E> first;                   // Head pointer
    private Node<E> last;                    // Tail pointer
    private int size;
    
    private static class Node<E> {
        E item;                              // Element
        Node<E> next;                        // Next node
        Node<E> prev;                        // Previous node
        
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
    
    // Add at end - O(1)
    void linkLast(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null) first = newNode;
        else l.next = newNode;
        size++;
    }
}
```

#### Constructors

```java
// 1. Default constructor
LinkedList<String> list1 = new LinkedList<>();

// 2. From collection
LinkedList<String> list2 = new LinkedList<>(List.of("A", "B", "C"));
```

#### List Methods (Same as ArrayList)

```java
LinkedList<String> list = new LinkedList<>();

// All List interface methods work same as ArrayList
list.add("A");                               // O(1) at end
list.add(0, "Z");                            // O(1) at start (better than ArrayList!)
list.get(0);                                 // O(n) - must traverse (worse than ArrayList!)
list.remove(0);                              // O(1) at start (better than ArrayList!)
list.set(0, "X");                            // O(n) - must traverse
// ... all other List methods
```

#### Deque-Specific Methods (LinkedList implements Deque)

```java
LinkedList<String> list = new LinkedList<>();

// Add at front - O(1)
list.addFirst("A");                          // [A]
list.addFirst("Z");                          // [Z, A]

// Add at end - O(1)
list.addLast("B");                           // [Z, A, B]

// Remove from front - O(1)
String removed1 = list.removeFirst();        // "Z", list=[A, B]

// Remove from end - O(1)
String removed2 = list.removeLast();         // "B", list=[A]

// Peek (don't remove) - O(1)
list.addAll(List.of("B", "C"));              // [A, B, C]
String first = list.getFirst();              // "A"
String last = list.getLast();                // "C"

// peek methods return null if empty (no exception)
String peekFirst = list.peekFirst();         // "A"
String peekLast = list.peekLast();           // "C"

// offer methods (from Queue) - O(1)
boolean offered1 = list.offerFirst("Z");     // true, [Z, A, B, C]
boolean offered2 = list.offerLast("D");      // true, [Z, A, B, C, D]

// poll methods (from Queue) - O(1)
String polled1 = list.pollFirst();           // "Z", list=[A, B, C, D]
String polled2 = list.pollLast();            // "D", list=[A, B, C]
```

#### Stack Operations (LIFO)

```java
LinkedList<String> stack = new LinkedList<>();

// push - add to front - O(1)
stack.push("A");                             // [A]
stack.push("B");                             // [B, A]
stack.push("C");                             // [C, B, A]

// pop - remove from front - O(1)
String popped = stack.pop();                 // "C", stack=[B, A]

// peek - look at front - O(1)
String top = stack.peek();                   // "B"
```

### List Iteration Patterns

#### 1. Traditional for loop with index - O(n)
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

// Forward iteration
for (int i = 0; i < list.size(); i++) {
    String element = list.get(i);            // O(1) for ArrayList, O(n) for LinkedList!
    System.out.println(element);
}

// Backward iteration
for (int i = list.size() - 1; i >= 0; i--) {
    System.out.println(list.get(i));
}

// With modification
for (int i = 0; i < list.size(); i++) {
    list.set(i, list.get(i).toLowerCase());  // ✅ Can modify
}

// DANGER: Avoid for LinkedList - O(n²) total!
LinkedList<String> linked = new LinkedList<>(list);
for (int i = 0; i < linked.size(); i++) {    // ❌ Each get(i) is O(n)!
    System.out.println(linked.get(i));       // Total: O(n²)
}
```

#### 2. Enhanced for loop - O(n)
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

// Clean, efficient for both ArrayList and LinkedList
for (String element : list) {                // Uses iterator internally
    System.out.println(element);
}

// CANNOT modify list structure
for (String s : list) {
    // list.remove(s);                       // ❌ ConcurrentModificationException
    // list.add("X");                        // ❌ ConcurrentModificationException
}

// Can modify elements (if mutable objects)
List<StringBuilder> builders = new ArrayList<>();
builders.add(new StringBuilder("hello"));
for (StringBuilder sb : builders) {
    sb.append(" world");                     // ✅ Modifies object content
}
```

#### 3. Iterator - O(n)
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

// Basic iteration
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String element = it.next();
    System.out.println(element);
}

// Safe removal during iteration
it = list.iterator();
while (it.hasNext()) {
    String element = it.next();
    if (element.equals("B")) {
        it.remove();                         // ✅ Safe removal
    }
}
// list = [A, C, D]

// ListIterator - bidirectional + more features
ListIterator<String> listIt = list.listIterator();

// Forward
while (listIt.hasNext()) {
    int index = listIt.nextIndex();
    String element = listIt.next();
    System.out.println(index + ": " + element);
    
    // Can add, set, remove
    if (element.equals("C")) {
        listIt.set("C-modified");            // Replace current
        listIt.add("C-new");                 // Insert after current
    }
}

// Backward
while (listIt.hasPrevious()) {
    String element = listIt.previous();
    System.out.println(element);
}
```

#### 4. forEach method (Java 8) - O(n)
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));

// Simple forEach
list.forEach(element -> System.out.println(element));

// Method reference
list.forEach(System.out::println);

// With index (not built-in, need workaround)
AtomicInteger index = new AtomicInteger();
list.forEach(element -> {
    System.out.println(index.getAndIncrement() + ": " + element);
});

// CANNOT modify list structure
list.forEach(element -> {
    // list.remove(element);                 // ❌ ConcurrentModificationException
});
```

#### 5. Stream API (Java 8) - O(n) + operation cost
```java
List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

// Filter and collect
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());           // [2, 4, 6, 8, 10]

// Map and collect
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);                // 55

// Find first matching
Optional<Integer> first = numbers.stream()
    .filter(n -> n > 5)
    .findFirst();                            // Optional[6]

// Count
long count = numbers.stream()
    .filter(n -> n > 5)
    .count();                                // 5

// anyMatch, allMatch, noneMatch
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);    // true
boolean allPos = numbers.stream().allMatch(n -> n > 0);          // true
boolean noneNeg = numbers.stream().noneMatch(n -> n < 0);        // true

// Sorting
List<Integer> sorted = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// Distinct
List<Integer> withDupes = List.of(1, 2, 2, 3, 3, 3);
List<Integer> unique = withDupes.stream()
    .distinct()
    .collect(Collectors.toList());           // [1, 2, 3]
```

#### 6. Parallel Streams (Java 8) - For large lists
```java
List<Integer> large = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    large.add(i);
}

// Parallel processing - uses Fork/Join
long count = large.parallelStream()
    .filter(n -> n % 2 == 0)
    .count();

// When to use:
// - Large datasets (>10,000 elements)
// - CPU-intensive operations
// - Independent operations (no shared state)
```

### List.of(), Arrays.asList(), and new ArrayList() Comparison

```java
// 1. List.of() - Immutable, null-hostile (Java 9+)
List<String> immutable = List.of("A", "B", "C");
// immutable.add("D");                       // ❌ UnsupportedOperationException
// immutable.set(0, "X");                    // ❌ UnsupportedOperationException
// immutable.remove(0);                      // ❌ UnsupportedOperationException
// List.of("A", null);                       // ❌ NullPointerException
// Size: 3, Memory: Minimal

// 2. Arrays.asList() - Fixed-size, backed by array
String[] array = {"A", "B", "C"};
List<String> fixedSize = Arrays.asList(array);
fixedSize.set(0, "X");                       // ✅ Can modify elements
array[0] = "Z";                              // Affects list!
// fixedSize.add("D");                       // ❌ UnsupportedOperationException
// fixedSize.remove(0);                      // ❌ UnsupportedOperationException
fixedSize.set(1, null);                      // ✅ Allows null
// Size: 3, Memory: Just wraps array

// 3. new ArrayList() - Mutable, resizable
List<String> mutable = new ArrayList<>(List.of("A", "B", "C"));
mutable.add("D");                            // ✅ Can add
mutable.remove(0);                           // ✅ Can remove
mutable.set(0, "X");                         // ✅ Can modify
mutable.add(null);                           // ✅ Allows null
// Size: dynamic, Memory: Array + overhead + unused capacity

| Feature | List.of() | Arrays.asList() | new ArrayList() |
|---------|-----------|-----------------|-----------------|
| Mutable elements | ❌ No | ✅ Yes | ✅ Yes |
| Add/Remove | ❌ No | ❌ No | ✅ Yes |
| Null elements | ❌ No | ✅ Yes | ✅ Yes |
| Memory | Minimal | Minimal | Overhead |
| Thread-safe | ✅ Yes | ❌ No | ❌ No |
| Backed by array | ❌ No | ✅ Yes | ✅ Yes |
```


### Exam Traps

- ❌ **TRAP 1:** ConcurrentModificationException during iteration
  ```java
  List<String> list = new ArrayList<>(List.of("A", "B", "C"));
  
  for (String s : list) {
      list.remove(s);                        // ❌ ConcurrentModificationException
  }
  
  // Use iterator.remove() or removeIf()
  list.removeIf(s -> s.equals("A"));         // ✅ Safe
  
  Iterator<String> it = list.iterator();
  while (it.hasNext()) {
      if (it.next().equals("B")) {
          it.remove();                       // ✅ Safe
      }
  }
  ```

- ❌ **TRAP 2:** List.of() creates immutable list
  ```java
  List<String> list = List.of("A", "B", "C");
  list.add("D");                             // ❌ UnsupportedOperationException
  list.set(0, "X");                          // ❌ UnsupportedOperationException
  list.remove(0);                            // ❌ UnsupportedOperationException
  
  // Create mutable copy
  List<String> mutable = new ArrayList<>(List.of("A", "B", "C")); // ✅
  ```

- ❌ **TRAP 3:** Arrays.asList() creates fixed-size list
  ```java
  List<String> list = Arrays.asList("A", "B", "C");
  list.set(0, "X");                          // ✅ Can modify elements
  list.add("D");                             // ❌ UnsupportedOperationException
  list.remove(0);                            // ❌ UnsupportedOperationException
  
  // Backed by original array!
  String[] arr = {"A", "B", "C"};
  List<String> list2 = Arrays.asList(arr);
  arr[0] = "Z";                              // Changes list2 too!
  ```

- ❌ **TRAP 4:** LinkedList get(index) is O(n), not O(1)
  ```java
  LinkedList<String> list = new LinkedList<>();
  for (int i = 0; i < 1000; i++) {
      list.add("Item" + i);
  }
  
  // This is O(n²) total! - VERY SLOW
  for (int i = 0; i < list.size(); i++) {    // ❌ Bad!
      System.out.println(list.get(i));       // Each get is O(n)
  }
  
  // Use iterator or enhanced for - O(n)
  for (String s : list) {                    // ✅ Good - O(n) total
      System.out.println(s);
  }
  ```

- ❌ **TRAP 5:** remove(int) vs remove(Object) ambiguity
  ```java
  List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
  
  list.remove(2);                            // Removes index 2 (element "3")
  // list = [1, 2, 4, 5]
  
  list.remove(Integer.valueOf(2));          // Removes element "2"
  // list = [1, 4, 5]
  
  // For Integer lists, specify carefully!
  ```

- ❌ **TRAP 6:** subList() is a view, not a copy
  ```java
  List<String> list = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
  List<String> sub = list.subList(1, 4);     // [B, C, D] - VIEW!
  
  sub.set(0, "X");                           // Modifies original!
  // list = [A, X, C, D, E]
  
  list.add("F");                             // Invalidates sublist!
  // sub.size();                             // ❌ ConcurrentModificationException
  
  // Create independent copy
  List<String> copy = new ArrayList<>(list.subList(1, 4)); // ✅
  ```

- ❌ **TRAP 7:** List.of() doesn't allow null
  ```java
  List<String> list1 = List.of("A", null, "B"); // ❌ NullPointerException
  
  List<String> list2 = new ArrayList<>();
  list2.add("A");
  list2.add(null);                           // ✅ ArrayList allows null
  list2.add("B");                            // [A, null, B]
  ```

- ❌ **TRAP 8:** ArrayList capacity vs size
  ```java
  List<String> list = new ArrayList<>(100); // capacity=100, size=0
  
  System.out.println(list.size());          // 0, not 100!
  // list.get(0);                            // ❌ IndexOutOfBoundsException
  
  // Must add elements
  for (int i = 0; i < 100; i++) {
      list.add("Item");                      // Now size grows
  }
  ```

- ❌ **TRAP 9:** toArray() returns Object[], not type-specific
  ```java
  List<String> list = new ArrayList<>(List.of("A", "B", "C"));
  
  Object[] arr1 = list.toArray();            // Object[]
  // String[] wrong = list.toArray();        // ❌ Compilation error
  
  String[] arr2 = list.toArray(new String[0]); // ✅ Correct
  String[] arr3 = list.toArray(new String[list.size()]); // ✅ Pre-sized
  ```

- ❌ **TRAP 10:** replaceAll() modifies in place
  ```java
  List<String> list = new ArrayList<>(List.of("a", "b", "c"));
  list.replaceAll(String::toUpperCase);      // Modifies original!
  // list = [A, B, C]
  
  // If you need original preserved
  List<String> original = List.of("a", "b", "c");
  List<String> upper = original.stream()
                               .map(String::toUpperCase)
                               .collect(Collectors.toList()); // ✅ New list
  ```

- ❌ **TRAP 11:** ArrayList ensureCapacity() doesn't change size
  ```java
  ArrayList<String> list = new ArrayList<>();
  list.ensureCapacity(100);                  // Pre-allocate capacity
  
  System.out.println(list.size());           // 0 (size unchanged!)
  // list.get(0);                            // ❌ IndexOutOfBoundsException
  ```

- ❌ **TRAP 12:** Collections.unmodifiableList() is shallow
  ```java
  List<StringBuilder> list = new ArrayList<>();
  list.add(new StringBuilder("hello"));
  
  List<StringBuilder> unmod = Collections.unmodifiableList(list);
  // unmod.add(new StringBuilder("x"));      // ❌ UnsupportedOperationException
  
  unmod.get(0).append(" world");             // ✅ Can modify contents!
  // Original list also affected
  ```

---

## Exam Questions - List Interface

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **List questions:** ~4-6 questions (8-12% of exam)

---

### Question 1: ConcurrentModificationException
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
    if (s.equals("B")) {
        list.remove(s);
    }
}
System.out.println(list);
```
**What is the result?**  
A. [A, C]  
B. [A, B, C]  
C. ConcurrentModificationException  
D. Compilation error  

**Answer:** C  
**Explanation:** Enhanced for loop uses iterator internally. Modifying the list structure (remove) while iterating throws ConcurrentModificationException. Use `iterator.remove()` or `removeIf()` instead.

---

### Question 2: List.of() immutability
```java
List<String> list = List.of("A", "B", "C");
list.set(0, "Z");
list.add("D");
System.out.println(list);
```
**What is the result?**  
A. [Z, B, C, D]  
B. [Z, B, C]  
C. UnsupportedOperationException at line 2  
D. UnsupportedOperationException at line 3  

**Answer:** C  
**Explanation:** `List.of()` creates an IMMUTABLE list (Java 9+). Cannot modify elements with `set()`, cannot add/remove. The exception is thrown at the first modification attempt (line 2).

---

### Question 3: Arrays.asList() fixed size
```java
String[] arr = {"A", "B", "C"};
List<String> list = Arrays.asList(arr);
list.set(0, "Z");
arr[1] = "X";
list.add("D");
System.out.println(list);
```
**What is the result?**  
A. [Z, X, C, D]  
B. [Z, X, C]  
C. UnsupportedOperationException at line 3  
D. UnsupportedOperationException at line 5  

**Answer:** D  
**Explanation:** `Arrays.asList()` creates FIXED-SIZE list backed by original array. Can modify elements (`set()` works), array changes affect list, but CANNOT add/remove. Exception thrown at `add()`.

---

### Question 4: LinkedList get() performance
```java
LinkedList<Integer> list = new LinkedList<>();
for (int i = 0; i < 1000; i++) {
    list.add(i);
}

for (int i = 0; i < list.size(); i++) {
    System.out.println(list.get(i));
}
```
**What is the time complexity?**  
A. O(n)  
B. O(n log n)  
C. O(n²)  
D. O(1)  

**Answer:** C  
**Explanation:** LinkedList `get(i)` is O(n) because it must traverse from head/tail. Loop calls `get()` n times, making total complexity O(n²). Use iterator or enhanced for instead for O(n).

---

### Question 5: remove(int) vs remove(Object)
```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30, 40));
list.remove(2);
list.remove(Integer.valueOf(20));
System.out.println(list);
```
**What is the output?**  
A. [10, 40]  
B. [10, 30, 40]  
C. [10, 20, 40]  
D. [10]  

**Answer:** A  
**Explanation:** First `remove(2)` removes element at INDEX 2 (value 30), making list [10, 20, 40]. Second `remove(Integer.valueOf(20))` removes OBJECT 20, making list [10, 40].

---

### Question 6: subList() is a view
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
List<String> sub = list.subList(1, 3);
sub.set(0, "X");
list.add("E");
System.out.println(sub.get(0));
```
**What is the result?**  
A. X  
B. B  
C. ConcurrentModificationException  
D. Compilation error  

**Answer:** C  
**Explanation:** `subList()` returns a VIEW backed by original list. Modifying sublist affects original. But adding to original list invalidates the sublist view, causing ConcurrentModificationException on subsequent sublist access.

---

### Question 7: ArrayList capacity vs size
```java
ArrayList<String> list = new ArrayList<>(50);
System.out.println(list.size());
System.out.println(list.get(0));
```
**What is the result?**  
A. 50<br>null  
B. 0<br>IndexOutOfBoundsException  
C. 50<br>IndexOutOfBoundsException  
D. 0<br>null  

**Answer:** B  
**Explanation:** `new ArrayList<>(50)` sets CAPACITY to 50, but SIZE remains 0. `size()` returns 0. `get(0)` throws IndexOutOfBoundsException because no elements have been added yet.

---

### Question 8: toArray() type
```java
List<String> list = new ArrayList<>(List.of("A", "B"));
String[] arr = list.toArray();
```
**What is the result?**  
A. arr = ["A", "B"]  
B. Compilation error  
C. ClassCastException  
D. NullPointerException  

**Answer:** B  
**Explanation:** `toArray()` without parameters returns `Object[]`, not `String[]`. Cannot assign `Object[]` to `String[]`. Must use `toArray(new String[0])` to get typed array.

---

### Question 9: List.of() with null
```java
List<String> list = List.of("A", null, "B");
System.out.println(list.size());
```
**What is the result?**  
A. 3  
B. 2  
C. NullPointerException  
D. Compilation error  

**Answer:** C  
**Explanation:** `List.of()` is null-hostile and throws NullPointerException if any element is null. Use `new ArrayList<>()` or `Arrays.asList()` if you need null elements.

---

### Question 10: replaceAll() behavior
```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
List<String> result = list.replaceAll(String::toUpperCase);
System.out.println(list);
System.out.println(result);
```
**What is the result?**  
A. [a, b, c]<br>[A, B, C]  
B. [A, B, C]<br>[A, B, C]  
C. [A, B, C]<br>null  
D. Compilation error  

**Answer:** D  
**Explanation:** `replaceAll()` returns `void`, not a List. It modifies the list IN PLACE. Cannot assign void to a List variable. Correct: `list.replaceAll(String::toUpperCase);` without assignment.

---

**End of Part 2 - List Interface**

**Continue with:**
- **[Part 3: Set Interface →](CHAPTER-05-PART3-SET.md)** (HashSet, TreeSet, LinkedHashSet)
- **Part 4: Map Interface** (HashMap, TreeMap, LinkedHashMap)
- **Part 5: Queue/Deque** + Summary

