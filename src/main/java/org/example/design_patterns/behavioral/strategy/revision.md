# Strategy Design Pattern - Revision Notes

## 1. Intent

**Strategy** is a **Behavioral Design Pattern** that allows us to encapsulate multiple algorithms behind a common interface and switch between them without modifying the client.

> **"Change behavior, not client code."**

---

# 2. When to Identify Strategy

Look for code like:

```java
if (paymentMethod == UPI) {
    ...
} else if (paymentMethod == CREDIT_CARD) {
    ...
} else if (...) {
    ...
}
```

Ask yourself:

* Are there multiple ways of performing the same task?
* Does only the algorithm change?
* Will new algorithms be added in the future?

If yes, Strategy is a strong candidate.

---

# 3. Problems with if-else

* Violates Open/Closed Principle.
* CheckoutService grows as new algorithms are added.
* Harder to test.
* Harder to maintain.
* Higher chance of regressions.

---

# 4. Design Evolution

### Step 1

Move each payment algorithm into its own class.

```
CreditCardPayment
UPIPayment
NetBankingPayment
```

Problem:

CheckoutService still contains if-else.

---

### Step 2

Find common behavior.

All payment classes perform:

```
pay()
```

Create an abstraction.

```
Payment
```

---

### Step 3

Program to the abstraction.

```
CheckoutService
        |
        v
    Payment
```

CheckoutService no longer knows whether it is talking to:

* UPIPayment
* CreditCardPayment
* NetBankingPayment

---

# 5. Why Interface Instead of Abstract Class?

Choose **interface** because:

* Only behavior is common.
* No shared implementation.
* No shared state.

Use an abstract class only when common implementation or fields exist.

---

# 6. Payment Interface (Learning Version)

```java
public interface Payment {
    void pay(BigDecimal amount);
}
```

Production version may evolve to:

```java
PaymentResponse pay(PaymentRequest request);
```

---

# 7. Why BigDecimal?

Never use:

* float
* double

for money.

Reason:

Floating point precision errors.

Use:

```
BigDecimal
```

---

# 8. Stateless Strategies

Good:

```java
public void pay(BigDecimal amount) {
    ...
}
```

Avoid:

```java
private BigDecimal amount;
```

Reason:

Request-specific data should not become object state.

Stateless services are:

* easier to reuse
* thread-safe
* ideal as Spring singletons

---

# 9. Mutable Strategy Context

```java
CheckoutServiceMutable
```

Characteristics:

* Holds Payment
* Has setter
* Strategy can change at runtime

Example:

```java
checkout.setPayment(new UPIPayment());

checkout.checkout(amount);

checkout.setPayment(new CreditCardPayment());

checkout.checkout(amount);
```

Advantages:

* Demonstrates runtime switching.
* Classic GoF implementation.

Disadvantages:

* Mutable shared state.
* Needs care in multithreaded applications.

---

# 10. Immutable Strategy Context

```java
private final Payment payment;
```

Characteristics:

* Dependency fixed after construction.
* No setter.
* Easier to reason about.
* Preferred for backend services.

Example:

```java
CheckoutService checkout =
    new CheckoutService(new UPIPayment());
```

Need another strategy?

Create another context.

---

# 11. final Reference

```java
private final Payment payment;
```

Means:

Reference cannot change.

It does **NOT** make the Payment object immutable.

Example:

```java
payment = new CreditCardPayment(); // Not allowed
```

But if Payment has mutable fields, they can still change.

---

# 12. Constructor Injection vs Setter Injection

## Constructor Injection

Pros:

* Dependency mandatory.
* Immutable reference.
* Easier to reason about.
* Preferred in Spring.

## Setter Injection

Pros:

* Runtime strategy switching.
* Demonstrates Strategy nicely.

Cons:

* Mutable state.
* Requires careful thread safety.

---

# 13. Thread Safety

Safe:

```java
public void pay(BigDecimal amount) {

    BigDecimal tax =
            amount.multiply(new BigDecimal("0.03"));

}
```

Reason:

Method parameters and local variables are stored in each thread's own stack frame.

Not shared.

Unsafe:

```java
private BigDecimal lastAmount;
```

If multiple threads modify it simultaneously, race conditions occur.

---

# 14. Java Memory

## Stack

* method parameters
* local variables

Per thread.

Safe.

## Heap

* instance variables
* static variables

Shared.

Requires synchronization or immutability.

---

# 15. Spring Singleton Best Practice

Good:

```java
@Service
class UPIPayment {

    public void pay(BigDecimal amount) {
        ...
    }
}
```

Bad:

```java
@Service
class UPIPayment {

    private BigDecimal currentAmount;
}
```

Never store request-specific state inside singleton beans.

---

# 16. Open/Closed Principle

Adding Crypto payment should ideally require:

```
+ CryptoPayment.java
```

Only a new class.

CheckoutService should remain unchanged.

---

# 17. Strategy Pattern Structure

```
                 Payment
                    ^
                    |
      +-------------+-------------+
      |                           |
UPIPayment              CreditCardPayment
      ^
      |
CheckoutService
```

Context delegates work to the strategy.

---

# 18. Real Spring Boot Approach

Instead of changing the strategy inside CheckoutService:

```
Payment payment = strategyMap.get(method);

payment.pay(amount);
```

Spring injects all strategies.

CheckoutService chooses one per request.

No mutable shared state.

---

# 19. Advantages

* Eliminates if-else chains.
* Follows Open/Closed Principle.
* Easy to add new algorithms.
* Promotes composition over inheritance.
* Easy to test.
* Easy to mock.
* Encourages Dependency Injection.

---

# 20. Disadvantages

* More classes.
* Slight increase in complexity.
* Requires selecting the appropriate strategy.
* Can be overkill when only one algorithm exists.

---

# 21. Interview Questions

### Q1. Why is Strategy a Behavioral pattern?

Because the behavior (algorithm) changes while the client remains unchanged.

---

### Q2. Why interface instead of abstract class?

Only behavior is shared.

---

### Q3. Why BigDecimal?

Financial precision.

---

### Q4. Why constructor injection?

Immutable dependency and simpler reasoning.

---

### Q5. Why not a public Payment field?

Use encapsulation.

A setter can validate, log, or reject invalid strategies.

---

### Q6. Does `final` make the object immutable?

No.

It only makes the reference immutable.

---

### Q7. Why are Spring singleton services usually stateless?

Because instance fields are shared across threads.

Method parameters and local variables are not.

---

### Q8. How many classes change when adding a new payment type?

Ideally:

* Add one new strategy implementation.
* Existing client remains unchanged.

---

# 22. Key Takeaways

* Strategy is about **changing algorithms**.
* The client depends on an abstraction, not implementations.
* Runtime polymorphism removes `if-else`.
* Prefer stateless strategies.
* Use constructor injection for immutable dependencies in production.
* Mutable contexts are useful for demonstrating runtime strategy switching.
* In Spring, strategy selection is typically performed per request rather than by mutating a singleton service.

If you'd like, I can also create a **one-page interview cheat sheet** for Strategy that you can revise in under 5 minutes before interviews.
