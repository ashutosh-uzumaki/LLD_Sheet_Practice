# Factory Method Pattern

> **Pattern Type:** Creational Pattern  
> **Difficulty:** ⭐⭐☆☆☆  
> **Interview Frequency:** Very High (SDE-2)

---

# Intent

Factory Method provides an interface for creating objects while delegating the decision of **which concrete object to create** to the factory.

Instead of the client creating objects using `new`, the client asks the factory to create the appropriate object.

---

# Problem Statement

Design a Notification System that supports multiple notification channels.

### Functional Requirements

- Send Email notification
- Send SMS notification
- Client specifies the notification channel
- One request uses exactly one channel
- Immediate sending only

### Out of Scope

- Retry
- Queue
- Scheduling
- User Preferences
- Templates
- Database
- Authentication

---

# Requirement Discovery (Interview Thinking)

## Step 1: What is the user trying to do?

Send a notification.

---

## Step 2: What data is required?

- sender
- recipient
- message
- notification channel

These naturally become fields inside `NotificationRequest`.

---

## Step 3: Who performs the action?

Someone has to send the notification.

Hence,

```text
NotificationSender
```

---

## Step 4: Are there multiple implementations?

Yes.

- Email
- SMS

Hence,

```text
NotificationSender
        ^
        |
---------------------
|                   |
Email            SMS
```

---

## Step 5: Who creates these objects?

Creation logic should not be inside the client.

Hence,

```text
NotificationFactory
```

---

# Class Responsibilities

## NotificationRequest

Contains all request data.

Responsibilities:

- sender
- recipient
- message
- channel

---

## NotificationSender

Represents the behavior of sending notifications.

```java
public interface NotificationSender {
    void send(NotificationRequest request);
}
```

---

## EmailNotificationSender

Concrete implementation responsible for sending Email.

---

## SmsNotificationSender

Concrete implementation responsible for sending SMS.

---

## NotificationFactory

Responsible only for object creation.

```java
public interface NotificationFactory {

    NotificationSender createSender(
            NotificationChannel channel);
}
```

---

## NotificationService

Responsible for orchestration.

It does **NOT**

- know how Email works
- know how SMS works
- create objects

It simply coordinates the flow.

---

# Request Flow

```text
Client
   |
   v
NotificationService.send(request)
   |
   v
NotificationFactory.createSender(channel)
   |
   v
EmailNotificationSender / SmsNotificationSender
   |
   v
sender.send(request)
```

---

# UML

```text
                     NotificationSender
                             ^
                             |
               +-------------+-------------+
               |                           |
EmailNotificationSender      SmsNotificationSender


                  NotificationFactory
                            ^
                            |
               NotificationFactoryImpl


NotificationService
        |
        +------------> NotificationFactory
```

---

# Java Implementation

## NotificationChannel.java

```java
public enum NotificationChannel {
    EMAIL,
    SMS
}
```

---

## NotificationRequest.java

```java
public class NotificationRequest {

    private final String sender;
    private final String recipient;
    private final String message;
    private final NotificationChannel channel;

    public NotificationRequest(
            String sender,
            String recipient,
            String message,
            NotificationChannel channel) {

        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.channel = channel;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public NotificationChannel getChannel() {
        return channel;
    }
}
```

---

## NotificationSender.java

```java
public interface NotificationSender {

    void send(NotificationRequest request);

}
```

---

## EmailNotificationSender.java

```java
public class EmailNotificationSender
        implements NotificationSender {

    @Override
    public void send(NotificationRequest request) {

        System.out.println(
                "Sending Email to "
                        + request.getRecipient());
    }
}
```

---

## SmsNotificationSender.java

```java
public class SmsNotificationSender
        implements NotificationSender {

    @Override
    public void send(NotificationRequest request) {

        System.out.println(
                "Sending SMS to "
                        + request.getRecipient());
    }
}
```

---

## NotificationFactory.java

```java
public interface NotificationFactory {

    NotificationSender createSender(
            NotificationChannel channel);

}
```

---

## NotificationFactoryImpl.java

```java
public class NotificationFactoryImpl
        implements NotificationFactory {

    @Override
    public NotificationSender createSender(
            NotificationChannel channel) {

        switch (channel) {

            case EMAIL:
                return new EmailNotificationSender();

            case SMS:
                return new SmsNotificationSender();

            default:
                throw new IllegalArgumentException(
                        "Unsupported Notification Channel");
        }
    }
}
```

---

## NotificationService.java

```java
public class NotificationService {

    private final NotificationFactory notificationFactory;

    public NotificationService(
            NotificationFactory notificationFactory) {

        this.notificationFactory = notificationFactory;
    }

    public void send(NotificationRequest request) {

        NotificationSender sender =
                notificationFactory.createSender(
                        request.getChannel());

        sender.send(request);
    }
}
```

---

# Sequence Diagram

```text
Client
   |
   | send(request)
   v
NotificationService
   |
   | createSender(channel)
   v
NotificationFactory
   |
   | returns EmailNotificationSender
   v
NotificationService
   |
   | send(request)
   v
EmailNotificationSender
```

---

# Why Factory Method?

Without Factory:

```java
if(channel == EMAIL){
    new EmailNotificationSender();
}else{
    new SmsNotificationSender();
}
```

Problems:

- Client knows concrete classes.
- Client contains object creation logic.
- Difficult to extend.
- Violates Open/Closed Principle.

With Factory Method:

```java
NotificationSender sender =
        notificationFactory.createSender(channel);
```

Benefits:

- Client does not know concrete implementations.
- Creation logic is centralized.
- Easy to extend.
- Cleaner architecture.

---

# Factory Method vs Simple Factory

| Simple Factory | Factory Method |
|---------------|----------------|
| One concrete factory | Factory abstraction |
| Usually implemented using if/else | Creation delegated through factory |
| Simpler | More extensible |
| Not an official GoF pattern | Official GoF Creational Pattern |

---

# SOLID Principles

## Single Responsibility Principle

Each class has one responsibility.

- NotificationService → Orchestration
- NotificationFactory → Creation
- NotificationSender → Sending

---

## Open Closed Principle

New notification channels can be added by extending the system instead of modifying existing business logic.

---

## Dependency Inversion Principle

NotificationService depends on the abstraction.

```java
NotificationFactory
```

instead of

```java
NotificationFactoryImpl
```

---

# Common Interview Questions

### Why Factory Method instead of Strategy?

Strategy changes behavior.

Factory creates objects.

---

### Why not use Singleton?

Singleton solves a different problem.

Factory answers:

> How should I create the object?

Singleton answers:

> How many instances should exist?

---

### Why doesn't NotificationService store NotificationSender?

Because sender depends on the incoming request.

Different requests may require different implementations.

---

### What if WhatsApp is introduced?

Create

- WhatsAppNotificationSender

Update factory creation logic (or introduce separate factories if evolving toward Abstract Factory).

---

# Key Learnings

- Factory is responsible for object creation.
- Service orchestrates the request.
- Sender performs the work.
- Requirements should drive the design.
- Do not introduce patterns without a requirement.
- Avoid premature optimization.
- Think in terms of responsibilities instead of classes.

---

# LLD Discovery Checklist

Before writing any code, answer these questions:

1. What is the user trying to do?
2. What data is required?
3. Who performs the action?
4. Are there multiple implementations?
5. Who creates the objects?
6. What is the request flow?
7. What classes naturally appear?
8. Which design pattern best fits?