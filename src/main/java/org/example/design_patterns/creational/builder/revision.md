# Builder Pattern - SDE-2 Interview Revision Notes

# 1. Intent

**Builder Pattern** separates the construction of a complex object from its representation, allowing the same construction process to create different configurations.

---

# 2. When should I use Builder?

Use Builder when:

* Object has many constructor parameters.
* Some parameters are mandatory while others are optional.
* Constructor overloading becomes difficult to maintain (Constructor Explosion).
* You want immutable objects.
* You want a readable object creation API.

Example:

```java
Laptop laptop = new Laptop.Builder("i7", "16GB", "512GB")
        .gpu("RTX 4060")
        .backlitKeyboard(true)
        .build();
```

---

# 3. Problems Builder Solves

## Constructor Explosion

```java
Laptop(cpu)

Laptop(cpu, ram)

Laptop(cpu, ram, storage)

Laptop(cpu, ram, storage, gpu)

Laptop(cpu, ram, storage, gpu, warranty)
```

As optional fields increase, constructors increase rapidly.

---

## Setter Approach

```java
Laptop laptop = new Laptop();

laptop.setCpu(...);
laptop.setRam(...);
```

Problems:

* Partially initialized object
* Mutable object
* Difficult to validate
* Object may remain in an inconsistent state

---

# 4. Structure

```
Client
   |
   v
Builder -----> Product
                (Laptop)
```

Builder stores intermediate state.

`build()` constructs the final immutable object.

---

# 5. Components

## Product

```java
public class Laptop
```

Responsibilities:

* Holds final state.
* Immutable.
* Private constructor.

---

## Builder

```java
public static class Builder
```

Responsibilities:

* Stores construction state.
* Provides fluent API.
* Performs validation.
* Creates final object.

---

# 6. Why Builder is static?

Builder should not require an existing Laptop instance.

Wrong:

```java
Laptop laptop = new Laptop();

Laptop.Builder builder = laptop.new Builder();
```

Correct:

```java
Laptop.Builder builder = new Laptop.Builder();
```

Benefits:

* No outer object required.
* No unnecessary outer reference.
* Logically grouped with Laptop.

---

# 7. Why Product constructor is private?

Without a private constructor:

```java
new Laptop(...)
```

can bypass the Builder.

Private constructor forces object creation through Builder.

---

# 8. Required vs Optional Fields

Required fields:

* CPU
* RAM
* Storage

Pass through Builder constructor.

Optional fields:

* GPU
* Warranty
* Backlit Keyboard
* OS

Configure using fluent methods.

Example:

```java
new Laptop.Builder(cpu, ram, storage)
```

instead of

```java
new Laptop.Builder(cpu, ram, storage, gpu, warranty)
```

---

# 9. Fluent API

Each setter returns the Builder itself.

```java
public Builder gpu(String gpu){
    this.gpu = gpu;
    return this;
}
```

Allows:

```java
new Builder(...)
        .gpu(...)
        .os(...)
        .build();
```

---

# 10. Why return `this`?

Every setter modifies the **same Builder object**.

```java
builder.cpu(...)
       .ram(...)
       .storage(...)
```

No new Builder objects are created.

---

# 11. build()

Responsibilities:

* Validate Builder state.
* Construct Product.
* Return Product.

Example:

```java
public Laptop build(){
    validate();

    return new Laptop(this);
}
```

Never return the Builder.

---

# 12. Validation

Typical validations:

```java
CPU != null

RAM != null

Storage != null
```

Two approaches:

### Fail Fast

Throw immediately.

```java
if(cpu == null)
    throw new IllegalStateException();
```

Pros

* Simple
* Easy to implement
* Common in Builder implementations

---

### Collect All Errors

```java
List<String> errors
```

Collect every validation error.

Throw once.

Useful for:

* Forms
* Config files
* Bean validation

---

# 13. Why not return null?

Bad:

```java
return null;
```

Later:

```java
laptop.getCpu();
```

Produces:

```
NullPointerException
```

Prefer:

```java
throw new IllegalStateException(...)
```

Fail early with a meaningful message.

---

# 14. Immutability

Product fields should be:

```java
private final
```

Object state never changes after construction.

Benefits:

* Thread-safe
* Predictable
* Easier debugging
* Easier caching

---

# 15. Builder Constructor

Accept only required fields.

Good:

```java
Builder(cpu, ram, storage)
```

Avoid:

```java
Builder(cpu, ram, storage, gpu)
```

GPU is optional.

---

# 16. Client Usage

```java
Laptop laptop =
    new Laptop.Builder("i7", "16GB", "512GB")
        .gpu("RTX 4060")
        .backlitKeyboard(true)
        .build();
```

---

# 17. Time Complexity

Construction:

```
O(number of fields)
```

Space:

```
O(number of fields)
```

---

# 18. Advantages

* Readable API
* Avoids constructor explosion
* Supports immutable objects
* Separates construction from representation
* Easier validation
* Easy to extend with optional fields

---

# 19. Disadvantages

* Extra Builder class
* Slightly more code
* Product and Builder usually evolve together

---

# 20. Builder vs Factory

| Builder                       | Factory                        |
| ----------------------------- | ------------------------------ |
| Builds step by step           | Creates in one call            |
| Best for many optional fields | Best when choosing object type |
| Focus on construction         | Focus on object creation       |

---

# 21. Interview Questions

### Q1. Why Builder instead of constructor?

Avoid constructor explosion and improve readability.

---

### Q2. Why not setters?

Setters create mutable, partially initialized objects.

---

### Q3. Why private constructor?

Force object creation through Builder.

---

### Q4. Why static Builder?

Builder doesn't need an outer Product instance.

---

### Q5. Why return `this`?

Supports fluent method chaining.

---

### Q6. Why does build() return Product?

Construction is complete.

Returning Builder defeats the purpose.

---

### Q7. Why validate in build()?

Ensures object is always created in a valid state.

---

### Q8. Throw exception or return null?

Throw exception.

Fail fast with meaningful errors.

---

### Q9. Fail Fast vs Collect All Errors?

Builder:

* Usually Fail Fast

Forms / Configurations:

* Collect All Errors

---

### Q10. Required vs Optional parameters?

Required:

* Builder constructor

Optional:

* Fluent setter methods

---

### Q11. Does adding a new field violate Open/Closed Principle?

No.

The domain model itself changed.

Both Product and Builder evolve together.

---

# 22. Implementation Checklist

* Product fields are `final`
* Product constructor is `private`
* Nested `public static Builder`
* Builder stores all fields
* Builder constructor accepts only required fields
* Optional fields via fluent methods
* Every setter returns `this`
* `build()` validates
* `build()` creates Product
* Product is immutable

---

# 23. Common Mistakes

❌ Public Product constructor

❌ Returning `void` from Builder methods

❌ Returning Builder from `build()`

❌ Passing optional fields in Builder constructor

❌ Mutable Product fields

❌ Forgetting validation

❌ Returning `null` on validation failure

---

# 24. Recognition Pattern

If the interview problem has:

* Many optional fields
* Constructor explosion
* Immutable object
* Readable object creation

→ Think **Builder Pattern**.
