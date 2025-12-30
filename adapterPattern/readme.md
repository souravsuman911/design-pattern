The **Adapter pattern** converts one interface into another so incompatible classes can work together without modification.

**Intent:**
The Adapter pattern allows incompatible interfaces to work together by converting the interface of one class into another that the client expects.

**Problem it solves:**
You want to reuse an existing class, but its interface doesn’t match what your code requires—and you can’t or shouldn’t modify that existing class.

**Core idea:**
Adapter acts as a translator between two incompatible interfaces.

**Structure:**

*    Client → uses a Target interface
*    Target → expected interface
*    Adaptee → existing class with incompatible interface
*    Adapter → implements Target and internally calls Adaptee



                                    +----------------+
                                    |     Client     |
                                    +----------------+
                                            |
                                            | uses
                                            v
                                      <<interface>>
                                    +----------------+
                                    |     ITarget     |  
                                    | + pay(amount)  |
                                    +----------------+
                                            ^
                                            | implements
                                    +----------------------+
                                    |      Adapter         |
                                    | - adaptee: Adaptee   |
                                    | + pay(amount)        |
                                    +----------------------+
                                            |
                                            | delegates
                                            v
                                    +----------------------+
                                    |      Adaptee         |
                                    | + makePayment(value) |
                                    +----------------------+


**Types**
* Object Adapter (most common) :- Uses composition (adapter has-a adaptee)
* Class Adapter (less common) :- Uses inheritance (adapter is-a adaptee)

**When to use**
* Integrating legacy code
* Working with third-party libraries
* Avoiding changes to stable or closed-source classes
* Real-world analogy

**Key benefits**
* Promotes reusability
* Follows Open/Closed Principle
* Decouples client from concrete implementations


**Important Notes**
* Adapter is about interface mismatch
* Not used to add behavior → that’s Decorator
* Not used to simplify subsystem → that’s Facade
* Most adapters in Java are Object Adapters

**JDK Real Examples**
* InputStreamReader → adapts InputStream to Reader
* Arrays.asList() → adapts array to List
* Executor wrapping Runnable

