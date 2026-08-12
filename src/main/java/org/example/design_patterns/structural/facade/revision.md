# Facade Design Pattern - SDE-2 Java LLD Revision

## 1. Intent

Provide a simple entry point for a client to perform a higher-level operation when that operation internally requires coordinating multiple existing classes.

The key idea discovered during learning:

> The client should express **what it wants**, while the Facade owns the knowledge of **which internal operations are required and in what sequence**.

A Facade primarily **hides complexity and coordination**. It does not necessarily change the behavior of the underlying subsystem classes.

---

# 2. Problem

Consider a simple video player.

The system already has:

```java
class VideoFile {
    void load(String fileName) {
        System.out.println("Loading " + fileName);
    }

    void play() {
        System.out.println("Playing video");
    }
}

class AudioPlayer {
    void start() {
        System.out.println("Starting audio");
    }
}

class Screen {
    void on() {
        System.out.println("Turning screen on");
    }
}
```

Without a Facade, the client needs to know all the internal steps:

```java
VideoFile videoFile = new VideoFile();
AudioPlayer audioPlayer = new AudioPlayer();
Screen screen = new Screen();

videoFile.load("movie.mp4");
audioPlayer.start();
screen.on();
videoFile.play();
```

The client's actual intention is much simpler:

> "Play this movie."

Yet the client must know:

- Which classes are involved.
- Which methods to call.
- The correct order.
- The internal workflow required to play the video.

This creates unnecessary coupling between the client and the subsystem.

---

# 3. Recognition Checklist

Think about Facade when:

- One client operation requires several lower-level objects.
- The client knows too much about those objects.
- The client must coordinate multiple calls.
- The client must know the correct sequence of calls.
- A higher-level operation can be expressed much more simply.
- You want clients to depend on one entry point instead of many subsystem classes.
- The internal workflow may change while the client's intent remains the same.

Typical smell:

```text
Client
  |
  +--> Service A
  +--> Service B
  +--> Service C
  +--> Service D
```

and the client knows the sequence:

```text
A -> B -> C -> D
```

Potential Facade:

```text
Client
  |
  +--> Facade
          |
          +--> Service A
          +--> Service B
          +--> Service C
          +--> Service D
```

The client now says:

```java
facade.performOperation();
```

---

# 4. The Core Insight Derived During Learning

The learning started from a problem rather than a definition.

The original video-player client had to do:

```java
videoFile.load("movie.mp4");
audioPlayer.start();
screen.on();
videoFile.play();
```

We asked:

> Why should the client know all these steps?

The desired client API became:

```java
player.play("movie.mp4");
```

Then we derived:

1. The client knows too much about the internal workflow.
2. The workflow belongs somewhere other than the client.
3. `VideoPlayer` should coordinate the existing objects.
4. `VideoPlayer` needs references to `VideoFile`, `AudioPlayer`, and `Screen`.
5. These are separate things, so inheritance does not represent the relationship.
6. `VideoPlayer` uses composition.
7. The client interacts with one simple operation.
8. The underlying subsystem classes continue doing their own jobs.
9. `VideoPlayer` mainly coordinates existing behavior.
10. This object is the Facade.

This is the interview-level derivation to remember.

---

# 5. Participants

## Client

The object that wants to perform a high-level operation.

The client should ideally know only the simple entry point:

```java
player.play("movie.mp4");
```

It should not need to understand the complete internal workflow.

---

## Facade

The object providing the simplified entry point.

Example:

```java
VideoPlayer
```

Its responsibility is to coordinate the required subsystem operations.

Example:

```java
public void play(String fileName) {
    videoFile.load(fileName);
    audioPlayer.start();
    screen.on();
    videoFile.play();
}
```

---

## Subsystem Classes

The existing classes that perform individual responsibilities.

Example:

```text
VideoFile
AudioPlayer
Screen
```

They remain focused on their own jobs.

The Facade does not need to replace them.

---

# 6. Basic UML

```text
                  +----------------+
                  |     Client     |
                  +-------+--------+
                          |
                          | play()
                          v
                  +----------------+
                  |   VideoPlayer  |
                  |    Facade      |
                  +-------+--------+
                          |
             +------------+------------+
             |            |            |
             v            v            v
      +-----------+ +-----------+ +-----------+
      | VideoFile | |AudioPlayer| |  Screen   |
      +-----------+ +-----------+ +-----------+
```

Relationship:

```text
Client --> Facade
Facade --> Subsystem classes
```

The Facade generally uses **composition**.

---

# 7. Why Composition Instead of Inheritance?

The relationships are:

```text
VideoPlayer HAS-A VideoFile
VideoPlayer HAS-A AudioPlayer
VideoPlayer HAS-A Screen
```

They are not:

```text
AudioPlayer IS-A VideoPlayer
Screen IS-A VideoPlayer
VideoFile IS-A VideoPlayer
```

Therefore inheritance would model the domain incorrectly.

Composition:

```java
class VideoPlayer {
    private VideoFile videoFile;
    private AudioPlayer audioPlayer;
    private Screen screen;
}
```

Inheritance:

```java
class AudioPlayer extends VideoPlayer
```

would incorrectly claim that an AudioPlayer is a VideoPlayer.

Interview answer:

> I use composition because the Facade collaborates with existing subsystem objects rather than representing an "is-a" relationship with them.

---

# 8. Complete Java Implementation - Video Player

```java
class VideoFile {

    void load(String fileName) {
        System.out.println("Loading " + fileName);
    }

    void play() {
        System.out.println("Playing video");
    }
}

class AudioPlayer {

    void start() {
        System.out.println("Starting audio");
    }
}

class Screen {

    void on() {
        System.out.println("Turning screen on");
    }
}

class VideoPlayer {

    private final VideoFile videoFile;
    private final AudioPlayer audioPlayer;
    private final Screen screen;

    public VideoPlayer() {
        this.videoFile = new VideoFile();
        this.audioPlayer = new AudioPlayer();
        this.screen = new Screen();
    }

    public void play(String fileName) {
        videoFile.load(fileName);
        audioPlayer.start();
        screen.on();
        videoFile.play();
    }
}
```

Client:

```java
public class Main {

    public static void main(String[] args) {

        VideoPlayer player = new VideoPlayer();

        player.play("movie.mp4");
    }
}
```

---

# 9. Runtime / Sequence Flow

Client:

```java
player.play("movie.mp4");
```

Execution:

```text
Client
  |
  | play("movie.mp4")
  v
VideoPlayer
  |
  | videoFile.load()
  v
VideoFile
  |
  | audioPlayer.start()
  v
AudioPlayer
  |
  | screen.on()
  v
Screen
  |
  | videoFile.play()
  v
VideoFile
```

The important point is that the client does not coordinate this sequence.

---

# 10. Checkout Example

Suppose an order checkout requires:

```text
InventoryService
PaymentService
OrderService
NotificationService
```

Without a Facade:

```java
inventoryService.checkStock(order);
paymentService.pay(order);
orderService.createOrder(order);
notificationService.sendConfirmation(order);
```

The client knows:

- all four classes
- all four operations
- the required sequence

With a Facade:

```java
checkoutService.checkout(order);
```

Facade:

```java
class CheckoutService {

    private InventoryService inventoryService;
    private PaymentService paymentService;
    private OrderService orderService;
    private NotificationService notificationService;

    public CheckoutService() {
        inventoryService = new InventoryService();
        paymentService = new PaymentService();
        orderService = new OrderService();
        notificationService = new NotificationService();
    }

    public void checkout(Order order) {
        inventoryService.checkStock(order);
        paymentService.pay(order);
        orderService.createOrder(order);
        notificationService.sendConfirmation(order);
    }
}
```

The key discovery:

> If the client's only intention is "checkout this order", the client should not need to know the four-step internal workflow.

---

# 11. What Facade Hides

A Facade can hide:

- Number of subsystem classes involved.
- Method-level details.
- Ordering of operations.
- Object creation.
- Coordination logic.
- Internal workflow.
- Low-level implementation details.

For example:

```text
Client:
    checkout(order)

Facade:
    check inventory
    process payment
    create order
    send notification
```

The client cares about the first statement.

The Facade owns the second workflow.

---

# 12. What Facade Does NOT Necessarily Do

A Facade does not necessarily:

- Change subsystem behavior.
- Replace subsystem classes.
- Add behavior around every method.
- Make subsystem classes impossible to access.
- Require inheritance.
- Require a complex abstraction hierarchy.

A Facade primarily provides a simpler entry point.

---

# 13. Important Coupling Insight

Without Facade:

```text
Client
  |
  +--> InventoryService
  +--> PaymentService
  +--> OrderService
  +--> NotificationService
```

The client is coupled to many implementation details.

With Facade:

```text
Client
  |
  +--> CheckoutService
          |
          +--> InventoryService
          +--> PaymentService
          +--> OrderService
          +--> NotificationService
```

The client depends on one higher-level interface.

If the internal workflow changes:

```text
Old:
A -> B -> C -> D

New:
A -> X -> B -> C -> D
```

ideally the client remains:

```java
checkoutService.checkout(order);
```

while the Facade changes.

---

# 14. SOLID Principles

## Single Responsibility Principle

The subsystem classes continue owning their individual responsibilities.

Example:

```text
InventoryService -> inventory responsibility
PaymentService -> payment responsibility
NotificationService -> notification responsibility
```

The Facade owns the higher-level coordination exposed to the client.

Important nuance:

A Facade should not become a giant "god class" containing all business logic.

It should primarily coordinate.

---

## Open/Closed Principle

Adding or changing internal workflow may require modifying the Facade, but the client can remain unchanged.

Example:

```text
Client:
checkout(order)
```

Internal workflow:

```text
check stock
pay
create order
notify
```

can evolve without changing the client's call.

Facade does not automatically guarantee OCP. It simply gives a stable boundary that can reduce the blast radius of internal changes.

---

## Dependency Inversion Principle

A simple educational Facade can instantiate subsystem objects directly:

```java
this.paymentService = new PaymentService();
```

That is acceptable for learning the pattern.

In production code, dependency injection is usually preferable:

```java
public CheckoutService(
        InventoryService inventoryService,
        PaymentService paymentService,
        OrderService orderService,
        NotificationService notificationService) {

    this.inventoryService = inventoryService;
    this.paymentService = paymentService;
    this.orderService = orderService;
    this.notificationService = notificationService;
}
```

This improves:

- Testability
- Configurability
- Extensibility
- Dependency management

---

# 15. Constructor Creation vs Dependency Injection

## Simple learning version

```java
public CheckoutService() {
    inventoryService = new InventoryService();
    paymentService = new PaymentService();
    orderService = new OrderService();
    notificationService = new NotificationService();
}
```

Pros:

- Very easy to understand.
- Self-contained.
- Good for demonstrating the pattern.

Cons:

- Tightly couples the Facade to concrete implementations.
- Harder to unit test.
- Harder to replace implementations.

---

## Production-oriented version

```java
public CheckoutService(
        InventoryService inventoryService,
        PaymentService paymentService,
        OrderService orderService,
        NotificationService notificationService) {

    this.inventoryService = inventoryService;
    this.paymentService = paymentService;
    this.orderService = orderService;
    this.notificationService = notificationService;
}
```

This is generally preferable in Spring Boot.

---

# 16. Facade vs Adapter

## Adapter

Problem:

> The interface I have does not match the interface my client expects.

Adapter changes the interface.

```text
Client
  |
  | expected interface
  v
Adapter
  |
  | translates
  v
Existing incompatible class
```

Mental model:

> **Adapter = make this existing thing fit my interface.**

Example:

```text
Client expects:
pay()

Existing class:
makePayment()

Adapter:
pay() -> makePayment()
```

---

## Facade

Problem:

> The client has to interact with too many classes/steps to accomplish one operation.

Facade provides a simpler entry point.

```text
Client
  |
  v
Facade
  |
  +--> Service A
  +--> Service B
  +--> Service C
```

Mental model:

> **Facade = give me one simple door to a complicated subsystem.**

---

## Key Difference

| Adapter | Facade |
|---|---|
| Interface compatibility | Interface simplification |
| Usually wraps one existing component or a small set | Usually coordinates multiple subsystem classes |
| Translates calls | Orchestrates calls |
| Makes incompatible interface usable | Makes complex subsystem easier to use |
| "Make these interfaces fit" | "Hide these details behind one entry point" |

Interview question:

> Why isn't this Adapter?

Answer:

> Because I'm not translating an incompatible interface. The subsystem classes are usable already. I'm providing a simpler entry point that coordinates multiple existing operations.

---

# 17. Facade vs Decorator

Decorator:

> Add behavior/responsibility around an existing object while preserving its interface.

Example:

```text
Coffee
  |
  +--> MilkDecorator
          |
          +--> SugarDecorator
```

The Decorator focuses on extending behavior.

Facade:

```text
Client
  |
  v
Facade
  |
  +--> A
  +--> B
  +--> C
```

The Facade focuses on simplifying access.

Mental model:

```text
Decorator -> add behavior
Facade    -> simplify access
```

Interview answer:

> Decorator wraps an object to add behavior while generally preserving the interface. Facade wraps or coordinates a subsystem to provide a simpler interface.

---

# 18. Facade vs Strategy

Strategy:

> Select one algorithm/behavior from multiple interchangeable implementations.

Example:

```text
PaymentStrategy
   |
   +--> UPI
   +--> CreditCard
   +--> Wallet
```

The client selects or receives a strategy.

Facade:

```text
CheckoutFacade
   |
   +--> inventory
   +--> payment
   +--> order
   +--> notification
```

Mental model:

```text
Strategy -> choose HOW something is done
Facade   -> simplify WHAT the client needs to call
```

A Facade may internally use Strategy.

These patterns are not mutually exclusive.

---

# 19. Facade vs Factory

Factory:

> Encapsulate object creation.

Example:

```java
Payment payment = PaymentFactory.create(PaymentType.UPI);
```

The client does not need to know which concrete class to instantiate.

Facade:

> Encapsulate access to a multi-step operation.

Example:

```java
checkoutService.checkout(order);
```

Mental model:

```text
Factory -> object creation
Facade  -> subsystem access / coordination
```

A Facade can use a Factory internally.

---

# 20. Facade vs Mediator

This distinction is important in interviews.

## Facade

The main goal is:

> Simplify the interface presented to clients.

```text
Client
  |
  v
Facade
  |
  +--> A
  +--> B
  +--> C
```

The subsystem components may not need to know about the Facade.

---

## Mediator

The main goal is:

> Centralize communication between peer objects so they do not communicate directly with each other.

```text
        A
        |
        v
     Mediator
      /    \
     v      v
    B        C
```

The participants communicate through the Mediator.

Mental model:

```text
Facade  -> simplifies client-to-subsystem interaction
Mediator -> simplifies object-to-object interaction
```

---

# 21. Facade vs Service Layer

This is a common backend interview trap.

They can look extremely similar.

A Service Layer often:

- Contains application/business use cases.
- Coordinates domain operations.
- Defines transactional boundaries.
- Applies business rules.
- Handles application-level workflows.

A Facade primarily:

- Provides a simplified interface.
- Hides subsystem complexity.
- Coordinates existing components.

Therefore:

> A Spring `@Service` can act as a Facade, but not every Service is necessarily a Facade.

Example:

```java
@Service
class CheckoutService {

    public void checkout(Order order) {
        inventoryService.checkStock(order);
        paymentService.pay(order);
        orderService.createOrder(order);
        notificationService.sendConfirmation(order);
    }
}
```

This can simultaneously be:

- A Spring Service.
- An application service.
- A Facade over several subsystem services.

The name does not determine the pattern. The **responsibility and role** do.

---

# 22. Facade vs API Gateway

These are very different architectural levels.

## Facade

Usually an in-process object/class.

```text
Java application
    |
    v
Facade
    |
    +--> internal components
```

## API Gateway

A system-level network boundary.

```text
Client
   |
   v
API Gateway
   |
   +--> Service A
   +--> Service B
   +--> Service C
```

API Gateway responsibilities may include:

- Routing
- Authentication/authorization integration
- Rate limiting
- TLS termination
- Request aggregation
- Protocol translation
- Observability
- Load balancing

A Facade does not become an API Gateway merely because both provide one entry point.

Mental model:

```text
Facade      -> code-level simplification
API Gateway -> system/network-level boundary
```

---

# 23. Production Use Cases

Common production-style uses:

## Checkout

```text
CheckoutFacade
    |
    +--> Inventory
    +--> Payment
    +--> Order
    +--> Notification
```

Client:

```java
checkoutFacade.checkout(order);
```

---

## Video Processing

```text
VideoProcessingFacade
    |
    +--> Decoder
    +--> AudioProcessor
    +--> FrameProcessor
    +--> Encoder
    +--> Storage
```

Client:

```java
videoFacade.process(video);
```

---

## Report Generation

```text
ReportFacade
    |
    +--> DataFetcher
    +--> Aggregator
    +--> Formatter
    +--> Exporter
```

Client:

```java
reportFacade.generate(request);
```

---

## File Upload Workflow

```text
UploadFacade
    |
    +--> Validator
    +--> Storage
    +--> MetadataService
    +--> VirusScanner
    +--> NotificationService
```

Client:

```java
uploadFacade.upload(file);
```

---

# 24. Spring Boot Relevance

Facade is often naturally implemented using a Spring `@Service`.

Example:

```java
@Service
public class CheckoutService {

    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final NotificationService notificationService;

    public CheckoutService(
            InventoryService inventoryService,
            PaymentService paymentService,
            OrderService orderService,
            NotificationService notificationService) {

        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    public void checkout(Order order) {

        inventoryService.checkStock(order);
        paymentService.pay(order);
        orderService.createOrder(order);
        notificationService.sendConfirmation(order);
    }
}
```

Controller:

```java
@RestController
class CheckoutController {

    private final CheckoutService checkoutService;

    CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public void checkout(@RequestBody Order order) {
        checkoutService.checkout(order);
    }
}
```

The Controller does not need to coordinate the internal services.

```text
HTTP Client
    |
    v
Controller
    |
    v
CheckoutService
    |
    +--> InventoryService
    +--> PaymentService
    +--> OrderService
    +--> NotificationService
```

Important:

> Don't force the word "Facade" into every Spring Service. Identify the role first.

---

# 25. Common Mistakes

## Mistake 1: Thinking Facade means "one class with many methods"

Not enough.

The important question is:

> Is the class providing a simplified interface to a more complicated subsystem?

---

## Mistake 2: Putting all business logic into the Facade

A Facade should not automatically become a god object.

Prefer:

```text
Facade
  -> coordinates

Subsystem services
  -> own their responsibilities
```

---

## Mistake 3: Using inheritance

Incorrect reasoning:

```java
class AudioPlayer extends VideoPlayer
```

because these are not "is-a" relationships.

Use composition.

---

## Mistake 4: Thinking Facade changes subsystem behavior

Usually the subsystem classes retain their responsibilities.

The Facade primarily coordinates them.

---

## Mistake 5: Confusing Facade with Adapter

Ask:

> Am I translating an incompatible interface?

If yes, think Adapter.

Ask:

> Am I simplifying access to several existing operations?

If yes, think Facade.

---

## Mistake 6: Confusing Facade with Decorator

Ask:

> Am I adding behavior around an existing object?

If yes, think Decorator.

Ask:

> Am I providing a simpler entry point?

If yes, think Facade.

---

## Mistake 7: Confusing Facade with Strategy

Ask:

> Am I choosing between interchangeable algorithms?

If yes, think Strategy.

---

## Mistake 8: Overengineering a tiny subsystem

Not every class calling another class requires a Facade.

A Facade is useful when the abstraction meaningfully reduces client complexity or coupling.

---

# 26. Common Interview Traps

## Trap 1: "Facade always hides all subsystem classes"

Not necessarily.

A Facade provides a simplified entry point. Whether subsystem classes are accessible depends on the API/design.

---

## Trap 2: "Facade always means orchestration"

Orchestration is common, but the defining idea is the **simplified interface to a subsystem**.

Do not blindly label every orchestrator a Facade.

---

## Trap 3: "Facade must create all subsystem objects"

No.

It can use:

- Constructor injection.
- Dependency injection.
- Factories.
- Spring-managed beans.
- Existing objects.

---

## Trap 4: "Facade and Service Layer are the same"

They can overlap, but they are not identical concepts.

A Service Layer may own business/application logic and transactions.

A Facade emphasizes simplified access to a subsystem.

---

## Trap 5: "Facade is always better"

No.

Adding a Facade introduces another abstraction.

For a trivial subsystem:

```text
Client -> Service
```

may already be perfectly clear.

Use a Facade when the simplification provides meaningful value.

---

# 27. Advantages

- Simplifies client code.
- Reduces client coupling to subsystem classes.
- Hides internal workflow.
- Centralizes coordination.
- Gives clients a stable high-level entry point.
- Makes subsystem usage easier.
- Can reduce the blast radius of internal workflow changes.
- Improves readability at the call site.

---

# 28. Disadvantages

- Adds another abstraction.
- Facade can become a God Object if responsibilities keep accumulating.
- Can hide useful subsystem capabilities if designed too aggressively.
- Internal changes may still require Facade changes.
- Poorly designed Facades can become tightly coupled to many concrete classes.
- Does not automatically solve business transaction/failure semantics.
- Does not automatically make a system loosely coupled everywhere.

---

# 29. Important Tradeoffs

## Simplicity vs flexibility

Facade:

```java
checkout(order);
```

is simpler.

Direct subsystem access:

```java
inventory.checkStock(order);
payment.pay(order);
...
```

may offer more flexibility.

---

## Encapsulation vs access

A Facade can intentionally expose only common workflows.

Advanced clients may still need direct subsystem APIs in some systems.

---

## Stable interface vs Facade growth

A Facade can become a useful stable boundary.

But if every new feature is added to the same Facade:

```text
CheckoutFacade
  + checkout
  + refund
  + cancel
  + retry
  + reserve
  + ...
```

it can become a God Object.

Split responsibilities when necessary.

---

# 30. Interview Defense

If asked:

> Why introduce another object?

Answer:

> The client was directly coordinating multiple subsystem classes and had to know their workflow and ordering. The Facade gives the client one high-level operation and moves that coordination responsibility behind a stable boundary.

---

If asked:

> Why does this reduce coupling?

Answer:

> The client no longer depends directly on every subsystem involved in the workflow. It depends primarily on the Facade. Internal subsystem changes can therefore often be isolated behind the Facade.

---

If asked:

> Why composition?

Answer:

> The Facade uses the subsystem classes; it is not a specialization of them. This is a has-a relationship, so composition models the design correctly.

---

If asked:

> What happens if another subsystem is added?

Answer:

> If the new subsystem is part of the same workflow, the Facade may need to change, but the client can continue using the same high-level operation.

---

If asked:

> What happens if we remove the Facade?

Answer:

> The client has to coordinate the subsystem classes directly, exposing internal workflow and increasing coupling.

---

# 31. Interview Questions and Reasoning

## Q1. Why does the client need a Facade?

Because the client otherwise needs to know several subsystem classes and their workflow.

---

## Q2. What responsibility belongs to the client?

The client should express the high-level intent.

Example:

```java
checkout(order);
```

It should generally not own the detailed internal sequence.

---

## Q3. What responsibility belongs to the Facade?

The Facade provides the simplified entry point and coordinates the required subsystem operations.

---

## Q4. Why introduce another object?

To move subsystem coordination away from clients and provide a simpler interface.

---

## Q5. What does the Facade actually simplify?

It simplifies the client's interaction with multiple classes, methods, and workflow steps.

---

## Q6. Why should the client depend on the Facade instead?

Because the client cares about the higher-level operation, not the internal subsystem workflow.

---

## Q7. Is Facade hiding complexity or changing behavior?

Primarily hiding complexity and simplifying access.

The underlying subsystem behavior can remain unchanged.

---

## Q8. Why composition?

Because the Facade uses subsystem objects rather than being a subtype of them.

---

## Q9. Why isn't this Adapter?

Because there is no incompatible interface being translated.

The problem is subsystem complexity, not interface incompatibility.

---

## Q10. Why isn't this Decorator?

Because we are not primarily adding behavior around an existing object.

We are simplifying access to multiple existing components.

---

## Q11. Why isn't this Strategy?

Because we are not selecting among interchangeable algorithms.

We are coordinating a workflow behind a simpler entry point.

---

## Q12. Why isn't this Factory?

Because the primary responsibility is not object creation.

The Facade's primary role is simplified subsystem access.

---

## Q13. What happens when another subsystem is added?

The Facade may need to coordinate it, while the client can remain unchanged.

---

## Q14. Who owns orchestration responsibility?

The Facade can own the coordination required for the high-level operation, while individual subsystem classes retain their own responsibilities.

---

## Q15. How does Facade reduce coupling?

It reduces the client's direct dependencies on multiple subsystem components and their workflow.

---

## Q16. What are the tradeoffs?

Benefits:

- Simpler client.
- Lower client-to-subsystem coupling.
- Centralized workflow.

Costs:

- Additional abstraction.
- Potential Facade bloat.
- Possible loss of flexibility if too much is hidden.

---

# 32. Recognition Mental Model

When you see:

```text
Client needs one business operation

BUT

Client must call:
A
B
C
D

in a particular sequence
```

ask:

> "Should one object provide a simpler entry point and own this coordination?"

If yes, investigate Facade.

Do not automatically conclude Facade just because multiple method calls exist.

---

# 33. One-Line Mental Models

```text
Facade  = simplify access
Adapter = translate interface
Decorator = add behavior
Strategy = choose algorithm
Factory = create object
Observer = notify dependents
```

The most important Facade phrase:

> **One simple entry point to a more complicated subsystem.**

---

# 34. Quick Comparison Table

| Pattern | Main Problem | Core Idea |
|---|---|---|
| Facade | Too much subsystem complexity exposed to client | Simplify access |
| Adapter | Incompatible interface | Translate interface |
| Decorator | Need to add behavior | Wrap and extend |
| Strategy | Multiple interchangeable algorithms | Encapsulate/choose algorithm |
| Factory | Object creation complexity | Encapsulate creation |
| Observer | Dependent objects need updates | Publish notifications |
| Mediator | Objects communicate too directly | Centralize communication |

---

# 35. Production Interview Nuance

A strong SDE-2 answer should avoid saying:

> "Facade means putting all services inside one class."

Instead say:

> "I would use a Facade when a client needs to interact with several subsystem components to perform a coherent higher-level operation. The Facade exposes that higher-level operation and hides the subsystem coordination from the client."

Then discuss:

- Dependency injection.
- Testability.
- Failure handling.
- Transaction boundaries.
- Whether the Facade is becoming a God Object.
- Whether a Service Layer is a more appropriate abstraction.
- Whether clients still need lower-level operations.

---

# 36. Final Cheat Sheet

## Recognize

```text
Client knows too many subsystem classes
        +
Client knows workflow/order
        +
One high-level operation is desired
        =
Consider Facade
```

## Structure

```text
Client
  |
  v
Facade
  |
  +--> Subsystem A
  +--> Subsystem B
  +--> Subsystem C
```

## Relationship

```text
Facade HAS-A subsystem objects
```

Use composition.

## Purpose

```text
Simplify client interaction
Reduce client coupling
Hide coordination
```

## Not:

```text
Adapter -> interface translation
Decorator -> behavior extension
Strategy -> algorithm selection
Factory -> object creation
Mediator -> peer communication
API Gateway -> network/system boundary
```

---

# 37. Questions Discussed During Learning

1. What should change so the client doesn't need to know all individual classes and steps?
2. Should VideoPlayer depend on VideoFile, AudioPlayer, and Screen or should they inherit from VideoPlayer?
3. Is an AudioPlayer a VideoPlayer, or does VideoPlayer simply need to use AudioPlayer?
4. What methods belong inside VideoPlayer.play()?
5. Who owns responsibility for coordinating the subsystem classes?
6. Where should VideoPlayer get the subsystem objects from?
7. What should the VideoPlayer constructor do?
8. What objects should be created inside the constructor?
9. What complexity has been hidden from the client?
10. If another internal step is added, who should change?
11. Is the Facade changing subsystem behavior or coordinating existing operations?
12. Why should the Facade use composition?
13. Why introduce VideoPlayer instead of letting the client call all three classes?
14. Does Facade completely hide subsystem classes?
15. What smell indicates Facade in a checkout workflow?
16. What does the client know that it shouldn't need to know?
17. Which object should know the checkout sequence?
18. What should CheckoutService contain?
19. How should the dependencies be created?
20. What should be the first checkout operation?
21. Why does the client benefit from CheckoutService?
22. How does Facade reduce coupling?
23. What happens when another subsystem is added?
24. Why isn't the pattern Adapter?
25. Why isn't it Decorator?
26. Why isn't it Strategy?
27. Why isn't it Factory?
28. How is it different from Mediator?
29. How is it different from Service Layer?
30. How is it different from API Gateway?

---

# 38. Final Interview Answer

If asked:

> "Explain the Facade pattern."

A concise SDE-2 answer:

> "Facade provides a simplified entry point to a group of existing subsystem classes. I use it when the client would otherwise need to know multiple classes and coordinate their workflow to perform one higher-level operation. The Facade encapsulates that coordination and exposes a simpler API, reducing coupling between the client and the subsystem. It typically uses composition rather than inheritance. Unlike Adapter, it isn't primarily translating an incompatible interface; unlike Decorator, it isn't primarily adding behavior."

---

# 39. Current Learning Status

Facade concept:

- Derived from problem: **Done**
- Client complexity: **Understood**
- Simplified entry point: **Understood**
- Subsystem concept: **Understood through example**
- Composition: **Understood**
- Orchestration: **Understood**
- Coupling: **Understood**
- Java implementation: **Partially done / should be coded in the next practice session**
- Code review: **Pending**
- SOLID deep dive: **Pending practical review**
- Pattern comparisons: **Initial understanding**
- Production/Spring Boot: **Covered at introductory level**
- Final interview drills: **Pending**
- Proxy: **Parked**
- Flyweight: **Optional / can be skipped for current QuestionSheet-driven preparation**

Recommended next action:

> Code the Facade implementation from scratch without looking at this document, then defend each design decision as if in an SDE-2 interview.
