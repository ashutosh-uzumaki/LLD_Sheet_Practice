# LLD-01: Singleton Pattern Revision Notes

## 1. Intent

Ensure:

* Only one instance of a class exists.
* Global access point to that instance.

Examples:

* Logger
* Configuration Manager
* Cache Manager
* Connection Pool Manager
* Metrics Manager

---

# 2. When to Use

Use Singleton when:

* Object should exist only once.
* Shared configuration is required.
* Shared resource management is needed.
* Global access is acceptable.

Example:

Logger should have:

* Same sinks
* Same formatter
* Same configuration

across the application.

---

# 3. Basic Singleton

```java
private static Singleton instance;

private Singleton() {}

public static Singleton getInstance() {
    if(instance == null) {
        instance = new Singleton();
    }
    return instance;
}
```

Problem:

Not thread-safe.

---

# 4. Race Condition

Two threads:

```text
Thread-1 -> instance == null
Thread-2 -> instance == null
```

Both create object.

Result:

```text
2 Singleton objects created
```

Requirement violated.

---

# 5. Synchronized Singleton

```java
public static synchronized Singleton getInstance() {
    if(instance == null) {
        instance = new Singleton();
    }
    return instance;
}
```

Pros:

* Thread-safe
* Easy to understand

Cons:

* Lock acquired on every call
* Performance overhead

---

# 6. Double Checked Locking (DCL)

```java
private static volatile Singleton instance;

public static Singleton getInstance() {

    if(instance == null) {

        synchronized (Singleton.class) {

            if(instance == null) {
                instance = new Singleton();
            }
        }
    }

    return instance;
}
```

Goal:

* Synchronize only during first creation.
* Avoid lock after object exists.

---

# 7. Why Two Checks?

First Check:

```java
if(instance == null)
```

Purpose:

Performance.

Avoid synchronization once instance exists.

Second Check:

```java
if(instance == null)
```

inside synchronized block.

Purpose:

Correctness.

Scenario:

```text
Thread-1 -> enters first if
Thread-2 -> enters first if

Thread-1 creates instance

Thread-2 acquires lock later
```

Without second check:

```text
Thread-2 creates another instance
```

---

# 8. Why volatile?

Without volatile:

```java
instance = new Singleton();
```

can be reordered.

Expected:

```text
1. Allocate memory
2. Initialize object
3. Assign reference
```

Possible Reordering:

```text
1. Allocate memory
2. Assign reference
3. Initialize object
```

Another thread may see:

```java
instance != null
```

before initialization completes.

Result:

Partially constructed object.

volatile guarantees:

* Visibility
* Prevents dangerous instruction reordering

Interview Answer:

"volatile prevents another thread from observing a partially initialized object."

---

# 9. Eager Initialization

```java
private static final Singleton INSTANCE =
        new Singleton();

public static Singleton getInstance() {
    return INSTANCE;
}
```

Pros:

* Thread-safe
* Simple

Cons:

* Created even if never used
* Startup overhead possible
* Wasted memory if unused

---

# 10. Holder Idiom

```java
class Singleton {

    private Singleton() {}

    private static class Holder {
        private static final Singleton INSTANCE =
                new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

Why it works:

* JVM class loading is thread-safe.
* Holder class loads only when first accessed.

Benefits:

* Lazy initialization
* Thread-safe
* No synchronized
* No volatile
* No DCL complexity

Preferred in many Java applications.

---

# 11. Singleton Drawbacks

## Hidden Dependencies

Bad:

```java
Logger.getInstance()
```

Dependency not visible.

Good:

```java
PaymentService(Logger logger)
```

Dependency explicit.

---

## Harder Unit Testing

Difficult to replace:

```java
Logger.getInstance()
```

with:

```java
MockLogger
```

during tests.

---

## Global State

Any module can modify shared object.

Tests can affect each other.

Creates coupling.

---

# 12. Singleton vs Spring

In Spring Boot:

Usually avoid manual Singleton.

Reason:

Spring already provides:

```java
@Singleton Scope
```

(default bean scope)

Prefer:

```java
@Autowired
Logger logger;
```

instead of:

```java
Logger.getInstance();
```

---

# 13. SOLID Discussion

## SRP

Logger should not:

* Format logs
* Write logs
* Manage storage

Separate:

```text
Logger
Formatter
Sink
```

---

## OCP

Add:

```text
KafkaSink
DatabaseSink
JsonFormatter
XmlFormatter
```

without modifying existing code.

---

## DIP

Depend on:

```java
Sink
Formatter
```

not concrete implementations.

---

# 14. Interview FAQs

Q. Why Singleton?

A.

Shared infrastructure component requiring one consistent instance and configuration.

---

Q. Why not synchronized getInstance()?

A.

Synchronization overhead on every call.

---

Q. Why DCL?

A.

Synchronize only during first initialization.

---

Q. Why volatile?

A.

Prevents instruction reordering and partially initialized objects.

---

Q. Best Singleton implementation?

A.

For most Java applications:

Holder Idiom.

---

Q. Biggest drawback?

A.

Global state, hidden dependencies, harder testing.

---

# 15. 30-Second Revision

Singleton:

* One instance
* Global access point
* Basic version not thread-safe
* synchronized = safe but slow
* DCL = efficient + thread-safe
* volatile required in DCL
* Holder Idiom = preferred Java solution
* Drawbacks = global state + testing difficulty
* In Spring prefer DI over manual Singleton
