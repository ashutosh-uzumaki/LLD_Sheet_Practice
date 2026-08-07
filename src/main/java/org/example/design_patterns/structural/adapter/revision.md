# Adapter Pattern — Revision Document

---

## 1. The Problem (Why Adapter Exists)

You have a class you **cannot modify** (third-party SDK, legacy jar), but your system depends on a different interface. The two APIs are incompatible — different method names, parameters, return types.

**Adapter bridges that gap without touching either side.**

---

## 2. The Three Roles

| Role | What It Is | Your Ajio Example |
|------|------------|-------------------|
| **Target** | The interface your system already depends on | `PaymentGateway` |
| **Adaptee** | The incompatible third-party class you cannot modify | `RazorPay` |
| **Adapter** | The translator — implements Target, wraps Adaptee | `RazorpayAdapter` |

Memory trick: **Adaptee** is the outsider being *adapted into* your system. **Target** is the contract you're *targeting* to satisfy.

---

## 3. The Concrete Mismatches (What Made Them Incompatible)

| | `PaymentGateway` | `RazorPay` |
|--|-----------------|------------|
| **Method name** | `processPayment` | `charge` |
| **Parameters** | `(String userId, BigDecimal amount, String currency)` | `(BigDecimal amount, String userId)` |
| **Return type** | `boolean` | `String` |

---

## 4. Full Annotated Code

### Target — the interface your system depends on
```java
public interface PaymentGateway {
    boolean processPayment(String userId, BigDecimal amount, String currency);
}
```

### Adaptee — third-party class, cannot modify
```java
public class RazorPay {
    // Different method name, different parameter order, different return type
    public String charge(BigDecimal amount, String userId) {
        return "RazorPay charged: " + amount + " for user: " + userId;
    }
}
```

### Adapter — the translator
```java
public class RazorpayAdapter implements PaymentGateway {

    // Hold the adaptee as a field — Object Adapter style (preferred in Java)
    private final RazorPay razorPay;

    // Inject via constructor — Dependency Inversion Principle
    // Enables testing with mocks, doesn't hardcode the instance
    public RazorpayAdapter(RazorPay razorPay) {
        this.razorPay = razorPay;
    }

    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        // Translate: reorder parameters, map return type
        String result = razorPay.charge(amount, userId);
        return result != null && !result.isEmpty();
    }
}
```

### Usage — caller sees only PaymentGateway, never knows about RazorPay
```java
PaymentGateway gateway = new RazorpayAdapter(new RazorPay());
gateway.processPayment("user123", new BigDecimal("499.00"), "INR");
```

---

## 5. Object Adapter vs Class Adapter

### Object Adapter (what you built — always prefer this in Java)
```java
public class RazorpayAdapter implements PaymentGateway {
    private final RazorPay razorPay; // composition — holds adaptee as field

    public RazorpayAdapter(RazorPay razorPay) { this.razorPay = razorPay; }

    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        String result = razorPay.charge(amount, userId);
        return result != null && !result.isEmpty();
    }
}
```

### Class Adapter (avoid in Java)
```java
// Extends RazorPay AND implements PaymentGateway
public class RazorpayAdapter extends RazorPay implements PaymentGateway {
    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        String result = charge(amount, userId); // inherited from RazorPay
        return result != null && !result.isEmpty();
    }
}
```

**Why Class Adapter is problematic:**
- Java doesn't support multiple inheritance
- If `RazorpayAdapter` already needs to extend another class, you're stuck
- Composition over inheritance — always

**Rule: Use Object Adapter (composition) in Java. Always.**

---

## 6. Adding a Second Gateway — OCP in Action

When Ajio onboards PayU:

```java
public class PayUAdapter implements PaymentGateway {
    private final PayUClient payUClient;

    public PayUAdapter(PayUClient payUClient) { this.payUClient = payUClient; }

    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        // translate to PayU's API
        PayUResponse response = payUClient.makePayment(userId, amount.doubleValue());
        return response.isSuccess();
    }
}
```

- `PaymentGateway` — untouched
- `RazorpayAdapter` — untouched
- New adapter added, nothing broken

**This is OCP: open for extension, closed for modification.**

---

## 7. SOLID Mapping

| Principle | How Adapter Satisfies It |
|-----------|--------------------------|
| **O — Open/Closed** | Add new gateways by creating new adapters, never modifying existing code |
| **D — Dependency Inversion** | Inject `RazorPay` via constructor, don't `new` it inside the method |
| **S — Single Responsibility** | Adapter's only job is translation — no business logic inside |

---

## 8. When To Reach For Adapter

**Trigger condition:** You have a class you **cannot modify**, but you need it to satisfy an interface your system already depends on.

The "cannot modify" part is everything. That's what separates Adapter from just refactoring.

**Classic real-world triggers:**
- Integrating a third-party SDK (Razorpay, Stripe, Twilio)
- Wrapping a legacy internal library that's too risky to change
- Using an open-source library that doesn't match your domain interface

---

## 9. The Trap — Adapter on Code You Own

```java
// You OWN this class — it's in your codebase
public class LegacyReportGenerator {
    public void generateXmlReport(String data, String format, boolean verbose) { ... }
}

// Someone wraps it in an Adapter instead of fixing it
public class ReportAdapter implements ReportGenerator {
    private final LegacyReportGenerator legacy;
    ...
}
```

**What's wrong:** Adapter is for code you **cannot** change. If you own it, the correct move is to **refactor** it. Wrapping it hides a mess — future developers now trace through an unnecessary translation layer on top of broken code.

**Rule: Adapter is for external/unmodifiable code. If you own it, fix it.**

---

## 10. Interview Q&A

**Q: What is the Adapter pattern?**
A: It wraps an incompatible class to make it look like an interface your system already expects — without modifying either side.

**Q: What are the three roles in Adapter?**
A: Target (interface your system depends on), Adaptee (incompatible class you cannot modify), Adapter (the translator that implements Target and wraps Adaptee).

**Q: Object Adapter vs Class Adapter — which do you prefer and why?**
A: Object Adapter always in Java. Class Adapter requires extending the Adaptee, which burns your one `extends` and violates composition-over-inheritance. Object Adapter holds the Adaptee as a field — flexible, testable, no inheritance constraint.

**Q: Which SOLID principles does Adapter satisfy?**
A: OCP — new integrations added via new adapters, existing code untouched. DIP — Adaptee injected via constructor, not created inside. SRP — Adapter only translates, no business logic.

**Q: How is Adapter different from Decorator?**
A: Adapter changes the interface (makes an incompatible API fit your contract). Decorator keeps the same interface but adds behaviour. Different intent entirely.

**Q: When should you NOT use Adapter?**
A: When you own the class. If you can modify it, refactor it — don't wrap a mess in a translation layer.

**Q: Why inject the Adaptee via constructor instead of creating it inside the method?**
A: DIP — don't create your dependencies, receive them. Enables mocking in tests, allows swapping implementations without touching the adapter.

**Q: How does Adapter support OCP?**
A: Each new third-party integration gets its own adapter. Your core code (`PaymentGateway` callers, existing adapters) never changes — you only add new adapters.

---

## 11. 30-Second Cheatsheet

```
ADAPTER — "make the outsider fit in"

Roles:    Target (your interface) ← Adapter (translator) → Adaptee (their class)
Java:     implements Target, holds Adaptee as private final field (Object Adapter)
Inject:   Adaptee via constructor (DIP)
SOLID:    OCP (add adapters, don't modify) + DIP (inject, don't create)
Trap:     Don't adapt code you OWN — refactor it instead
Trigger:  Third-party SDK / legacy jar you CANNOT modify
```