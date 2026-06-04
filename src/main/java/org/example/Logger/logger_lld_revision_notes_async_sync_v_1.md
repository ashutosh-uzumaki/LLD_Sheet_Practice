# Logger LLD Revision Notes (V1)

## 1. Requirements

### Functional Requirements
- Log messages with levels:
  - DEBUG
  - INFO
  - WARN
  - ERROR
  - FATAL
- Multiple sinks support
  - Console
  - File
- Different threshold per sink
- Different formatter per sink
- Clean API:

```java
logger.info("message")
logger.error("payment failed")
```

- Support:
  - Sync logging
  - Async logging

---

## 2. High Level Flow

```text
Logger
   ↓
Create LogEvent
   ↓
Dispatcher Strategy
(sync / async)
   ↓
Sink Template Method
(threshold → formatter → write)
   ↓
ConsoleSink / FileSink
```

---

## 3. Classes and Responsibilities

### Logger

**Responsibility:**
- Orchestrator
- Create `LogEvent`
- Delegate dispatching
- Expose clean APIs

**Methods:**

```java
info()
debug()
warn()
error()
fatal()
```

Internally:

```java
private log(LogLevel level,
            String message)
```

Why?
- Avoid duplicate code
- Single source of truth

Creates:

```java
new LogEvent(
    level,
    message,
    Instant.now()
)
```

Then:

```java
dispatcher.dispatch(event)
```

### Why Singleton?
- Single shared logger instance
- Centralized logging
- Avoid multiple logger objects

### Singleton Used
**DCL (Double Checked Locking)**

Why?
- Lazy initialization
- Thread-safe
- Better performance than synchronized method

Need:

```java
private static volatile Logger instance;
```

Why volatile?
- Prevent instruction reordering
- Avoid partially initialized object visibility

---

### LogEvent

**Responsibility:**
Represents one logging event.

Fields:

```java
private final LogLevel logLevel;
private final String message;
private final Instant timeStamp;
```

Why immutable?
- Thread-safe
- Once event created it should never change

Why `Instant`?
- UTC consistent timestamp
- Same time irrespective of machine timezone

Why timestamp passed from Logger?
- Logger owns event creation
- Correct event creation time

Only getters.

---

### LogLevel (Enum)

Fields:

```java
DEBUG(1)
INFO(2)
WARN(3)
ERROR(4)
FATAL(5)
```

Why explicit priority?

Avoid:

```java
ordinal()
```

Problem:
If new level added in middle:

```text
ordering breaks
```

Method:

```java
shouldLog(LogLevel threshold)
```

Logic:

```java
this.priority >= threshold.priority
```

Why inside enum?
- Domain logic belongs to domain object
- Encapsulation

---

### Formatter (Strategy Pattern)

**Responsibility:**
Convert `LogEvent` → String

Interface:

```java
String format(LogEvent event)
```

Why String return type?
- Sink ultimately writes text
- Clean contract
- Avoid casting and ambiguity

### TextFormatter

Output:

```text
[timestamp] [INFO] message
```

Example:

```text
[2026-05-24T15:02:00Z] [INFO] Payment success
```

### JsonFormatter

Output:

```json
{"timestamp":"...","message":"...","level":"INFO"}
```

Why compact JSON?
- Production logging style
- Easy parsing
- One event = one line
- Better for log aggregation

Why StringBuilder?
- Simpler
- No extra dependency
- Interview friendly

Production alternative:
- Jackson
- Gson

Pattern Used:

```text
Strategy Pattern
```

Why?
- Formatting behavior changes
- Plug-and-play formatter

---

### Sink (Template Method Pattern)

**Responsibility:**
Own logging pipeline.

Pipeline:

```text
threshold check
      ↓
format
      ↓
write
```

Fields:

```java
private final Formatter formatter;
private final LogLevel threshold;
```

Why private?
- Child does not need access
- Encapsulation
- Least exposure principle

Constructor Injection:

```java
Sink(Formatter formatter,
     LogLevel threshold)
```

Why constructor injection?
- Mandatory dependency
- Valid object from birth
- Stable configuration

### Template Method

```java
public final void log(LogEvent event)
```

Why final?
- Prevent child from breaking flow

Flow:

```java
threshold check
formatter.format(event)
write(formattedLog)
```

### Why write() protected?

```java
protected abstract
void write(String formattedLog)
```

Why protected?
- Internal implementation detail
- Child should override
- Outside world should not call it

Why String parameter?
- Sink owns formatting
- Child only writes destination

Pattern Used:

```text
Template Method Pattern
```

Changing step:

```text
write()
```

Fixed steps:

```text
threshold
format
workflow
```

---

### ConsoleSink

Responsibility:

```text
write to console
```

Only:

```java
System.out.println()
```

Very tiny class.

Why?
- Parent handles threshold + formatting

---

### FileSink

Extra field:

```java
private final Path filePath;
```

Why `Path` not String?
- Modern Java API
- Better semantics
- Platform independent

Uses:

```java
Files.writeString()
```

Options:

```java
StandardOpenOption.CREATE
StandardOpenOption.APPEND
```

Why append?
- Logs are history
- Avoid overwrite

Why `System.lineSeparator()`?
- Cross platform newline

Thread safety concern:
- Multiple threads writing file

Potential solution:

```text
synchronize sink
```

---

## 4. Dispatcher Strategy

### Why Introduced?

Question:
What changes?

Only:

```text
delivery mechanism
```

Sync:

```text
immediate dispatch
```

Async:

```text
queue + worker thread
```

Pattern:

```text
Strategy Pattern
```

---

### LogDispatcher

Responsibility:

```text
delivery strategy
```

Method:

```java
dispatch(LogEvent event)
```

---

### SyncDispatcher

Flow:

```java
for(Sink sink : sinks){
    sink.log(event);
}
```

Immediate dispatch.

---

### AsyncDispatcher

Flow:

```text
Producer Thread
(logger)
      ↓
queue.put(event)
      ↓
return immediately
```

Background Worker:

```text
queue.take()
      ↓
dispatch to sinks
```

Fields:

```java
BlockingQueue<LogEvent>
List<Sink>
Thread workerThread
```

Queue Used:

```java
BlockingQueue<LogEvent>
```

Why?
- Thread-safe
- Producer consumer built-in
- No wait/notify complexity

### Why take() not poll()?

```java
take()
```

Why?
- Blocks automatically
- No CPU busy waiting

### Why while(true)?

Without:

```text
thread processes one event
then dies
```

With:

```text
continuously consume logs
```

### Why worker starts in constructor?

Avoid:

```text
forgot-to-start bug
```

Object ready from birth.

### Daemon thread or normal thread?

Use:

```text
normal thread
```

Why?
- JVM waits
- Remaining logs can flush

Daemon thread problem:

```text
JVM kills it immediately
logs may be lost
```

Production:

```text
shutdown()
queue draining
```

---

## 5. Relationships

### Logger → Dispatcher

```text
Composition
```

Why?
- Logger owns dispatcher

### Logger → LogEvent

```text
Dependency
```

Logger creates event.

### Sink → Formatter

```text
Composition
```

Sink cannot exist meaningfully without formatter.

### Sink → LogLevel

```text
Composition
```

Threshold part of sink config.

### ConsoleSink IS-A Sink

```text
Inheritance
```

### FileSink IS-A Sink

```text
Inheritance
```

### LogEvent uses LogLevel

```text
Composition
```

---

## 6. Design Patterns Used

### Singleton

```text
Logger
```

Why?
- Single logging instance

### Strategy Pattern

```text
Formatter
Dispatcher
```

Why?
- Behavior changes independently

### Template Method Pattern

```text
Sink
```

Why?
- Common flow fixed
- Writing behavior changes

---

## 7. Thread Safety Discussion

### Singleton Creation

Thread-safe?

```text
YES
```

Because:

```text
volatile
DCL
synchronized
```

### Logging Operation

Initially:

```text
NOT thread-safe
```

Problem:

```text
multiple threads writing file
```

Better solution:

```text
synchronize at sink level
```

Why not logger level?

Avoid:

```text
coarse-grained locking
all logging blocked
```

---

## 8. Interview Improvements

Possible future improvements:

```text
Async Logger
Queue draining
Shutdown hook
File rotation
JSON escaping
BufferedWriter
Batch writes
Retry policy
Database sink
Kafka sink
MDC / correlation ID
Global config
```

---

## 9. Quick Revision Questions

Ask yourself:

```text
Why Sink abstract?
Why write protected?
Why log final?
Why formatter strategy?
Why dispatcher strategy?
Why BlockingQueue?
Why take() not poll()?
Why while(true)?
Why constructor injection?
Why Instant?
Why not ordinal()?
Why DCL + volatile?
Why sync at sink level?
```

