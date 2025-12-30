The **Decorator Design Pattern** is a structural pattern used to add new behavior to an object dynamically, without changing its class.
It follows the principle: “Open for extension, closed for modification.”

🔹 When to use Decorator?

  * When you want to add features to objects at runtime
  * When inheritance would lead to `class explosion`. Class explosion is a software design anti-pattern where an excessive number of classes are created to handle every tiny variation or combination, leading to complex, hard-to-maintain code
  * When you want to combine behaviors flexibly

🔹 Core Idea

    You wrap an object inside another object (the decorator) that adds extra behavior before or after delegating the call to the wrapped object.

🔹 Structure

    Component – Interface / abstract class
    ConcreteComponent – Base implementation
    Decorator – Abstract class implementing Component
    ConcreteDecorators – Add extra behavior


        ┌─────────────────────┐
        │     Component       │
        │   <<interface>>     │
        │---------------------│
        │ + operation()       │
        └─────────▲───────────┘
                  │implements 
         ┌────────┴─────────────────────┐
         │                              │
    ┌──────────────--─┐      ┌────────────────-─────┐
    │ConcreteComponent│      │     Decorator        │
    │-----------------│      │    <<abstract>>      │
    │ + operation()   │      │----------------------│
    └──────────────-──┘      │ - component:Component│
                             │ + operation()        │
                             └─────────▲──────────-─┘
                                       │extends
                            ┌──────────┴─────---------──────┐
                            │                               │
                            ┌──────────────--──┐    ┌────────────────--┐
                            │ConcreteDecoratorA│    │ConcreteDecoratorB│
                            │----------------  │    │------------------│
                            │ + operation()    │    │ + operation()    │
                            └────────────────--┘    └────────--────────┘



Core Idea of Decorator Pattern (in simple words)

    Imagine you walk into a coffee shop.
    You first order a plain coffee.
    This is the base object.
    It already works on its own.

Now you decide:
    “Add milk”
    “Add sugar”

Instead of the shop having separate items like:
*     Coffee
*     Coffee + Milk
*     Coffee + Sugar
*     Coffee + Milk + Sugar
*     (which would explode into many combinations),

the shop does something smarter.
What actually happens

The plain coffee is kept as-is.
    When you add milk:
    Your coffee is wrapped with milk
    When you add sugar:
    That milk coffee is wrapped again with sugar

Each add-on:
    Looks like a coffee
    Contains a coffee
    Adds its own cost and description

So finally:
Sugar wraps Milk, which wraps Coffee
But to you, it’s still just “a coffee”.
