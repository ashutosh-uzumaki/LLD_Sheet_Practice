# Abstract Factory Pattern - Revision Notes

## 1. Intent

Provide an interface for creating **families of related or dependent objects** without specifying their concrete classes.

---

# 2. When to Use

Use Abstract Factory when:

* Multiple objects belong to the same family.
* Objects should always be used together.
* Client should not know the concrete implementations.
* You want to switch the entire family at runtime.

Examples:

* Cross-platform UI Toolkit
* Gaming Console Accessories
* Theme Engines (Dark/Light)
* Cloud Providers (AWS/Azure/GCP)
* Database Families

---

# 3. Problem Statement

Suppose our application supports multiple platforms.

Current platforms:

* Platform A
* Platform B

Each platform requires:

* Button
* TextBox

Requirement:

* LoginScreen should not know which platform it is using.
* It should only ask for a Button and TextBox.
* Platform determines the concrete implementation.

---

# 4. Structure

```
                PlatformFactory
              /                 \
     PlatformAFactory     PlatformBFactory
          |                     |
          |                     |
      creates               creates
      /      \              /      \
PlatformAButton      PlatformATextBox
PlatformBButton      PlatformBTextBox

Button <--------- PlatformAButton
Button <--------- PlatformBButton

TextBox <-------- PlatformATextBox
TextBox <-------- PlatformBTextBox

LoginScreen
      |
      |
PlatformFactory
```

---

# 5. Participants

## Abstract Factory

Defines methods for creating every product.

```java
interface PlatformFactory {
    Button createButton();
    TextBox createTextBox();
}
```

---

## Concrete Factory

Creates one family of products.

Example:

```java
PlatformAFactory
```

returns

* PlatformAButton
* PlatformATextBox

---

## Abstract Products

```java
Button
TextBox
```

---

## Concrete Products

Platform A

* PlatformAButton
* PlatformATextBox

Platform B

* PlatformBButton
* PlatformBTextBox

---

## Client

```
LoginScreen
```

Depends only on

```
PlatformFactory
Button
TextBox
```

Never on concrete classes.

---

# 6. Flow

```
Main

↓

Detect platform

↓

Create PlatformFactory

↓

Inject into LoginScreen

↓

LoginScreen

↓

factory.createButton()

↓

factory.createTextBox()
```

---

# 7. Why not create products directly?

Bad

```java
Button button = new PlatformAButton();
```

Problems:

* Tight coupling
* LoginScreen changes whenever platform changes
* Violates Open/Closed Principle

---

Good

```java
Button button = factory.createButton();
```

LoginScreen never changes.

---

# 8. Why return Button instead of PlatformAButton?

Correct

```java
Button createButton();
```

Wrong

```java
PlatformAButton createButton();
```

Reason:

The client should program against the abstraction.

The client doesn't care whether it receives:

* PlatformAButton
* PlatformBButton
* PlatformCButton

It only knows it has a Button.

This is:

**Program to interfaces, not implementations.**

---

# 9. Family of Products

The biggest idea behind Abstract Factory.

Example:

Platform A Family

* PlatformAButton
* PlatformATextBox

Platform B Family

* PlatformBButton
* PlatformBTextBox

The factory guarantees compatible products.

Never mix families.

Bad

```
PlatformAButton
PlatformBTextBox
```

Good

```
PlatformAButton
PlatformATextBox
```

---

# 10. Why not separate factories?

Instead of

```
PlatformFactory
```

Suppose we have

```
ButtonFactory
TextBoxFactory
CheckBoxFactory
```

Advantage:

Easy to add new product types.

Disadvantage:

Nothing prevents

```
PlatformAButton
PlatformBTextBox
```

Families become inconsistent.

This is why Abstract Factory exists.

---

# 11. Open/Closed Principle

### Adding a New Family

Example:

Platform C

Need to create:

* PlatformCFactory
* PlatformCButton
* PlatformCTextBox

Existing code remains unchanged.

✅ Excellent support.

---

### Adding a New Product

Example:

CheckBox

Need to modify:

* PlatformFactory
* PlatformAFactory
* PlatformBFactory

Need to create:

* CheckBox
* PlatformACheckBox
* PlatformBCheckBox

❌ Existing factories must change.

---

# 12. Trade-off

Abstract Factory is optimized for

✅ Adding new families.

It is not optimized for

❌ Adding new product types.

---

# 13. Difference from Factory Method

## Factory Method

Creates

**One product**

Example

```
ButtonFactory

↓

Button
```

---

## Abstract Factory

Creates

**A family of related products**

Example

```
PlatformFactory

↓

Button
TextBox
CheckBox
```

---

# 14. Relationship Between Factory Method & Abstract Factory

Abstract Factory usually consists of multiple factory methods.

Example

```java
Button createButton();

TextBox createTextBox();
```

Each creation method is itself a factory method.

So,

Factory Method focuses on creating one object.

Abstract Factory groups multiple related factory methods together.

---

# 15. Advantages

* Loose coupling
* Encapsulates object creation
* Easy to switch product families
* Keeps compatible products together
* Client depends only on abstractions
* Supports Dependency Injection
* Easy to add new families

---

# 16. Disadvantages

* Difficult to add new product types
* More interfaces and classes
* Increased complexity for small projects

---

# 17. Real-world Examples

* Java Swing Look & Feel
* Cross-platform UI libraries
* Theme engines
* Cloud SDKs
* Database provider implementations
* Payment gateway families
* Gaming console ecosystems

---

# 18. Interview Decision Checklist

Use Abstract Factory when:

* Do I have multiple related products?
* Should these products always be used together?
* Can different families be swapped?
* Do I want the client independent of concrete classes?
* Do I want to prevent mixing product families?

If **Yes** to most of these, choose Abstract Factory.

---

# 19. Common Interview Questions

### Q1. Why not use `new`?

Because it tightly couples the client to concrete implementations.

---

### Q2. Why return interfaces?

To program against abstractions and hide implementation details.

---

### Q3. Why one PlatformFactory instead of separate factories?

To guarantee products belong to the same family.

---

### Q4. Is Abstract Factory good at adding new product types?

No.

Every factory must be modified.

---

### Q5. Is Abstract Factory good at adding new families?

Yes.

Just add a new concrete factory and its products.

---

### Q6. Can Abstract Factory internally use Factory Method?

Yes.

Each method like

```java
createButton()
```

or

```java
createTextBox()
```

is itself a factory method. An Abstract Factory is essentially a collection of related factory methods.

---

# 20. One-Line Memory Trick

> **Factory Method creates one product. Abstract Factory creates an entire compatible family of products.**

---

# 21. Key Takeaway

**Don't choose Abstract Factory because there are multiple products. Choose it because those products belong to the same family and must always work together.**
