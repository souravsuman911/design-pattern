The **Builder Design Pattern** is about controlling object creation complexity while keeping the object immutable, readable, and safe to construct.

**Problem**
* An object has many optional parameters
* Constructor overloads explode (new A(a,b), new A(a,b,c), new A(a,b,c,d,e))
* Object creation logic is complex or step-wise
* You want immutability but flexible construction
* Different representations of the same object are needed
* making it less readable, less maintainable and bug prone.

`new HttpRequest(
    "POST",
    "/payments",
    headers,
    queryParams,
    body,
    timeout,
    retryPolicy,
    authToken,
    proxy,
    followRedirects
);`

**Core Idea**
* Separate the construction of a complex object from its representation, so the same construction process can create different representations.
* or Move object creation logic out of constructors and into a fluent, controlled, step-by-step API.


**Key Characteristics**
* Object creation	_Step-by-step_
* Constructor complexity   _Eliminated_
* Immutability	_Easy_
* Readability	_High_
* **Validation	 _Centralized_**
* Telescoping constructors	_Avoided_



                                        +----------------------+
                                        |     Product          |
                                        +----------------------+
                                        | - field1             |
                                        | - field2             |
                                        | - field3             |
                                        +----------------------+
                                        | + builder()/execute()|
                                        +-----------+----------+
                                                    |
                                                    v
                                          +-------------------+
                                          |     Builder       |
                                          +-------------------+
                                          | + field1()        |
                                          | + field2()        |
                                          | + field3()        |
                                          | + build()         |
                                          +-------------------+


* uses multiple inheritance - step builder

**When NOT to Use Builder**
* Object has ≤3 fields
* All fields are mandatory
* Construction is trivial
* Performance-critical hot paths (builder alloc overhead)

**You’ve already used it if you’ve worked with:**
* StringBuilder
* StringBuilder.append()
* OkHttpClient.Builder
* HttpRequest.Builder (Java 11)
* Spring’s UriComponentsBuilder
* JPA CriteriaBuilder

# **Builder Director**

Director encapsulates the construction algorithm, enabling multiple products to be built using the same steps.

**Purpose**
* Construction has a fixed sequence of steps
* Same construction logic creates different representations
* You want to reuse construction workflow
  
**Typical domains**
* Report generation
* Document / PDF builders
* UI layout engines
* Workflow-based object creation

# **Step Builder**

Step Builder moves validation from runtime to compile time by encoding construction rules in the type system.

**Purpose**
* Enforces mandatory fields and order at compile time
* Prevents creation of invalid objects


**When to Use**
* Public APIs / SDKs
* Mandatory configuration sequence
* Misuse must be impossible
* Business rules demand strict order

**Typical domains**
* API request builders
* Security / auth configuration
* Payment & transaction flows

**Key Characteristics**
**Uses multiple interfaces
Fluent API with restricted chaining
No Director required
Order enforced by return types**

**Advantages**
* Compile-time safety
* No invalid states
* Self-documenting API
* Eliminates runtime validation

**Drawbacks**
* ⚠ Many interfaces → verbosity
* ⚠ Harder to refactor
* ⚠ Overkill for internal code
* ⚠ Less flexible ordering



                            +---------------------------+
                            |         Request           |   
                            +---------------------------+
                            | - url                     |
                            | - method                  |
                            | - headers                 |
                            | - body                    |
                            +---------------------------+
                            | - Request(Builder)        |
                            +-------------+-------------+
                                          ▲
                                          |
                            +---------------------------------+
                            |        StepBuilder              |
                            +---------------------------------+
                            | + header() : IOptionalStep      |
                            | + queryParam() : IOptionalStep  |
                            | + timeout() : IOptionalStep     |
                            | + build() : Request             |
                            +-------------+-------------------+
                                          ▲
                                          |
                            +---------------------------------+
                            |     IOptionalStep               |
                            +---------------------------------+
                            | + header() : IOptionalStep      |
                            | + queryParam() : IOptionalStep  |
                            | + timeout() : IOptionalStep     |
                            | + build() : Request             |
                            | + body() : IOptionalStep        |
                            | + method() : IHeaderStep        |
                            | + url() : IMethodStep           |
                            +-------------+-------------------+
                            ▲            ▲                   ▲
                            |            |                   |          
    +---------------------------+        |                   |
    |      IBodyStep            |        |                   |
    +---------------------------+        |                   |
    | + body() : IOptionalStep  |        |                   |
    +-------------+-------------+        |                   |
                                         |                   |
                                         |                   |
                            +---------------------------+    |
                            |     IMethodStep           |    |
                            +---------------------------+    |
                            | + method() : IHeaderStep  |    |
                            +-------------+-------------+    |
                                                             |
                                                             |
                                                +---------------------------+
                                                |      IUrlStep             |
                                                +---------------------------+
                                                | + url() : IMethodStep     |
                                                +---------------------------+

Relations
Request class - HttpRequest
HttpRequestStepBuilder has HttpRequest
HttpRequestStepBuilder implements IUrlStep, IMethodStep, IBodyStep, IOptionalStep