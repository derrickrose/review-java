# Problem 3 — Parallel Client PnL Aggregation (Full Detailed Version)

## Context
In trading systems, millions of trade executions may arrive throughout the day.  
Each trade belongs to a client and carries:
- an instrument symbol,
- a buy/sell quantity,
- and an execution price.

To compute **Profit and Loss (PnL)** per client efficiently, the system must leverage parallel computation.

PnL per trade:
```
PnL = quantity * price
```

Goal: **Aggregate total PnL per client using multiple threads safely and efficiently.**

## Trade Model
```java
class Trade {
    String clientId;
    String instrument;
    int quantity;     // positive for buy, negative for sell
    double price;     // execution price
}
```

## Task
Implement:

```java
public class ParallelPnlCalculator {
    public Map<String, Double> computePnl(List<Trade> trades, int threads);
}
```

### The method must:
- Split the trade list into N segments.
- Process each segment in parallel.
- Compute partial PnL maps (per thread).
- Merge all partial maps into a final aggregated map.

---

## Functional Requirements

### 1. Partition trades for parallel processing
Common approaches:
- Divide `trades.size()` by `threads`.
- Assign each chunk to a worker.

### 2. Use a fixed thread pool
```java
ExecutorService pool = Executors.newFixedThreadPool(threads);
```

### 3. Workers return partial results
Each worker:
- Iterates trades in its assigned range.
- Computes a local `HashMap<String, Double>` like:
  ```
  clientPnlMap.put(clientId, clientPnlMap.getOrDefault(clientId, 0.0) + quantity * price);
  ```
- Returns this partial map via `Callable<Map<String, Double>>`.

### 4. Main thread merges results
Use a merge strategy such as:
```java
globalMap.merge(clientId, pnl, Double::sum);
```

### 5. Shutdown + await completion
- Retrieve results with `Future.get()`
- Shutdown the executor after merging.

---

## Correctness Requirements
- No shared mutable map inside worker threads.
- Prevent race conditions by merging in the main thread.
- Ensure all PnL contributions are accounted for.
- Handle empty input list correctly.
- Order of results does not matter.

---

## Performance Requirements
- Must scale with increasing number of threads.
- Avoids synchronization inside worker loops.
- Efficient merging (client counts may reach tens of thousands).
- Must not allocate unnecessary structures.

---

## Edge Cases
- `threads == 1` → process sequentially.
- `trades.size() == 0` → return empty map.
- Many trades for one client + few for others.
- Large input sizes (millions of trades).

---

## Evaluation Criteria (Interview)

### Technical
- Ability to use ExecutorService correctly.
- Safe parallelism without data races.
- Efficient merging strategy.
- Understanding of partitioning trade-offs.

### Conceptual
- Why using a global synchronized map is wrong.
- Benefits of per-thread partial aggregation.
- Understanding CPU vs memory bottlenecks.

### Code Quality
- Clean structure.
- Well-defined responsibilities.
- Clear naming and logic.

---

## Notes
This is a classic interview test for:
- Senior Java backend roles,
- Finance/trading systems,
- Distributed computation concepts.

The key insight is:
> **Use local partial maps in each thread and merge them after.**

