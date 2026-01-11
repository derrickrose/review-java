# Chapter 5: Working with Arrays and Collections - PART 3: Set Interface

[← Part 2: List](CHAPTER-05-PART2-LIST.md) | [Back to Main Guide](README.md) | [Next: Part 4 - Map →](CHAPTER-05-PART4-MAP.md)

---

## 3. Set Interface

**Exam Objective:** Use Set interface and implementations (HashSet, TreeSet, LinkedHashSet)

### What is Set?
Unordered collection (except LinkedHashSet/TreeSet) that contains NO DUPLICATES. Elements are unique based on equals()/hashCode() contract. Part of java.util.Collection framework. Main implementations: HashSet (hash table), TreeSet (Red-Black tree), LinkedHashSet (hash table + linked list).

### When to Use

| Implementation | When to Use | Ordering | Performance |
|----------------|-------------|----------|-------------|
| **HashSet** | Fast operations, no order needed | No order | O(1) avg |
| **TreeSet** | Need sorted order, range operations | Sorted (natural/comparator) | O(log n) |
| **LinkedHashSet** | Need insertion order maintained | Insertion order | O(1) avg |
| **EnumSet** | Elements are enum values | Enum declaration order | O(1) |
| **CopyOnWriteArraySet** | Thread-safe, read-heavy, rare writes | Insertion order | O(n) |

### Why Use Set
- **Automatic duplicate prevention:** No need to manually check
- **Fast membership testing:** O(1) for HashSet, O(log n) for TreeSet
- **Mathematical set operations:** Union, intersection, difference
- **Clean code:** Set operations are declarative
- **Memory efficient:** No duplicate storage overhead

### Performance Characteristics

#### HashSet vs TreeSet vs LinkedHashSet - Complete Comparison

| Operation | HashSet | TreeSet | LinkedHashSet | Winner |
|-----------|---------|---------|---------------|--------|
| **add(E)** | O(1) avg | O(log n) | O(1) avg | HashSet/LinkedHashSet |
| **remove(Object)** | O(1) avg | O(log n) | O(1) avg | HashSet/LinkedHashSet |
| **contains(Object)** | O(1) avg | O(log n) | O(1) avg | HashSet/LinkedHashSet |
| **size()** | O(1) | O(1) | O(1) | Tie |
| **clear()** | O(n) | O(n) | O(n) | Tie |
| **iterator()** | O(capacity + n) | O(n) | O(n) | TreeSet/LinkedHashSet |
| **first()/last()** | N/A | O(1) | N/A | TreeSet only |
| **higher()/lower()** | N/A | O(log n) | N/A | TreeSet only |
| **Iteration order** | Random | Sorted | Insertion | Depends on need |
| **Memory per element** | ~32 bytes | ~40 bytes | ~40 bytes | HashSet |
| **Null elements** | One null | No null | One null | HashSet/LinkedHashSet |
| **Fail-fast iterator** | Yes | Yes | Yes | Tie |

**Worst-case scenarios:**
- HashSet: O(n) when all elements hash to same bucket (poor hash function)
- TreeSet: Always O(log n) guaranteed
- LinkedHashSet: O(n) same as HashSet worst case

#### Space Complexity

| Implementation | Space per element | Total Space | Notes |
|----------------|------------------|-------------|-------|
| **HashSet** | ~32 bytes (entry + overhead) | O(n) + unused buckets | Load factor affects space |
| **TreeSet** | ~40 bytes (node + 2 pointers + color bit) | O(n) | Red-Black tree overhead |
| **LinkedHashSet** | ~40 bytes (entry + before/after pointers) | O(n) + unused buckets | Combination overhead |

#### HashSet Internal Behavior

```java
// HashSet is backed by HashMap
// Default initial capacity: 16
// Default load factor: 0.75 (resizes when 75% full)
Set<String> set = new HashSet<>();              // capacity=16, threshold=12

// Add 12 elements - no resize
// Add 13th element - resize to 32, threshold=24

// Specify capacity to avoid resizes
Set<String> optimized = new HashSet<>(1000);    // Pre-allocate

// With load factor
Set<String> custom = new HashSet<>(1000, 0.5f); // Resize at 50% full
```

### Real-World Examples

#### Example 1: Online Users Tracker (HashSet - Fast Lookup)
```java
public class OnlineUsersTracker {
    // HashSet: fast add/remove/contains, no order needed
    private Set<String> activeUsers = new HashSet<>();
    private Map<String, Instant> loginTimes = new HashMap<>();
    
    // User login - O(1) average
    public void userLogin(String username) {
        boolean isNew = activeUsers.add(username);  // O(1)
        if (isNew) {
            loginTimes.put(username, Instant.now());
            System.out.println("New login: " + username);
        } else {
            System.out.println("Already online: " + username);
        }
        System.out.println("Active users: " + activeUsers.size());
    }
    
    // User logout - O(1) average
    public void userLogout(String username) {
        boolean wasOnline = activeUsers.remove(username); // O(1)
        if (wasOnline) {
            loginTimes.remove(username);
            System.out.println("Logged out: " + username);
        }
    }
    
    // Check if user is online - O(1) average
    public boolean isUserOnline(String username) {
        return activeUsers.contains(username);         // O(1)
    }
    
    // Get all active users - O(n)
    public Set<String> getActiveUsers() {
        return new HashSet<>(activeUsers);             // Defensive copy
    }
    
    // Get users logged in for more than duration - O(n)
    public Set<String> getUsersOnlineLongerThan(Duration duration) {
        Set<String> result = new HashSet<>();
        Instant threshold = Instant.now().minus(duration);
        
        for (String user : activeUsers) {
            Instant loginTime = loginTimes.get(user);
            if (loginTime.isBefore(threshold)) {
                result.add(user);
            }
        }
        return result;
    }
    
    // Get active user count - O(1)
    public int getActiveUserCount() {
        return activeUsers.size();
    }
}
```
**Why HashSet:** Online user tracking needs fast O(1) lookups to check if user is online. Duplicates automatically prevented (same user can't be "online" twice). No ordering needed.

#### Example 2: Leaderboard (TreeSet - Sorted Order)
```java
public class Leaderboard {
    // TreeSet: automatically maintains sorted order
    private TreeSet<Score> topScores;
    private static final int MAX_SCORES = 10;
    
    // Score class with natural ordering
    public static class Score implements Comparable<Score> {
        private String playerName;
        private int points;
        private Instant timestamp;
        
        public Score(String playerName, int points) {
            this.playerName = playerName;
            this.points = points;
            this.timestamp = Instant.now();
        }
        
        @Override
        public int compareTo(Score other) {
            // Higher points first, then earlier timestamp
            int pointsCompare = Integer.compare(other.points, this.points);
            if (pointsCompare != 0) return pointsCompare;
            return this.timestamp.compareTo(other.timestamp);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Score)) return false;
            Score score = (Score) o;
            return points == score.points && 
                   playerName.equals(score.playerName);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(playerName, points);
        }
    }
    
    public Leaderboard() {
        // TreeSet automatically sorts by Score.compareTo()
        topScores = new TreeSet<>();
    }
    
    // Add score - O(log n)
    public void addScore(Score score) {
        topScores.add(score);                        // O(log n)
        
        // Keep only top 10
        while (topScores.size() > MAX_SCORES) {
            topScores.pollLast();                    // O(log n) - remove lowest
        }
    }
    
    // Get top score - O(1)
    public Score getTopScore() {
        return topScores.isEmpty() ? null : topScores.first(); // O(1)
    }
    
    // Get all top scores - O(n)
    public List<Score> getTopScores() {
        return new ArrayList<>(topScores);           // Maintains order
    }
    
    // Get scores above threshold - O(log n) + O(k) where k = matching scores
    public Set<Score> getScoresAbove(int threshold) {
        Score pivot = new Score("", threshold);
        return topScores.headSet(pivot);             // O(log n) to find
    }
    
    // Get rank of score - O(log n) + O(k)
    public int getRank(Score score) {
        int rank = 1;
        for (Score s : topScores) {
            if (s.equals(score)) return rank;
            rank++;
        }
        return -1; // Not in top scores
    }
    
    // Check if score qualifies for top 10 - O(1)
    public boolean wouldQualify(int points) {
        if (topScores.size() < MAX_SCORES) return true;
        return points > topScores.last().getPoints(); // O(1)
    }
}
```
**Why TreeSet:** Leaderboard needs automatic sorting by score. TreeSet maintains sorted order on every insertion (O(log n)). Can efficiently get top score (O(1)), remove lowest score (O(log n)), and get scores in range.

#### Example 3: Tag System (LinkedHashSet - Insertion Order)
```java
public class ArticleTagSystem {
    // LinkedHashSet: maintains insertion order, no duplicates
    private LinkedHashSet<String> tags = new LinkedHashSet<>();
    private static final int MAX_TAGS = 20;
    
    // Add tag - O(1) average
    public boolean addTag(String tag) {
        if (tags.size() >= MAX_TAGS) {
            System.out.println("Maximum tags reached");
            return false;
        }
        
        String normalized = tag.toLowerCase().trim();
        boolean added = tags.add(normalized);        // O(1)
        
        if (added) {
            System.out.println("Tag added: " + normalized);
        } else {
            System.out.println("Tag already exists: " + normalized);
        }
        return added;
    }
    
    // Remove tag - O(1) average
    public boolean removeTag(String tag) {
        String normalized = tag.toLowerCase().trim();
        return tags.remove(normalized);              // O(1)
    }
    
    // Has tag - O(1) average
    public boolean hasTag(String tag) {
        String normalized = tag.toLowerCase().trim();
        return tags.contains(normalized);            // O(1)
    }
    
    // Get tags in insertion order - O(n)
    public List<String> getTags() {
        return new ArrayList<>(tags);                // Preserves order!
    }
    
    // Get tags as comma-separated string - O(n)
    public String getTagsAsString() {
        return String.join(", ", tags);              // Insertion order
    }
    
    // Move tag to end (re-insert) - O(1)
    public void promoteTag(String tag) {
        String normalized = tag.toLowerCase().trim();
        if (tags.remove(normalized)) {               // O(1)
            tags.add(normalized);                    // O(1) - adds to end
        }
    }
    
    // Replace tags - O(n)
    public void setTags(Collection<String> newTags) {
        tags.clear();
        for (String tag : newTags) {
            if (tags.size() >= MAX_TAGS) break;
            tags.add(tag.toLowerCase().trim());
        }
    }
}
```
**Why LinkedHashSet:** Tag system needs O(1) duplicate prevention and insertion order preservation. When displaying tags, they appear in the order they were added. HashSet provides O(1) operations, linked list maintains order.

#### Example 4: Unique Visitor Tracking with Deduplication
```java
public class UniqueVisitorTracker {
    // Multiple HashSets for different time windows
    private Set<String> todayVisitors = new HashSet<>();
    private Set<String> weekVisitors = new HashSet<>();
    private Set<String> monthVisitors = new HashSet<>();
    
    // Record visitor - O(1) for each set
    public void recordVisit(String visitorId) {
        todayVisitors.add(visitorId);                // O(1)
        weekVisitors.add(visitorId);                 // O(1)
        monthVisitors.add(visitorId);                // O(1)
    }
    
    // Get unique visitor counts - O(1) each
    public int getTodayUniqueVisitors() {
        return todayVisitors.size();
    }
    
    public int getWeekUniqueVisitors() {
        return weekVisitors.size();
    }
    
    public int getMonthUniqueVisitors() {
        return monthVisitors.size();
    }
    
    // Find new visitors this week (not in previous weeks) - O(n)
    public Set<String> getNewVisitorsThisWeek(Set<String> previousWeekVisitors) {
        Set<String> newVisitors = new HashSet<>(weekVisitors);
        newVisitors.removeAll(previousWeekVisitors); // Set difference
        return newVisitors;
    }
    
    // Find returning visitors (in both week and month) - O(n)
    public Set<String> getReturningVisitors() {
        Set<String> returning = new HashSet<>(weekVisitors);
        returning.retainAll(monthVisitors);          // Set intersection
        return returning;
    }
    
    // Reset daily stats
    public void resetDaily() {
        todayVisitors.clear();
    }
    
    // Reset weekly stats
    public void resetWeekly() {
        weekVisitors.clear();
    }
}
```
**Why HashSet:** Visitor tracking inherently needs deduplication - same visitor shouldn't be counted twice. HashSet automatically handles this with O(1) add operations. Set operations (difference, intersection) provide analytics.

### HashSet - Complete Reference

#### Internal Structure
```java
// HashSet is backed by HashMap internally
public class HashSet<E> {
    private transient HashMap<E, Object> map;
    private static final Object PRESENT = new Object();
    
    public HashSet() {
        map = new HashMap<>();
    }
    
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }
    
    public boolean contains(Object o) {
        return map.containsKey(o);
    }
}
```

#### Constructors

```java
// 1. Default constructor - capacity 16, load factor 0.75
Set<String> set1 = new HashSet<>();

// 2. With initial capacity
Set<String> set2 = new HashSet<>(100);

// 3. With capacity and load factor
Set<String> set3 = new HashSet<>(100, 0.5f);  // Resize at 50% full

// 4. From another collection
Set<String> source = List.of("A", "B", "C", "C"); // Note: duplicate C
Set<String> set4 = new HashSet<>(source);     // [A, B, C] - duplicate removed

// Size vs capacity
Set<Integer> nums = new HashSet<>(100);       // capacity=100, size=0
```

#### Adding Elements

```java
Set<String> set = new HashSet<>();

// add(E) - O(1) average
boolean added1 = set.add("A");                // true, set=[A]
boolean added2 = set.add("B");                // true, set=[A, B]
boolean added3 = set.add("A");                // false (duplicate!), set=[A, B]

// addAll(Collection) - O(m) where m=collection size
set.addAll(List.of("C", "D", "B"));           // [A, B, C, D] - B not added again
// Returns true if set changed
boolean changed = set.addAll(List.of("E"));   // true

// Null element
set.add(null);                                // ✅ One null allowed
// set=[A, B, C, D, E, null]

// Add returns boolean - useful for checking
if (set.add("F")) {
    System.out.println("F was new");
} else {
    System.out.println("F already existed");
}
```

#### Removing Elements

```java
Set<String> set = new HashSet<>(Set.of("A", "B", "C", "D", "E"));

// remove(Object) - O(1) average
boolean removed1 = set.remove("B");           // true, set=[A, C, D, E]
boolean removed2 = set.remove("X");           // false (not found)

// removeAll(Collection) - O(n) where n=this set size
set.removeAll(Set.of("A", "C", "Z"));         // [D, E] - Z ignored

// retainAll(Collection) - keep only these - O(n)
set.addAll(Set.of("A", "B", "C"));            // [D, E, A, B, C]
set.retainAll(Set.of("B", "C", "D"));         // [D, B, C]

// removeIf(Predicate) - Java 8 - O(n)
set.addAll(Set.of("A", "E", "F"));
set.removeIf(s -> s.compareTo("D") < 0);      // [D, E, F]

// clear() - O(n)
set.clear();                                  // []
```

#### Checking Elements

```java
Set<String> set = new HashSet<>(Set.of("A", "B", "C"));

// contains(Object) - O(1) average
boolean hasB = set.contains("B");             // true
boolean hasX = set.contains("X");             // false

// isEmpty() - O(1)
boolean empty = set.isEmpty();                // false

// size() - O(1)
int size = set.size();                        // 3

// containsAll(Collection) - O(m) where m=argument size
boolean hasAll = set.containsAll(Set.of("A", "B")); // true
boolean hasAll2 = set.containsAll(Set.of("A", "X")); // false
```

#### Set Operations (Mathematical)

```java
Set<Integer> set1 = new HashSet<>(Set.of(1, 2, 3, 4));
Set<Integer> set2 = new HashSet<>(Set.of(3, 4, 5, 6));

// Union - all elements from both sets - O(n + m)
Set<Integer> union = new HashSet<>(set1);
union.addAll(set2);                           // [1, 2, 3, 4, 5, 6]

// Intersection - common elements - O(n) where n=smaller set
Set<Integer> intersection = new HashSet<>(set1);
intersection.retainAll(set2);                 // [3, 4]

// Difference - in set1 but not in set2 - O(n)
Set<Integer> difference = new HashSet<>(set1);
difference.removeAll(set2);                   // [1, 2]

// Symmetric Difference - in either but not both - O(n + m)
Set<Integer> symDiff = new HashSet<>(set1);
symDiff.addAll(set2);                         // Union first
Set<Integer> inter = new HashSet<>(set1);
inter.retainAll(set2);                        // Intersection
symDiff.removeAll(inter);                     // Remove common
// symDiff = [1, 2, 5, 6]

// Subset check - O(n)
boolean isSubset = set2.containsAll(Set.of(3, 4)); // true

// Disjoint check - no common elements - O(n*m) naive, O(n) smart
boolean disjoint = Collections.disjoint(set1, Set.of(7, 8)); // true
```

### TreeSet - Complete Reference

#### Internal Structure
```java
// TreeSet is backed by TreeMap (Red-Black tree)
public class TreeSet<E> extends AbstractSet<E>
        implements NavigableSet<E> {
    private transient NavigableMap<E, Object> m;
    private static final Object PRESENT = new Object();
    
    public TreeSet() {
        m = new TreeMap<>();
    }
    
    public boolean add(E e) {
        return m.put(e, PRESENT) == null;
    }
}

// Red-Black Tree properties:
// 1. Each node is red or black
// 2. Root is black
// 3. Leaves (null) are black
// 4. Red nodes have black children
// 5. All paths from root to leaves have same number of black nodes
// Guarantees: O(log n) for add, remove, contains
```

#### Constructors

```java
// 1. Default - natural ordering
TreeSet<Integer> set1 = new TreeSet<>();
set1.addAll(List.of(5, 2, 8, 1, 9));
// set1 = [1, 2, 5, 8, 9] - automatically sorted

// 2. With Comparator - custom ordering
TreeSet<String> set2 = new TreeSet<>(Comparator.reverseOrder());
set2.addAll(List.of("A", "C", "B"));
// set2 = [C, B, A] - reverse alphabetical

// 3. From collection - natural ordering
TreeSet<Integer> set3 = new TreeSet<>(List.of(3, 1, 2));
// set3 = [1, 2, 3]

// 4. From another SortedSet - same ordering
TreeSet<Integer> set4 = new TreeSet<>(set3);

// Custom comparator example - sort by length then alphabetically
TreeSet<String> byLength = new TreeSet<>((s1, s2) -> {
    int lenCompare = Integer.compare(s1.length(), s2.length());
    return lenCompare != 0 ? lenCompare : s1.compareTo(s2);
});
byLength.addAll(List.of("a", "bbb", "cc", "dddd"));
// [a, cc, bbb, dddd]
```

#### Basic Operations (Like HashSet but O(log n))

```java
TreeSet<Integer> set = new TreeSet<>();

// add(E) - O(log n)
set.add(5);                                   // [5]
set.add(2);                                   // [2, 5]
set.add(8);                                   // [2, 5, 8]
set.add(2);                                   // [2, 5, 8] - duplicate ignored

// remove(Object) - O(log n)
set.remove(5);                                // [2, 8]

// contains(Object) - O(log n)
boolean has8 = set.contains(8);               // true

// size(), isEmpty(), clear() - Same as HashSet
int size = set.size();                        // 2
```

#### NavigableSet Methods (TreeSet-Specific)

```java
TreeSet<Integer> set = new TreeSet<>(Set.of(1, 3, 5, 7, 9, 11, 13));

// first() - lowest element - O(1)
Integer first = set.first();                  // 1

// last() - highest element - O(1)
Integer last = set.last();                    // 13

// higher(E) - least element strictly greater - O(log n)
Integer higher = set.higher(7);               // 9
Integer higher2 = set.higher(13);             // null (no higher)

// lower(E) - greatest element strictly less - O(log n)
Integer lower = set.lower(7);                 // 5
Integer lower2 = set.lower(1);                // null (no lower)

// ceiling(E) - least element >= given - O(log n)
Integer ceiling = set.ceiling(7);             // 7 (7 exists)
Integer ceiling2 = set.ceiling(6);            // 7 (next higher)

// floor(E) - greatest element <= given - O(log n)
Integer floor = set.floor(7);                 // 7 (7 exists)
Integer floor2 = set.floor(6);                // 5 (next lower)

// pollFirst() - remove and return lowest - O(log n)
Integer removed1 = set.pollFirst();           // 1, set=[3,5,7,9,11,13]

// pollLast() - remove and return highest - O(log n)
Integer removed2 = set.pollLast();            // 13, set=[3,5,7,9,11]

// descendingSet() - reverse view - O(1) to create
NavigableSet<Integer> desc = set.descendingSet();
System.out.println(desc);                     // [11, 9, 7, 5, 3]
// Changes to desc affect original!

// descendingIterator() - iterate in reverse
Iterator<Integer> descIt = set.descendingIterator();
while (descIt.hasNext()) {
    System.out.println(descIt.next());        // 11, 9, 7, 5, 3
}
```

#### Subset Views

```java
TreeSet<Integer> set = new TreeSet<>(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

// headSet(E) - elements < toElement - O(log n) to create
SortedSet<Integer> head = set.headSet(6);     // [1, 2, 3, 4, 5]

// tailSet(E) - elements >= fromElement - O(log n) to create
SortedSet<Integer> tail = set.tailSet(6);     // [6, 7, 8, 9, 10]

// subSet(from, to) - elements in [from, to) - O(log n) to create
SortedSet<Integer> sub = set.subSet(4, 8);    // [4, 5, 6, 7]

// Navigable versions with inclusive/exclusive control
NavigableSet<Integer> head2 = set.headSet(6, true);  // [1, 2, 3, 4, 5, 6] - inclusive
NavigableSet<Integer> tail2 = set.tailSet(6, false); // [7, 8, 9, 10] - exclusive
NavigableSet<Integer> sub2 = set.subSet(4, true, 8, false); // [4, 5, 6, 7]

// Views are BACKED by original - changes affect both!
head.remove(1);                               // Removes 1 from original set too
sub.add(6);                                   // ❌ IllegalArgumentException - out of range
```

### LinkedHashSet - Complete Reference

```java
// Combines HashSet performance with insertion-order iteration
// Backed by LinkedHashMap (hash table + doubly-linked list)

// Constructor
LinkedHashSet<String> set = new LinkedHashSet<>();

// All operations same as HashSet with O(1) average complexity
set.add("C");                                 // [C]
set.add("A");                                 // [C, A]
set.add("B");                                 // [C, A, B]

// Iteration maintains insertion order!
for (String s : set) {
    System.out.print(s + " ");                // C A B (insertion order)
}

// Compare with HashSet - random order
Set<String> hashSet = new HashSet<>();
hashSet.addAll(List.of("C", "A", "B"));
for (String s : hashSet) {
    System.out.print(s + " ");                // Could be: A B C, or B C A, etc.
}

// Use case: LRU cache (access order)
LinkedHashSet<String> cache = new LinkedHashSet<>(16, 0.75f, true); // access-order
// Note: LinkedHashSet doesn't support access-order, use LinkedHashMap instead
```

### equals() and hashCode() Contract

**CRITICAL for HashSet/LinkedHashSet correctness:**

```java
// BAD - no hashCode/equals override
class BadPerson {
    String name;
    int age;
    
    BadPerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

Set<BadPerson> bad = new HashSet<>();
bad.add(new BadPerson("Alice", 25));
bad.add(new BadPerson("Alice", 25));          // Treated as different!
System.out.println(bad.size());               // 2 - WRONG!

// GOOD - proper hashCode/equals
class GoodPerson {
    String name;
    int age;
    
    GoodPerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodPerson)) return false;
        GoodPerson that = (GoodPerson) o;
        return age == that.age && Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

Set<GoodPerson> good = new HashSet<>();
good.add(new GoodPerson("Alice", 25));
good.add(new GoodPerson("Alice", 25));        // Recognized as duplicate
System.out.println(good.size());              // 1 - CORRECT!

// Contract rules:
// 1. If a.equals(b), then a.hashCode() == b.hashCode()
// 2. If a.hashCode() == b.hashCode(), a.equals(b) MAY be true (collision OK)
// 3. equals() and hashCode() must use same fields
// 4. If object is in Set, don't modify fields used in hashCode/equals!
```

### Comparable vs Comparator for TreeSet

```java
// Option 1: Natural ordering - implement Comparable
class Score implements Comparable<Score> {
    int points;
    
    Score(int points) { this.points = points; }
    
    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.points, other.points);
    }
}

TreeSet<Score> scores1 = new TreeSet<>();
scores1.addAll(List.of(new Score(100), new Score(50), new Score(75)));
// Sorted by points: 50, 75, 100

// Option 2: Custom ordering - provide Comparator
TreeSet<Score> scores2 = new TreeSet<>((s1, s2) -> 
    Integer.compare(s2.points, s1.points)     // Reverse order
);
scores2.addAll(scores1);
// Sorted: 100, 75, 50

// Comparator methods (Java 8)
TreeSet<String> byLength = new TreeSet<>(
    Comparator.comparing(String::length)
              .thenComparing(Comparator.naturalOrder())
);
byLength.addAll(List.of("a", "bbb", "cc"));
// [a, cc, bbb] - length first, then alphabetically

// TreeSet requires all elements to be mutually comparable
TreeSet<Object> invalid = new TreeSet<>();
invalid.add(1);
// invalid.add("string");                     // ❌ ClassCastException
```


### Exam Traps

- ❌ **TRAP 1:** TreeSet requires Comparable or Comparator
  ```java
  class Person { String name; }
  
  TreeSet<Person> set = new TreeSet<>();
  set.add(new Person());                     // ❌ ClassCastException
  // Person doesn't implement Comparable!
  
  // Fix: implement Comparable or provide Comparator
  TreeSet<Person> set2 = new TreeSet<>((p1, p2) -> 
      p1.name.compareTo(p2.name)
  );
  ```

- ❌ **TRAP 2:** Modifying elements in Set breaks contract
  ```java
  class Mutable {
      int value;
      @Override
      public int hashCode() { return value; }
      @Override
      public boolean equals(Object o) {
          return o instanceof Mutable && ((Mutable)o).value == value;
      }
  }
  
  Mutable m = new Mutable();
  m.value = 10;
  Set<Mutable> set = new HashSet<>();
  set.add(m);                                // Added with hashCode based on 10
  
  m.value = 20;                              // Modifying after add!
  set.contains(m);                           // ❌ false! Can't find it anymore
  // HashSet looks in wrong bucket (bucket for hashCode 20, not 10)
  ```

- ❌ **TRAP 3:** Set.of() doesn't allow null
  ```java
  Set<String> set = Set.of("A", null, "B"); // ❌ NullPointerException
  
  Set<String> hashSet = new HashSet<>();
  hashSet.add(null);                         // ✅ One null allowed
  
  TreeSet<String> treeSet = new TreeSet<>();
  treeSet.add(null);                         // ❌ NullPointerException (can't compare)
  ```

- ❌ **TRAP 4:** Set.of() doesn't allow duplicates
  ```java
  Set<String> set = Set.of("A", "B", "A");  // ❌ IllegalArgumentException
  // Set.of() checks for duplicates at creation!
  
  Set<String> hashSet = new HashSet<>(List.of("A", "B", "A"));
  // ✅ OK - silently ignores duplicate: [A, B]
  ```

- ❌ **TRAP 5:** TreeSet uses compareTo(), not equals()
  ```java
  class Person implements Comparable<Person> {
      String name;
      int age;
      
      @Override
      public int compareTo(Person other) {
          return name.compareTo(other.name);  // Only compares name!
      }
      
      @Override
      public boolean equals(Object o) {
          if (!(o instanceof Person)) return false;
          Person p = (Person) o;
          return name.equals(p.name) && age == p.age; // Compares name AND age
      }
  }
  
  TreeSet<Person> set = new TreeSet<>();
  set.add(new Person("Alice", 25));
  set.add(new Person("Alice", 30));          // Treated as duplicate!
  // TreeSet uses compareTo(), sees same name = duplicate
  // size() = 1, not 2!
  ```

- ❌ **TRAP 6:** HashSet iteration order is unpredictable
  ```java
  Set<Integer> set = new HashSet<>();
  set.addAll(List.of(1, 2, 3, 4, 5));
  
  for (Integer i : set) {
      System.out.print(i + " ");             // Could be: 1 2 3 4 5
  }                                          // Or: 4 2 1 5 3, etc.
  // Order depends on hash codes and bucket allocation
  
  // Use LinkedHashSet for insertion order
  Set<Integer> linked = new LinkedHashSet<>();
  linked.addAll(List.of(1, 2, 3, 4, 5));
  // Always iterates: 1 2 3 4 5
  ```

- ❌ **TRAP 7:** add() returns boolean
  ```java
  Set<String> set = new HashSet<>();
  boolean added1 = set.add("A");             // true (new element)
  boolean added2 = set.add("A");             // false (duplicate)
  
  // Common mistake - ignoring return value
  set.add("B");
  set.add("B");                              // Silently ignored, no error
  ```

- ❌ **TRAP 8:** TreeSet subset views are backed by original
  ```java
  TreeSet<Integer> set = new TreeSet<>(Set.of(1, 2, 3, 4, 5));
  SortedSet<Integer> sub = set.subSet(2, 4); // [2, 3]
  
  sub.add(2);                                // ✅ OK - in range
  // sub.add(5);                             // ❌ IllegalArgumentException - out of range
  
  set.add(10);                               // Modifies original
  // sub.size();                             // ❌ ConcurrentModificationException
  ```

- ❌ **TRAP 9:** equals() and hashCode() must match
  ```java
  class Bad {
      int value;
      
      @Override
      public boolean equals(Object o) {
          return o instanceof Bad && ((Bad)o).value == value;
      }
      // Missing hashCode()! Uses default Object.hashCode()
  }
  
  Set<Bad> set = new HashSet<>();
  Bad b1 = new Bad(); b1.value = 10;
  Bad b2 = new Bad(); b2.value = 10;
  
  set.add(b1);
  set.add(b2);                               // Both added! size = 2
  // Different hashCodes even though equals() returns true
  ```

- ❌ **TRAP 10:** retainAll() vs removeAll() confusion
  ```java
  Set<Integer> set = new HashSet<>(Set.of(1, 2, 3, 4, 5));
  
  set.retainAll(Set.of(2, 3, 6));            // Keep only 2, 3
  // set = [2, 3]
  
  set = new HashSet<>(Set.of(1, 2, 3, 4, 5));
  set.removeAll(Set.of(2, 3, 6));            // Remove 2, 3
  // set = [1, 4, 5]
  ```

- ❌ **TRAP 11:** TreeSet first()/last() throw exception if empty
  ```java
  TreeSet<Integer> set = new TreeSet<>();
  
  // set.first();                            // ❌ NoSuchElementException
  // set.last();                             // ❌ NoSuchElementException
  
  // Use pollFirst()/pollLast() for safe removal
  Integer first = set.pollFirst();           // null if empty
  ```

- ❌ **TRAP 12:** Collections.unmodifiableSet() is shallow
  ```java
  Set<StringBuilder> set = new HashSet<>();
  set.add(new StringBuilder("hello"));
  
  Set<StringBuilder> unmod = Collections.unmodifiableSet(set);
  // unmod.add(new StringBuilder("x"));      // ❌ UnsupportedOperationException
  
  unmod.iterator().next().append(" world"); // ✅ Can modify content!
  // Original set also affected
  ```

---

## Exam Questions - Set Interface

**EXAM FORMAT - Java SE 21 (1Z0-830):**
- **50 questions** in **120 minutes**
- **Passing score:** 68% (34 correct minimum)
- **Set questions:** ~3-5 questions (6-10% of exam)

---

### Question 1: Set duplicates
```java
Set<String> set = new HashSet<>();
set.add("A");
set.add("B");
set.add("A");
System.out.println(set.size());
```
**What is the output?**  
A. 1  
B. 2  
C. 3  
D. Compilation error  

**Answer:** B  
**Explanation:** Sets don't allow duplicates. The second `add("A")` is ignored (returns false). Set contains ["A", "B"], size = 2.

---

### Question 2: TreeSet ordering
```java
Set<Integer> set = new TreeSet<>();
set.addAll(List.of(5, 2, 8, 1, 9));
System.out.println(set);
```
**What is the output?**  
A. [5, 2, 8, 1, 9]  
B. [1, 2, 5, 8, 9]  
C. [9, 8, 5, 2, 1]  
D. Undefined order  

**Answer:** B  
**Explanation:** TreeSet maintains elements in sorted (natural) order. Integers are sorted numerically: [1, 2, 5, 8, 9].

---

### Question 3: TreeSet requires Comparable
```java
class Person { String name; }

Set<Person> set = new TreeSet<>();
set.add(new Person());
```
**What is the result?**  
A. Set contains one Person  
B. Compilation error  
C. ClassCastException  
D. NullPointerException  

**Answer:** C  
**Explanation:** TreeSet requires elements to be Comparable or provide a Comparator. Person doesn't implement Comparable, so ClassCastException is thrown when trying to compare.

---

### Question 4: Set.of() with null
```java
Set<String> set = Set.of("A", null, "B");
```
**What is the result?**  
A. set = [A, null, B]  
B. set = [A, B]  
C. NullPointerException  
D. Compilation error  

**Answer:** C  
**Explanation:** `Set.of()` is null-hostile and throws NullPointerException if any element is null. Use `new HashSet<>()` if you need null elements.

---

### Question 5: Set.of() with duplicates
```java
Set<String> set = Set.of("A", "B", "A");
```
**What is the result?**  
A. set = [A, B]  
B. set = [A, B, A]  
C. IllegalArgumentException  
D. Compilation error  

**Answer:** C  
**Explanation:** `Set.of()` checks for duplicates at creation time and throws IllegalArgumentException if any duplicates are found.

---

### Question 6: Modifying element in Set
```java
class Mutable {
    int value;
    public int hashCode() { return value; }
    public boolean equals(Object o) {
        return o instanceof Mutable && ((Mutable)o).value == value;
    }
}

Mutable m = new Mutable();
m.value = 10;
Set<Mutable> set = new HashSet<>();
set.add(m);
m.value = 20;
System.out.println(set.contains(m));
```
**What is the output?**  
A. true  
B. false  
C. Compilation error  
D. Exception  

**Answer:** B  
**Explanation:** Modifying an object's hash-affecting fields after adding it to HashSet breaks the contract. The set looks in the wrong bucket (for hashCode 20), can't find the object (stored in bucket for hashCode 10).

---

### Question 7: LinkedHashSet ordering
```java
Set<Integer> set = new LinkedHashSet<>();
set.addAll(List.of(5, 2, 8, 1));
System.out.println(set);
```
**What is the output?**  
A. [1, 2, 5, 8]  
B. [5, 2, 8, 1]  
C. [8, 5, 2, 1]  
D. Undefined order  

**Answer:** B  
**Explanation:** LinkedHashSet maintains insertion order. Elements are iterated in the order they were added: [5, 2, 8, 1].

---

### Question 8: TreeSet compareTo vs equals
```java
class Person implements Comparable<Person> {
    String name;
    int age;
    Person(String name, int age) { this.name = name; this.age = age; }
    
    public int compareTo(Person other) {
        return name.compareTo(other.name);
    }
}

TreeSet<Person> set = new TreeSet<>();
set.add(new Person("Alice", 25));
set.add(new Person("Alice", 30));
System.out.println(set.size());
```
**What is the output?**  
A. 0  
B. 1  
C. 2  
D. Compilation error  

**Answer:** B  
**Explanation:** TreeSet uses `compareTo()` to determine duplicates, not `equals()`. Both Person objects have the same name, so `compareTo()` returns 0, treating them as duplicates. Only first is added.

---

### Question 9: retainAll behavior
```java
Set<Integer> set = new HashSet<>(Set.of(1, 2, 3, 4, 5));
set.retainAll(Set.of(2, 3, 6));
System.out.println(set);
```
**What is the output?**  
A. [1, 4, 5]  
B. [2, 3, 6]  
C. [2, 3]  
D. [1, 2, 3, 4, 5, 6]  

**Answer:** C  
**Explanation:** `retainAll()` keeps only elements that are in BOTH sets (intersection). Elements 2 and 3 are in both sets. Result: [2, 3].

---

### Question 10: TreeSet first() on empty set
```java
TreeSet<Integer> set = new TreeSet<>();
Integer first = set.first();
```
**What is the result?**  
A. null  
B. 0  
C. NoSuchElementException  
D. Compilation error  

**Answer:** C  
**Explanation:** `first()` throws NoSuchElementException if the set is empty. Use `pollFirst()` to get null for empty sets instead of an exception.

---

**End of Part 3 - Set Interface**

**Continue with:**
- **[Part 4: Map Interface →](CHAPTER-05-PART4-MAP.md)** (HashMap, TreeMap, LinkedHashMap)
- **Part 5: Queue/Deque** + Summary

