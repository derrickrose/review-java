# Problem 7 — Group Trades by Client and Instrument (Full Detailed Version)

## Context
In trading and risk systems, data is often analyzed at multiple dimensions:
- per client
- per instrument (symbol)
- per time bucket, etc.

A very common operation is to **group trades** by both **client** and **instrument** in order to:
- compute PnL per client + instrument,
- compute exposures,
- generate reports,
- drive downstream risk pipelines.

This problem focuses on **Java collections**, **maps of maps**, and **clean grouping logic**. No finance-specific knowledge is required beyond understanding that trades have `clientId` and `instrument`.

---

## Trade Model

```java
class Trade {
    String clientId;    // e.g. "CLIENT_123"
    String instrument;  // e.g. "AAPL", "EURUSD"
    int quantity;       // positive buy, negative sell
    double price;       // execution price
}
```

You are given a flat list of trades, potentially large, in no particular order.

---

## Task

Implement a method that groups all trades into a nested map:

```java
Map<String, Map<String, List<Trade>>> groupTrades(List<Trade> trades)
```

Where:

- Outer key: `clientId`
- Inner key: `instrument`
- Innermost value: `List<Trade>` for that `(clientId, instrument)` pair

In other words:

```text
clientId → instrument → list of trades
```

Example:

If trades contain:

- clientId = "C1", instrument = "AAPL"
- clientId = "C1", instrument = "GOOG"
- clientId = "C2", instrument = "AAPL"

The resulting structure should look like:

```text
{
  "C1" -> {
            "AAPL" -> [trade1, trade3, ...],
            "GOOG" -> [trade2, ...]
          },
  "C2" -> {
            "AAPL" -> [tradeX, ...]
          }
}
```

---

## Functional Requirements

1. **Create outer map by clientId**
   - For each distinct `clientId`, there must be an entry:
     ```java
     Map<String, Map<String, List<Trade>>> result = new HashMap<>();
     ```

2. **Create inner map by instrument per client**
   - For each `(clientId, instrument)` pair, maintain a `List<Trade>`.

3. **Add trades to correct bucket**
   - Iterate all trades once.
   - For each trade:
     - Resolve / create `Map<String, List<Trade>>` for its clientId.
     - Resolve / create `List<Trade>` for its instrument.
     - Append the trade to the list.

4. **Return the nested map**
   - All trades must appear exactly once in exactly one list.

---

## Complexity & Performance

- Let `N` = number of trades:
  - Time complexity should be **O(N)**.
  - Each trade is visited once.
- Space complexity:
  - O(N) for the lists and map entries.
- This must handle **hundreds of thousands** of trades.

No need for parallelism here (unless explicitly asked in a follow-up).

---

## Edge Cases

- Empty input list → return an empty map (not null).
- Single client, single instrument → one entry in both maps.
- Many instruments per client or many clients sharing same instrument.
- Duplicated trades (by business meaning) must still be included; this method doesn’t deduplicate.

---

## Implementation Approaches

### Classic imperative approach

Using explicit `get` / `put` logic:

```java
Map<String, Map<String, List<Trade>>> result = new HashMap<>();

for (Trade t : trades) {
    Map<String, List<Trade>> byInstrument =
        result.computeIfAbsent(t.clientId, id -> new HashMap<>());

    List<Trade> bucket =
        byInstrument.computeIfAbsent(t.instrument, inst -> new ArrayList<>());

    bucket.add(t);
}
```

### Streams-based approach (optional)

Using Java Streams:

```java
import static java.util.stream.Collectors.*;

Map<String, Map<String, List<Trade>>> result =
    trades.stream().collect(
        groupingBy(t -> t.clientId,
            groupingBy(t -> t.instrument))
    );
```

This version is concise but may be less explicit for beginners.

---

## Evaluation Criteria (Interview)

### Correctness
- All trades are grouped to the correct `(client, instrument)` bucket.
- No trade is lost or mis-grouped.

### Data structure understanding
- Proper use of nested maps and lists.
- Understanding of `computeIfAbsent` or groupingBy collectors.

### Code quality
- Clear naming (`byClient`, `byInstrument`, `bucket` etc.).
- Readable, maintainable implementation.
- Reasonable choice between imperative vs streams style.

### Performance
- Single pass over the list.
- No unnecessary copying or reprocessing.

---

## Notes

This is a **foundational backend operation** in trading, risk, and reporting systems.  
While simple, it reveals:
- Ability to model nested data,
- Comfort with Java collections,
- Clarity of thinking in transforming flat lists into structured aggregations.

