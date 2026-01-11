# Problem 4 — Distributed Exposure Aggregation (Full Detailed Version)

## Context
In trading and risk systems, “exposure” represents how much a client is affected by the movement of a specific financial instrument.
You must simulate a scalable Map/Reduce-style aggregation.

## Position Model
```java
class Position {
    String clientId;
    String instrument;
    double exposure; // positive or negative
}
```

## Goal
Implement distributed-style aggregation:
1. Partition positions by node using:
   `nodeIndex = Math.abs(clientId.hashCode()) % numNodes`
2. Each node locally aggregates exposure per client.
3. The coordinator merges all node results into a global map.

## API
```java
public Map<String, Double> aggregate(List<Position> positions, int numNodes);
```

## Requirements
- Partition deterministically per client.
- Build per-node local maps.
- Merge all local maps safely.
- O(N) partitioning, O(N) aggregation.
- Works for millions of positions.

## Edge Cases
- numNodes = 1 → sequential aggregation.
- Empty list → return empty map.
- Large skew: many positions for one client.

## Evaluation Criteria
- Correct hashing & partitioning.
- Correct merging logic.
- Clean Map/Reduce structure.
- Efficient memory use.

