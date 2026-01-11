# Problem 1 — Sliding Window VWAP Calculator (Full Detailed Version)

## Context
A VWAP (Volume Weighted Average Price) is a core metric in trading systems. It represents the average traded price of an instrument over a time window, weighted by trade volume. You receive a high‑frequency stream of ticks (price updates with volume). The goal is to maintain a *sliding window* of ticks per instrument and compute VWAP efficiently.

## Tick Model
```java
class Tick {
    String instrument;    // e.g. "AAPL", "EURUSD"
    double price;         // trade price
    double volume;        // trade volume
    long timestamp;       // epoch millis (non-decreasing per stream)
}
```

## VWAP Formula
Given all ticks for a symbol received in the last W milliseconds:

VWAP = sum(price_i * volume_i) / sum(volume_i)

Ticks older than (latestTimestamp - W) must be ignored.

## Task
Implement:
```java
public class VwapCalculator {
    public VwapCalculator(long windowMillis);
    public void onTick(Tick tick);
    public Optional<Double> getVwap(String instrument);
}
```

## Functional Requirements
- Each instrument maintains its own sliding window.
- When `onTick` is called:
  - Insert the new tick.
  - Evict all expired ticks.
  - Update running aggregates.
- `getVwap(instrument)`:
  - Returns empty if the window has no valid ticks.
  - Otherwise returns VWAP using maintained aggregates.

## Performance Requirements
- Must process hundreds of thousands of ticks per second.
- Eviction must be amortized O(1).
- Memory must never grow unbounded.

Recommended structure:
- A `Deque<Tick>` per instrument.
- Two running sums:
  - `sumPriceVolume`
  - `sumVolume`
- On each new tick:
  - Add price*volume to sumPriceVolume.
  - Add volume to sumVolume.
  - Pop expired ticks from head of deque while updating sums.

## Correctness Constraints
- Handle multiple instruments independently.
- Handle edge case: sumVolume == 0 ⇒ return Optional.empty().
- Do not recompute VWAP by iterating the entire window on every call.
- Eviction must strictly follow timestamp rules.

## Evaluation Criteria (Interview)
- Choice of data structures for efficiency.
- Correct sliding window eviction logic.
- Correct handling of corner cases.
- Computational efficiency and memory safety.
- Code clarity and maintainability.

This problem is intentionally realistic: real trading systems compute sliding metrics over massive volumes of ticks and require extremely efficient window maintenance.
