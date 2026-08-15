# Chain of Responsibility Design Pattern - Revision Notes

## 1. Intent

Allow a request to move through a chain of potential handlers until one handler accepts responsibility for processing it.

The client should not need to know which concrete handler will ultimately process the request.

Core flow:

```text
Client
  |
  v
First Handler
  |
  | Can I handle it?
  +---- Yes ----> Handle/Approve ----> STOP
  |
  +---- No -----> Forward to next
                         |
                         v
                    Next Handler
```

---

## 2. Problem

Consider an expense approval system:

```text
Manager        -> can approve up to ₹10,000
SeniorManager  -> can approve up to ₹50,000
Director       -> can approve up to ₹100,000
```

A request could be:

```text
₹5,000   -> Manager
₹30,000  -> SeniorManager
₹80,000  -> Director
₹150,000 -> Nobody -> reject/fail
```

### Naive design

A central orchestrator could contain:

```java
if (amount <= 10_000) {
    manager.approve(request);
} else if (amount <= 50_000) {
    seniorManager.approve(request);
} else if (amount <= 100_000) {
    director.approve(request);
} else {
    throw ...
}
```

### Problems

- Orchestrator knows every concrete approver.
- Adding a new approval level requires modifying the orchestrator.
- Hierarchy/decision logic becomes centralized.
- Client/orchestrator becomes coupled to concrete handlers.
- Violates the spirit of Open/Closed Principle as the hierarchy grows.
- Processing logic becomes harder to extend independently.

---

## 3. Recognition Checklist

Consider Chain of Responsibility when:

- A request may be handled by one of several possible handlers.
- The current handler may be unable to handle the request.
- Responsibility can move to another handler.
- The client should not decide which concrete handler handles the request.
- The order of possible handlers matters.
- The chain may be configured independently of request processing.
- Adding/removing/reordering handlers should require little or no modification to existing handlers.
- Processing can stop once a handler accepts responsibility.

Typical mental question:

> "I have a request, but I don't want the caller to decide which object should handle it. Can the request move through possible handlers until one takes responsibility?"

---

## 4. Mental Model

Think:

```text
Request
   |
   v
Handler A
   |
   | cannot handle
   v
Handler B
   |
   | cannot handle
   v
Handler C
   |
   | can handle
   v
Process
   |
  STOP
```

The important behavior is **responsibility transfer**.

The current handler decides:

```text
Can I handle this?
    |
    +-- Yes --> handle
    |
    +-- No ---> forward
```

---

## 5. Participants

### Handler / Approver

Common abstraction containing:

- Reference to the next handler.
- Chain construction behavior.
- Common request-processing flow.
- Forwarding behavior.
- Common eligibility/decision logic when appropriate.
- Extension point for concrete handling.

### Concrete Handler

Examples:

- `Manager`
- `SeniorManager`
- `Director`

Each concrete handler provides its specific handling behavior.

### Client / Configuration

Creates/configures the chain and sends the request to the first handler.

The client does **not** traverse the chain itself.

---

## 6. UML

```text
                         +---------------------------+
                         |        Approver           |
                         +---------------------------+
                         | - next: Approver         |
                         | - limit: BigDecimal      |
                         +---------------------------+
                         | + setNext(Approver)      |
                         | + process(Request)       |
                         | + forward(Request)       |
                         | # approve(Request)       |
                         +-------------+-------------+
                                       ^
                     +-----------------+------------------+
                     |                 |                  |
              +------+-------+  +------+---------+  +-----+------+
              |   Manager    |  | SeniorManager  |  |  Director  |
              +--------------+  +----------------+  +------------+
              | # approve()  |  | # approve()    |  | # approve()|
              +--------------+  +----------------+  +------------+
```

Chain relationship:

```text
Manager
   |
   v
SeniorManager
   |
   v
Director
```

Important:

```text
Manager IS-A Approver
SeniorManager IS-A Approver

Manager HAS-A Approver (next)
```

So the design uses both:

- Inheritance for the common abstraction.
- Composition for the chain relationship.

---

## 7. Complete Java Implementation

### ExpenseRequest

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class ExpenseRequest {
    private final String expenseId;
    private final BigDecimal amount;

    public ExpenseRequest(String expenseId, BigDecimal amount) {
        this.expenseId = expenseId;
        this.amount = amount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
```

### Approver

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public abstract class Approver {

    private Approver next;
    private final BigDecimal limit;

    protected Approver(BigDecimal limit) {
        this.limit = limit;
    }

    public void setNext(Approver approver) {
        this.next = approver;
    }

    public void forward(ExpenseRequest request) {
        if (next == null) {
            throw new RuntimeException(
                    "Request cannot be approved. Check the limits!"
            );
        }

        next.process(request);
    }

    private boolean canProcess(BigDecimal amount) {
        return amount.compareTo(limit) <= 0;
    }

    public void process(ExpenseRequest request) {
        if (canProcess(request.getAmount())) {
            approve(request);
        } else {
            forward(request);
        }
    }

    protected abstract void approve(ExpenseRequest request);
}
```

### Manager

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class Manager extends Approver {

    public Manager() {
        super(new BigDecimal("10000"));
    }

    @Override
    protected void approve(ExpenseRequest request) {
        System.out.println(
                "Manager approved " + request.getExpenseId()
        );
    }
}
```

### SeniorManager

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class SeniorManager extends Approver {

    public SeniorManager() {
        super(new BigDecimal("50000"));
    }

    @Override
    protected void approve(ExpenseRequest request) {
        System.out.println(
                "Senior Manager approved " + request.getExpenseId()
        );
    }
}
```

### Director

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class Director extends Approver {

    public Director() {
        super(new BigDecimal("100000"));
    }

    @Override
    protected void approve(ExpenseRequest request) {
        System.out.println(
                "Director approved " + request.getExpenseId()
        );
    }
}
```

### Chain configuration / demo

```java
package org.example.design_patterns.behavioral.chain_of_responsibility;

import java.math.BigDecimal;

public class ChainOfResponsibilityDemo {

    public static void main(String[] args) {

        Approver manager = new Manager();
        Approver seniorManager = new SeniorManager();
        Approver director = new Director();

        manager.setNext(seniorManager);
        seniorManager.setNext(director);

        ExpenseRequest request =
                new ExpenseRequest("1", new BigDecimal("30000"));

        manager.process(request);
    }
}
```

---

## 8. Runtime / Sequence Flow

For:

```text
Manager       -> ₹10,000
SeniorManager -> ₹50,000
Director      -> ₹100,000

Request       -> ₹30,000
```

Execution:

```text
Client
  |
  | manager.process(request)
  v
Manager
  |
  | canProcess(30000)?
  | false
  v
forward(request)
  |
  v
SeniorManager
  |
  | canProcess(30000)?
  | true
  v
approve(request)
  |
  v
STOP
```

### ₹5,000

```text
Manager
  |
  | canProcess -> true
  v
approve()
  |
 STOP
```

### ₹80,000

```text
Manager
  |
  | false
  v
SeniorManager
  |
  | false
  v
Director
  |
  | true
  v
approve()
  |
 STOP
```

### ₹150,000

```text
Manager       -> cannot
SeniorManager -> cannot
Director      -> cannot
Director.next -> null

                    |
                    v
                 Failure
```

---

## 9. Important Encapsulation Decisions

### `next` should be private

```java
private Approver next;
```

Reason:

- Subclasses should not directly manipulate the chain.
- Chain internals remain encapsulated.
- Base class controls forwarding.
- Prevents subclasses from accidentally corrupting the chain.

### `limit` should be private and final

```java
private final BigDecimal limit;
```

Reason:

- Approval authority is configuration.
- It should not casually change during object lifetime.
- Subclasses should not directly mutate it.
- The value is supplied through the parent constructor.

Example:

```java
public Manager() {
    super(new BigDecimal("10000"));
}
```

### `process()` should be public

It is the public entry point for processing.

```java
manager.process(request);
```

The client should not bypass the workflow.

### `approve()` should be protected

```java
protected abstract void approve(ExpenseRequest request);
```

Reason:

- It is an internal extension point for subclasses.
- The outer client should not directly call `approve()`.
- `process()` decides whether `approve()` should execute.

### `canProcess()` visibility depends on the business rule

If every approver must always follow:

```text
amount <= limit
```

then it can be private.

If concrete handlers genuinely need different eligibility rules, it can be protected.

Do not make methods protected merely for hypothetical future flexibility.

---

## 10. SOLID Principles

### Single Responsibility Principle

The abstraction owns common chain mechanics:

- Processing flow.
- Forwarding.
- Chain relationship.

Concrete classes own their specific approval behavior.

### Open/Closed Principle

Adding a new handler should generally not require changing existing handlers.

Example:

```text
Manager → SeniorManager → Director
```

can become:

```text
Manager → SeniorManager → AVP → Director
```

without modifying `Manager`, `SeniorManager`, or `Director`.

Only chain composition changes.

### Liskov Substitution Principle

The chain stores:

```java
Approver next;
```

Any concrete `Approver` can occupy that position.

```java
next = new Manager();
next = new SeniorManager();
next = new Director();
```

### Interface Segregation Principle

Not a major driver of this example. The abstraction remains intentionally small.

### Dependency Inversion Principle

The chain points to:

```java
Approver
```

rather than concrete classes:

```java
SeniorManager
Director
VP
```

This keeps handlers coupled to the abstraction.

---

## 11. Composition vs Inheritance

Inheritance:

```text
Manager       -> Approver
SeniorManager -> Approver
Director      -> Approver
```

This means:

> Manager IS-A Approver.

Composition:

```java
private Approver next;
```

This means:

> Manager HAS-A next Approver.

The chain itself is built through composition.

This allows:

```text
Manager → SeniorManager
```

to be changed to:

```text
Manager → Director
```

without modifying `Manager`.

---

## 12. Who Builds the Chain?

The client or a dedicated configuration/factory component can build the chain:

```java
manager.setNext(seniorManager);
seniorManager.setNext(director);
```

A dedicated component could be:

```text
ApprovalChainFactory
```

Its responsibility would be:

- Create/configure handlers.
- Define the hierarchy.
- Connect handlers.

The Factory does **not** make the overall solution a Factory Pattern.

Different problems are being solved:

```text
Factory
→ How are objects created/configured?

Chain of Responsibility
→ How does a request move through handlers?
```

---

## 13. Client Responsibility

The client should know the starting point:

```java
manager.process(request);
```

The client should NOT manually traverse:

```java
manager.canProcess()
seniorManager.canProcess()
director.canProcess()
```

Otherwise the client becomes the orchestrator of the business flow.

The chain itself owns request traversal.

---

## 14. Why `next` Uses the Abstraction

Prefer:

```java
private Approver next;
```

over:

```java
private SeniorManager next;
```

because the next handler could be:

```text
SeniorManager
Director
VP
AVP
FutureApprover
```

without modifying the current handler.

This is programming to an abstraction and composition over concrete coupling.

---

## 15. What Happens When Nobody Handles the Request?

The chain eventually reaches:

```java
next == null
```

At that point there is no further handler.

Possible business outcomes:

- Throw a domain-specific exception.
- Return a rejection result.
- Escalate to another subsystem.
- Return an explicit "unhandled" result.

For the learning implementation we used an exception.

In production, prefer a meaningful domain-specific exception over:

```java
throw new RuntimeException(...);
```

For example:

```java
class ExpenseApprovalException extends RuntimeException {
    ...
}
```

---

## 16. Important Stop/Continue Rule

Our expense approval chain follows:

```text
Can handle?
    |
    +-- YES --> approve --> STOP
    |
    +-- NO  --> forward
```

Once one handler accepts responsibility, the request does not automatically continue to later handlers.

This is important.

A sequence such as:

```text
Authentication
    ↓
Authorization
    ↓
Validation
    ↓
Logging
```

where every step must execute is more naturally thought of as a **pipeline**.

Do not automatically call every sequence of handlers Chain of Responsibility.

### Chain of Responsibility

```text
Who should handle this request?
```

Potentially one handler handles it and processing stops.

### Pipeline

```text
Which processing steps must execute?
```

Multiple/all steps may execute.

---

## 17. Strategy vs Chain of Responsibility

### Strategy

Choose **one behavior/algorithm**.

```text
PaymentService
      |
      +-- UPI
      +-- CreditCard
      +-- PayPal
```

Question:

> Which behavior should I use?

### Chain of Responsibility

Move responsibility through possible handlers.

```text
Request
   |
   v
Manager
   |
   v
SeniorManager
   |
   v
Director
```

Question:

> Who should handle this request?

### Key distinction

Strategy:

```text
Choose one implementation.
```

Chain:

```text
Try handlers until one accepts responsibility.
```

---

## 18. Chain of Responsibility vs Decorator

### Decorator

Adds behavior around an object.

```text
LoggingDecorator
    ↓
CachingDecorator
    ↓
Service
```

The decorators usually contribute behavior and wrap the underlying object.

### Chain of Responsibility

Passes responsibility from one potential handler to another.

```text
Handler A
   ↓
Handler B
   ↓
Handler C
```

The key question is:

```text
Decorator:
"What additional behavior should surround this object?"

Chain:
"Who should handle this request?"
```

A chain can stop when a handler handles the request. A decorator normally continues through the wrapped object to compose behavior.

---

## 19. Chain of Responsibility vs Adapter

### Adapter

Converts one interface into another compatible interface.

```text
Client
  ↓
Target Interface
  ↓
Adapter
  ↓
Existing Incompatible Service
```

Question:

> How do I make this incompatible object usable through the interface I expect?

### Chain

Moves request responsibility between compatible handlers.

Question:

> Which handler should process this request?

Adapter solves **interface compatibility**.

Chain solves **responsibility delegation**.

---

## 20. Chain of Responsibility vs Facade

### Facade

Provides one simplified entry point to a subsystem.

```text
Client
  ↓
Facade
  ├── Service A
  ├── Service B
  └── Service C
```

Question:

> How do I simplify access to several subsystem operations?

### Chain

Provides a sequence of potential handlers.

```text
Client
  ↓
Handler A
  ↓
Handler B
  ↓
Handler C
```

Question:

> Who should handle this request?

A Facade may internally call several components. It does not inherently delegate responsibility until one component handles a request.

---

## 21. Chain of Responsibility vs Mediator

### Chain

Handlers are connected in a directional sequence:

```text
A → B → C → D
```

The next handler is the natural next participant.

### Mediator

A mediator coordinates communication among multiple peer objects.

```text
        Component A
             |
Component B - Mediator - Component C
             |
        Component D
```

Components communicate through a central coordinator rather than passing a request along a linear chain.

### Key distinction

Chain:

```text
Sequential responsibility delegation
```

Mediator:

```text
Centralized coordination between peers
```

---

## 22. Advantages

- Reduces coupling between client and concrete handlers.
- Client does not need to know the full chain.
- Easy to add handlers.
- Easy to reorder handlers through configuration.
- Supports different chains for different contexts.
- Keeps handler-specific behavior localized.
- Avoids giant `if/else` or `switch` orchestration logic.
- Works naturally with composition.
- Individual handlers can be tested independently.

---

## 23. Disadvantages

- Request may pass through many objects.
- Debugging the runtime flow can be harder.
- Ordering of handlers matters.
- Incorrect configuration can cause incorrect business behavior.
- Request may reach the end without being handled.
- A very long chain can make execution harder to reason about.
- If every handler does substantial work, the pattern can become difficult to trace.
- The client/configuration layer still needs to build a valid chain.

---

## 24. Common Mistakes

### Mistake 1: Giant if/else orchestrator

```java
if (amount <= 10000) ...
else if (amount <= 50000) ...
```

This defeats the purpose of decoupling request handling.

### Mistake 2: Concrete `next` type

Bad:

```java
private SeniorManager next;
```

Better:

```java
private Approver next;
```

### Mistake 3: Handler creates its own next handler

Bad:

```java
this.next = new SeniorManager();
```

This hardcodes the hierarchy into the handler.

### Mistake 4: Client manually traverses the chain

Bad:

```java
manager.canProcess();
seniorManager.canProcess();
director.canProcess();
```

The client becomes the chain orchestrator.

### Mistake 5: Public `approve()`

If clients can directly call:

```java
manager.approve(request);
```

they can bypass the common processing flow.

Prefer:

```java
protected abstract void approve(...);
```

with:

```java
public void process(...)
```

as the entry point.

### Mistake 6: Every handler duplicates forwarding logic

Bad:

```java
if (...) {
    approve();
} else {
    if (next != null) {
        next.process();
    }
}
```

in every subclass.

Keep common traversal behavior in the abstraction.

### Mistake 7: Confusing Chain with Pipeline

A pipeline where every step must execute is not automatically the same as a chain where one handler eventually accepts responsibility.

### Mistake 8: Overusing `protected`

Do not make fields/methods protected just because subclasses might someday need them.

Prefer the smallest visibility required.

---

## 25. Production Use Cases

Common real-world examples include:

### Approval workflows

```text
Employee
  ↓
Manager
  ↓
Senior Manager
  ↓
Director
```

### Support escalation

```text
L1 Support
   ↓
L2 Support
   ↓
L3 Support
   ↓
Engineering
```

### Logging

Historically, logging-level handlers are a classic example:

```text
Debug
 ↓
Info
 ↓
Warn
 ↓
Error
```

The exact implementation may vary by framework.

### Request processing

Potential handlers can process a request and either handle it or delegate it.

### Authorization / validation

Can use chain-like structures when responsibility is conditionally delegated.

However, if every validation step must always execute, a pipeline/filter/interceptor model may be more appropriate.

---

## 26. Spring Boot / Production Relevance

Spring applications frequently contain structures that resemble chains.

Examples include:

- Servlet filters.
- Spring Security filter chains.
- Handler interceptors.
- Request processing interceptors.
- Middleware-style processing.
- Exception handling flows.
- Validation/processing chains.

A conceptual filter chain looks like:

```text
HTTP Request
    ↓
Filter A
    ↓
Filter B
    ↓
Filter C
    ↓
Controller
```

But remember:

**Not every Spring chain is exactly the classic Chain of Responsibility implementation.**

Some are better described as pipelines because multiple filters execute.

The important interview skill is recognizing the underlying delegation structure rather than blindly labeling every sequence a design pattern.

---

## 27. How to Make the Chain Configurable

Instead of hardcoding:

```java
manager.setNext(seniorManager);
seniorManager.setNext(director);
```

inside concrete handlers, configuration can own it.

Possible approaches:

- Factory.
- Builder.
- Spring configuration.
- Dependency injection.
- Ordered list of handlers.
- Configuration properties.

Example conceptual Spring configuration:

```text
ApprovalChainConfiguration
        ↓
Manager
        ↓
SeniorManager
        ↓
Director
```

The handlers themselves remain unaware of the overall hierarchy.

---

## 28. Testing Individual Handlers

A handler can be tested independently.

For example:

```text
Manager limit = ₹10,000

₹5,000
→ Manager approves

₹20,000
→ Manager does not approve
→ forwards
```

Test cases should cover:

- Request within handler limit.
- Request above handler limit.
- Correct next handler invocation.
- End-of-chain failure.
- Correct approval behavior.
- Chain ordering.

This is one advantage of separating:

```text
process()
canProcess()
approve()
forward()
```

---

## 29. Interview Questions Discussed

### Q1. Why can't one handler solve the request?

Because responsibility may depend on the request. A Manager may handle small expenses while a Director handles larger expenses.

---

### Q2. What should happen if the current handler cannot process the request?

It should delegate/forward the request to its configured next handler.

---

### Q3. Who decides the next handler?

The current handler forwards to its configured `next`.

The client does not decide dynamically during request processing.

---

### Q4. Should the client know the complete chain?

No.

The client should normally know only the entry point:

```java
manager.process(request);
```

The rest is encapsulated in the chain.

---

### Q5. Why introduce a Handler/Approver abstraction?

Because every handler shares common behavior:

- `next`
- forwarding
- request-processing flow
- potentially common eligibility logic

It also allows the chain to hold:

```java
Approver next;
```

instead of concrete types.

---

### Q6. Why composition?

Each handler contains:

```java
private Approver next;
```

This creates a configurable has-a relationship.

The hierarchy can change without changing the handler implementation.

---

### Q7. What is the successor/next handler?

The next object to which the current handler delegates the request when it cannot handle it.

In our implementation:

```java
private Approver next;
```

---

### Q8. When should the chain stop?

In our expense approval example, it stops when a handler can process and successfully approves the request.

```text
canProcess == true
    ↓
approve()
    ↓
STOP
```

---

### Q9. What happens if nobody handles the request?

The chain reaches its end:

```java
next == null
```

and the system should reject/fail the request according to business requirements.

---

### Q10. What happens if a new handler is added?

Ideally existing handlers don't need modification.

The chain is reconfigured:

```text
Manager → SeniorManager → AVP → Director
```

instead of:

```text
Manager → SeniorManager → Director
```

---

### Q11. How does this reduce coupling?

The client does not know every concrete handler.

Each handler knows only:

```java
Approver next;
```

rather than:

```java
SeniorManager next;
```

or:

```java
Director next;
```

---

### Q12. Why isn't this Strategy?

Strategy chooses one implementation/algorithm.

Chain passes responsibility through multiple potential handlers.

```text
Strategy:
choose one behavior

Chain:
try handlers until one accepts responsibility
```

---

### Q13. Why isn't this Decorator?

Decorator adds/wraps behavior around an object.

Chain delegates responsibility to another potential handler.

---

### Q14. Why isn't this Adapter?

Adapter converts an incompatible interface into the interface expected by the client.

Chain delegates request responsibility.

---

### Q15. Why isn't this Facade?

Facade simplifies access to multiple subsystem components.

Chain decides which handler should take responsibility.

---

### Q16. Why isn't this Mediator?

Mediator centrally coordinates communication among peer objects.

Chain uses sequential delegation:

```text
A → B → C
```

---

### Q17. How would you make the chain configurable?

Use external configuration, a factory, builder, dependency injection, Spring configuration, or another composition mechanism.

---

### Q18. How would you test an individual handler?

Instantiate it independently and test:

- It approves eligible requests.
- It forwards ineligible requests.
- It invokes the correct next handler.
- It handles end-of-chain behavior correctly.

---

### Q19. Should `next` be private or protected?

Prefer private.

The chain's internal reference should remain encapsulated.

---

### Q20. Should `limit` be private or protected?

Prefer:

```java
private final BigDecimal limit;
```

when the limit is immutable configuration.

---

### Q21. Should `approve()` be public?

No, not for our design.

Prefer:

```java
protected abstract void approve(...)
```

so clients cannot bypass `process()`.

---

### Q22. Should `process()` be public?

Yes.

It is the public entry point into the handler chain.

---

### Q23. Should `canProcess()` be private or protected?

Depends on the business invariant.

If every handler must obey the same limit rule:

```java
private
```

If handlers genuinely need customized eligibility rules:

```java
protected
```

Do not expose it unnecessarily.

---

### Q24. Should chain construction live inside Manager?

No, not if we want flexible composition.

Hardcoding:

```java
manager.next = new SeniorManager();
```

couples Manager to the hierarchy.

Prefer external composition.

---

### Q25. Is using a Factory to build the chain the Factory Pattern?

Not necessarily.

The Factory solves creation/configuration.

The Chain solves request delegation.

Patterns can be combined.

---

### Q26. Can the client configure a chain that skips SeniorManager?

Technically yes:

```text
Manager → Director
```

Whether it is a valid business configuration depends on the application's rules.

If business validation is required, a dedicated configuration/factory component can enforce those rules.

---

### Q27. Should every handler continue after handling the request?

Not necessarily.

In our expense approval problem:

```text
handle → STOP
```

If every stage must execute, the problem starts looking more like a processing pipeline.

---

### Q28. Is every validation pipeline Chain of Responsibility?

No.

If all validators must execute:

```text
Validation A
    ↓
Validation B
    ↓
Validation C
```

that is more naturally a pipeline.

If responsibility moves until one handler accepts it:

```text
Handler A
    ↓ cannot handle
Handler B
    ↓ can handle
STOP
```

that fits Chain of Responsibility more directly.

---

## 30. Interview-Ready Explanation

A concise SDE-2 answer:

> "I would use Chain of Responsibility when a request can be handled by one of several potential handlers and I don't want the caller to know which concrete handler should process it. Each handler has a reference to the next handler. It either handles the request or delegates it. This keeps the client decoupled from the chain and allows the chain to be extended or reordered through composition."

Expense example:

```text
Client
  ↓
Manager
  ↓ cannot handle
SeniorManager
  ↓ cannot handle
Director
  ↓
approve
```

---

## 31. Quick Revision Cheatsheet

```text
Pattern:
Chain of Responsibility

Family:
Behavioral

Core problem:
Multiple possible handlers for a request.

Key idea:
Current handler either handles or forwards.

Core relationship:
Handler HAS-A next Handler.

Abstraction:
Approver / Handler

Important methods:
process()
forward()
setNext()
approve()

Client:
Starts processing, does not traverse the chain.

Composition:
External configuration builds the chain.

Inheritance:
Concrete handlers implement the common abstraction.

Stops:
When a handler accepts responsibility.

End of chain:
Reject/fail if nobody handles.

Main benefit:
Reduced coupling between client and concrete handlers.

OCP:
Add/reorder handlers without modifying existing handlers.

Strategy:
Choose one behavior.

Decorator:
Add behavior around an object.

Adapter:
Convert interface.

Facade:
Simplify subsystem access.

Mediator:
Coordinate peers centrally.

Pipeline:
Multiple/all processing stages execute.

Chain:
Responsibility moves until a handler handles it.
```

---

## 32. Final Mental Model

Do not memorize a diagram.

Ask:

```text
I have a request.
I don't want the caller deciding who handles it.

Can I start at one handler?

If it can handle:
    handle and stop.

If it cannot:
    pass to the next handler.

Can I configure the chain externally?

Yes.

Can every handler depend only on an abstraction?

Yes.

Can I add/reorder handlers without modifying existing handlers?

Ideally yes.
```

That reasoning should lead you to Chain of Responsibility naturally.

---

## 33. Status

### Completed

- [x] Derived the problem
- [x] Identified need for multiple possible handlers
- [x] Derived request forwarding
- [x] Derived chain composition
- [x] Designed `Approver` abstraction
- [x] Implemented `next`
- [x] Implemented `setNext()`
- [x] Implemented `forward()`
- [x] Implemented `process()`
- [x] Implemented `canProcess()`
- [x] Implemented `approve()`
- [x] Implemented Manager
- [x] Implemented SeniorManager
- [x] Derived client decoupling
- [x] Derived composition over inheritance for chain construction
- [x] Discussed OCP
- [x] Discussed abstraction vs concrete dependency
- [x] Compared Strategy
- [x] Compared pipeline behavior
- [x] Discussed Factory for chain configuration
- [x] Discussed encapsulation decisions

### Potential follow-up

- [ ] Final code cleanup
- [ ] Domain-specific exception
- [ ] Additional concrete handlers
- [ ] More interview challenges
- [ ] Detailed SOLID defense
- [ ] Production/Spring Boot discussion
- [ ] Full pattern comparison under interview pressure
