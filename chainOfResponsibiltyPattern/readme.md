The **Chain of Responsibility** is a behavioral design pattern that allows a request to be passed along a chain of handlers until one or more handlers process it.
The sender does not know which handler will handle the request.

**Intent**
* Decouple sender from receiver
* Allow multiple objects to handle a request
* Avoid large if–else or switch blocks
* Enable dynamic request processing order

        Key                   Participants
        Participant	        Role
        Client	        Initiates the request
        Handler               (Interface/Abstract class) Declares request handling method
        Concrete Handler	Handles request or forwards to next
        Chain	                Sequence of handlers

**Structure**

                                     Client
                                       |
                         Handler (interface / abstract)
                                       |
                                ConcreteHandler1
                                       |
                                ConcreteHandler2
                                       |
                                ConcreteHandler3

        Each handler: 
        Processes part of the request
        Forwards the rest to the next handler

**UML**

                                +----------------------+
                                |        Client        |
                                +----------------------+
                                          |
                                          | sends request
                                          v
                          +------------------------------------+
                          |        Handler (Interface /        |
                          |            Abstract Class)         |
                          |------------------------------------|
                          | - next : Handler                   |
                          |------------------------------------|
                          | + setNext(h: Handler)              |
                          | + handle(request)                  |
                          +------------------------------------+
                                          |
                          -----------------------------------------------
                          |                     |                       |
                +--------------------+ +--------------------+ +--------------------+
                | ConcreteHandlerA   | | ConcreteHandlerB   | | ConcreteHandlerC   |
                |--------------------| |--------------------| |--------------------|
                | + handle(request)  | | + handle(request)  | | + handle(request)  |
                +--------------------+ +--------------------+ +--------------------+

**Client**
* Initiates the request
* Knows only the first handler
* Does not know which handler will process the request

**Handler (Core of the Pattern)**
`Responsibilities:
Declares the request handling operation
Maintains reference to the next handler`

**UML Details:**
* - next : Handler       // successor reference
* + handle(request)      // request processing
* + setNext(handler)     // chain wiring

**Concrete Handlers**
`Each concrete handler:
Implements handle(request)
Either:
Handles the request
Forwards it to next`

**Request Flow (Logical)**
* Client → HandlerA → HandlerB → HandlerC → (handled / ignored)


**Important UML Design Notes**
* ✔ Handler has self-reference (next)
* ✔ Client is decoupled from concrete handlers
* ✔ Supports Open/Closed Principle
* ✔ Supports runtime chain configuration

**Characteristics**
✔ Advantages
* Loose coupling between sender & handlers
* Easy to add new handlers (Open/Closed Principle)
* Improves readability and maintainability
* Follows Single Responsibility Principle

❌ Disadvantages
* Request may go unhandled
* Debugging can be difficult in long chains
* Performance overhead if chain is long
* Chain order matters

**Design Guidelines (Best Practices)**
* ✅ Use abstract class when handlers share logic
* ✅ Keep handlers stateless if possible
* ✅ Client should know only the first handler


**When to Use CoR**
* Use Chain of Responsibility when:
* Multiple handlers can process a request
* Order of processing matters
* You want runtime flexibility
* You want to remove conditional logic

**When NOT to Use**
* Only one handler exists
* Request must always be handled by a known object
* Performance is extremely critical
* Chain becomes too complex

**Real-World Examples**
* ATM cash dispensing
* Logging frameworks (INFO → DEBUG → ERROR)
* Servlet filters
* Spring Security filter chain
* Approval workflows
* Event bubbling in UI frameworks