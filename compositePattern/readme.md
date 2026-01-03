The **Composite Design Pattern** is a structural pattern used to represent part–whole hierarchies (tree structures).
It allows clients to treat individual objects (leaves) and groups of objects (composites) in exactly the same way.

**The Core Problem It Solves**
**In many systems, objects naturally form hierarchies, for example:**
* File systems (files & folders)
* UI components (buttons, panels, windows)
* Organization structures (employee, manager)
* Menus and sub-menus

**Without Composite Client code must check**
* Is this a single object or a collection?


**This leads to:**
* if–else logic
* instanceof checks
* Tight coupling
* Poor extensibility


**The Core Idea**

Define a common abstraction for both simple objects and complex (composed) objects.
Then build tree structures using that abstraction.

* Leaf → represents an individual object
* Composite → represents a group of objects
* Both implement the same interface
* Composite stores children of the same interface type
* Operations are handled recursively


    Key Principle Behind Composite
    “Treat part and whole uniformly.”

**From the client’s perspective:**
* Calling an operation on a leaf
* Calling the same operation on a composite
* → looks exactly the same

**The client does not know and does not care whether it is dealing with:**
* a single object
* or a collection of objects

**High-Level Structure (Conceptual)**

**Component**
* Common interface or abstract class
* Declares operations applicable to all objects

**Leaf**
* Implements the component
* Represents indivisible objects
* Has no children

**Composite**
* Implements the component
* Contains children of type Component
* Delegates work to its children

**Client**
* Uses only the Component abstraction
* Never distinguishes between leaf and composite

**How It Works (Conceptually)**
The client invokes an operation on a component
If the component is a leaf
    It performs the operation directly
If the component is a composite
    It forwards the operation to its children
    Results are often aggregated

This recursive delegation is the heart of the pattern.

**Why Composite Is Powerful**
* Eliminates conditional logic in client code
* Encourages recursive, elegant solutions
* Makes hierarchical structures easy to extend
* Aligns strongly with:
Open/Closed Principle
Single Responsibility Principle

**When to Use Composite**

* You need to represent tree-like structures
* You want uniform behavior for single and grouped objects
* You want to avoid client-side complexity
* Operations naturally propagate through a hierarchy

**When NOT to Use It**

* Avoid Composite if:
* Your objects do not form a hierarchy
* You need very different behavior for leaves and composites
* The abstraction becomes forced or misleading