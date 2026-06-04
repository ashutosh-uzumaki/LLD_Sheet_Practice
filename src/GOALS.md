# SDE-2 LLD + HLD Interview Preparation Roadmap

**Duration:** 4 Weeks
**Time Commitment:** 10 Hours / Week
**Goal:** Start applying after 4 weeks

---

# Objective

Prepare for:

* 45-minute LLD interview rounds
* Variations handling
* UML diagram discussion
* Test case writing
* Concurrency handling
* HLD discussion

    * Scalability
    * Reliability
    * Fault tolerance
* SDE-2 backend-focused design discussions

---

# Universal Practice Template (For Every Question)

For **every system design question**, follow this exact structure.

## 1. Requirement Clarification

### Functional Requirements

### Non-functional Requirements

### Scope Boundaries

---

## 2. Entity Extraction

Identify:

* Core entities
* Supporting entities
* Enums
* Interfaces

---

## 3. Responsibility Ownership

Assign responsibilities to classes.

Questions:

* Who owns what?
* Which class should control behavior?
* Avoid God classes

---

## 4. Relationships

Model:

* Association
* Aggregation
* Composition
* Inheritance

---

## 5. UML Diagram

Must include:

* Classes
* Interfaces
* Relationships
* Cardinality (where applicable)

---

## 6. Core APIs / Main Flows

Examples:

* create()
* update()
* reserve()
* release()
* process()

Also include:

* Sequence walkthrough

---

## 7. Design Patterns

For every question discuss:

### Pattern Used

### Why Used

### Alternatives

### Tradeoffs

---

## 8. Concurrency Handling

Questions to answer:

* Race conditions?
* Shared mutable state?
* Lock granularity?
* Thread safety?

Java Concepts:

* synchronized
* ReentrantLock
* ReadWriteLock
* ConcurrentHashMap
* AtomicInteger
* ExecutorService
* BlockingQueue
* CompletableFuture

---

## 9. Test Cases

Must include:

### Happy Path

### Edge Cases

### Failure Cases

### Concurrent Request Cases

---

## 10. HLD Thinking

For every system answer:

### Scalability

How does system scale?

### Reliability

How are failures handled?

### Fault Tolerance

What happens if service crashes?

### Bottlenecks

Potential performance issues?

### DB Choice

SQL vs NoSQL?

### Cache

What can be cached?

### Queueing

Kafka / async processing?

---

## 11. Variations

Answer:

> "What changes in your design?"

This is mandatory.

---

# WEEK 1 (10 HOURS)

# Java OOP + SOLID + Design Patterns

## OOP in Java

1. Encapsulation
2. Abstraction
3. Inheritance
4. Polymorphism
5. Composition vs Inheritance ⭐
6. Association vs Aggregation vs Composition ⭐
7. Interface vs Abstract Class ⭐
8. Immutable Objects
9. equals() & hashCode()
10. Deep Copy vs Shallow Copy
11. Dependency Injection Basics
12. Object Modeling

---

## SOLID Principles

1. SRP
2. OCP
3. LSP
4. ISP
5. DIP

For each principle:

* Bad implementation
* Fix yourself first
* Correct implementation
* Real-world example
* LLD usage

---

## High ROI Design Patterns

### Creational

1. Factory Pattern
2. Abstract Factory Pattern
3. Builder Pattern
4. Singleton Pattern

### Structural

5. Decorator Pattern
6. Adapter Pattern
7. Composite Pattern
8. Facade Pattern

### Behavioral

9. Strategy Pattern ⭐
10. Observer Pattern ⭐
11. State Pattern ⭐
12. Chain of Responsibility ⭐
13. Command Pattern
14. Template Method Pattern

For every pattern:

* Implement in Java
* Real-world use case
* LLD interview usage

---

# WEEK 2 (10 HOURS)

# Core LLD Foundations

## 1. Parking Lot ⭐⭐⭐⭐⭐

### Variations

* Reserved parking
* EV charging slots
* Dynamic pricing
* Nearest slot allocation
* Multi-entry/exit gates
* Parking history
* Concurrent slot assignment

### Focus

* OOP fundamentals
* Composition
* Strategy Pattern
* Concurrency

---

## 2. Vending Machine ⭐⭐⭐⭐⭐

### Variations

* Refund
* Coupons
* Inventory refill
* Dynamic pricing
* Multiple payment methods

### Focus

* State Pattern
* Workflow modeling

---

## 3. ATM System ⭐⭐⭐⭐

### Variations

* Multiple banks
* PIN retry limit
* Rollback
* Cash denomination optimization
* Card blocking

### Focus

* State machine
* Transaction modeling

---

## 4. Elevator System ⭐⭐⭐⭐⭐

### Variations

* VIP mode
* Peak-hour optimization
* Fire emergency mode
* Maintenance mode

### Focus

* Scheduling
* Strategy Pattern

---

# WEEK 3 (10 HOURS)

# Booking + Product Systems

## 5. BookMyShow ⭐⭐⭐⭐⭐

### Variations

* Seat lock expiry
* Concurrent booking
* VIP seats
* Coupons
* Refund
* Dynamic pricing
* Waitlist

### Focus

* Concurrency
* Locking
* State transitions

---

## 6. Hotel Booking System ⭐⭐⭐⭐⭐

### Variations

* Room categories
* Dynamic pricing
* Loyalty membership
* Cancellation policy
* Overbooking

### Focus

* Availability modeling

---

## 7. Event Management System ⭐⭐⭐⭐⭐

### Variations

* VIP tickets
* Tiered pricing
* QR validation
* Multi-day events
* Event cancellation
* Capacity limits

### Focus

* Ticket lifecycle

---

## 8. Meeting Scheduler ⭐⭐⭐⭐

### Variations

* Recurring meetings
* Room booking
* Time-zone support
* Conflict resolution
* Auto-rescheduling

### Focus

* Time modeling

---

## 9. Spotify ⭐⭐⭐⭐⭐

### Variations

* Shuffle mode
* Repeat mode
* Premium vs Free
* Offline mode
* Collaborative playlists
* Device sync

### Focus

* Strategy Pattern
* Observer Pattern

---

# WEEK 4 (10 HOURS)

# Real Product + Backend Systems

## 10. Cab Booking System (Uber/Ola) ⭐⭐⭐⭐⭐

### Variations

* Surge pricing
* Driver reassignment
* Ride pooling
* Scheduled rides
* Multiple vehicle categories

### Focus

* Matching logic
* Strategy Pattern

---

## 11. Food Delivery System ⭐⭐⭐⭐⭐

### Variations

* Coupons
* Live tracking
* Delivery reassignment
* Refund handling
* Multi-order batching

### Focus

* State transitions

---

## 12. Splitwise ⭐⭐⭐⭐⭐

### Variations

* Equal split
* Exact split
* Percentage split
* Simplify debt graph
* Multi-currency

### Focus

* Domain modeling

---

## 13. Inventory Management System ⭐⭐⭐⭐⭐

### Base

* Add inventory
* Update stock
* Reserve stock
* Release stock
* Deduct stock

### Variations

* Multi-warehouse inventory
* Reserved vs available inventory
* Low stock alerts
* Inventory reconciliation
* Concurrent stock updates
* Rollback after payment failure
* Batch/expiry handling
* SKU variants (size/color)
* Idempotency handling

### Focus

* Concurrency
* Consistency
* State transitions
* Reservation model

### HLD Thinking

* Distributed inventory
* Cache invalidation
* Event-driven updates
* Prevent overselling

---

## 14. Notification System ⭐⭐⭐⭐⭐

### Variations

* Email/SMS/Push
* Retry mechanism
* Scheduling
* Priority notifications
* User preferences

### Focus

* Observer Pattern
* Strategy Pattern

---

## 15. Logger Framework ⭐⭐⭐⭐⭐

### Variations

* Async logging
* Multiple appenders
* Filtering
* File rotation

### Focus

* Chain of Responsibility
* Decorator Pattern

---

## 16. Job Scheduler ⭐⭐⭐⭐⭐

### Variations

* Retry
* Cron scheduling
* Delayed jobs
* Priority jobs
* Dependency graph

### Focus

* Scheduling
* Queueing

---

# Tier 2 (Continue While Applying)

## 17. Rate Limiter

### Variations

* Fixed Window
* Sliding Window
* Token Bucket
* Distributed limiter

---

## 18. Cache (LRU / LFU / TTL)

### Variations

* TTL
* LFU
* Thread-safe cache
* Distributed cache

---

## 19. E-commerce Order System

### Variations

* Refund
* Partial cancellation
* Coupons
* Inventory shortage

---

## 20. Wallet / Payment Gateway

### Variations

* Retry
* Refund
* Multiple providers
* Webhooks
* Fraud hooks

---

## 21. Calendar System

## 22. File System

## 23. Jira / Trello

## 24. API Gateway

## 25. URL Shortener (LLD)

## 26. Online IDE

## 27. Chess (Later)

---

# Interview Success Checklist

For EVERY question:

* [ ] Solve base question in < 45 mins
* [ ] Handle 3+ variations confidently
* [ ] Explain UML diagram
* [ ] Write test cases
* [ ] Explain concurrency handling
* [ ] Discuss scalability
* [ ] Discuss reliability
* [ ] Discuss fault tolerance
* [ ] Explain tradeoffs
* [ ] Defend design decisions
