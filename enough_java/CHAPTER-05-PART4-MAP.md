# Chapter 5: Working with Arrays and Collections - PART 4: Map Interface

[← Part 3: Set](CHAPTER-05-PART3-SET.md) | [Back to Main Guide](README.md) | [Next: Part 5 - Queue/Deque →](CHAPTER-05-PART5-QUEUE.md)

---

## 4. Map Interface

**Exam Objective:** Use Map interface and implementations (HashMap, TreeMap, LinkedHashMap)

### What is Map?
Key-value pair collection where each key maps to exactly one value. Keys are unique (like Set), values can be duplicated. NOT part of Collection interface (separate hierarchy). Main implementations: HashMap (hash table), TreeMap (Red-Black tree), LinkedHashMap (hash table + linked list).

### When to Use

| Implementation | When to Use | Key Ordering | Performance |
|----------------|-------------|--------------|-------------|
| **HashMap** | Fast operations, no order needed | No order | O(1) avg |
| **TreeMap** | Need sorted keys, range operations | Sorted (natural/comparator) | O(log n) |
| **LinkedHashMap** | Need insertion/access order | Insertion or access order | O(1) avg |
| **Hashtable** | Legacy code only (synchronized) | No order | O(1) avg - DON'T USE |
| **ConcurrentHashMap** | Thread-safe, high concurrency | No order | O(1) avg |
| **EnumMap** | Keys are enum values | Enum declaration order | O(1) |
| **WeakHashMap** | Keys can be garbage collected | No order | O(1) avg |

### Why Use Map
- **Key-value association:** Natural representation of relationships
- **Fast lookup by key:** O(1) for HashMap, O(log n) for TreeMap
- **Unique keys:** Automatic duplicate key prevention (overwrites value)
- **Flexible values:** Values can be any type, including collections
- **Rich API:** 40+ methods including compute operations (Java 8+)
- **Null handling:** HashMap allows one null key + multiple null values

### Performance Characteristics

#### HashMap vs TreeMap vs LinkedHashMap - Complete Comparison

| Operation | HashMap | TreeMap | LinkedHashMap | Winner |
|-----------|---------|---------|---------------|--------|
| **put(K, V)** | O(1) avg | O(log n) | O(1) avg | HashMap/LinkedHashMap |
| **get(K)** | O(1) avg | O(log n) | O(1) avg | HashMap/LinkedHashMap |
| **remove(K)** | O(1) avg | O(log n) | O(1) avg | HashMap/LinkedHashMap |
| **containsKey(K)** | O(1) avg | O(log n) | O(1) avg | HashMap/LinkedHashMap |
| **containsValue(V)** | O(n) | O(n) | O(n) | Tie |
| **size()** | O(1) | O(1) | O(1) | Tie |
| **clear()** | O(n) | O(n) | O(n) | Tie |
| **keySet()** | O(1) create | O(1) create | O(1) create | Tie |
| **values()** | O(1) create | O(1) create | O(1) create | Tie |
| **entrySet()** | O(1) create | O(1) create | O(1) create | Tie |
| **firstKey()/lastKey()** | N/A | O(log n) | N/A | TreeMap only |
| **higherKey(K)** | N/A | O(log n) | N/A | TreeMap only |
| **Iteration order** | Random | Sorted by key | Insertion/access | Depends on need |
| **Memory per entry** | ~36 bytes | ~48 bytes | ~48 bytes | HashMap |
| **Null keys** | One null | No null | One null | HashMap/LinkedHashMap |
| **Null values** | Multiple | Multiple | Multiple | Tie |

**Worst-case scenarios:**
- HashMap: O(n) when all keys hash to same bucket (before Java 8), O(log n) after (tree bins)
- TreeMap: Always O(log n) guaranteed
- LinkedHashMap: O(n) same as HashMap worst case

#### Space Complexity

| Implementation | Space per entry | Total Space | Notes |
|----------------|-----------------|-------------|-------|
| **HashMap** | ~36 bytes (entry + overhead) | O(n) + unused buckets | Load factor affects space |
| **TreeMap** | ~48 bytes (node + pointers + color) | O(n) | Red-Black tree overhead |
| **LinkedHashMap** | ~48 bytes (entry + before/after) | O(n) + unused buckets | Combination overhead |

#### HashMap Internal Behavior

```java
// HashMap internal structure (simplified)
public class HashMap<K, V> {
    transient Node<K,V>[] table;             // Array of buckets
    int size;                                // Number of entries
    int threshold;                           // Resize threshold
    final float loadFactor;                  // Load factor (default 0.75)
    
    static class Node<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;                      // For collisions (chaining)
    }
    
    // Default capacity: 16
    // Default load factor: 0.75 (resize at 75% full)
    // Resize: doubles capacity (16 → 32 → 64 → 128...)
}

// Hash calculation
int hash = key.hashCode();
int bucket = (hash ^ (hash >>> 16)) & (table.length - 1);

// Java 8+: If bucket has >8 entries, converts to Red-Black tree (O(log n) worst case)
```

### Real-World Examples

#### Example 1: Student Registry (HashMap - Fast Lookup)
```java
public class StudentRegistry {
    // HashMap: fast O(1) lookups by student ID
    private Map<String, Student> students = new HashMap<>();
    
    public static class Student {
        String id;
        String name;
        int age;
        double gpa;
        
        public Student(String id, String name, int age, double gpa) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gpa = gpa;
        }
    }
    
    // Add or update student - O(1) average
    public void registerStudent(Student student) {
        Student old = students.put(student.id, student); // O(1)
        if (old == null) {
            System.out.println("Registered new student: " + student.name);
        } else {
            System.out.println("Updated student: " + student.name);
        }
    }
    
    // Get student by ID - O(1) average
    public Student getStudent(String studentId) {
        return students.get(studentId);              // O(1)
    }
    
    // Remove student - O(1) average
    public Student removeStudent(String studentId) {
        return students.remove(studentId);           // O(1)
    }
    
    // Check if student exists - O(1) average
    public boolean hasStudent(String studentId) {
        return students.containsKey(studentId);      // O(1)
    }
    
    // Get all student IDs - O(1) to get set, O(n) to iterate
    public Set<String> getAllStudentIds() {
        return new HashSet<>(students.keySet());     // Defensive copy
    }
    
    // Get all students - O(n)
    public Collection<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    // Get students by GPA threshold - O(n)
    public List<Student> getStudentsByGPA(double minGpa) {
        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.gpa >= minGpa) {
                result.add(student);
            }
        }
        return result;
    }
    
    // Count by age - O(n)
    public Map<Integer, Long> countByAge() {
        Map<Integer, Long> ageCounts = new HashMap<>();
        for (Student student : students.values()) {
            ageCounts.merge(student.age, 1L, Long::sum); // merge method!
        }
        return ageCounts;
    }
    
    // Get or create default - O(1)
    public Student getOrCreateDefault(String studentId) {
        return students.computeIfAbsent(studentId, id -> 
            new Student(id, "Unknown", 0, 0.0)
        );
    }
    
    // Update GPA - O(1)
    public void updateGPA(String studentId, double newGpa) {
        students.computeIfPresent(studentId, (id, student) -> {
            student.gpa = newGpa;
            return student;
        });
    }
}
```
**Why HashMap:** Student registry needs fast O(1) lookups by student ID. No ordering needed. HashMap provides best performance for get/put/remove operations.

#### Example 2: Dictionary with Sorted Words (TreeMap - Sorted Keys)
```java
public class Dictionary {
    // TreeMap: automatically maintains sorted order of words
    private TreeMap<String, String> definitions = new TreeMap<>();
    
    // Add word and definition - O(log n)
    public void addWord(String word, String definition) {
        String normalized = word.toLowerCase();
        definitions.put(normalized, definition);     // O(log n)
    }
    
    // Get definition - O(log n)
    public String getDefinition(String word) {
        return definitions.get(word.toLowerCase());  // O(log n)
    }
    
    // Get first word alphabetically - O(log n)
    public String getFirstWord() {
        return definitions.isEmpty() ? null : definitions.firstKey(); // O(log n)
    }
    
    // Get last word alphabetically - O(log n)
    public String getLastWord() {
        return definitions.isEmpty() ? null : definitions.lastKey(); // O(log n)
    }
    
    // Get words starting with prefix - O(log n + k) where k = result size
    public Map<String, String> getWordsStartingWith(String prefix) {
        String lowerPrefix = prefix.toLowerCase();
        // Find range of keys starting with prefix
        String start = lowerPrefix;
        String end = lowerPrefix + Character.MAX_VALUE;
        return new TreeMap<>(definitions.subMap(start, end));
    }
    
    // Get next word after given word - O(log n)
    public String getNextWord(String word) {
        return definitions.higherKey(word.toLowerCase()); // O(log n)
    }
    
    // Get previous word before given word - O(log n)
    public String getPreviousWord(String word) {
        return definitions.lowerKey(word.toLowerCase()); // O(log n)
    }
    
    // Get words in alphabetical range - O(log n + k)
    public Map<String, String> getWordsInRange(String from, String to) {
        return new TreeMap<>(definitions.subMap(
            from.toLowerCase(), 
            to.toLowerCase()
        ));
    }
    
    // Get all words in alphabetical order - O(n)
    public List<String> getAllWordsSorted() {
        return new ArrayList<>(definitions.keySet()); // Already sorted!
    }
    
    // Get words in reverse alphabetical order - O(n)
    public List<String> getAllWordsReversed() {
        return new ArrayList<>(definitions.descendingKeySet());
    }
    
    // Count words - O(1)
    public int getWordCount() {
        return definitions.size();
    }
}
```
**Why TreeMap:** Dictionary needs alphabetical ordering of words. TreeMap provides O(log n) operations with automatic sorting. Efficient range queries for words starting with prefix.

#### Example 3: LRU Cache (LinkedHashMap - Access Order)
```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;
    
    // Create LRU cache with access-order
    public LRUCache(int maxCapacity) {
        super(16, 0.75f, true);              // true = access-order (not insertion)
        this.maxCapacity = maxCapacity;
    }
    
    // Override to enable LRU eviction
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;         // Remove oldest when exceeding capacity
    }
    
    // get() automatically moves entry to end (most recently used)
    // put() automatically adds to end
    
    // Usage example
    public static void main(String[] args) {
        LRUCache<String, String> cache = new LRUCache<>(3);
        
        cache.put("1", "one");               // [1]
        cache.put("2", "two");               // [1, 2]
        cache.put("3", "three");             // [1, 2, 3]
        
        cache.get("1");                      // Access "1" - moves to end: [2, 3, 1]
        
        cache.put("4", "four");              // Exceeds capacity, removes "2": [3, 1, 4]
        
        System.out.println(cache.containsKey("2")); // false (evicted)
        System.out.println(cache);           // {3=three, 1=one, 4=four}
    }
}
```
**Why LinkedHashMap:** LRU cache needs to track access order to evict least recently used entries. LinkedHashMap with access-order mode automatically maintains this ordering with O(1) operations.

#### Example 4: Configuration Manager (LinkedHashMap - Insertion Order)
```java
public class ConfigurationManager {
    // LinkedHashMap: maintains insertion order for predictable iteration
    private LinkedHashMap<String, String> config = new LinkedHashMap<>();
    
    // Load configuration - maintains order
    public void loadConfig(List<String> lines) {
        for (String line : lines) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                config.put(parts[0].trim(), parts[1].trim());
            }
        }
    }
    
    // Get value - O(1)
    public String get(String key) {
        return config.get(key);
    }
    
    // Get with default - O(1)
    public String get(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    // Set value - O(1)
    public void set(String key, String value) {
        config.put(key, value);
    }
    
    // Save configuration - maintains insertion order
    public List<String> saveConfig() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            lines.add(entry.getKey() + "=" + entry.getValue());
        }
        return lines;
    }
    
    // Get all keys in insertion order - O(n)
    public List<String> getAllKeys() {
        return new ArrayList<>(config.keySet()); // Preserves order!
    }
}
```
**Why LinkedHashMap:** Configuration files need predictable ordering for human readability. LinkedHashMap maintains insertion order with O(1) operations.

#### Example 5: Word Frequency Counter (HashMap with Compute Methods)
```java
public class WordFrequencyCounter {
    private Map<String, Integer> frequencies = new HashMap<>();
    
    // Count word occurrence - O(1)
    public void addWord(String word) {
        String normalized = word.toLowerCase();
        
        // Method 1: merge (Java 8+) - most concise
        frequencies.merge(normalized, 1, Integer::sum); // O(1)
        
        // Method 2: compute (Java 8+)
        // frequencies.compute(normalized, (k, v) -> v == null ? 1 : v + 1);
        
        // Method 3: computeIfPresent + putIfAbsent
        // frequencies.putIfAbsent(normalized, 0);
        // frequencies.computeIfPresent(normalized, (k, v) -> v + 1);
        
        // Method 4: Traditional (verbose)
        // Integer count = frequencies.get(normalized);
        // frequencies.put(normalized, count == null ? 1 : count + 1);
    }
    
    // Add multiple words - O(n)
    public void addWords(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                frequencies.merge(word, 1, Integer::sum);
            }
        }
    }
    
    // Get frequency - O(1)
    public int getFrequency(String word) {
        return frequencies.getOrDefault(word.toLowerCase(), 0);
    }
    
    // Get most frequent words - O(n log k) where k = top count
    public List<Map.Entry<String, Integer>> getTopWords(int count) {
        return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(count)
            .collect(Collectors.toList());
    }
    
    // Get total word count - O(n)
    public int getTotalWords() {
        return frequencies.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
    
    // Get unique word count - O(1)
    public int getUniqueWordCount() {
        return frequencies.size();
    }
}
```
**Why HashMap with merge():** Word counting benefits from HashMap's O(1) operations and Java 8's `merge()` method, which handles null values elegantly and provides clean, concise code.

### HashMap - Complete Reference

#### Internal Structure
```java
// HashMap internal (simplified)
public class HashMap<K, V> {
    transient Node<K,V>[] table;             // Buckets array
    transient int size;                      // Number of entries
    int threshold;                           // Resize threshold = capacity × loadFactor
    final float loadFactor;                  // Default 0.75
    
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;                      // Linked list for collisions
        
        // Java 8+: If bucket has >8 entries, converts to TreeNode (Red-Black tree)
    }
    
    // Capacity must be power of 2: 16, 32, 64, 128, 256...
    // Default initial capacity: 16
    // Resize: newCapacity = oldCapacity × 2
}
```

#### Constructors

```java
// 1. Default constructor - capacity 16, load factor 0.75
Map<String, Integer> map1 = new HashMap<>();

// 2. With initial capacity
Map<String, Integer> map2 = new HashMap<>(100);

// 3. With capacity and load factor
Map<String, Integer> map3 = new HashMap<>(100, 0.5f); // Resize at 50% full

// 4. From another map
Map<String, Integer> source = Map.of("A", 1, "B", 2);
Map<String, Integer> map4 = new HashMap<>(source);

// Capacity calculation for known size
int size = 1000;
int capacity = (int) Math.ceil(size / 0.75);  // Avoid resize
Map<String, Integer> optimized = new HashMap<>(capacity);
```

#### Basic Operations

```java
Map<String, Integer> map = new HashMap<>();

// put(K, V) - add or update - O(1) average
Integer old1 = map.put("A", 1);              // null (new key), map={A=1}
Integer old2 = map.put("B", 2);              // null, map={A=1, B=2}
Integer old3 = map.put("A", 10);             // 1 (old value), map={A=10, B=2}

// get(K) - O(1) average
Integer val1 = map.get("A");                 // 10
Integer val2 = map.get("X");                 // null (key not found)

// getOrDefault(K, defaultValue) - Java 8 - O(1) average
Integer val3 = map.getOrDefault("X", 0);     // 0 (default)
Integer val4 = map.getOrDefault("A", 0);     // 10 (found)

// remove(K) - O(1) average
Integer removed1 = map.remove("B");          // 2, map={A=10}
Integer removed2 = map.remove("X");          // null (not found)

// remove(K, V) - remove only if matches value - Java 8 - O(1) average
map.put("C", 3);                             // {A=10, C=3}
boolean removed3 = map.remove("C", 3);       // true, removed
boolean removed4 = map.remove("A", 5);       // false, value doesn't match

// containsKey(K) - O(1) average
boolean hasA = map.containsKey("A");         // true
boolean hasX = map.containsKey("X");         // false

// containsValue(V) - O(n) - iterates all entries!
map.putAll(Map.of("B", 2, "C", 3));
boolean has10 = map.containsValue(10);       // true
boolean has99 = map.containsValue(99);       // false

// size() - O(1)
int size = map.size();                       // 3

// isEmpty() - O(1)
boolean empty = map.isEmpty();               // false

// clear() - O(n)
map.clear();                                 // {}
```

#### Compute Methods (Java 8+)

```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);

// compute(K, BiFunction) - compute value based on key and old value - O(1) avg
map.compute("A", (k, v) -> v == null ? 1 : v + 1);  // {A=2, B=2}
map.compute("C", (k, v) -> v == null ? 1 : v + 1);  // {A=2, B=2, C=1}

// computeIfAbsent(K, Function) - compute only if absent - O(1) avg
map.computeIfAbsent("D", k -> k.length());   // {A=2, B=2, C=1, D=1}
map.computeIfAbsent("A", k -> 999);          // No change (A exists)

// computeIfPresent(K, BiFunction) - compute only if present - O(1) avg
map.computeIfPresent("A", (k, v) -> v * 10); // {A=20, B=2, C=1, D=1}
map.computeIfPresent("X", (k, v) -> 999);    // No change (X absent)

// Return null from function to REMOVE entry
map.computeIfPresent("B", (k, v) -> null);   // {A=20, C=1, D=1} - B removed

// merge(K, V, BiFunction) - merge new value with existing - O(1) avg
map.merge("A", 5, Integer::sum);             // A: 20 + 5 = 25
map.merge("E", 5, Integer::sum);             // E: null + 5 = 5 (new entry)
// {A=25, C=1, D=1, E=5}

// Common use: word counting
Map<String, Integer> counts = new HashMap<>();
String[] words = {"a", "b", "a", "c", "b", "a"};
for (String word : words) {
    counts.merge(word, 1, Integer::sum);     // Increment or create with 1
}
// counts = {a=3, b=2, c=1}

// putIfAbsent(K, V) - put only if absent - Java 8 - O(1) avg
map.putIfAbsent("F", 6);                     // Added, {A=25, C=1, D=1, E=5, F=6}
map.putIfAbsent("A", 999);                   // Not added (A exists)

// replace(K, V) - replace only if key exists - Java 8 - O(1) avg
Integer old1 = map.replace("A", 100);        // 25 (old value), A=100
Integer old2 = map.replace("X", 999);        // null (X not found, no change)

// replace(K, oldV, newV) - replace only if matches - Java 8 - O(1) avg
boolean replaced1 = map.replace("A", 100, 101); // true, A=101
boolean replaced2 = map.replace("A", 100, 102); // false, value doesn't match
```

#### Bulk Operations

```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);

// putAll(Map) - O(m) where m = source map size
map.putAll(Map.of("C", 3, "D", 4));          // {A=1, B=2, C=3, D=4}

// replaceAll(BiFunction) - Java 8 - O(n)
map.replaceAll((k, v) -> v * 10);            // {A=10, B=20, C=30, D=40}

// forEach(BiConsumer) - Java 8 - O(n)
map.forEach((k, v) -> System.out.println(k + "=" + v));
```

#### View Collections

```java
Map<String, Integer> map = new HashMap<>();
map.putAll(Map.of("A", 1, "B", 2, "C", 3));

// keySet() - returns Set view - O(1) to create
Set<String> keys = map.keySet();             // [A, B, C] (unordered)
// View is BACKED by map - changes affect both
keys.remove("A");                            // Removes from map too! {B=2, C=3}

// values() - returns Collection view - O(1) to create
Collection<Integer> values = map.values();   // [2, 3]
// values.remove(2);                         // Removes B=2 from map

// entrySet() - returns Set<Entry> view - O(1) to create
Set<Map.Entry<String, Integer>> entries = map.entrySet();
for (Map.Entry<String, Integer> entry : entries) {
    System.out.println(entry.getKey() + "=" + entry.getValue());
    // Can modify value through entry
    entry.setValue(entry.getValue() * 10);   // Modifies map!
}
// map = {B=20, C=30}

// Entry interface methods
Map.Entry<String, Integer> entry = entries.iterator().next();
String key = entry.getKey();                 // Key
Integer value = entry.getValue();            // Value
entry.setValue(100);                         // Update value in map
```


### TreeMap - Complete Reference

#### Internal Structure
```java
// TreeMap is backed by Red-Black tree
public class TreeMap<K, V> extends AbstractMap<K, V>
        implements NavigableMap<K, V> {
    private transient Entry<K,V> root;       // Root node
    private int size;
    
    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;                     // Left child
        Entry<K,V> right;                    // Right child
        Entry<K,V> parent;                   // Parent node
        boolean color = BLACK;               // Red-Black tree color
    }
}

// Red-Black tree guarantees O(log n) for all operations
// Keys are always sorted (natural order or comparator)
```

#### Constructors

```java
// 1. Default - natural ordering
TreeMap<Integer, String> map1 = new TreeMap<>();
map1.put(3, "three");
map1.put(1, "one");
map1.put(2, "two");
System.out.println(map1);                    // {1=one, 2=two, 3=three} - sorted!

// 2. With Comparator - custom ordering
TreeMap<String, Integer> map2 = new TreeMap<>(Comparator.reverseOrder());
map2.putAll(Map.of("A", 1, "B", 2, "C", 3));
System.out.println(map2);                    // {C=3, B=2, A=1} - reverse

// 3. From another map - natural ordering
TreeMap<String, Integer> map3 = new TreeMap<>(Map.of("B", 2, "A", 1));
// {A=1, B=2} - sorted

// 4. From SortedMap - same ordering
TreeMap<String, Integer> map4 = new TreeMap<>(map2);

// Custom comparator - sort by key length
TreeMap<String, Integer> byLength = new TreeMap<>((k1, k2) -> {
    int lenCompare = Integer.compare(k1.length(), k2.length());
    return lenCompare != 0 ? lenCompare : k1.compareTo(k2);
});
byLength.putAll(Map.of("a", 1, "bbb", 2, "cc", 3));
// {a=1, cc=3, bbb=2} - sorted by length
```

#### Basic Operations (Like HashMap but O(log n))

```java
TreeMap<String, Integer> map = new TreeMap<>();

// put(K, V) - O(log n)
map.put("B", 2);                             // {B=2}
map.put("A", 1);                             // {A=1, B=2} - automatically sorted
map.put("C", 3);                             // {A=1, B=2, C=3}

// get(K) - O(log n)
Integer val = map.get("B");                  // 2

// remove(K) - O(log n)
map.remove("B");                             // {A=1, C=3}

// containsKey(K) - O(log n)
boolean has = map.containsKey("A");          // true

// size(), isEmpty(), clear() - same as HashMap
```

#### NavigableMap Methods (TreeMap-Specific)

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.putAll(Map.of(1, "one", 3, "three", 5, "five", 7, "seven", 9, "nine"));

// firstKey() / lastKey() - O(log n)
Integer first = map.firstKey();              // 1
Integer last = map.lastKey();                // 9

// firstEntry() / lastEntry() - O(log n)
Map.Entry<Integer, String> firstEntry = map.firstEntry();
System.out.println(firstEntry.getKey() + "=" + firstEntry.getValue()); // 1=one

// lowerKey(K) - greatest key < given - O(log n)
Integer lower = map.lowerKey(5);             // 3
Integer lower2 = map.lowerKey(1);            // null (no lower)

// higherKey(K) - least key > given - O(log n)
Integer higher = map.higherKey(5);           // 7
Integer higher2 = map.higherKey(9);          // null (no higher)

// floorKey(K) - greatest key <= given - O(log n)
Integer floor1 = map.floorKey(5);            // 5 (exact match)
Integer floor2 = map.floorKey(4);            // 3 (next lower)

// ceilingKey(K) - least key >= given - O(log n)
Integer ceiling1 = map.ceilingKey(5);        // 5 (exact match)
Integer ceiling2 = map.ceilingKey(6);        // 7 (next higher)

// lowerEntry(), higherEntry(), floorEntry(), ceilingEntry() - same but return Entry
Map.Entry<Integer, String> entry = map.higherEntry(5);
System.out.println(entry);                   // 7=seven

// pollFirstEntry() - remove and return lowest - O(log n)
Map.Entry<Integer, String> removed1 = map.pollFirstEntry();
// removed1 = 1=one, map = {3=three, 5=five, 7=seven, 9=nine}

// pollLastEntry() - remove and return highest - O(log n)
Map.Entry<Integer, String> removed2 = map.pollLastEntry();
// removed2 = 9=nine, map = {3=three, 5=five, 7=seven}

// descendingMap() - reverse view - O(1) to create
NavigableMap<Integer, String> desc = map.descendingMap();
System.out.println(desc);                    // {7=seven, 5=five, 3=three}
// Changes to desc affect original!

// descendingKeySet() - reverse key set - O(1)
NavigableSet<Integer> descKeys = map.descendingKeySet();
System.out.println(descKeys);                // [7, 5, 3]
```

#### Submap Views

```java
TreeMap<Integer, String> map = new TreeMap<>();
for (int i = 1; i <= 10; i++) {
    map.put(i, "value" + i);
}
// {1=value1, 2=value2, ..., 10=value10}

// headMap(K) - keys < toKey - O(log n) to create
SortedMap<Integer, String> head = map.headMap(5);
System.out.println(head);                    // {1=value1, 2=value2, 3=value3, 4=value4}

// tailMap(K) - keys >= fromKey - O(log n) to create
SortedMap<Integer, String> tail = map.tailMap(7);
System.out.println(tail);                    // {7=value7, 8=value8, 9=value9, 10=value10}

// subMap(from, to) - keys in [from, to) - O(log n) to create
SortedMap<Integer, String> sub = map.subMap(4, 8);
System.out.println(sub);                     // {4=value4, 5=value5, 6=value6, 7=value7}

// Navigable versions with inclusive/exclusive control
NavigableMap<Integer, String> head2 = map.headMap(5, true);  // Include 5
NavigableMap<Integer, String> tail2 = map.tailMap(7, false); // Exclude 7
NavigableMap<Integer, String> sub2 = map.subMap(4, true, 8, false);

// Views are BACKED by original map!
head.put(3, "THREE");                        // Modifies original map
// head.put(10, "TEN");                      // ❌ IllegalArgumentException - out of range

// Original map modifications can invalidate views
map.put(15, "value15");
// head.size();                              // ❌ ConcurrentModificationException
```

### LinkedHashMap - Complete Reference

```java
// LinkedHashMap maintains insertion order OR access order
// Backed by hash table + doubly-linked list

// Insertion-order mode (default)
LinkedHashMap<String, Integer> insertion = new LinkedHashMap<>();
insertion.put("C", 3);
insertion.put("A", 1);
insertion.put("B", 2);
System.out.println(insertion);               // {C=3, A=1, B=2} - insertion order

// Access-order mode (for LRU cache)
LinkedHashMap<String, Integer> access = new LinkedHashMap<>(16, 0.75f, true);
access.put("A", 1);
access.put("B", 2);
access.put("C", 3);
access.get("A");                             // Access A - moves to end
System.out.println(access);                  // {B=2, C=3, A=1} - A moved to end

// All HashMap operations work with O(1) average
// Iteration is O(n) and follows insertion/access order
for (String key : insertion.keySet()) {
    System.out.print(key + " ");             // C A B (insertion order)
}

// removeEldestEntry() - override for LRU eviction
class LimitedMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    
    LimitedMap(int maxSize) {
        super(16, 0.75f, true);              // Access-order
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;             // Auto-remove oldest when exceeding size
    }
}
```

### Map.of() and Map.ofEntries() (Java 9+)

```java
// Map.of() - immutable map - up to 10 entries
Map<String, Integer> map1 = Map.of("A", 1, "B", 2);
// map1.put("C", 3);                         // ❌ UnsupportedOperationException

// Map.ofEntries() - immutable map - any number of entries
Map<String, Integer> map2 = Map.ofEntries(
    Map.entry("A", 1),
    Map.entry("B", 2),
    Map.entry("C", 3)
);

// Constraints:
// - Immutable (cannot add/remove/modify)
// - No null keys or values
// - No duplicate keys (throws IllegalArgumentException)
// - Iteration order undefined

// Map.of("A", null);                        // ❌ NullPointerException
// Map.of("A", 1, "A", 2);                   // ❌ IllegalArgumentException

// For mutable copy
Map<String, Integer> mutable = new HashMap<>(Map.of("A", 1, "B", 2));
```

### Exam Traps

- ❌ **TRAP 1:** TreeMap requires Comparable keys or Comparator
  ```java
  class Person { String name; }
  
  Map<Person, String> map = new TreeMap<>();
  map.put(new Person(), "value");            // ❌ ClassCastException
  // Person doesn't implement Comparable!
  
  // Fix: provide Comparator
  Map<Person, String> map2 = new TreeMap<>((p1, p2) -> 
      p1.name.compareTo(p2.name)
  );
  ```

- ❌ **TRAP 2:** Modifying key object after insertion breaks HashMap
  ```java
  class MutableKey {
      int value;
      @Override
      public int hashCode() { return value; }
      @Override
      public boolean equals(Object o) {
          return o instanceof MutableKey && ((MutableKey)o).value == value;
      }
  }
  
  MutableKey key = new MutableKey();
  key.value = 10;
  Map<MutableKey, String> map = new HashMap<>();
  map.put(key, "data");
  
  key.value = 20;                            // Modified after insertion!
  map.get(key);                              // ❌ null! Can't find it
  // HashMap looks in wrong bucket
  ```

- ❌ **TRAP 3:** Map.of() doesn't allow null keys or values
  ```java
  Map<String, Integer> map = Map.of("A", 1, "B", null); // ❌ NullPointerException
  Map<String, Integer> map2 = Map.of(null, 1);          // ❌ NullPointerException
  
  // HashMap allows one null key + multiple null values
  Map<String, Integer> hashMap = new HashMap<>();
  hashMap.put(null, 1);                      // ✅ OK
  hashMap.put("A", null);                    // ✅ OK
  ```

- ❌ **TRAP 4:** Map.of() doesn't allow duplicate keys
  ```java
  Map<String, Integer> map = Map.of("A", 1, "B", 2, "A", 3);
  // ❌ IllegalArgumentException - duplicate key
  ```

- ❌ **TRAP 5:** TreeMap uses compareTo(), not equals()
  ```java
  class Person implements Comparable<Person> {
      String name;
      int age;
      
      @Override
      public int compareTo(Person other) {
          return name.compareTo(other.name);  // Only compares name
      }
  }
  
  TreeMap<Person, String> map = new TreeMap<>();
  Person p1 = new Person(); p1.name = "Alice"; p1.age = 25;
  Person p2 = new Person(); p2.name = "Alice"; p2.age = 30;
  
  map.put(p1, "first");
  map.put(p2, "second");                     // Overwrites! Same name = same key
  System.out.println(map.size());            // 1, not 2
  ```

- ❌ **TRAP 6:** put() returns old value, not new value
  ```java
  Map<String, Integer> map = new HashMap<>();
  Integer old1 = map.put("A", 1);            // null (new key)
  Integer old2 = map.put("A", 10);           // 1 (old value)
  Integer old3 = map.put("B", 2);            // null (new key)
  ```

- ❌ **TRAP 7:** get() returns null for missing keys AND null values
  ```java
  Map<String, Integer> map = new HashMap<>();
  map.put("A", null);                        // Null value
  
  Integer val1 = map.get("A");               // null (null value)
  Integer val2 = map.get("X");               // null (missing key)
  
  // Can't distinguish! Use containsKey()
  boolean hasA = map.containsKey("A");       // true (key exists)
  boolean hasX = map.containsKey("X");       // false (key missing)
  ```

- ❌ **TRAP 8:** keySet(), values(), entrySet() are VIEWS, not copies
  ```java
  Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2, "C", 3));
  Set<String> keys = map.keySet();
  
  keys.remove("A");                          // Removes from map too!
  // map = {B=2, C=3}
  
  // Create copy if you need independence
  Set<String> keysCopy = new HashSet<>(map.keySet()); // ✅ Independent
  ```

- ❌ **TRAP 9:** TreeMap submap views are backed by original
  ```java
  TreeMap<Integer, String> map = new TreeMap<>();
  for (int i = 1; i <= 10; i++) map.put(i, "v" + i);
  
  SortedMap<Integer, String> sub = map.subMap(3, 7);
  sub.put(4, "FOUR");                        // Modifies original!
  // sub.put(10, "TEN");                     // ❌ IllegalArgumentException - out of range
  
  map.put(15, "v15");                        // Invalidates submap!
  // sub.size();                             // ❌ ConcurrentModificationException
  ```

- ❌ **TRAP 10:** containsValue() is O(n), not O(1)
  ```java
  Map<String, Integer> map = new HashMap<>();
  for (int i = 0; i < 100000; i++) {
      map.put("key" + i, i);
  }
  
  boolean has = map.containsKey("key50000");  // O(1) - fast
  boolean hasVal = map.containsValue(50000);  // O(n) - slow! Scans all values
  ```

- ❌ **TRAP 11:** HashMap allows null key, TreeMap doesn't
  ```java
  Map<String, Integer> hashMap = new HashMap<>();
  hashMap.put(null, 1);                      // ✅ One null key allowed
  
  Map<String, Integer> treeMap = new TreeMap<>();
  treeMap.put(null, 1);                      // ❌ NullPointerException
  // Can't compare null with compareTo()
  ```

- ❌ **TRAP 12:** compute/merge functions that return null REMOVE entry
  ```java
  Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2));
  
  map.compute("A", (k, v) -> null);          // Removes A!
  map.computeIfPresent("B", (k, v) -> null); // Removes B!
  // map = {}
  ```

- ❌ **TRAP 13:** merge() third parameter is for EXISTING values only
  ```java
  Map<String, Integer> map = new HashMap<>();
  
  map.merge("A", 5, Integer::sum);           // A doesn't exist
  // Result: A=5 (uses second parameter, NOT remapping function)
  
  map.merge("A", 3, Integer::sum);           // A exists (value=5)
  // Result: A=8 (5 + 3, uses remapping function)
  ```

- ❌ **TRAP 14:** putIfAbsent() vs computeIfAbsent() behavior
  ```java
  Map<String, List<Integer>> map = new HashMap<>();
  
  // putIfAbsent - evaluates value even if key exists
  map.put("A", new ArrayList<>());
  map.putIfAbsent("A", new ArrayList<>());   // Creates unused ArrayList
  
  // computeIfAbsent - only computes if needed
  map.computeIfAbsent("A", k -> new ArrayList<>()); // Doesn't create ArrayList
  
  // computeIfAbsent is lazy, putIfAbsent is eager
  ```

- ❌ **TRAP 15:** HashMap iteration order is unpredictable
  ```java
  Map<Integer, String> map = new HashMap<>();
  map.put(1, "one");
  map.put(2, "two");
  map.put(3, "three");
  
  for (Integer key : map.keySet()) {
      System.out.print(key + " ");           // Could be: 1 2 3, or 2 1 3, etc.
  }
  
  // Use LinkedHashMap for predictable order
  ```

---

## Exam Questions - Map Interface

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **Map questions:** ~5-7 questions (10-14% of exam)

---

### Question 1: Map duplicates
```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);
map.put("A", 3);
System.out.println(map.size());
```
**What is the output?**  
A. 1  
B. 2  
C. 3  
D. Compilation error  

**Answer:** B  
**Explanation:** Maps don't allow duplicate keys. The second `put("A", 3)` overwrites the first value. Map contains {A=3, B=2}, size = 2.

---

### Question 2: TreeMap ordering
```java
Map<Integer, String> map = new TreeMap<>();
map.put(5, "five");
map.put(2, "two");
map.put(8, "eight");
System.out.println(map.keySet());
```
**What is the output?**  
A. [5, 2, 8]  
B. [2, 5, 8]  
C. [8, 5, 2]  
D. Undefined order  

**Answer:** B  
**Explanation:** TreeMap maintains keys in sorted (natural) order. Keys are sorted numerically: [2, 5, 8].

---

### Question 3: TreeMap requires Comparable
```java
class Person { String name; }

Map<Person, String> map = new TreeMap<>();
map.put(new Person(), "value");
```
**What is the result?**  
A. Map contains one entry  
B. Compilation error  
C. ClassCastException  
D. NullPointerException  

**Answer:** C  
**Explanation:** TreeMap requires keys to be Comparable or provide a Comparator. Person doesn't implement Comparable, so ClassCastException is thrown when trying to compare keys.

---

### Question 4: Map.of() with null
```java
Map<String, Integer> map = Map.of("A", 1, "B", null);
```
**What is the result?**  
A. map = {A=1, B=null}  
B. map = {A=1}  
C. NullPointerException  
D. Compilation error  

**Answer:** C  
**Explanation:** `Map.of()` doesn't allow null keys or values and throws NullPointerException. Use `new HashMap<>()` if you need null values.

---

### Question 5: put() return value
```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
Integer result = map.put("A", 10);
System.out.println(result);
```
**What is the output?**  
A. 1  
B. 10  
C. null  
D. Compilation error  

**Answer:** A  
**Explanation:** `put()` returns the OLD value associated with the key (or null if it was a new key). First put stores A=1, second put updates to A=10 and returns the old value 1.

---

### Question 6: Modifying key after insertion
```java
class MutableKey {
    int value;
    public int hashCode() { return value; }
    public boolean equals(Object o) {
        return o instanceof MutableKey && ((MutableKey)o).value == value;
    }
}

MutableKey key = new MutableKey();
key.value = 10;
Map<MutableKey, String> map = new HashMap<>();
map.put(key, "data");
key.value = 20;
System.out.println(map.get(key));
```
**What is the output?**  
A. "data"  
B. null  
C. Compilation error  
D. Exception  

**Answer:** B  
**Explanation:** Modifying a mutable key's hash-affecting fields after insertion breaks HashMap. The map looks in the wrong bucket (for hashCode 20, but key is stored in bucket for hashCode 10), returns null.

---

### Question 7: LinkedHashMap ordering
```java
Map<Integer, String> map = new LinkedHashMap<>();
map.put(5, "five");
map.put(2, "two");
map.put(8, "eight");
System.out.println(map.keySet());
```
**What is the output?**  
A. [2, 5, 8]  
B. [5, 2, 8]  
C. [8, 5, 2]  
D. Undefined order  

**Answer:** B  
**Explanation:** LinkedHashMap maintains insertion order. Keys are iterated in the order they were inserted: [5, 2, 8].

---

### Question 8: keySet() is a view
```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);
map.put("B", 2);
Set<String> keys = map.keySet();
keys.remove("A");
System.out.println(map.size());
```
**What is the output?**  
A. 0  
B. 1  
C. 2  
D. UnsupportedOperationException  

**Answer:** B  
**Explanation:** `keySet()` returns a VIEW backed by the original map, not a copy. Removing from the keySet removes from the map. After removing "A", map = {B=2}, size = 1.

---

### Question 9: merge() behavior
```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 5);
map.merge("A", 3, Integer::sum);
map.merge("B", 7, Integer::sum);
System.out.println(map);
```
**What is the output?**  
A. {A=3, B=7}  
B. {A=8, B=7}  
C. {A=5, B=7}  
D. Compilation error  

**Answer:** B  
**Explanation:** `merge()` for existing key "A" uses remapping function: 5 + 3 = 8. For new key "B", uses the provided value 7 (no remapping). Result: {A=8, B=7}.

---

### Question 10: TreeMap null key
```java
Map<String, Integer> map = new TreeMap<>();
map.put(null, 1);
```
**What is the result?**  
A. map = {null=1}  
B. NullPointerException  
C. IllegalArgumentException  
D. Compilation error  

**Answer:** B  
**Explanation:** TreeMap doesn't allow null keys because it needs to compare keys using `compareTo()`, and you can't compare null. HashMap allows one null key, but TreeMap throws NullPointerException.

---

**End of Part 4 - Map Interface**

**Continue with:**
- **[Part 5: Queue/Deque + Complete Summary →](CHAPTER-05-PART5-QUEUE.md)**

