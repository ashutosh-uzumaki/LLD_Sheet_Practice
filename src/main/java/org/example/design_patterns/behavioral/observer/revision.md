# Observer Pattern — Revision Document
> Behavioral Pattern | One-to-Many | Event-Driven

---

## UML Structure

```
                    +-----------------------------+
                    |   <<interface>>             |
                    |   OrderObserver             |
                    +-----------------------------+
                    | + onOrderPlaced(event)      |
                    +-------------^---------------+
                                  |
                    implements    |
              ┌───────────────────┤
              │                   │
+-------------+------+  +---------+----------+
| EmailService       |  | InventoryService   |
+--------------------+  +--------------------+
| onOrderPlaced(e)   |  | onOrderPlaced(e)   |
+--------------------+  +--------------------+

                    +-----------------------------+
                    |   Order  (Subject)          |
                    +-----------------------------+
                    | - orderId: String           |
                    | - productIds: List<String>  |
                    | - amount: BigDecimal        |
                    | - observers: List<Observer> |
                    +-----------------------------+
                    | + registerObserver()        |
                    | + unregisterObserver()      |
                    | + placeOrder()              |
                    | - onOrderPlaced()  ← private|
                    +-----------------------------+

                    +-----------------------------+
                    |   OrderPlaceEvent           |
                    +-----------------------------+
                    | - orderId: String (final)   |
                    | - productIds: List  (final) |
                    | - amount: BigDecimal (final)|
                    +-----------------------------+
                    | + getOrderId()              |
                    | + getProductIds()           |
                    | + getAmount()               |
                    +-----------------------------+
```

---

## Complete Java Implementation

### OrderObserver (Contract)

```java
public interface OrderObserver {
    void onOrderPlaced(OrderPlaceEvent event);
}
```

---

### OrderPlaceEvent (Immutable Data Carrier)

```java
public class OrderPlaceEvent {
    private final String orderId;
    private final List<String> productIds;
    private final BigDecimal amount;

    public OrderPlaceEvent(String orderId, List<String> productIds, BigDecimal amount) {
        this.orderId = Objects.requireNonNull(orderId, "orderId cannot be null");
        this.productIds = new ArrayList<>(Objects.requireNonNull(productIds, "productIds cannot be null"));
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
    }

    public String getOrderId() { return orderId; }

    public List<String> getProductIds() {
        return Collections.unmodifiableList(productIds); // defensive — caller cannot mutate
    }

    public BigDecimal getAmount() { return amount; }

    @Override
    public String toString() {
        return "OrderPlaceEvent{orderId='" + orderId + "', productIds=" + productIds + ", amount=" + amount + '}';
    }
}
```

**Why every decision:**

| Decision | Reason |
|---|---|
| `private final` on all fields | Immutable after construction — safe to share across threads |
| `new ArrayList<>()` in constructor | Defensive copy — caller can't corrupt the event after handing it in |
| `Objects.requireNonNull` | Fail fast at boundary — not mid-notification |
| `Collections.unmodifiableList` in getter | Defensive — observer can't mutate internal list |
| `toString()` override | Readable logs — default prints `OrderPlaceEvent@6d06d69c` |

---

### Order (Subject)

```java
public class Order {
    private final String orderId;
    private final List<String> productIds;
    private final BigDecimal amount;
    private final List<OrderObserver> observers;

    public Order(String orderId, List<String> productIds, BigDecimal amount) {
        this.orderId = Objects.requireNonNull(orderId, "orderId cannot be null");
        this.productIds = new ArrayList<>(Objects.requireNonNull(productIds, "productIds cannot be null"));
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        observers = new CopyOnWriteArrayList<>(); // thread-safe — reads >> writes
    }

    public void registerObserver(OrderObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void unregisterObserver(OrderObserver observer) {
        observers.remove(Objects.requireNonNull(observer));
    }

    public void placeOrder() {
        OrderPlaceEvent event = new OrderPlaceEvent(this.orderId, this.productIds, this.amount);
        System.out.println("Order placed: " + orderId);
        onOrderPlaced(event);
    }

    private void onOrderPlaced(OrderPlaceEvent event) { // private — internal mechanism
        for (OrderObserver observer : observers) {
            observer.onOrderPlaced(event);
        }
    }
}
```

**Why every decision:**

| Decision | Reason |
|---|---|
| `private final` fields | Order state doesn't change after construction |
| `new ArrayList<>()` on productIds | Caller can't mutate Order's internal list |
| `CopyOnWriteArrayList` for observers | Thread-safe iteration — no ConcurrentModificationException |
| `Objects.requireNonNull` in register | Prevent null observers silently poisoning the list |
| `private onOrderPlaced()` | Notification is an internal concern — outsiders call `placeOrder()` only |
| `placeOrder()` builds event from `this` | Order already holds all data — don't make caller re-pass it |

---

### Concrete Observers

```java
public class EmailService implements OrderObserver {
    @Override
    public void onOrderPlaced(OrderPlaceEvent event) {
        System.out.println("Sending email for: " + event);
    }
}

public class InventoryService implements OrderObserver {
    @Override
    public void onOrderPlaced(OrderPlaceEvent event) {
        System.out.println("Reducing inventory for orderId: "
            + event.getOrderId()
            + " products: " + event.getProductIds());
    }
}
```

---

### Demo (Main)

```java
public class ObserverDemo {
    public static void main(String[] args) {
        Order order = new Order("12", List.of("P1", "P2"), new BigDecimal("1999.00"));

        order.registerObserver(new EmailService());
        order.registerObserver(new InventoryService());

        order.placeOrder();

        // Output:
        // Order placed: 12
        // Sending email for: OrderPlaceEvent{orderId='12', productIds=[P1, P2], amount=1999.00}
        // Reducing inventory for orderId: 12 products: [P1, P2]
    }
}
```

---

## Notification Flow

```
order.placeOrder()
        │
        ▼
Build OrderPlaceEvent (from this.orderId, this.productIds, this.amount)
        │
        ▼
private onOrderPlaced(event)
        │
        ▼
for each observer in CopyOnWriteArrayList
        │
        ├──▶ emailService.onOrderPlaced(event)
        │
        ├──▶ inventoryService.onOrderPlaced(event)
        │
        └──▶ analyticsService.onOrderPlaced(event)
```

---

## When to Reach for Observer

**The single question:**
> Does one state change need to trigger N independent reactions, where the subject shouldn't know who's reacting?

**Recognition keywords in interviews:**
- "notify everyone", "subscribers", "interested parties"
- "event occurred", "broadcast changes"
- "one change triggers many actions"

**The checklist:**

```
✅ 1 → N fan-out
✅ Subject shouldn't know who's listening
✅ Observers change independently of subject
✅ Fire and forget (subject doesn't need a response)
✅ Same JVM (otherwise use Kafka)
```

---

## When NOT to Use Observer

| Situation | Why Observer is wrong |
|---|---|
| Only one thing reacts | Overkill — direct call is simpler |
| Reactions must happen in strict order | Observer gives no ordering guarantee |
| Subject needs observer's response | Observer is fire-and-forget — subject gets nothing back |
| Cross-service, async, needs replay | Use Kafka / message broker instead |

---

## SOLID Principles Applied

| Principle | How Observer applies |
|---|---|
| **SRP** | Order manages order state only. Email logic stays in EmailService. Each class has one reason to change. |
| **OCP** | Add WhatsAppService? Create `class WhatsAppService implements OrderObserver`. Zero changes to Order. |
| **DIP** | Order depends on `OrderObserver` interface — not on `EmailService` or `InventoryService` concretions. |
| **ISP** | Observer interface exposes only `onOrderPlaced(event)`. Nothing unnecessary. |

---

## Observable Interface (GoF vs Your Design)

**GoF canonical version** — two interfaces:

```java
public interface Observable {
    void registerObserver(OrderObserver o);
    void unregisterObserver(OrderObserver o);
    void notifyObservers(OrderPlaceEvent e);
}
```

**When to add it:**

| Situation | Decision |
|---|---|
| Only one subject (Order) | Skip it — your current design is cleaner |
| Multiple subjects (Order, Payment, Shipment) | Add `AbstractObservable` to avoid duplicating plumbing |

---

## Thread Safety

**The problem:**

```
Thread A → iterating observers in onOrderPlaced()
Thread B → calls unregisterObserver() → removes one observer

Result → ConcurrentModificationException
```

ArrayList's iterator captures `modCount` at start. Any structural change increments `modCount`. On next `iterator.next()` — mismatch detected → exception thrown.

**Three fixes compared:**

| Fix | How | When |
|---|---|---|
| `CopyOnWriteArrayList` | Every write makes a fresh array copy. Readers always see stable snapshot. | ✅ Best for Observer — reads >> writes |
| Snapshot copy | `new ArrayList<>(observers)` before iterating | Simple, works when reads and writes are roughly equal |
| `synchronized` on all methods | Locks the whole list | ❌ Risky — alien method call under lock → deadlock |

**Why not synchronized:**

```java
// DANGER — alien method call while holding lock
private synchronized void onOrderPlaced(OrderPlaceEvent event) {
    for (OrderObserver observer : observers) {
        observer.onOrderPlaced(event); // ← you don't own this code
        // if observer calls registerObserver() internally → DEADLOCK
    }
}
```

---

## Memory Leak Trap

**The scenario:**

```java
// Long-lived subject
EventBus eventBus = new EventBus();

// Short-lived observers registered but never unregistered
for (each request) {
    RequestHandler handler = new RequestHandler();
    eventBus.registerObserver(handler); // ← never removed
}
```

`eventBus` holds strong references to thousands of dead `RequestHandler` objects. GC cannot collect them — `eventBus` is alive and pointing to them.

**Two fixes:**

```java
// Fix 1 — Explicit unregister (default choice)
order.registerObserver(emailService);
order.placeOrder();
order.unregisterObserver(emailService); // ← always clean up

// Fix 2 — Weak references (when you can't control observer lifecycle)
private final List<WeakReference<OrderObserver>> observers = new CopyOnWriteArrayList<>();
```

---

## Observer vs Kafka

| | Observer (Code Level) | Kafka (Infrastructure Level) |
|---|---|---|
| Scope | Same JVM, in-memory | Cross-service, network |
| Coupling | Subject holds direct reference | Producer has zero knowledge of consumers |
| Delivery | Synchronous by default | Asynchronous, persistent |
| Replay | No | Yes |
| Use when | Lightweight, in-process fan-out | Cross-service, async, needs durability |

**The connection:** Kafka IS Observer at infrastructure level. Same intent — one event, N independent reactions, producer doesn't know who's listening — different altitude.

---

## Observer vs Other Patterns

| Pattern | How it differs from Observer |
|---|---|
| **Strategy** | One caller picks ONE implementation. Only one executes. Observer notifies ALL. |
| **Chain of Responsibility** | Request passes along a chain until one handler handles it. Observer notifies all — no chain. |
| **Mediator** | Central coordinator manages communication between objects. Observer is direct subject → observer. |

---

## Production Considerations

### Async Notification (Preferred in Production)

```
Synchronous (your current impl):        Asynchronous (production):
Order                                   Order
  ↓                                       │
Email     ← blocks                  ┌─────┼─────┐
  ↓                                  ▼     ▼     ▼
SMS       ← blocks               Email   SMS  Analytics
  ↓                              (parallel, independent)
Analytics ← blocks
```

### Transaction Safety

```
❌ Wrong:                           ✅ Correct:
BEGIN TRANSACTION                   BEGIN TRANSACTION
  ↓                                   ↓
Publish Event ← too early           Update DB
  ↓                                   ↓
Update DB                           COMMIT
  ↓                                   ↓
COMMIT                              Publish Event ← after commit
```

Never notify before DB commit. Users receive notifications for rolled back transactions.

### Failure Isolation

```java
private void onOrderPlaced(OrderPlaceEvent event) {
    for (OrderObserver observer : observers) {
        try {
            observer.onOrderPlaced(event);
        } catch (Exception e) {
            // log and continue — one failure must not stop others
            log.error("Observer {} failed", observer.getClass().getSimpleName(), e);
        }
    }
}
```

---

## Interview Questions & Answers

**Q1. Why Observer over direct service calls?**
Direct calls create tight coupling — Order must import and know every downstream service. Adding a new service means modifying Order. Observer inverts this — new services register themselves, Order never changes. OCP compliance.

**Q2. Which SOLID principles does Observer satisfy?**
SRP — each observer owns its own reaction logic. OCP — new observers added without modifying subject. DIP — subject depends on the Observer interface, not concrete implementations. ISP — observer interface exposes only what's needed.

**Q3. Push vs Pull model?**
Push — subject sends data in the event (your implementation). Observer gets everything it needs immediately. Pull — subject sends only notification, observer fetches data itself. Push is preferred — no extra lookups, better for distributed systems. Pull gives more flexibility but increases coupling.

**Q4. Why immutable event object?**
Thread-safe — multiple observers can read it concurrently without synchronization. Historical accuracy — event captures state at the moment it occurred, not current state. Consistency — all observers see the same data.

**Q5. ArrayList or CopyOnWriteArrayList?**
CopyOnWriteArrayList for read-heavy observer lists. Observer lists are registered once, read on every notification. ArrayList throws ConcurrentModificationException when modified during iteration across threads.

**Q6. Should notifyObservers() be public or private?**
Private. Notification is an internal mechanism triggered by state change. Outsiders should call `placeOrder()` — not trigger notifications directly. Public notification breaks encapsulation and lets callers fire events without state changes.

**Q7. Should Publisher be an interface?**
GoF says yes. Production often skips it for a single subject. Add `Observable` interface only when multiple subjects (Order, Payment, Shipment) share the same plumbing — extract to `AbstractObservable` to avoid duplication.

**Q8. Can one observer's failure stop others?**
No — each observer is independent. Wrap each `observer.handle(event)` in try-catch, log the failure, and continue the loop. One broken email service must not prevent inventory update.

**Q9. Notify before or after DB commit?**
Always after successful commit. Notifying before commit means observers react to a transaction that may roll back — users get confirmation emails for failed orders.

**Q10. When do you use Kafka instead of code-level Observer?**
Cross-service fan-out, async delivery, message persistence, or replay capability. Kafka is Observer at infrastructure level — same intent (one event, N reactions, producer doesn't know consumers), different altitude.

**Q11. How do you handle the memory leak in Observer?**
Always call `unregisterObserver()` when the observer's lifecycle ends. For long-lived subjects with short-lived observers, use `WeakReference<Observer>` so GC can collect observers even if the subject still holds a reference.

**Q12. How is Observer different from Strategy?**
Strategy — one caller picks ONE implementation, only that one executes. Observer — one publisher notifies ALL subscribers, everyone executes. Strategy is about choosing behavior, Observer is about broadcasting events.

---

## 30-Second Revision Cheatsheet

```
WHEN TO USE
One state change → N independent reactions
Subject must not know who's listening

STRUCTURE
Subject        → holds List<Observer>, fires event
Observer       → interface with handle(event)
Event          → immutable data carrier
ConcreteObs    → implements Observer, owns reaction logic

FLOW
State Change
  ↓
Build immutable Event
  ↓
Loop over CopyOnWriteArrayList
  ↓
observer.handle(event) on each

SOLID
SRP — reactions separated into own classes
OCP — new observer = new class, zero existing changes
DIP — subject depends on interface, not concretions

PRODUCTION TRAPS
✅ CopyOnWriteArrayList — not ArrayList
✅ private notifyObservers() — not public
✅ Publish AFTER commit — not before
✅ try-catch per observer — failure isolation
✅ unregisterObserver() — prevent memory leak
✅ Async for performance — not synchronous chain

OBSERVER vs KAFKA
Same intent. Different altitude.
In-process + sync → Observer
Cross-service + async + replay → Kafka
```