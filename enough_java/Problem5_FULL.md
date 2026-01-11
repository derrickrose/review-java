# Problem 5 — Intraday Risk Engine System Design (Full Detailed Version)

## Context
Trading systems must continuously monitor risk throughout the trading day.  
An **intraday risk engine** receives:
- Real-time trades,
- Market data updates,
- Position changes,

and must compute updated risk metrics **immediately and reliably**.

This problem tests senior-level system design skills in:
- High throughput ingestion,
- Real-time computation,
- Caching,
- Fault tolerance,
- Event-driven architecture,
- Horizontal scaling.

It is NOT about financial math — the focus is on **system architecture**.

---

## Requirements

### 1. **Inputs to the system**
The risk engine must consume:

#### ✔ Trades
Executed trades change client positions.

#### ✔ Market data
Prices and volatility feed risk calculations.

#### ✔ Portfolio updates
Manual adjustments, corrections, cancellations.

Each of these arrives as a real-time event stream.

---

### 2. **Core responsibilities**

#### ✔ Maintain live positions  
- Update per-client, per-instrument positions.
- Store safely and efficiently.
- Persist state for crash recovery.

#### ✔ Recompute risk metrics  
For every new event (trade or market tick):
- Update position
- Fetch required market data
- Compute risk metrics (e.g., exposure)

Risk computation must be **incremental**, not full recomputation.

#### ✔ Expose query APIs  
- `GET /risk/{clientId}`
- `GET /risk/all`
- `GET /positions/{clientId}`

Queries must be:
- Low-latency
- Consistent
- Efficient

---

## High-Level Architecture

### 1. **Event Ingestion Layer**
A high-throughput message bus (Kafka-like) receives:
- Trades
- Market data
- Position events

Producers push updates; consumers subscribe.

---

### 2. **Risk Service (Real-Time Compute Engine)**

Each instance:
- Subscribes to event streams
- Maintains in-memory state
- Computes incremental updates

To scale horizontally:
- Partition by clientId
- Each partition handled by exactly one instance

This ensures:
- No locking
- No cross-node state contention

---

### 3. **In-Memory State**
Store:
- Positions map  
- Market data snapshot  
- Current risk metrics  

Data structures must:
- Provide O(1) access
- Support very fast updates
- Be snapshotted to disk

---

### 4. **Storage Layer**
Persist:
- Event log (append-only)
- Snapshots (checkpoints)
- Rebuild state after crash using:
  - Last snapshot
  - Replay of events after snapshot

---

### 5. **API Layer**
A lightweight REST or gRPC service.

Endpoints query the local in-memory risk state.

If running multiple compute nodes:
- API router routes queries to the correct partition.

---

## Performance Requirements
- Process thousands of trades per second.
- Market data at 10,000+ ticks/sec.
- Risk recomputation < 5ms.
- API response < 10ms.
- Horizontal scale-out capability.

---

## Fault Tolerance
- Event log ensures lossless ingestion.
- Periodic snapshots allow fast recovery.
- If a compute node fails:
  - Its partition is reassigned.
  - Rebuilt from snapshot + replay.

---

## Evaluation Criteria (Interview)

### Technical Depth
- Understanding partitioning strategies  
- Efficient in-memory state management  
- Event-driven architecture clarity  

### Practical Considerations
- Handling backpressure  
- Snapshotting frequency  
- Crash recovery  
- Hot standby servers  

### Communication
- Ability to draw a coherent architecture  
- Identifying bottlenecks  
- Prioritizing latency and throughput  

---

## Summary
An intraday risk engine is a **stateful real-time compute system**, not a batch job.  
Your design must balance:
- Performance
- Reliability
- Scalability
- Simplicity

This question is standard for senior backend roles in finance.

