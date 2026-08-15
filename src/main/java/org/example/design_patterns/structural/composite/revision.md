# Composite Design Pattern — SDE-2 Java LLD Revision

## 1. Intent

Allow individual objects and groups of objects to be treated uniformly through a common abstraction, while allowing groups to contain other groups.

The key idea is:

```text
Common Component
   ├── Leaf
   └── Composite
          └── List<Component>
```

A Composite can contain Components, and because the Composite itself is a Component, recursive structures arise naturally.

---

## 2. Problem

Example: file system.

We have:

- `File` — an individual object with its own size.
- `Directory` — a group that contains files and possibly other directories.

Both should support:

```java
getSize()
```

For a file:

```text
File.getSize() → own size
```

For a directory:

```text
Directory.getSize()
    → sum child.getSize()
```

The client should be able to work with either one through a common abstraction without checking whether it received a `File` or `Directory`.

Example:

```text
Documents/
├── resume.pdf      10 KB
├── photo.jpg       20 KB
└── Work/
    └── report.pdf  30 KB

Documents.getSize() → 60 KB
```

---

## 3. Recognition Checklist

Composite is a strong candidate when most of these are true:

- [ ] There are individual objects and groups of objects.
- [ ] Both individual objects and groups support a meaningful common operation.
- [ ] A group can contain individual objects.
- [ ] A group can contain other groups.
- [ ] The client benefits from treating an individual and a group uniformly.
- [ ] The operation can naturally propagate through the hierarchy.
- [ ] The hierarchy is expected to grow or contain arbitrary nesting.
- [ ] The common abstraction is meaningful rather than artificial.

### Strong interview signal

Think:

> "An individual can do X, a group can also do X, and the group can contain other things that can do X."

That is a strong Composite signal.

### Composite can be overengineering when

- There is no meaningful common operation.
- The hierarchy is trivial.
- There is only one client and direct traversal is much clearer.
- Introducing the abstraction adds more complexity than it removes.
- The individual and group do not genuinely share behavior.

---

## 4. Derivation From the File System Problem

### Step 1: Individual and group

We start with:

```text
File
Directory
```

Both need:

```text
getSize()
```

### Step 2: Common abstraction

Introduce:

```java
interface StorageItem {
    int getSize();
}
```

### Step 3: Leaf

`File` implements `StorageItem` and returns its own size.

### Step 4: Composite

`Directory` implements `StorageItem` and contains:

```java
List<StorageItem>
```

### Step 5: Recursion

Because:

```java
Directory implements StorageItem
```

a directory can be stored inside:

```java
List<StorageItem>
```

Therefore:

```text
Directory
├── File
├── File
└── Directory
    ├── File
    └── Directory
        └── File
```

The tree is not the starting definition. It emerges naturally from:

```text
Composite contains Component
+
Composite is itself a Component
```

### Step 6: Uniform client

The client can use:

```java
StorageItem item = ...;
item.getSize();
```

without checking whether it is a `File` or `Directory`.

---

## 5. UML

```text
                 <<interface>>
                  StorageItem
                       |
                  getSize()
                       |
             +---------+---------+
             |                   |
           File              Directory
             |                   |
        getSize()        getSize()
                              |
                              |
                     List<StorageItem>
                              |
                     +--------+--------+
                     |                 |
                   File            Directory
```

---

## 6. Participants

### Component

Common abstraction used by clients.

Example:

```java
StorageItem
```

Contains operations meaningful to both Leaf and Composite.

### Leaf

Individual object that does not contain child Components.

Example:

```java
File
```

### Composite

Group object that contains Components and implements the same common abstraction.

Example:

```java
Directory
```

### Client

Works against the Component abstraction and ideally does not need to know whether it received a Leaf or Composite.

---

## 7. Leaf vs Composite

| Aspect | Leaf | Composite |
|---|---|---|
| Represents | Individual object | Group of objects |
| Has children | No | Yes |
| Implements Component | Yes | Yes |
| Common operation | Implements directly | Often delegates/aggregates |
| Example | File | Directory |

---

## 8. Component Abstraction

Our common abstraction:

```java
public interface StorageItem {
    int getSize();
}
```

### Why only `getSize()`?

Because both `File` and `Directory` can meaningfully provide it.

`add()` and `remove()` do not belong in the common interface because a `File` has no children.

This follows the principle:

> Put genuinely common capabilities in the common abstraction.

---

## 9. Complete Java Implementation

```java
package org.example.design_patterns.structural.composite;

public interface StorageItem {
    int getSize();
}
```

```java
package org.example.design_patterns.structural.composite;

public class File implements StorageItem {
    private final int size;

    public File(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}
```

```java
package org.example.design_patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

public class Directory implements StorageItem {

    private final List<StorageItem> items;

    public Directory() {
        this.items = new ArrayList<>();
    }

    public void add(StorageItem item) {
        items.add(item);
    }

    public void remove(StorageItem item) {
        items.remove(item);
    }

    @Override
    public int getSize() {
        int size = 0;

        for (StorageItem item : items) {
            size += item.getSize();
        }

        return size;
    }
}
```

### Immutable variant

If the hierarchy must not change after construction:

```java
public final class Directory implements StorageItem {

    private final List<StorageItem> items;

    public Directory(List<StorageItem> items) {
        this.items = List.copyOf(items);
    }

    @Override
    public int getSize() {
        int size = 0;

        for (StorageItem item : items) {
            size += item.getSize();
        }

        return size;
    }
}
```

Important:

```java
final
```

on the field prevents reassignment of the reference, not mutation of the collection.

Use:

```java
List.copyOf(...)
```

or another defensive immutable representation when true immutability is required.

---

## 10. Runtime / Recursive Flow

Example:

```text
Documents
├── File(10)
├── File(20)
└── Work
    └── File(30)
```

Calling:

```java
documents.getSize();
```

produces:

```text
Documents.getSize()
    |
    +-- File(10).getSize() → 10
    |
    +-- File(20).getSize() → 20
    |
    +-- Work.getSize()
            |
            +-- File(30).getSize() → 30

Total = 60
```

The Composite does not need:

```java
if (item instanceof File)
```

or:

```java
if (item instanceof Directory)
```

It simply does:

```java
item.getSize();
```

Polymorphism dispatches to the correct implementation.

---

## 11. Why Polymorphism Matters

Bad approach:

```java
for (StorageItem item : items) {
    if (item instanceof File) {
        ...
    } else if (item instanceof Directory) {
        ...
    }
}
```

Problems:

- Tight coupling to concrete classes.
- Directory must know every new StorageItem type.
- New types require modifying existing traversal logic.
- Recursive behavior becomes centralized in the wrong place.
- The common abstraction loses much of its value.

Better:

```java
for (StorageItem item : items) {
    size += item.getSize();
}
```

Each object owns its own implementation.

---

## 12. SOLID Principles

### Single Responsibility Principle

`File`:

- owns its own size behavior.

`Directory`:

- owns child management.
- owns aggregation of child sizes.

A separate application service can own higher-level workflows if needed.

### Open/Closed Principle

`Directory` depends on:

```java
StorageItem
```

rather than concrete classes.

Adding:

```java
ArchiveFile implements StorageItem
```

does not require changing:

```java
Directory.getSize()
```

because it already calls:

```java
item.getSize()
```

### Liskov Substitution Principle

A `File` or `Directory` can be used wherever `StorageItem` is expected, provided the `StorageItem` contract remains valid.

Potential LSP problems arise if the common interface promises operations that some implementations cannot meaningfully support.

For example:

```java
interface StorageItem {
    int getSize();
    void add(StorageItem item);
}
```

would be problematic because `File` cannot meaningfully add children.

### Interface Segregation Principle

Do not force `File` to implement:

```java
add()
remove()
```

when it does not need them.

A capability-specific interface can be used when appropriate:

```java
interface Compressible {
    void compress();
}
```

### Dependency Inversion Principle

Higher-level client code can depend on:

```java
StorageItem
```

rather than concrete `File` or `Directory` types.

---

## 13. Composition vs Inheritance

Inheritance models:

```text
is-a
```

Composition models:

```text
has-a
```

Wrong:

```java
class Directory extends File
```

because:

```text
Directory is not a File
```

Correct:

```java
class Directory implements StorageItem {
    private List<StorageItem> items;
}
```

because:

```text
Directory has StorageItems
```

The important Composite relationship is:

```text
Composite has Components
```

And because:

```text
Composite is a Component
```

a Composite can contain another Composite.

---

## 14. Why the Recursive Structure Appears

Do not memorize:

> Composite means tree.

Derive it:

```text
Directory implements StorageItem
```

therefore:

```text
Directory is a StorageItem
```

and:

```java
List<StorageItem>
```

can contain a Directory.

Therefore:

```text
Directory
  → contains StorageItem
  → StorageItem can be Directory
  → Directory can contain Directory
  → recursion
```

The hierarchy naturally emerges.

---

## 15. Who Owns Child Management?

`Directory` should own:

```java
add(StorageItem item)
remove(StorageItem item)
```

rather than exposing its internal list.

Prefer:

```java
directory.add(file);
```

over:

```java
directory.getItems().add(file);
```

This preserves encapsulation and allows `Directory` to enforce invariants.

---

## 16. Cycle Problem

Mutable Composite structures can accidentally create cycles.

Direct cycle:

```text
A
└── A
```

Indirect cycle:

```text
A
└── B
    └── C
        └── A
```

Then:

```java
A.getSize();
```

could recursively call itself forever and eventually produce a `StackOverflowError`.

### Where should cycle validation live?

Normally at the point where the relationship is created:

```java
directory.add(item);
```

because `Directory` owns the child relationship.

A production implementation may need to walk the candidate subtree or otherwise maintain parent/ancestor information to prevent indirect cycles.

---

## 17. Immutability

Mutable version:

```java
directory.add(file);
directory.remove(file);
```

Immutable version:

```java
new Directory(List.of(file1, file2));
```

Use:

```java
List.copyOf(items)
```

to prevent external mutation.

Removing `add/remove` and making the class `final` can help, but `final` alone does not make the object immutable.

---

## 18. Testing

Minimum functional tests:

### Test 1: Leaf

```text
File(10).getSize() == 10
```

### Test 2: Simple Composite

```text
Directory
├── File(10)
└── File(20)

Expected = 30
```

### Test 3: Nested Composite

```text
Directory
├── File(10)
└── Directory
    └── File(20)

Expected = 30
```

The nested test is especially important because it proves recursive behavior.

### Additional tests

- Empty directory.
- Multiple levels of nesting.
- Removing a child.
- Attempting to create a cycle.
- Very deep hierarchy if stack depth is a production concern.
- Large number of children.

---

# 19. Pattern Comparisons

## Composite vs Strategy

### Strategy

Choose or inject an interchangeable algorithm.

```text
PaymentService
    |
    +-- UPIPayment
    +-- CardPayment
    +-- WalletPayment
```

### Composite

Represent an individual/group hierarchy.

```text
Directory
├── File
├── File
└── Directory
```

Mental model:

```text
Strategy  → Which algorithm?
Composite  → What objects make up this hierarchy?
```

They can coexist.

Example:

```text
Composite
    → File/Directory hierarchy

Strategy
    → Normal/Compressed size calculation
```

But do not introduce Strategy merely because it can be technically combined with Composite. There should be a genuine algorithm-variation problem.

---

## Composite vs Decorator

### Decorator

Wrap one object to add or modify behavior.

```text
Notification
    ↓
LoggingDecorator
    ↓
RetryDecorator
    ↓
MetricsDecorator
```

### Composite

Group multiple Components and treat the group as one Component.

```text
NotificationGroup
├── EmailNotification
├── SMSNotification
└── NotificationGroup
```

Mental model:

```text
Decorator → Add behavior around one object.
Composite → Represent a group as a Component.
```

---

## Composite vs Adapter

### Adapter

Translate one interface into another.

Example:

```text
LegacyFile
calculateBytes()
       ↓
   Adapter
       ↓
StorageItem
getSize()
```

The problem is interface incompatibility.

### Composite

The interfaces already align around the common Component abstraction.

Mental model:

```text
Adapter   → Make incompatible interfaces work together.
Composite → Make individual + group objects work uniformly.
```

---

## Composite vs Facade

### Facade

Simplify access to a subsystem.

```text
Client
  ↓
FileSystemFacade
  ↓
PermissionService
StorageService
MetadataService
```

### Composite

Represent a hierarchy.

```text
StorageItem
├── File
└── Directory
```

Mental model:

```text
Facade    → Simplify subsystem access.
Composite → Model a hierarchy uniformly.
```

---

## Composite vs Chain of Responsibility

### Chain of Responsibility

Pass a request through a sequence of handlers.

```text
Request
   ↓
Manager
   ↓
SeniorManager
   ↓
Director
```

The request moves through a processing path.

### Composite

Operate over a hierarchy of objects.

```text
Directory
├── File
├── File
└── Directory
```

Mental model:

```text
Composite
→ hierarchy of objects

Chain of Responsibility
→ process a request through handlers
```

Important:

Chain of Responsibility does not necessarily stop after one handler. Multiple handlers may participate, for example when two approval levels are required.

The distinction is the **purpose and relationship**, not simply whether one or multiple handlers execute.

---

# 20. Composite vs Inheritance

Do not confuse:

```text
is-a
```

with:

```text
has-a
```

Composite uses a common abstraction, which may be an interface or superclass, but the key structural relationship is:

```text
Composite has Components
```

It is not:

```text
Composite extends Leaf
```

---

# 21. Composite vs Plain Traversal

You do not automatically need Composite whenever a tree exists.

Plain traversal may be better when:

- The system is tiny.
- There are very few concrete types.
- Only one client performs traversal.
- There is no meaningful common operation.
- The hierarchy is not expected to evolve.
- A simple explicit traversal is clearer.

Composite becomes valuable when the common abstraction genuinely simplifies clients and encapsulates recursive behavior.

---

# 22. Production Use Cases

Common examples:

### File systems

```text
Directory
├── File
└── Directory
```

Operations:

- Size.
- Permissions.
- Search.
- Delete.
- Storage usage.

### Organization hierarchy

```text
Department
├── Employee
└── Department
```

Operations:

- Total salary.
- Headcount.
- Permissions.
- Resource allocation.

### Product catalog

```text
Category
├── Product
└── Category
```

Operations:

- Total price.
- Inventory.
- Search.
- Product count.

### Menus

```text
Menu
├── MenuItem
└── Menu
```

Operations:

- Render.
- Price calculation.
- Search.

### Drawing editors

```text
ShapeGroup
├── Circle
├── Rectangle
└── ShapeGroup
```

Operations:

- Render.
- Move.
- Resize.
- Calculate bounds.

---

# 23. Spring Boot Relevance

Spring Boot does not provide Composite automatically.

Composite is primarily a **domain/object design technique**.

Typical structure:

```text
Spring-managed application services
        |
        ↓
Domain objects
        |
        ├── Employee
        ├── Team
        └── Organization
```

For example:

```java
@Service
public class OrganizationCostService {
    public BigDecimal calculateCost(OrganizationNode node) {
        return node.getCost();
    }
}
```

The domain objects can remain ordinary Java objects.

Do not make every `Employee` or `Team` a Spring `@Component` merely because Spring is being used.

`@Component` is generally for objects whose lifecycle/dependencies should be managed by the Spring container, such as:

- Services.
- Repositories.
- Factories.
- Application-level components.

Domain entities/value objects are normally created from application data rather than registered as thousands of Spring beans.

---

# 24. Production Concerns

### Mutability

Mutable child structures require careful invariant enforcement.

### Cycle prevention

If the hierarchy is intended to be a tree, prevent:

```text
A → B → C → A
```

### Thread safety

If multiple threads mutate or traverse the structure concurrently, decide whether:

- The structure is immutable.
- Access is synchronized.
- A concurrent data structure is appropriate.
- Reads/writes are coordinated externally.

### Deep recursion

A very deep hierarchy can cause stack problems with recursive implementations.

An iterative traversal can be considered if depth is unbounded.

### Performance

For:

```text
Directory.getSize()
```

a recursive traversal is roughly:

```text
O(N)
```

where `N` is the number of nodes visited.

If size is cached, reads can be faster but mutations become more complex because ancestor totals must be maintained.

---

# 25. Caching Tradeoff

Two approaches:

### Calculate on read

```text
getSize()
    → traverse descendants
```

Advantages:

- Simple.
- No stale cached values.
- Easy mutation model.

Disadvantages:

- Repeated reads can repeatedly traverse the tree.

### Maintain cached totals

```text
add(file)
    → update parent
    → update ancestors
```

Advantages:

- Fast reads.

Disadvantages:

- More complex mutations.
- Need to maintain ancestor state.
- More opportunities for inconsistency.

For an interview, start with the simpler traversal unless requirements clearly demand cached reads.

---

# 26. Common Mistakes

### Mistake 1: Putting `add/remove` in Component

```java
interface Component {
    void add(Component c);
    void remove(Component c);
}
```

This forces Leaves to support operations they don't need.

### Mistake 2: Using `instanceof`

```java
if (item instanceof File) ...
if (item instanceof Directory) ...
```

This defeats much of the polymorphic abstraction.

### Mistake 3: Making Directory extend File

A Directory is not a File.

### Mistake 4: Confusing Composite with Decorator

Decorator adds/wraps behavior.

Composite represents groups.

### Mistake 5: Assuming every hierarchy needs Composite

A tree alone is not enough.

### Mistake 6: Adding every possible operation to Component

This can create a bloated interface and ISP/LSP problems.

### Mistake 7: Introducing Strategy unnecessarily

Strategy should solve a genuine interchangeable-algorithm problem.

### Mistake 8: Exposing internal child collections

Prefer:

```java
directory.add(item);
```

over exposing:

```java
directory.getItems().add(item);
```

### Mistake 9: Ignoring cycles in a mutable hierarchy

If the structure is intended to be a tree, enforce that invariant.

---

# 27. Interview Questions and Reasoning

## Q1. How would you design File and Directory?

Derive:

```text
StorageItem
├── File
└── Directory
       └── List<StorageItem>
```

Both support `getSize()`.

---

## Q2. Calculate size on read or maintain it on add?

Two valid approaches.

Read-time traversal:

- Simpler.
- More expensive repeated reads.

Cached value:

- Faster reads.
- More complex mutations.

---

## Q3. What common abstraction would you introduce?

A common abstraction such as:

```java
StorageItem
```

with:

```java
getSize()
```

---

## Q4. What should children contain?

```java
List<StorageItem>
```

because both File and Directory implement StorageItem.

---

## Q5. Should Component expose add/remove?

Usually no.

Only the Composite needs child-management operations.

This supports ISP and keeps the common interface focused.

---

## Q6. How does recursion arise?

Composite implements Component and contains Components.

Therefore a Composite can contain another Composite.

---

## Q7. Should client check File vs Directory?

No.

Prefer:

```java
item.getSize();
```

over concrete-type checks.

---

## Q8. Why is polymorphism useful?

Each object owns its own behavior.

```text
File.getSize()
Directory.getSize()
```

The client does not need concrete-type knowledge.

---

## Q9. Why can Directory contain Directory?

Because:

```java
Directory implements StorageItem
```

and:

```java
List<StorageItem>
```

can contain it.

---

## Q10. Who owns child management?

The Composite.

It should enforce child-related invariants.

---

## Q11. Why not put add/remove in Component?

Because Leaf objects do not need them.

Otherwise we violate interface segregation and potentially create substitution problems.

---

## Q12. Why not Directory extend File?

Because:

```text
Directory is not a File.
```

Use composition for the containment relationship.

---

## Q13. Why is this composition?

Because:

```text
Directory has StorageItems.
```

It owns a collection of child Components.

---

## Q14. What is the OCP benefit?

New `StorageItem` implementations can be added without changing Directory's traversal:

```java
for (StorageItem item : items) {
    size += item.getSize();
}
```

---

## Q15. Can Component keep growing operations?

Be careful.

Only add operations that are meaningful to both Leaf and Composite.

Capability-specific behavior can use separate interfaces.

---

## Q16. When is Composite overengineering?

When the common abstraction provides little value, the hierarchy is tiny, or direct traversal is clearer.

---

## Q17. Why isn't Composite Decorator?

Decorator wraps one object to add/modify behavior.

Composite groups multiple Components and treats the group as a Component.

---

## Q18. Why isn't Composite Chain of Responsibility?

Composite:

```text
hierarchy of objects
```

Chain of Responsibility:

```text
request processing through handlers
```

---

## Q19. Why isn't Composite Strategy?

Strategy varies the algorithm.

Composite represents the object hierarchy.

---

## Q20. Why isn't Composite Adapter?

Adapter translates an incompatible interface into the interface the client expects.

Composite creates a common abstraction for individual/group objects.

---

## Q21. Why isn't Composite Facade?

Facade simplifies access to multiple subsystem services.

Composite models a hierarchy of objects.

---

## Q22. How would you test Composite?

Minimum:

1. Leaf.
2. Simple Composite.
3. Nested Composite.

Also test cycles, empty structures, mutation, and deep nesting as appropriate.

---

## Q23. How would you make Composite immutable?

- Remove `add/remove`.
- Supply children through construction.
- Defensive-copy the list.
- Use `List.copyOf(...)`.
- Consider making the class `final`.

---

## Q24. How do you prevent cycles?

Validate in the child-management operation, typically:

```java
directory.add(item);
```

because the Composite owns the relationship.

---

## Q25. How does Composite work with Strategy?

Composite handles:

```text
hierarchy
```

Strategy handles:

```text
interchangeable algorithm
```

They can coexist when there are genuinely two independent problems.

---

## Q26. How does Composite work with Decorator?

Composite:

```text
group objects
```

Decorator:

```text
wrap object and add behavior
```

---

## Q27. How does Composite work with Adapter?

Adapter can adapt a third-party component into the Component abstraction, after which it could potentially participate in the Composite hierarchy.

Example:

```text
LegacyFile
    ↓
Adapter
    ↓
StorageItem
    ↓
Directory
```

---

# 28. Quick Recognition Cheat Sheet

When you see:

```text
individual + group
        ↓
same operation
        ↓
group contains individual/group
        ↓
client should treat both uniformly
```

Think:

> **Composite**

### Mental picture

```text
Component
├── Leaf
└── Composite
      ├── Component
      ├── Component
      └── Composite
```

### One-line interview explanation

> **"Composite lets us represent individual objects and groups through a common abstraction, allowing the group to contain Components, including other Composites, so clients can operate on the hierarchy uniformly."**

---

# 29. Pattern Recognition Summary

```text
Strategy
→ Choose an interchangeable algorithm.

Decorator
→ Add or modify behavior around an object.

Adapter
→ Convert one interface into another.

Facade
→ Simplify access to a subsystem.

Chain of Responsibility
→ Pass a request through handlers for processing.

Composite
→ Treat individual objects and groups uniformly through a common abstraction.
```

---

# 30. Final Interview Answer Template

If asked:

> "What is Composite and when would you use it?"

Answer:

> "I'd use Composite when I have individual objects and groups of those objects that expose a meaningful common operation, and the groups can contain the same abstraction, including other groups. I introduce a common Component interface, implement individual objects as Leaves, and implement groups as Composites containing Components. This lets clients work against the abstraction without knowing whether they're dealing with an individual or a group, while recursive operations can naturally propagate through the hierarchy."

---

# 31. Core Example to Remember

```text
                 StorageItem
                     |
             +-------+-------+
             |               |
           File          Directory
                           |
                    List<StorageItem>
                           |
                    +------+------+
                    |             |
                  File        Directory
```

The most important code is:

```java
for (StorageItem item : items) {
    size += item.getSize();
}
```

The most important design insight is:

```text
Directory IS-A StorageItem
Directory HAS-A StorageItems
```

That combination creates the recursive Composite structure.

---

# 32. Personal Learning Takeaways From This Session

The design was derived rather than memorized:

1. Started with File and Directory.
2. Identified `getSize()` as common.
3. Created `StorageItem`.
4. Made File a Leaf.
5. Made Directory contain `StorageItem`.
6. Realized Directory itself is a StorageItem.
7. Derived recursive composition.
8. Removed concrete-type checks from the client.
9. Kept `add/remove` out of the common interface.
10. Connected the design to ISP, LSP, OCP, and encapsulation.
11. Distinguished composition from inheritance.
12. Compared Composite with Strategy, Decorator, Adapter, Facade, and Chain of Responsibility.
13. Considered cycle prevention.
14. Considered immutability.
15. Considered caching vs traversal.
16. Considered basic Spring Boot/domain-object relevance.

The most important interview habit is:

> **Don't start by saying "I will use Composite." Start by identifying individual + group + common operation + recursive containment + need for uniform treatment.**
