# Decorator Design Pattern --- SDE-2 Java Interview Revision

## 1. Intent

Decorator lets us **add responsibilities/behavior to an existing object
dynamically** without modifying the original class.

The decorator:

-   implements the same interface as the object it wraps
-   contains a reference to that same interface
-   delegates to the wrapped object
-   adds its own behavior before and/or after delegation
-   can itself be wrapped by another decorator

Core idea:

``` text
Client
  |
  v
Decorator
  |
  v
Decorator
  |
  v
Concrete Component
```

The caller continues to see the same abstraction.

------------------------------------------------------------------------

# 2. The Problem We Derived

We started with a pizza system.

``` java
interface Pizza {
    int price();
}
```

A simple implementation:

``` java
class SimplePizza implements Pizza {
    @Override
    public int price() {
        return 100;
    }
}
```

Then we wanted optional toppings:

-   Cheese
-   Mushroom
-   Olives
-   Jalapeno
-   Paneer
-   etc.

A naive inheritance approach could produce:

``` text
SimplePizza
├── CheesePizza
├── MushroomPizza
├── CheeseMushroomPizza
├── CheeseOlivePizza
├── MushroomOlivePizza
└── ...
```

For `n` independent optional features, there can be up to `2^n`
combinations.

This creates:

-   class explosion
-   rigid design
-   difficult maintenance
-   continuous modification/addition of subclasses
-   poor scalability as combinations grow

Decorator solves this by composing individual behaviors.

``` text
SimplePizza
    |
    v
Cheese
    |
    v
Mushroom
    |
    v
Olive
```

Each decorator represents one responsibility.

------------------------------------------------------------------------

# 3. Recognition Checklist

Consider Decorator when most of these are true:

-   There is an existing object/component.
-   You want to add optional responsibilities to it.
-   The behaviors are independently composable.
-   Different clients/use cases may want different combinations.
-   Combinations can change at runtime.
-   You want to avoid creating a subclass for every combination.
-   You don't want to modify the original component for every new
    behavior.
-   The additional behavior can be expressed through the same interface.

Strong interview clue:

> "We have one core service and several optional, independently
> composable behaviors that different flows may combine differently."

------------------------------------------------------------------------

# 4. The Core Structure

``` text
                    Component
                       ^
                       |
             +---------+---------+
             |                   |
      ConcreteComponent      Decorator
                                 ^
                                 |
                    +------------+------------+
                    |                         |
             ConcreteDecoratorA       ConcreteDecoratorB
```

In our pizza example:

``` text
                    Pizza
                      ^
                      |
          +-----------+-----------+
          |                       |
     SimplePizza            BaseDecorator
                                  ^
                                  |
                         +--------+--------+
                         |                 |
                    CheesePizza      MushroomPizza
```

Important:

`BaseDecorator` is **not mandatory**.

The essential Decorator pattern can work without it:

``` text
Pizza
├── SimplePizza
├── CheesePizza
└── MushroomPizza
```

A base decorator is mainly useful for removing duplicated wrapping
infrastructure.

------------------------------------------------------------------------

# 5. Participants

## Component

The common abstraction.

``` java
interface Pizza {
    int price();
}
```

It defines the behavior that both the original object and decorators
expose.

------------------------------------------------------------------------

## Concrete Component

The original/core implementation.

``` java
class SimplePizza implements Pizza {
    @Override
    public int price() {
        return 100;
    }
}
```

It provides the base behavior.

------------------------------------------------------------------------

## Decorator

A class that implements the same abstraction and contains another object
of that abstraction.

``` java
abstract class BaseDecorator implements Pizza {
    private final Pizza pizza;

    protected BaseDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public int price() {
        return pizza.price();
    }
}
```

Its responsibilities:

-   hold the wrapped component
-   preserve the same interface
-   delegate by default

------------------------------------------------------------------------

## Concrete Decorator

Adds one specific responsibility.

``` java
class CheesePizza extends BaseDecorator {

    private final int cost = 30;

    public CheesePizza(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int price() {
        return super.price() + cost;
    }
}
```

Another:

``` java
class MushroomPizza extends BaseDecorator {

    private final int cost = 30;

    public MushroomPizza(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int price() {
        return super.price() + cost;
    }
}
```

------------------------------------------------------------------------

# 6. Complete Java Implementation

## Component

``` java
package org.example.design_patterns.structural.decorator;

public interface Pizza {
    int price();
}
```

## Concrete Component

``` java
package org.example.design_patterns.structural.decorator;

public class SimplePizza implements Pizza {

    private final int cost = 100;

    @Override
    public int price() {
        return cost;
    }
}
```

## Base Decorator

``` java
package org.example.design_patterns.structural.decorator;

public abstract class BaseDecorator implements Pizza {

    private final Pizza pizza;

    public BaseDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public int price() {
        return pizza.price();
    }
}
```

## Cheese Decorator

``` java
package org.example.design_patterns.structural.decorator;

public class CheesePizza extends BaseDecorator {

    private final int cost = 30;

    public CheesePizza(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int price() {
        return super.price() + cost;
    }
}
```

## Mushroom Decorator

``` java
package org.example.design_patterns.structural.decorator;

public class MushroomPizza extends BaseDecorator {

    private final int cost = 30;

    public MushroomPizza(Pizza pizza) {
        super(pizza);
    }

    @Override
    public int price() {
        return super.price() + cost;
    }
}
```

## Client

``` java
package org.example.design_patterns.structural.decorator;

public class DecoratorDemo {

    public static void main(String[] args) {

        Pizza pizza = new SimplePizza();

        pizza = new CheesePizza(pizza);

        pizza = new MushroomPizza(pizza);

        System.out.println(pizza.price());
    }
}
```

With:

``` text
SimplePizza = 100
Cheese      = 30
Mushroom    = 30
```

Output:

``` text
160
```

------------------------------------------------------------------------

# 7. Runtime Object Structure

For:

``` java
Pizza pizza = new SimplePizza();
pizza = new CheesePizza(pizza);
pizza = new MushroomPizza(pizza);
```

The actual object chain is:

``` text
MushroomPizza
      |
      v
CheesePizza
      |
      v
SimplePizza
```

Each decorator contains a `Pizza`.

The outer object is still assignable to:

``` java
Pizza
```

because every decorator implements `Pizza`.

------------------------------------------------------------------------

# 8. Runtime Call Flow

When:

``` java
pizza.price();
```

is called:

``` text
MushroomPizza.price()
        |
        | super.price()
        v
BaseDecorator.price()
        |
        | pizza.price()
        v
CheesePizza.price()
        |
        | super.price()
        v
BaseDecorator.price()
        |
        | pizza.price()
        v
SimplePizza.price()
```

The call travels inward.

The result travels outward.

``` text
SimplePizza
    -> 100

CheesePizza
    -> 100 + 30 = 130

MushroomPizza
    -> 130 + 30 = 160
```

Important interview distinction:

``` text
METHOD CALL:
Mushroom -> Cheese -> Simple

RESULT:
Simple -> Cheese -> Mushroom
```

------------------------------------------------------------------------

# 9. Why Does Polymorphism Matter?

All of these are `Pizza`:

``` java
SimplePizza
CheesePizza
MushroomPizza
```

Therefore:

``` java
Pizza pizza;
```

can point to any of them.

This allows:

``` java
Pizza pizza = new SimplePizza();

pizza = new CheesePizza(pizza);

pizza = new MushroomPizza(pizza);
```

At runtime, dynamic dispatch chooses the correct implementation.

The decorator does not need to know whether it wraps:

``` text
SimplePizza
ThinCrustPizza
SpecialPizza
AnotherDecorator
```

It only knows:

``` java
Pizza
```

This is:

``` text
Composition + Polymorphism
```

working together.

------------------------------------------------------------------------

# 10. Why Is the Wrapped Object Private?

Typical implementation:

``` java
private final Pizza pizza;
```

Reasons:

-   encapsulation
-   prevents external manipulation
-   the wrapping relationship is an implementation detail
-   prevents reassignment after construction
-   makes the decorator's state safer

`final` means the reference cannot be reassigned after construction.

It does not mean the wrapped object itself is immutable.

------------------------------------------------------------------------

# 11. Why `BaseDecorator`?

Without a base decorator, every decorator may repeat:

``` java
private final Pizza pizza;

public CheesePizza(Pizza pizza) {
    this.pizza = pizza;
}
```

and:

``` java
private final Pizza pizza;

public MushroomPizza(Pizza pizza) {
    this.pizza = pizza;
}
```

The wrapping infrastructure is duplicated.

`BaseDecorator` extracts the common implementation:

``` java
private final Pizza pizza;

public BaseDecorator(Pizza pizza) {
    this.pizza = pizza;
}

@Override
public int price() {
    return pizza.price();
}
```

Concrete decorators only focus on their additional behavior.

``` text
BaseDecorator
    -> HOW to wrap/delegate

Concrete Decorator
    -> WHAT additional behavior to add
```

------------------------------------------------------------------------

# 12. Is `BaseDecorator` Mandatory?

No.

This is important.

Decorator can work as:

``` text
Pizza
├── SimplePizza
├── CheesePizza
└── MushroomPizza
```

where both decorators directly implement `Pizza`.

`BaseDecorator` is an implementation convenience.

It reduces duplication.

------------------------------------------------------------------------

# 13. Why Is `BaseDecorator` Abstract?

The main reason is semantic/design intent.

`BaseDecorator` is a reusable base implementation, not a meaningful
concrete pizza/decorator that clients should instantiate directly.

``` java
public abstract class BaseDecorator implements Pizza
```

prevents:

``` java
new BaseDecorator(...);
```

The `abstract` keyword is NOT what makes Decorator work.

It is also NOT what eliminates repeated `price()` implementations.

The actual benefit comes from sharing the wrapping/delegation
infrastructure.

------------------------------------------------------------------------

# 14. Why Is `price()` Normal in BaseDecorator?

`BaseDecorator` can provide default delegation:

``` java
@Override
public int price() {
    return pizza.price();
}
```

A concrete decorator can then extend that behavior:

``` java
@Override
public int price() {
    return super.price() + cost;
}
```

So:

``` text
BaseDecorator.price()
    -> wrapped object's price

CheesePizza.price()
    -> wrapped price + cheese

MushroomPizza.price()
    -> wrapped price + mushroom
```

The concrete decorator overrides `price()` because it has additional
behavior.

------------------------------------------------------------------------

# 15. Why `super.price()`?

Suppose:

``` text
CheesePizza
    |
    v
BaseDecorator
    |
    v
SimplePizza
```

When `CheesePizza.price()` executes:

``` java
return super.price() + 30;
```

`super.price()` calls:

``` java
BaseDecorator.price()
```

which does:

``` java
return pizza.price();
```

The `pizza` reference points to the wrapped `SimplePizza`.

Therefore:

``` text
CheesePizza.price()
      |
      v
BaseDecorator.price()
      |
      v
SimplePizza.price()
      |
      v
100
      |
      + 30
      |
      v
130
```

------------------------------------------------------------------------

# 16. Concrete Component Does Not Have to Be `SimplePizza`

The chain must ultimately have a concrete component, but it does not
have to be `SimplePizza`.

For example:

``` java
Pizza pizza = new ThinCrustPizza();

pizza = new CheesePizza(pizza);
pizza = new MushroomPizza(pizza);
```

The bottom of the chain is simply some concrete `Pizza`.

General structure:

``` text
Decorator
    |
Decorator
    |
Decorator
    |
Concrete Component
```

------------------------------------------------------------------------

# 17. Order of Decorators

For simple price additions:

``` text
Cheese -> Mushroom
```

and:

``` text
Mushroom -> Cheese
```

produce the same final price because both simply add costs.

But decorators do NOT have to be commutative.

Suppose:

``` text
SimplePizza = 100
Cheese = +30
Discount = 10%
```

Then:

``` text
Cheese -> Discount
```

means:

``` text
(100 + 30) * 0.9 = 117
```

while:

``` text
Discount -> Cheese
```

means:

``` text
100 * 0.9 + 30 = 120
```

Therefore:

> Decorator order can be behaviorally significant.

This becomes especially important with:

-   discounts
-   validation
-   authorization
-   retries
-   transactions
-   caching
-   logging
-   transformations

------------------------------------------------------------------------

# 18. Before/After Delegation

A decorator can add behavior before delegation:

``` java
@Override
public Response call(Request request) {
    validate(request);
    return super.call(request);
}
```

Or after:

``` java
@Override
public Response call(Request request) {
    Response response = super.call(request);
    log(response);
    return response;
}
```

Or both:

``` java
@Override
public Response call(Request request) {

    before();

    Response response = super.call(request);

    after(response);

    return response;
}
```

This is one reason Decorator is useful in backend systems.

------------------------------------------------------------------------

# 19. OCP Connection

Decorator supports the Open/Closed Principle.

Suppose:

``` java
class SimplePizza implements Pizza
```

is already tested and deployed.

We want to add cheese.

We do:

``` java
pizza = new CheesePizza(pizza);
```

We do NOT modify `SimplePizza`.

The system is:

``` text
Closed for modification
Open for extension
```

We extend behavior by creating a new decorator.

------------------------------------------------------------------------

# 20. Composition vs Inheritance

Inheritance tends to create a fixed hierarchy:

``` text
PaymentServiceImpl
├── LoggingPaymentService
├── FraudPaymentService
├── LoggingFraudPaymentService
├── LoggingMetricsPaymentService
└── ...
```

As optional behaviors grow, combinations grow.

Composition gives:

``` text
Logging
   |
   v
Fraud
   |
   v
Metrics
   |
   v
PaymentServiceImpl
```

Only individual behaviors need classes.

The combination is created at runtime.

Interview phrasing:

> "Composition lets us combine independent behaviors dynamically, so we
> create classes for individual responsibilities rather than classes for
> every possible combination."

------------------------------------------------------------------------

# 21. Backend Example: PaymentService

Suppose:

``` java
interface PaymentService {
    PaymentResponse pay(PaymentRequest request);
}
```

Core implementation:

``` java
class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        // actual payment processing
    }
}
```

Optional behaviors:

``` text
Logging
Fraud Check
Retry
Metrics
```

Possible composition:

``` text
Retry
   |
Logging
   |
Fraud
   |
PaymentServiceImpl
```

Or:

``` text
Logging
   |
PaymentServiceImpl
```

Different flows can use different compositions without creating
different core payment implementations.

------------------------------------------------------------------------

# 22. Who Decides Decorator Order?

The core component should not know about its decorators.

`PaymentServiceImpl` should simply process payments.

Some composition/root/application layer decides:

``` text
Retry
  |
Logging
  |
Fraud
  |
PaymentServiceImpl
```

This keeps the core component focused on its core responsibility.

------------------------------------------------------------------------

# 23. Decorator vs Orchestration

An orchestration service might explicitly coordinate:

``` text
PaymentOrchestrator
    |
    +--> Logging
    |
    +--> Fraud
    |
    +--> Payment
    |
    +--> Metrics
```

Decorator instead packages the behavior with the wrapped component:

``` text
LoggingPaymentService
       |
       v
FraudPaymentService
       |
       v
PaymentServiceImpl
```

Every layer remains a `PaymentService`.

This allows different compositions to be treated as the same
abstraction.

Example:

``` text
Normal:
Logging -> Payment

High Risk:
Logging -> Fraud -> Payment
```

No separate `PaymentServiceImpl` is required for each flow.

------------------------------------------------------------------------

# 24. Backend Example: Notification Service

Problem:

``` java
interface NotificationService {
    void send(Notification notification);
}
```

Core:

``` text
EmailNotificationService
```

Optional behaviors:

``` text
Logging
Retry
Metrics
```

Possible composition:

``` text
Metrics
   |
Retry
   |
Logging
   |
EmailNotificationService
```

Or:

``` text
Logging
   |
EmailNotificationService
```

The decorators preserve the same `send()` contract.

Do NOT invent a different operation such as:

``` java
logAndSend()
```

The common interface must remain intact so decorators can be stacked.

------------------------------------------------------------------------

# 25. Why Must Decorators Preserve the Same Interface?

Suppose:

``` java
interface NotificationService {
    void send(Notification notification);
}
```

If `LoggingDecorator` also exposes:

``` java
send()
```

then its output can be passed into another decorator:

``` text
MetricsDecorator
      |
      v
RetryDecorator
      |
      v
LoggingDecorator
      |
      v
EmailNotificationService
```

If every wrapper exposed a different API, the chain would stop being
transparently composable.

Core rule:

> The decorator and wrapped component must satisfy the same abstraction.

------------------------------------------------------------------------

# 26. Decorator vs Adapter

### Adapter

Problem:

> The interface is incompatible.

Adapter changes/ translates one interface into another.

``` text
Client
  |
Target Interface
  |
Adapter
  |
Adaptee
```

Example:

``` text
PaymentService
      |
PaymentAdapter
      |
ThirdPartyPaymentGateway
```

The third-party API might expose:

``` java
makeTransaction()
```

while the application expects:

``` java
pay()
```

Adapter translates the contract.

### Decorator

Problem:

> The interface already fits, but we want additional behavior.

``` text
Client
  |
PaymentService
  |
LoggingDecorator
  |
PaymentServiceImpl
```

Key interview distinction:

> **Adapter changes the interface. Decorator preserves the interface and
> changes/adds behavior.**

------------------------------------------------------------------------

# 27. Decorator vs Strategy

### Strategy

Strategy chooses between alternative ways of performing the same
operation.

``` text
Payment
  |
  +--> UPI
  +--> CreditCard
  +--> Wallet
```

Question being answered:

> "Which implementation/algorithm should I use?"

Usually one strategy is selected.

### Decorator

Decorator adds responsibilities around an existing implementation.

``` text
Payment
  |
Logging
  |
Fraud
  |
Metrics
  |
PaymentImpl
```

Question being answered:

> "What additional behavior should I add around this object?"

Multiple decorators can be composed.

Interview answer:

> "Strategy chooses one alternative implementation or algorithm.
> Decorator composes additional responsibilities around an existing
> implementation."

------------------------------------------------------------------------

# 28. Decorator vs Proxy

Proxy can look structurally similar:

``` text
Client
  |
Wrapper
  |
Real Object
```

Do not confuse structure with intent.

A proxy's primary intent is generally to **control access to the real
object**, such as:

-   lazy loading
-   access control
-   remote access
-   lifecycle management
-   indirection

Decorator's primary intent is to **add responsibilities/behavior** while
preserving the same abstraction.

Important:

> The code shape alone does not determine the pattern. Intent does.

We did not study Proxy in depth yet, so the detailed Proxy comparison
should be revisited after learning Proxy.

------------------------------------------------------------------------

# 29. Decorator vs Chain of Responsibility

Decorator:

``` text
A
 |
 v
B
 |
 v
C
 |
 v
Real Object
```

Every decorator generally delegates to the wrapped component and adds
behavior.

Chain of Responsibility:

``` text
Handler A
   |
   v
Handler B
   |
   v
Handler C
```

Each handler decides whether/how to handle or forward the request.

Key distinction:

> **Decorator wraps to add responsibilities. Chain of Responsibility
> passes a request through handlers that may handle or forward it.**

Decorator is about behavior composition.

Chain of Responsibility is about request handling/processing
responsibility.

------------------------------------------------------------------------

# 30. Common Interview Questions

## Q1. What problem does Decorator solve?

It allows us to add responsibilities dynamically without modifying the
original object or creating subclasses for every combination of optional
behavior.

------------------------------------------------------------------------

## Q2. Why not inheritance?

Inheritance creates a fixed hierarchy and can require subclasses for
combinations of optional behaviors.

With `n` independent optional behaviors, the number of combinations can
approach `2^n`.

Decorator creates individual behavior classes and composes them
dynamically.

------------------------------------------------------------------------

## Q3. Why composition?

Composition lets us combine independent behaviors at runtime.

Instead of:

``` text
CheeseMushroomOlivePizza
```

we can do:

``` text
Olive
  |
Mushroom
  |
Cheese
  |
SimplePizza
```

------------------------------------------------------------------------

## Q4. Why does the decorator implement the same interface?

So the decorator remains substitutable for the original component.

A `Pizza` reference can point to:

``` text
SimplePizza
CheesePizza
MushroomPizza
```

This allows decorators to be stacked.

------------------------------------------------------------------------

## Q5. Is Decorator possible without a BaseDecorator?

Yes.

The pattern itself only requires the common component abstraction and
decorators that wrap it.

`BaseDecorator` is a convenience for sharing wrapping/delegation code.

------------------------------------------------------------------------

## Q6. Why is the wrapped object private?

Encapsulation.

The decorator owns its internal wrapping relationship and prevents
callers from manipulating it directly.

------------------------------------------------------------------------

## Q7. Why `final`?

The decorator should normally not replace its wrapped component after
construction.

``` java
private final Pizza pizza;
```

The reference is fixed after construction.

------------------------------------------------------------------------

## Q8. Why is BaseDecorator abstract?

It is intended as a reusable base implementation rather than a concrete
object clients instantiate.

`abstract` is not what makes the Decorator pattern work.

------------------------------------------------------------------------

## Q9. Why is `price()` normal instead of abstract in BaseDecorator?

Because BaseDecorator can provide useful default delegation:

``` java
return pizza.price();
```

Concrete decorators override it only when they need to add behavior.

------------------------------------------------------------------------

## Q10. How does `super.price()` reach SimplePizza?

Example:

``` text
CheesePizza
    |
super.price()
    |
BaseDecorator.price()
    |
pizza.price()
    |
SimplePizza.price()
```

The `pizza` field points to the wrapped concrete object.

Polymorphism selects the actual implementation at runtime.

------------------------------------------------------------------------

## Q11. What happens if we wrap a ThinCrustPizza instead of SimplePizza?

Nothing fundamental changes.

The decorator only depends on:

``` java
Pizza
```

So:

``` java
Pizza pizza = new ThinCrustPizza();
pizza = new CheesePizza(pizza);
```

works.

------------------------------------------------------------------------

## Q12. Does decorator order always matter?

No.

If decorators only add independent costs, order may not matter.

But if decorators perform different transformations, order can change
behavior.

Example:

``` text
Cheese -> Discount
```

can differ from:

``` text
Discount -> Cheese
```

------------------------------------------------------------------------

## Q13. Who decides decorator order?

Usually the composition root/application configuration/factory/DI
configuration.

The core component should not know which decorators surround it.

------------------------------------------------------------------------

## Q14. Why not use an orchestration service?

Orchestration can work.

Decorator is useful when we want each behavior to be packaged with the
component under the same interface and allow different compositions to
be treated as the same abstraction.

The key distinction is where composition and responsibility live.

------------------------------------------------------------------------

## Q15. Decorator vs Adapter?

Adapter:

> "Make incompatible interfaces compatible."

Decorator:

> "Preserve the interface and add behavior."

------------------------------------------------------------------------

## Q16. Decorator vs Strategy?

Strategy:

> "Choose one alternative algorithm/implementation."

Decorator:

> "Add one or more responsibilities around an existing implementation."

------------------------------------------------------------------------

## Q17. Decorator vs Proxy?

Proxy primarily controls access/indirection to a real object.

Decorator primarily adds responsibilities.

Their structure can look similar, so identify the intent.

------------------------------------------------------------------------

## Q18. Decorator vs Chain of Responsibility?

Decorator wraps and adds behavior.

Chain of Responsibility passes a request through handlers that decide
whether to handle or forward it.

------------------------------------------------------------------------

## Q19. How does Decorator support OCP?

New behaviors are added through new decorators instead of modifying the
existing component.

Existing code remains closed for modification while the system is
extended with new classes.

------------------------------------------------------------------------

## Q20. What is the biggest tradeoff?

Decorator reduces inheritance/class explosion, but can create:

-   many small objects
-   deep wrapper chains
-   harder debugging
-   order dependencies
-   more complex runtime composition

------------------------------------------------------------------------

# 31. Common Mistakes

### Mistake 1: Different interface for decorators

Bad:

``` java
LoggingDecorator.logAndSend();
```

when the component exposes:

``` java
send();
```

This breaks transparent composition.

------------------------------------------------------------------------

### Mistake 2: Modifying the wrapped object

Decorator should normally wrap rather than mutate the original
component's implementation.

------------------------------------------------------------------------

### Mistake 3: Creating one class for every combination

That defeats the purpose.

Create:

``` text
Logging
Retry
Metrics
Fraud
```

not:

``` text
LoggingRetryMetricsFraudPaymentService
```

------------------------------------------------------------------------

### Mistake 4: Thinking BaseDecorator is mandatory

It is optional.

It is mainly for code reuse.

------------------------------------------------------------------------

### Mistake 5: Thinking `abstract` creates Decorator behavior

It does not.

The essential mechanism is:

``` text
same interface
+
composition
+
delegation
+
additional behavior
```

------------------------------------------------------------------------

### Mistake 6: Ignoring decorator order

Order can matter when behavior is not commutative.

------------------------------------------------------------------------

### Mistake 7: Putting composition knowledge into the core component

The core component should not know which decorators surround it.

------------------------------------------------------------------------

# 32. SOLID Analysis

## Single Responsibility Principle

Each concrete decorator can have one responsibility:

``` text
LoggingDecorator -> logging
MetricsDecorator -> metrics
RetryDecorator -> retry
FraudDecorator -> fraud
```

------------------------------------------------------------------------

## Open/Closed Principle

Add:

``` text
NewDecorator
```

without modifying:

``` text
ExistingComponent
```

------------------------------------------------------------------------

## Liskov Substitution Principle

Decorator implements the same abstraction.

If a method expects:

``` java
PaymentService
```

it can receive:

``` text
PaymentServiceImpl
LoggingPaymentService
FraudPaymentService
```

provided the decorator respects the contract.

------------------------------------------------------------------------

## Interface Segregation Principle

Decorator works best when the component interface is focused.

For example:

``` java
interface NotificationService {
    void send(Notification notification);
}
```

is easier to decorate than a giant interface with dozens of unrelated
operations.

------------------------------------------------------------------------

## Dependency Inversion Principle

The decorator depends on the abstraction:

``` java
private final PaymentService paymentService;
```

not directly on:

``` java
PaymentServiceImpl
```

This keeps the decorator loosely coupled.

------------------------------------------------------------------------

# 33. Advantages

-   Runtime behavior composition
-   Avoids subclass explosion
-   Supports OCP
-   Uses composition instead of rigid inheritance
-   Behaviors can be independently developed
-   Multiple decorators can be stacked
-   Same interface remains visible to clients
-   Good fit for optional cross-cutting responsibilities

------------------------------------------------------------------------

# 34. Disadvantages

-   Many small classes
-   Deep decorator chains can become difficult to debug
-   Order can affect behavior
-   Runtime object graph becomes more complex
-   Composition configuration can become difficult
-   A large number of decorators can make stack traces noisy

------------------------------------------------------------------------

# 35. Production/Spring Boot Relevance

Decorator-style thinking appears frequently in backend engineering.

Examples include:

-   logging around service calls
-   metrics
-   retry
-   caching
-   validation
-   authorization
-   tracing
-   request enrichment
-   transaction boundaries
-   rate limiting

Spring itself frequently uses proxy/AOP-based mechanisms, which can look
structurally similar to decorators.

Do not automatically call every wrapper a Decorator.

Ask:

> What is the intent?

If the purpose is to add a responsibility while preserving the same
abstraction, Decorator is a strong conceptual fit.

If the purpose is access control, lazy loading, remote indirection,
etc., Proxy may be the better pattern.

------------------------------------------------------------------------

# 36. Spring Boot Example Concept

Conceptually:

``` text
Client
  |
  v
Logging / Metrics / Retry
  |
  v
PaymentService
  |
  v
Actual implementation
```

The exact Spring mechanism may be AOP/proxy/interceptor rather than a
manually written GoF Decorator.

For interviews:

> "Spring frequently achieves similar wrapping behavior using
> proxies/AOP, although the exact mechanism is not necessarily a
> manually implemented Decorator."

------------------------------------------------------------------------

# 37. Quick Pattern Recognition

When you see:

> "Add optional behavior around an existing object."

Think:

**Decorator**

When you see:

> "Make incompatible interface compatible."

Think:

**Adapter**

When you see:

> "Choose one of several algorithms."

Think:

**Strategy**

When you see:

> "Control access to an object."

Think:

**Proxy**

When you see:

> "Pass a request through handlers that may handle or forward it."

Think:

**Chain of Responsibility**

------------------------------------------------------------------------

# 38. Interview Answer Template

If asked:

> "Explain Decorator."

A strong SDE-2 answer:

> "Decorator is a structural pattern used to add responsibilities to an
> existing object dynamically without modifying its class. The decorator
> implements the same interface as the component and contains a
> reference to another object of that interface. It delegates to the
> wrapped object and adds its own behavior before or after delegation.
> Because decorators implement the same abstraction, they can be stacked
> dynamically. This is useful when we have independently optional
> behaviors and want to avoid subclass explosion and support the
> Open/Closed Principle."

------------------------------------------------------------------------

# 39. 30-Second Version

> "Decorator uses composition to add responsibilities dynamically. The
> decorator implements the same interface as the component and wraps
> another component of that interface. Each decorator delegates to the
> wrapped object and adds its own behavior. Because all layers expose
> the same interface, we can stack them at runtime. It is useful for
> optional, independently composable behavior and avoids creating
> subclasses for every combination."

------------------------------------------------------------------------

# 40. Interview Questions We Actually Worked Through

These are the questions used during the learning session, with the
reasoning expected behind them.

## Q1. What should happen when `mushroom.price()` is called?

Expected reasoning:

> Mushroom should ask the wrapped Cheese object for its price and then
> add the mushroom cost.

This establishes delegation.

------------------------------------------------------------------------

## Q2. Why is the wrapped Pizza private?

Expected reasoning:

> The wrapped component is an internal implementation detail. Keeping it
> private preserves encapsulation and prevents outside code from
> manipulating the wrapping relationship.

------------------------------------------------------------------------

## Q3. Why can the same `Pizza` variable hold SimplePizza, CheesePizza, and MushroomPizza?

Expected reasoning:

> They all implement the same `Pizza` interface, so a `Pizza` reference
> can point to any of them. This is polymorphism and enables runtime
> composition.

------------------------------------------------------------------------

## Q4. Why introduce BaseDecorator?

Expected reasoning:

> Every decorator repeats the same wrapping infrastructure.
> BaseDecorator centralizes the wrapped reference, constructor, and
> default delegation.

------------------------------------------------------------------------

## Q5. Why is BaseDecorator abstract?

Expected reasoning:

> It represents a reusable base implementation rather than a concrete
> object clients should instantiate. `abstract` is not essential to the
> Decorator mechanism itself.

------------------------------------------------------------------------

## Q6. Should BaseDecorator.price() be abstract?

Expected reasoning:

> No. It can provide useful default delegation to the wrapped object.
> Concrete decorators override it when they need to add their own
> behavior.

------------------------------------------------------------------------

## Q7. How does CheesePizza get the SimplePizza price?

Expected reasoning:

``` text
CheesePizza.price()
 -> super.price()
 -> BaseDecorator.price()
 -> wrapped Pizza.price()
 -> SimplePizza.price()
```

Polymorphism determines which concrete `price()` implementation runs.

------------------------------------------------------------------------

## Q8. Do we always need SimplePizza?

Expected reasoning:

> We need a concrete component at the bottom of the chain, but it does
> not have to be SimplePizza. Any concrete implementation of Pizza can
> be decorated.

------------------------------------------------------------------------

## Q9. What happens with different decorator orders?

Expected reasoning:

> If decorators only add prices, order may not matter. If they perform
> operations such as discount, transformation, caching, etc., order can
> change the result.

------------------------------------------------------------------------

## Q10. Why does composition beat inheritance here?

Expected reasoning:

> Composition lets us combine individual behaviors dynamically.
> Inheritance would push us toward subclasses for combinations of
> optional behaviors, causing class explosion and rigidity.

------------------------------------------------------------------------

## Q11. Why does the decorator preserve the same interface?

Expected reasoning:

> So the decorated object remains substitutable for the original
> component and can itself be wrapped by another decorator.

------------------------------------------------------------------------

## Q12. Why not use orchestration?

Expected reasoning:

> Orchestration can coordinate behaviors, but Decorator packages each
> behavior together with the wrapped component under the same
> abstraction. This allows different compositions to be represented as
> the same service type and composed transparently.

------------------------------------------------------------------------

## Q13. Why not inheritance for PaymentService behaviors?

Expected reasoning:

> Optional independent behaviors create combinations. With many
> behaviors, inheritance can lead to many combination subclasses.
> Decorator lets us create individual behaviors and compose them
> dynamically.

------------------------------------------------------------------------

## Q14. What is the Adapter difference?

Expected reasoning:

> Adapter solves interface incompatibility. Decorator assumes the
> interface already fits and adds behavior while preserving that
> interface.

------------------------------------------------------------------------

## Q15. What is the Strategy difference?

Expected reasoning:

> Strategy chooses one alternative implementation or algorithm.
> Decorator adds one or more responsibilities around an existing
> implementation.

------------------------------------------------------------------------

## Q16. Why does a backend service make a good Decorator candidate?

Expected reasoning:

> A core service can have optional cross-cutting responsibilities such
> as logging, metrics, retry, fraud checking, caching, and tracing.
> Different flows can compose different combinations without changing
> the core service.

------------------------------------------------------------------------

# 41. Final Mental Model

Do not memorize the UML first.

Remember the derivation:

``` text
Need optional behavior
        ↓
Inheritance creates combinations
        ↓
Combinations explode
        ↓
Use composition
        ↓
Decorator wraps the existing component
        ↓
Decorator implements same interface
        ↓
Polymorphism makes decorator substitutable
        ↓
Decorators can be stacked
        ↓
Each decorator adds one responsibility
        ↓
New behavior requires a new decorator,
not modification of the existing component
```

The one sentence to remember:

> **Decorator = same interface + wrapping + delegation + additional
> responsibility.**
