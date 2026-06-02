# OOP in Java — SDE 2 Revision Notes

> Structured for revision. Each concept has a definition, when/where to use it, and interview Q&A.

---

## Table of Contents

1. [Encapsulation](#1-encapsulation)
2. [Inheritance](#2-inheritance)
3. [Polymorphism](#3-polymorphism)
4. [Abstraction](#4-abstraction)
5. [Interface vs Abstract Class](#5-interface-vs-abstract-class)
6. [SOLID Principles](#6-solid-principles)
7. [Key Object Methods](#7-key-object-methods-equals-hashcode-tostring)
8. [Common Modifiers](#8-common-modifiers-final-static-abstract)
9. [Composition vs Inheritance](#9-composition-vs-inheritance)
10. [Covariant Return Types](#10-covariant-return-types)

---

## 1. Encapsulation

### Definition
Bundling data (fields) and behaviour (methods) together, and restricting direct access to the data. Fields are kept `private`; access is controlled through public getters/setters.

### When to Use
- Always. Every class should encapsulate its internal state by default.
- Especially when you need to enforce invariants (e.g., balance must never go negative).
- When you want the freedom to change internal representation without breaking callers.

### Where to Use
- Domain model classes (`User`, `Order`, `BankAccount`)
- Any class where the internal state has constraints or business rules

### Code Example

```java
public class BankAccount {
    private double balance;  // hidden from outside

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalStateException("Insufficient funds");
        balance -= amount;
    }

    public double getBalance() {
        return balance;  // read-only access
    }
}
```

### Interview Questions & Answers

**Q: What is encapsulation and why does it matter?**
> It's the practice of hiding internal state and exposing only what's necessary through a controlled interface. It matters because it lets you enforce invariants, change internal implementation freely, and reduce unintended coupling between classes.

**Q: Can encapsulation be broken even with private fields?**
> Yes. If you return a mutable object (like a `List` or `Date`) from a getter, callers can mutate it directly. Fix by returning a copy (`Collections.unmodifiableList(...)`) or a defensive copy.

```java
// BAD — returns internal reference
public List<String> getItems() { return items; }

// GOOD — returns unmodifiable view
public List<String> getItems() { return Collections.unmodifiableList(items); }
```

**Q: Is using getters/setters always the right approach?**
> Not necessarily. Setters for every field is "anemic encapsulation" — it gives access to everything. Prefer behaviour-rich methods (`deposit()`, `withdraw()`) over generic setters (`setBalance()`).

---

## 2. Inheritance

### Definition
A mechanism where a subclass acquires the properties and behaviours of a parent class using `extends`. Models an **is-a** relationship.

### When to Use
- When a genuine is-a relationship exists (`Dog` is an `Animal`, `Manager` is an `Employee`).
- When subclasses share significant behaviour and differ only in specifics.
- When you want to leverage runtime polymorphism.

### When NOT to Use
- When the relationship is has-a (use composition instead).
- When you're inheriting just to reuse a few utility methods — that's abuse of inheritance.
- When the hierarchy would be more than 2–3 levels deep.

### Code Example

```java
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void speak() {
        System.out.println(name + " makes a sound.");
    }
}

public class Dog extends Animal {

    public Dog(String name) {
        super(name);  // must be the first statement
    }

    @Override
    public void speak() {
        System.out.println(name + " says: Woof!");
    }
}
```

### Interview Questions & Answers

**Q: Are constructors inherited in Java?**
> No. Constructors are not inherited. The subclass must define its own constructor and call `super()` as the first statement if the parent has no no-arg constructor.

**Q: What is the difference between method hiding and method overriding?**
> Overriding applies to instance methods and is resolved at runtime (dynamic dispatch). Hiding applies to static methods and is resolved at compile time based on the reference type — not the actual object.

```java
Animal a = new Dog("Rex");
a.speak();        // calls Dog.speak() — overriding, runtime dispatch
Animal.staticMethod();  // calls Animal's static method — hiding, compile-time
```

**Q: What happens if the parent class has no no-arg constructor and the subclass doesn't call super()?**
> Compile error. Java inserts an implicit `super()` call if none is provided, and if the parent has no no-arg constructor, this fails to compile.

**Q: What is the diamond problem and does Java have it?**
> The diamond problem occurs when a class inherits from two classes that both inherit from a common ancestor, causing ambiguity. Java avoids this by allowing only single class inheritance. The problem can still arise with interfaces having `default` methods — Java resolves it by requiring the implementing class to explicitly override the conflicting method.

---

## 3. Polymorphism

### Definition
The ability of a single interface to represent different underlying forms. In Java, this takes two forms:
- **Compile-time (static):** Method overloading — same name, different parameters.
- **Runtime (dynamic):** Method overriding — subclass provides specific implementation, resolved via the JVM's vtable.

### When to Use
- When you want to write code that works against a base type but behaves differently based on the actual object.
- In collections: `List<Animal> animals` holding `Dog`, `Cat`, `Bird`.
- In strategy/command patterns where behaviour varies.

### Code Example

```java
// Runtime polymorphism
Animal a = new Dog("Rex");
a.speak();  // → "Rex says: Woof!" — resolved at runtime, not compile time

// Compile-time polymorphism (overloading)
public class Printer {
    void print(int x)    { System.out.println("int: " + x); }
    void print(String s) { System.out.println("String: " + s); }
    void print(int x, int y) { System.out.println("two ints: " + x + ", " + y); }
}
```

### Interview Questions & Answers

**Q: What's the difference between overloading and overriding?**

| | Overloading | Overriding |
|---|---|---|
| Resolved at | Compile time | Runtime |
| Method signature | Must differ (params) | Must be identical |
| Return type | Can differ | Must be same or covariant |
| Access modifier | Any | Can't be more restrictive |
| `static` methods | Allowed | Not overridden (hidden) |

**Q: Can you override a private or static method?**
> No. Private methods are not visible to subclasses so cannot be overridden. Static methods belong to the class, not instances — a subclass can define one with the same signature (method hiding) but this is not polymorphism.

**Q: What is dynamic dispatch?**
> When a method is called on a reference, the JVM looks at the actual runtime type of the object (not the reference type) to determine which implementation to invoke. This is what makes `Animal a = new Dog(); a.speak()` call `Dog.speak()`.

**Q: Can constructors be polymorphic?**
> No. Constructors are not inherited and cannot be overridden, so they don't participate in dynamic dispatch. However, be careful — calling an overridden method from a constructor can cause bugs because the subclass may not be fully initialised yet.

---

## 4. Abstraction

### Definition
Hiding complex implementation details and exposing only the essential features. Achieved in Java via `abstract` classes and `interfaces`.

### When to Use
- When you want to define a contract that multiple classes must fulfil.
- When you want to allow callers to work with a concept without knowing the implementation.
- When building frameworks or libraries where the internals can change.

### Code Example

```java
// Abstract class — partial abstraction
public abstract class Shape {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();  // subclass must implement

    public void describe() {        // shared concrete behaviour
        System.out.println("Color: " + color + ", Area: " + area());
    }
}

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

### Interview Questions & Answers

**Q: What is the difference between abstraction and encapsulation?**
> Encapsulation is about hiding the *data* and controlling access to it. Abstraction is about hiding the *complexity* of implementation and showing only relevant behaviour. Encapsulation is the mechanism; abstraction is the design goal. A class can encapsulate its fields while also abstracting its behaviour through an interface.

**Q: Can an abstract class have a constructor?**
> Yes. An abstract class can (and often should) have a constructor. It's called via `super()` from subclass constructors. It cannot be instantiated directly, but the constructor is used to initialise common state inherited by all subclasses.

**Q: Can an abstract class have all concrete methods?**
> Yes. A class can be declared `abstract` without having any abstract methods. This prevents instantiation of a class that isn't meant to be used directly, even if all methods are implemented.

---

## 5. Interface vs Abstract Class

### Definition
Both enable abstraction. Interfaces define pure contracts; abstract classes provide partial implementations with shared state.

### Decision Guide

| | Interface | Abstract Class |
|---|---|---|
| Multiple inheritance | ✅ A class can implement many | ❌ Single extend only |
| Instance variables | ❌ Only constants (`public static final`) | ✅ Yes |
| Constructor | ❌ No | ✅ Yes |
| Method types | abstract, default, static, private (Java 9+) | abstract + concrete |
| Use when | Defining a capability/contract | Sharing state or partial implementation |

### When to Use Interface
- Defining capabilities that unrelated classes can share: `Runnable`, `Comparable`, `Serializable`, `Closeable`.
- You want multiple inheritance of type.
- You're defining an API that others will implement.

### When to Use Abstract Class
- Base classes in a type hierarchy with shared state (`Vehicle` with `make`, `model`, `year`).
- You want to provide a partial implementation and force subclasses to complete it.
- Template Method pattern: define the skeleton of an algorithm in the base class.

### Code Example

```java
// Interface — capability
public interface Flyable {
    void fly();
    default void land() {
        System.out.println("Landing...");  // default since Java 8
    }
}

// Abstract class — shared base
public abstract class Vehicle {
    protected String make;
    protected int year;

    public Vehicle(String make, int year) {
        this.make = make;
        this.year = year;
    }

    public abstract void startEngine();

    public String getInfo() {
        return make + " (" + year + ")";
    }
}

// A class can extend one and implement many
public class FlyingCar extends Vehicle implements Flyable, Comparable<FlyingCar> {
    // ...
}
```

### Interview Questions & Answers

**Q: Java 8 added default methods to interfaces. Does that make abstract classes obsolete?**
> Not entirely. Default methods allow shared behaviour in interfaces, but interfaces still can't hold instance state. If your base type needs fields shared across all implementations, you still need an abstract class. Abstract classes also allow constructors, which interfaces don't.

**Q: What happens when a class implements two interfaces with the same default method?**
> Compile error — the class must explicitly override the conflicting method to resolve the ambiguity.

```java
interface A { default void greet() { System.out.println("A"); } }
interface B { default void greet() { System.out.println("B"); } }

class C implements A, B {
    @Override
    public void greet() { A.super.greet(); }  // must resolve explicitly
}
```

**Q: Can an interface extend another interface?**
> Yes, and it can extend multiple interfaces. This is one way to build type hierarchies without classes.

---

## 6. SOLID Principles

### Definition
Five design principles that make OOP code more maintainable, extensible, and testable.

---

### S — Single Responsibility Principle (SRP)

**Definition:** A class should have only one reason to change.

**When to apply:** When a class is doing too many things — handling business logic, formatting output, and persisting data all in one place.

```java
// BAD — UserService does everything
class UserService {
    void createUser(User u) { /* business logic */ }
    void sendWelcomeEmail(User u) { /* email logic */ }
    void saveToDatabase(User u) { /* DB logic */ }
}

// GOOD — each class has one job
class UserService      { void createUser(User u) { ... } }
class EmailService     { void sendWelcome(User u) { ... } }
class UserRepository   { void save(User u) { ... } }
```

---

### O — Open/Closed Principle (OCP)

**Definition:** Classes should be open for extension but closed for modification.

**When to apply:** When adding new behaviour requires modifying existing, tested code.

```java
// BAD — adding a new shape requires modifying AreaCalculator
class AreaCalculator {
    double calculate(Object shape) {
        if (shape instanceof Circle c) return Math.PI * c.r * c.r;
        if (shape instanceof Rectangle r) return r.w * r.h;
        // must edit this method for every new shape
    }
}

// GOOD — extend without modifying
interface Shape { double area(); }
class Circle    implements Shape { public double area() { return Math.PI * r * r; } }
class Triangle  implements Shape { public double area() { return 0.5 * b * h; } }

class AreaCalculator {
    double calculate(Shape s) { return s.area(); }  // never changes
}
```

---

### L — Liskov Substitution Principle (LSP)

**Definition:** A subclass should be substitutable for its parent without breaking the program.

**Classic violation:**

```java
class Rectangle {
    int width, height;
    void setWidth(int w)  { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}

class Square extends Rectangle {
    @Override
    void setWidth(int w)  { this.width = this.height = w; }  // breaks LSP
    @Override
    void setHeight(int h) { this.width = this.height = h; }  // callers of Rectangle are surprised
}

// Code that assumes Rectangle behaviour breaks when given a Square:
Rectangle r = new Square();
r.setWidth(5);
r.setHeight(4);
System.out.println(r.area());  // expected 20, got 16
```

---

### I — Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to implement interfaces they don't use. Prefer small, focused interfaces.

```java
// BAD — fat interface
interface Worker {
    void work();
    void eat();
    void sleep();
}

// GOOD — segregated
interface Workable  { void work(); }
interface Feedable  { void eat(); }

class Robot implements Workable {
    public void work() { ... }
    // doesn't need to implement eat() or sleep()
}
```

---

### D — Dependency Inversion Principle (DIP)

**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

```java
// BAD — high-level class depends on concrete low-level class
class OrderService {
    private MySQLDatabase db = new MySQLDatabase();  // tightly coupled
}

// GOOD — depend on abstraction
interface Database { void save(Order o); }

class OrderService {
    private final Database db;
    public OrderService(Database db) { this.db = db; }  // injected
}

class MySQLDatabase implements Database { ... }
class MongoDatabase implements Database { ... }
```

### Interview Questions & Answers

**Q: Which SOLID principle do you find most commonly violated in real codebases?**
> SRP — classes tend to accumulate responsibilities over time as features are added. A `UserService` that started doing just user management ends up handling emails, permissions, and audit logging.

**Q: LSP in practice — when is inheritance actually wrong?**
> When the subclass can't honour the contract of the parent. The Square/Rectangle example is canonical. In practice, watch for subclasses that throw `UnsupportedOperationException` on inherited methods — that's a LSP violation.

---

## 7. Key Object Methods: equals, hashCode, toString

### Definition
Every Java class inherits these from `Object`. Override them when your objects need meaningful equality or need to work correctly in collections.

### Contract Rules

- **equals:** Reflexive, symmetric, transitive, consistent. `x.equals(null)` must return `false`.
- **hashCode:** If `a.equals(b)`, then `a.hashCode() == b.hashCode()`. The reverse doesn't need to hold.
- **Critical:** Always override both `equals` and `hashCode` together. Breaking one breaks `HashMap`, `HashSet`, etc.

### When to Override
- Domain objects where equality is based on fields, not identity (`User`, `Product`, `Order`).
- When objects will be used in `HashMap` or `HashSet`.
- `toString` whenever you want readable logs/debugging output.

### Code Example

```java
public class User {
    private final int id;
    private final String email;

    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return id == u.id && Objects.equals(email, u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);  // same fields as equals
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "'}";
    }
}
```

### Interview Questions & Answers

**Q: What happens if you override equals but not hashCode?**
> Two objects that are `equals` may have different hash codes. They'll be placed in different buckets in a `HashMap` or `HashSet`, so lookups will fail even when equality holds. The collection appears to contain duplicates.

**Q: Can two objects with different hashCodes be equal?**
> No — that would violate the contract. If `a.equals(b)` is true, their hash codes must be equal. However, two objects with the same hash code don't have to be equal (that's a hash collision, which is allowed).

**Q: What does `==` check vs `.equals()`?**
> `==` checks reference identity (same memory address). `.equals()` checks logical equality as defined by the method. For `String`: `"hello" == "hello"` may be true due to string interning, but never rely on that — always use `.equals()`.

---

## 8. Common Modifiers: final, static, abstract

### final

| Applied to | Effect |
|---|---|
| Class | Cannot be extended (`String`, `Integer`) |
| Method | Cannot be overridden |
| Variable | Reference cannot be reassigned (object itself can still mutate) |

```java
final List<String> list = new ArrayList<>();
list.add("hello");   // OK — mutating the object
list = new ArrayList<>();  // Compile error — reassigning the reference
```

**Interview Q: Does final make an object immutable?**
> No. `final` prevents the reference from being reassigned, but the object it points to can still be mutated. True immutability requires all fields to be `private final` and no mutating methods, plus defensive copies for mutable field types.

---

### static

| Applied to | Effect |
|---|---|
| Field | Shared across all instances of the class |
| Method | Belongs to class, not instance — no `this` access |
| Inner class | Doesn't hold reference to outer class instance |
| Block | Runs once when class is loaded |

```java
class Counter {
    private static int count = 0;  // shared
    public Counter() { count++; }
    public static int getCount() { return count; }
}
```

**Interview Q: Can a static method be overridden?**
> No — static methods are resolved at compile time based on the reference type. What looks like overriding is actually method hiding. It won't participate in runtime polymorphism.

---

### abstract

- Cannot instantiate an abstract class directly.
- A class with at least one abstract method must be declared abstract.
- A class can be `abstract` with zero abstract methods (to prevent instantiation).
- Subclasses must implement all abstract methods or also be declared `abstract`.

---

## 9. Composition vs Inheritance

### Definition
Composition means a class contains an instance of another class (has-a). Inheritance means a class extends another (is-a).

### Prefer Composition When
- The relationship is "has-a" not "is-a".
- You want to change behaviour at runtime.
- The hierarchy would grow deep and brittle.
- You need to combine behaviours from multiple sources.

### Code Example

```java
// Inheritance approach — brittle
class FlyingDuck extends Duck {
    void fly() { ... }
}

// Composition approach — flexible
interface FlyBehaviour   { void fly(); }
interface QuackBehaviour { void quack(); }

class FlyWithWings implements FlyBehaviour {
    public void fly() { System.out.println("Flying with wings"); }
}

class Duck {
    private FlyBehaviour flyBehaviour;
    private QuackBehaviour quackBehaviour;

    public Duck(FlyBehaviour fb, QuackBehaviour qb) {
        this.flyBehaviour = fb;
        this.quackBehaviour = qb;
    }

    // Can swap behaviour at runtime
    public void setFlyBehaviour(FlyBehaviour fb) { this.flyBehaviour = fb; }

    public void performFly()   { flyBehaviour.fly(); }
    public void performQuack() { quackBehaviour.quack(); }
}
```

### Interview Questions & Answers

**Q: Why is "favour composition over inheritance" a principle?**
> Inheritance creates tight coupling — changes in the parent ripple to all subclasses. Composition is more flexible: you can change behaviour at runtime, mock dependencies in tests, and combine behaviours without deep hierarchies. Inheritance also exposes internal details of the parent class to subclasses (white-box reuse).

**Q: Is inheritance always bad?**
> No. Inheritance is appropriate when there's a genuine is-a relationship and the Liskov Substitution Principle holds. It's the right tool for modelling type hierarchies and enabling runtime polymorphism. The problem is *misuse* — inheriting for code reuse alone.

---

## 10. Covariant Return Types

### Definition
Since Java 5, an overriding method can return a more specific (narrower) type than the parent's return type.

### When It's Useful
- Builder patterns, factory methods, fluent APIs.
- When callers of the subclass shouldn't need to cast.

### Code Example

```java
class Animal {
    public Animal create() {
        return new Animal();
    }
}

class Dog extends Animal {
    @Override
    public Dog create() {  // valid — Dog is a subtype of Animal
        return new Dog();
    }
}

Dog d = new Dog().create();  // no cast needed
```

### Interview Questions & Answers

**Q: Is covariant return type a form of overriding?**
> Yes. It's a valid override — the method signature matches (same name, same parameters) and the return type is a subtype of the parent's return type. The `@Override` annotation works here.

**Q: What about parameter types — can they be narrowed too?**
> No. Narrowing parameter types creates an overloaded method, not an override. To override, the parameter types must be exactly the same. This is the opposite of covariance — it's called contravariance, and Java doesn't support it for overriding.

---

## Quick Reference Card

| Concept | Core idea | Key rule |
|---|---|---|
| Encapsulation | Hide data, expose behaviour | Return defensive copies of mutable fields |
| Inheritance | is-a, code reuse | Constructors not inherited; `super()` must be first |
| Polymorphism | One interface, many forms | Static/private methods are NOT overridden |
| Abstraction | Hide complexity | Interface = contract; Abstract class = partial impl |
| equals/hashCode | Logical equality | Always override together; same fields in both |
| final | Prevent change | `final` ref ≠ immutable object |
| static | Class-level | Cannot access instance state; not polymorphic |
| Composition | has-a flexibility | Prefer over inheritance unless is-a is genuine |
| LSP | Substitutability | Subclass must honour parent's contract |
| DIP | Depend on abstractions | Inject dependencies; avoid `new ConcreteClass()` inside class bodies |

---

*Last updated for Java 17+ — default/static interface methods (Java 8), private interface methods (Java 9), pattern matching instanceof (Java 16+).*