# Observer Design Pattern (Behavioral)

# Intent

Define a **one-to-many dependency** between objects so that when one object (Publisher/Subject) changes its state, all dependent objects (Observers/Listeners) are automatically notified.

---

# Real Interview Problem

Design an e-commerce platform.

Whenever the price of a product changes:

* Send Email
* Send SMS
* Send Push Notification
* Update Analytics
* Invalidate Cache

Initially you may write:

```java
productService.updatePrice();

emailService.send();

smsService.send();

analyticsService.update();

cacheService.invalidate();
```

This works.

But every new subscriber requires modifying `ProductService`.

This violates:

* SRP
* OCP

Observer solves this problem.

---

# Recognition Checklist

If you hear:

* Notify everyone
* Subscribers
* Interested parties
* Event occurred
* Broadcast changes
* One change triggers many actions

Think:

> **Observer Pattern**

---

# Before Observer

```text
                ProductService

                     │

        ┌────────────┼────────────┐

        ▼            ▼            ▼

     Email         SMS      Analytics
```

Problems:

* Tight coupling
* Every new feature modifies ProductService
* Violates OCP
* Violates SRP

---

# After Observer

```text
                    Product
                 (Publisher)

                       │

        --------------------------------

        │              │              │

        ▼              ▼              ▼

     Email          SMS         Analytics

        ▲              ▲              ▲

        └────── PriceEventListener ───┘
```

Product does not know concrete subscribers.

It only knows:

```java
PriceEventListener
```

---

# UML

```text
                        +---------------------------+
                        | PriceChangePublisher      |
                        +---------------------------+
                        | + subscribe()            |
                        | + unsubscribe()          |
                        | + notifySubscribers()    |
                        +------------^-------------+
                                     |
                               implements
                                     |
                           +---------+---------+
                           |      Product      |
                           +-------------------+
                           | productId         |
                           | productName       |
                           | price             |
                           | listeners         |
                           +-------------------+
                           | changePrice()     |
                           +-------------------+

                 +----------------------------+
                 | PriceEventListener         |
                 +----------------------------+
                 | + handle(event)            |
                 +-------------^--------------+
                               |
                ---------------------------------------
                |                                     |
+------------------------------+   +------------------------------+
| EmailNotificationListener    |   | SmsNotificationListener      |
+------------------------------+   +------------------------------+
| handle(event)                |   | handle(event)                |
+------------------------------+   +------------------------------+
```

---

# Complete Java Implementation

## PriceChangedEvent

```java
public final class PriceChangedEvent {

    private final String productId;
    private final String productName;
    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;

    public PriceChangedEvent(
            String productId,
            String productName,
            BigDecimal oldPrice,
            BigDecimal newPrice) {

        this.productId = productId;
        this.productName = productName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }
}
```

---

## PriceEventListener

```java
public interface PriceEventListener {

    void handle(PriceChangedEvent event);

}
```

---

## PriceChangePublisher (Classic GoF)

```java
public interface PriceChangePublisher {

    void subscribe(PriceEventListener listener);

    void unsubscribe(PriceEventListener listener);

    void notifySubscribers(PriceChangedEvent event);

}
```

---

## Product

```java
public class Product implements PriceChangePublisher {

    private final String productId;
    private final String productName;
    private BigDecimal price;

    private final List<PriceEventListener> listeners =
            new ArrayList<>();

    public Product(
            String productId,
            String productName,
            BigDecimal price) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    @Override
    public void subscribe(PriceEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(PriceEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifySubscribers(PriceChangedEvent event) {

        for (PriceEventListener listener : listeners) {
            listener.handle(event);
        }
    }

    public void changePrice(BigDecimal newPrice) {

        if (price.compareTo(newPrice) == 0) {
            return;
        }

        BigDecimal oldPrice = this.price;
        this.price = newPrice;

        PriceChangedEvent event =
                new PriceChangedEvent(
                        productId,
                        productName,
                        oldPrice,
                        newPrice);

        notifySubscribers(event);
    }
}
```

---

## EmailNotificationListener

```java
public class EmailNotificationListener
        implements PriceEventListener {

    @Override
    public void handle(PriceChangedEvent event) {

        System.out.println(
                "Email sent for "
                        + event.getProductName());

    }
}
```

---

## SmsNotificationListener

```java
public class SmsNotificationListener
        implements PriceEventListener {

    @Override
    public void handle(PriceChangedEvent event) {

        System.out.println(
                "SMS sent for "
                        + event.getProductName());

    }
}
```

---

## Main

```java
public class Main {

    public static void main(String[] args) {

        Product iphone =
                new Product(
                        "P101",
                        "iPhone 18",
                        BigDecimal.valueOf(120000));

        iphone.subscribe(
                new EmailNotificationListener());

        iphone.subscribe(
                new SmsNotificationListener());

        iphone.changePrice(
                BigDecimal.valueOf(99999));
    }
}
```

---

# Sequence Diagram

```text
Product.changePrice()

        │

        ▼

Update Price

        │

        ▼

Create PriceChangedEvent

        │

        ▼

notifySubscribers()

        │

        ▼

for each listener

        │

        ▼

listener.handle(event)
```

---

# SOLID Principles

## SRP

Product manages:

* Product state
* Subscriber registration
* Notification

Email logic stays inside Email listener.

SMS logic stays inside SMS listener.

Responsibilities remain separated.

---

## OCP

Need WhatsApp?

Create:

```java
class WhatsAppListener implements PriceEventListener
```

No existing class changes.

---

## DIP

Product depends upon:

```java
PriceEventListener
```

Not:

```java
EmailNotificationListener
```

---

## ISP

Listener interface exposes only:

```java
handle(...)
```

Nothing unnecessary.

---

# Advantages

* Loose coupling
* Easy to add new subscribers
* OCP compliant
* Reusable
* Event driven
* High extensibility

---

# Disadvantages

* Notification order may matter
* Too many observers can affect performance
* Harder debugging
* Cascading notifications possible
* Thread safety must be considered

---

# Push vs Pull Model

## Push

```java
handle(PriceChangedEvent event)
```

Publisher pushes required data.

Pros

* No extra lookup
* Better for distributed systems

---

## Pull

```java
handle(Product product)
```

Subscriber fetches required information.

Pros

* Smaller event
* More flexibility

Cons

* Extra lookups
* Higher coupling

---

# Thread Safety

Never blindly use:

```java
ArrayList
```

If:

* many readers
* few writes

Prefer:

```java
CopyOnWriteArrayList
```

Reason:

* safe iteration
* no ConcurrentModificationException

---

# Production Version

Instead of

```java
notifySubscribers(event)
```

Spring Boot provides

```java
ApplicationEventPublisher
```

Listeners become

```java
@EventListener
```

or

```java
@TransactionalEventListener
```

---

# Transaction Discussion

Never notify:

```text
Before Commit
```

Correct flow

```text
BEGIN TRANSACTION

↓

Update DB

↓

Commit

↓

Publish Event

↓

Listeners Execute
```

Otherwise users receive notifications for rolled back transactions.

---

# Sync vs Async

Synchronous

```text
Product

↓

Email

↓

SMS

↓

Analytics
```

Simple but slow.

---

Asynchronous

```text
        Product

           │

 ┌─────────┼─────────┐

 ▼         ▼         ▼

Email     SMS     Analytics
```

Independent execution.

Preferred in production.

---

# Failure Handling

If Email fails:

* Email retries

Analytics should still execute.

Publisher should never stop notifying remaining subscribers.

Each subscriber owns its retry strategy.

---

# GoF vs Production

## GoF

```
Publisher Interface

↓

Product implements Publisher

↓

notifySubscribers() is public
```

---

## Production

```
Product

↓

private notifySubscribers()

↓

changePrice()

↓

ApplicationEventPublisher
```

Better encapsulation.

---

# Comparison with Strategy

## Strategy

One caller chooses ONE implementation.

```text
Payment

↓

UPI

OR

Card

OR

Crypto
```

Only one executes.

---

## Observer

One publisher informs EVERY subscriber.

```text
Product

↓

Email

↓

SMS

↓

Analytics
```

Everyone executes.

---

# Comparison with Singleton

| Singleton       | Observer                      |
| --------------- | ----------------------------- |
| One instance    | One publisher, many observers |
| Object creation | Communication                 |

---

# Comparison with Factory Method

| Factory         | Observer         |
| --------------- | ---------------- |
| Creates objects | Notifies objects |

---

# Comparison with Abstract Factory

| Abstract Factory        | Observer          |
| ----------------------- | ----------------- |
| Creates related objects | Broadcasts events |

---

# Comparison with Builder

| Builder               | Observer                |
| --------------------- | ----------------------- |
| Builds complex object | Reacts to state changes |

---

# Comparison with Prototype

| Prototype     | Observer        |
| ------------- | --------------- |
| Clones object | Notifies object |

---

# Common Interview Questions

### Why Observer?

To remove tight coupling between publisher and subscribers.

---

### Which SOLID principles?

* SRP
* OCP
* DIP
* ISP

---

### Push vs Pull?

Know both.

---

### Why immutable event?

* Thread-safe
* Historical fact
* Consistency

---

### notify before or after DB update?

After successful transaction commit.

---

### Sync or Async?

Production prefers async.

---

### ArrayList or CopyOnWriteArrayList?

CopyOnWriteArrayList for read-heavy observer lists.

---

### Should Publisher be an interface?

Classic GoF:

Yes.

Production:

Often no.

---

### Can one subscriber failure stop others?

No.

Subscribers should be independent.

---

# Common Mistakes

* Calling EmailService directly
* Tight coupling
* Mutable event
* Public notification without state change
* Using double for money
* Forgetting unsubscribe
* Not considering thread safety
* Publishing before commit

---

# Production Use Cases

* Spring Application Events
* Order Created Event
* Payment Success Event
* Inventory Updated
* Cache Invalidation
* Email Notifications
* Audit Logging
* Analytics
* Kafka Consumers
* RabbitMQ Consumers
* Domain Events (DDD)

---

# Memory Trick

Whenever you hear:

* Notify users
* Interested parties
* Event
* Subscribers
* Broadcast

Think:

```text
State changes

↓

Create Event

↓

Loop over listeners

↓

listener.handle(event)
```

That is Observer.

---

# 30-Second Revision Cheatsheet

```text
Intent
↓

One change → Many notifications

Publisher
↓

Maintains listeners

Observer
↓

Implements handle(event)

Flow
↓

State Change

↓

Create Event

↓

Notify All

↓

Each Listener Handles Independently

SOLID
↓

SRP
OCP
DIP
ISP

Production
↓

ApplicationEventPublisher

↓

@EventListener

↓

@TransactionalEventListener(AFTER_COMMIT)

↓

@Async
```
