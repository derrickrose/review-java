# Problem 2 — Thread-Safe LRU Price Cache (Full Detailed Version)

## Context
Trading systems often need to keep only the *most recently used* prices for a set of instruments.  
The cache must:
- Evict the least recently used entry when capacity is reached.
- Be safe under concurrent access (multiple threads performing get/put).
- Maintain O(1) average complexity for get/put operations.

This tests understanding of Java synchronization, data structures, and memory consistency.

## Goal
Implement a **thread-safe LRU cache** for instrument -> latestPrice mappings.

## API
```java
public class LruPriceCache {

    public LruPriceCache(int capacity);

    public Optional<Double> get(String instrument);

    public void put(String instrument, double price);
}
```

## Functional Requirements
### LRU Behavior
- `get()` marks an entry as recently used.
- `put()`:
  - If key exists → update price and mark as recently used.
  - If key does not exist:
    - Insert new entry.
    - If over capacity → evict the **least recently used** entry.

### Data Structure Constraints
- Must support O(1) get/put.
- Recommended approach:
  - `LinkedHashMap` with access-order
  - OR hashmap + doubly linked list (manual LRU)

## Concurrency Requirements
### MUST BE THREAD-SAFE
- Multiple threads may call `get()` and `put()` simultaneously.
- Avoid:
  - Race conditions
  - Inconsistent internal state
  - Lost updates

### Acceptable synchronization strategies:
1. **Synchronized methods** (coarse-grained lock)  
   Simple but reduces concurrency.

2. **ReentrantLock**  
   More controlled locking.

3. **ConcurrentHashMap + custom linked list lock**  
   Harder but more scalable.

Lock-free is *not required* and not expected.

## Correctness Requirements
- No key should be lost or corrupted.
- Evictions must occur exactly when needed.
- Recently-used entries must always move to the “top.”
- `Optional.empty()` returned if key not found.

## Performance Requirements
- O(1) average get/put.
- Minimal locking overhead.
- Avoid scanning entire map on every access.

## Evaluation Criteria (Interview)
- Correct understanding of LRU semantics.
- Correct concurrency control.
- Clean design and maintainable code.
- Awareness of tradeoffs (coarse lock vs fine-grained).
- Ability to explain how LinkedHashMap makes this easier.

## Interview Notes
This is a common senior Java interview problem.  
The simplest acceptable correct solution is:

- `LinkedHashMap` with `synchronized` methods.

A more advanced candidate might use:

- `ReentrantLock`
- Custom LRU list
- Segmented mapping

But **correctness and thread safety** matter far more than cleverness.

