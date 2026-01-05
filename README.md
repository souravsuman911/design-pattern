# design-pattern
# Design Patterns – Simple & Clear Overview

This README provides **easy-to-understand, concise explanations** of common design patterns. Each pattern includes **what it does, what problem it solves, and when to use it**, written in simple language.

---



## 🏗️ Creational Patterns

### Singleton

Ensures a class has **only one instance** and provides a global access point to it. Solves problems where multiple instances cause inconsistency (e.g., logging, configuration). Use when exactly one shared object is required.

**When to use:** Logging service, configuration manager, cache, thread pool

**Key idea:** One class → One object


### Factory Pattern

Creates objects without exposing the creation logic to the client. Solves tight coupling caused by directly using `new`. Use when the exact object type is decided at runtime.

**When to use:** Payment method creation, notification sender, UI component creation

**Key idea:** Let a factory decide which object to create


### Abstract Factory Pattern

Creates **families of related objects** without specifying concrete classes. Solves issues where multiple related objects must be used together. Use when your system needs to support multiple product families.

**When to use:** Cross-platform UI (Windows/Mac), themed components, database drivers

**Key idea:** Factory of factories


### Builder Pattern

Constructs complex objects step by step. Solves constructor overload and readability problems. Use when an object has many optional or configurable fields.

**When to use:** User profile creation, request/response objects, immutable objects

**Key idea:** Build object gradually

---



## 🧱 Structural Patterns


### Adapter Pattern

Allows incompatible interfaces to work together. Solves integration problems with legacy or third-party code. Use when you want to reuse existing code without changing it.

**When to use:** Legacy API integration, third-party libraries, old system compatibility

**Key idea:** Convert one interface into another


### Decorator Pattern

Adds new behavior to an object dynamically without changing its class. Solves subclass explosion problem. Use when you want flexible and reusable behavior extensions.

**When to use:** Adding features like logging, encryption, compression, UI styling

**Key idea:** Wrap object to add features


### Composite Pattern

Treats single objects and groups of objects uniformly. Solves problems with tree-like structures such as file systems. Use when you need to work with part–whole hierarchies.

**When to use:** File systems, menu structures, organization hierarchies

**Key idea:** Tree structure with same interface

---



## 🔄 Behavioral Patterns

### Observer Pattern

Defines a one-to-many relationship where observers are notified of changes automatically. Solves tight coupling between data and dependent components. Use for event-driven systems.

**When to use:** Event handling, UI updates, message/event listeners

**Key idea:** Notify all listeners on change


### Strategy Pattern

Defines a family of algorithms and allows switching between them at runtime. Solves large conditional (`if-else`) logic. Use when behavior changes based on context.

**When to use:** Sorting algorithms, payment strategies, discount calculation

**Key idea:** Replace conditional logic with polymorphism


### State Pattern

Allows an object to change its behavior when its internal state changes. Solves complex state-based condition handling. Use when object behavior depends heavily on its current state.

**When to use:** Vending machine, order lifecycle, ATM machine states

**Key idea:** Behavior changes with state


### Chain of Responsibility Pattern

Passes a request through a chain of handlers until one handles it. Solves tight coupling between sender and receiver. Use when multiple objects can process a request.

**When to use:** Approval workflows, ATM cash dispenser, request validation pipeline

**Key idea:** Request flows through handlers

---

## Why Use Design Patterns?

* Encourage **clean and maintainable code**
* Reduce **tight coupling** between classes
* Improve **scalability and extensibility**
* Widely used in **frameworks and real-world systems**
