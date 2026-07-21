# LLD-01: Singleton Pattern - Complete Revision Cheat Sheet (SDE-2 / 4+ YOE)

---

# 1. Intent

**Definition**

Ensure a class has only one instance and provide a global access point to it.

---

# 2. When to Use

Use Singleton when:

* Exactly one instance should exist.
* Shared configuration is required.
* Shared resource management is required.
* Object creation is expensive.
* Global access is acceptable.

Examples:

* Logger
* Configuration Manager
* Cache Manager
* Metrics Collector
* Connection Pool Manager

---

# 3. Basic Singleton (Not Thread Safe)

```java
public class Logger {

    private static Logger instance;

    private Logger(){}

    public static Logger getInstance() {
        if(instance == null){
            instance = new Logger();
        }
        return instance;
    }
}
```

Problem:

Two threads can create two different instances.

---

# 4. Race Condition

Example:

```
Thread-1
instance == null

Thread-2
instance == null

Thread-1
new Logger()

Thread-2
new Logger()
```

Result:

Two Singleton objects.

---

# 5. Synchronized Singleton

```java
public static synchronized Logger getInstance() {

    if(instance == null){
        instance = new Logger();
    }

    return instance;
}
```

Advantages

* Simple
* Thread-safe

Disadvantages

* Synchronization on every call
* Poor scalability
* Lock acquired even after initialization

---

# 6. Double Checked Locking (DCL)

```java
public class Logger {

    private static volatile Logger instance;

    private Logger(){}

    public static Logger getInstance() {

        if(instance == null){

            synchronized (Logger.class){

                if(instance == null){
                    instance = new Logger();
                }
            }
        }

        return instance;
    }
}
```

---

# 7. Why Two Null Checks?

## First Check

```java
if(instance == null)
```

Purpose:

Performance.

Avoid locking after object has already been created.

---

## Second Check

```java
if(instance == null)
```

Purpose:

Correctness.

Scenario:

```
Thread-1
passes first check

Thread-2
passes first check

Thread-1
creates instance

Thread-2
acquires lock later
```

Without second check:

```
Thread-2 creates another object.
```

---

# 8. Why volatile?

volatile provides TWO guarantees.

## A. Visibility

Writes by one thread become immediately visible to other threads.

Without volatile:

```
Thread-1

shutdown = false

Thread-2

shutdown = true

Thread-1
may continue seeing false
```

With volatile:

```
Thread-1 immediately observes true.
```

---

## B. Prevents Instruction Reordering

Object creation conceptually:

```
1. Allocate memory
2. Initialize object
3. Assign reference
```

Possible reordering without volatile:

```
1. Allocate memory
2. Assign reference
3. Initialize object
```

Another thread sees:

```
instance != null
```

but object initialization has not completed.

Result:

Partially initialized object.

volatile prevents this unsafe publication.

---

# 9. Why synchronized Alone Isn't Enough?

Synchronization protects object creation.

It does NOT guarantee that another thread cannot observe a partially initialized object after the reference is published.

volatile is required for safe publication in DCL.

---

# 10. Bill Pugh Holder Idiom

```java
public final class Logger {

    private Logger(){}

    private static class Holder {

        private static final Logger INSTANCE =
                new Logger();
    }

    public static Logger getInstance() {
        return Holder.INSTANCE;
    }
}
```

---

# 11. Why Bill Pugh Works

JVM guarantees:

* Class initialization is thread-safe.
* Nested static class is loaded only when first referenced.

Flow:

```
Application starts

↓

Logger class loaded

↓

Holder NOT loaded

↓

getInstance()

↓

Holder loaded

↓

INSTANCE created exactly once
```

Advantages

* Lazy initialization
* Thread-safe
* No synchronized
* No volatile
* Cleaner implementation

Preferred for plain Java applications.

---

# 12. Eager Initialization

```java
public class Logger {

    private static final Logger INSTANCE =
            new Logger();

    private Logger(){}

    public static Logger getInstance(){
        return INSTANCE;
    }
}
```

Advantages

* Very simple
* Thread-safe

Disadvantages

* Object created even if never used.
* Increased startup cost.
* Possible memory waste.

---

# 13. Multiton Pattern (Singleton Variation)

Requirement:

One ConfigManager per tenant.

Implementation:

```java
public class ConfigManager {

    private final String tenantId;

    private static final ConcurrentHashMap<String, ConfigManager>
            tenantMap = new ConcurrentHashMap<>();

    private ConfigManager(String tenantId){
        this.tenantId = tenantId;
    }

    public static ConfigManager getInstance(String tenantId){

        return tenantMap.computeIfAbsent(
                tenantId,
                ConfigManager::new
        );
    }
}
```

Why ConcurrentHashMap?

* Thread-safe
* Atomic computeIfAbsent()
* Fine-grained synchronization

---

# 14. Why computeIfAbsent?

Avoids:

```java
containsKey()

put()

get()
```

Race condition:

```
Thread-1
containsKey == false

Thread-2
containsKey == false
```

Both create object.

computeIfAbsent performs:

```
Check

↓

Create if absent

↓

Insert

↓

Return
```

atomically.

---

# 15. Lock Granularity

Method Synchronization

```
Thread-1 -> tenantA

Thread-2 -> tenantB

↓

Both compete for same lock
```

Poor scalability.

ConcurrentHashMap

```
tenantA

tenantB

tenantC
```

Different keys proceed concurrently.

Only same-key creation synchronizes.

---

# 16. Singleton vs Spring

Avoid:

```java
Logger.getInstance()
```

Prefer:

```java
@Autowired
Logger logger;
```

Reason:

Spring already manages singleton beans.

Default Spring bean scope:

```
singleton
```

Benefits:

* Better testability
* Dependency Injection
* Lower coupling

---

# 17. Drawbacks of Singleton

## Hidden Dependencies

Bad

```java
Logger.getInstance()
```

Good

```java
OrderService(Logger logger)
```

---

## Harder Testing

Cannot easily replace Singleton with:

* Mock
* Fake
* Stub

---

## Global State

Every module shares same object.

One module can affect another.

---

## Tight Coupling

Classes become tightly coupled to Singleton implementation.

---

# 18. SOLID Principles

## SRP

Logger

Should NOT:

* Format logs
* Write logs
* Manage storage

Separate:

* Logger
* Sink
* Formatter

---

## OCP

Add:

* KafkaSink
* DatabaseSink
* JsonFormatter
* XmlFormatter

without modifying Logger.

---

## DIP

Depend on:

```java
Sink

Formatter
```

instead of concrete implementations.

---

# 19. Complexity

| Implementation | First Creation |       Subsequent Calls |
| -------------- | -------------: | ---------------------: |
| Basic          |           O(1) |                   O(1) |
| Synchronized   |           O(1) | O(1) + synchronization |
| DCL            |           O(1) |                   O(1) |
| Bill Pugh      |           O(1) |                   O(1) |
| Multiton       |           O(1) |                   O(1) |

---

# 20. Interview FAQs

### Why Singleton?

Shared infrastructure component requiring one consistent instance.

---

### Why private constructor?

Prevent external instantiation.

---

### Why static instance?

Shared across all callers.

---

### Why first null check?

Avoid unnecessary synchronization after initialization.

---

### Why second null check?

Prevent multiple creations when several threads pass the first check.

---

### Why volatile?

Provides visibility and prevents instruction reordering.

---

### Why synchronized isn't enough?

It protects creation but doesn't prevent unsafe publication without volatile.

---

### Why Bill Pugh?

Relies on JVM class-loading guarantees.

Cleaner than DCL.

---

### Why ConcurrentHashMap for Multiton?

Allows thread-safe, atomic, per-key lazy initialization with minimal contention.

---

### Which Singleton would you use?

**Plain Java**

→ Bill Pugh Holder Idiom

**Spring Boot**

→ Prefer Dependency Injection and let Spring manage singleton beans.

---

# 21. Interview One-Liners

**Singleton**

One instance per JVM.

**Multiton**

One instance per key.

**volatile**

Visibility + prevents instruction reordering.

**DCL**

Synchronize only during first initialization.

**Bill Pugh**

Uses JVM class loading for lazy, thread-safe initialization.

**Spring**

Prefer DI over manual Singleton.

---

# 22. Revision Flow (30 Seconds)

```
Singleton

↓

Need only one object?

↓

Thread safety?

↓

DCL

↓

Why volatile?

↓

Visibility + no instruction reordering

↓

Better approach?

↓

Bill Pugh Holder

↓

Need one instance per tenant?

↓

Multiton

↓

Spring?

↓

Use DI instead of manual Singleton.
```

---

# Final Interview Verdict

After mastering this sheet, you should be able to:

✅ Implement Basic Singleton

✅ Implement Synchronized Singleton

✅ Implement Double Checked Locking

✅ Explain volatile

✅ Explain instruction reordering

✅ Implement Bill Pugh Holder Idiom

✅ Implement Multiton

✅ Discuss Spring Singleton vs Manual Singleton

✅ Answer all common SDE-2 Singleton interview follow-up questions confidently.
