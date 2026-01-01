The **State Design Pattern** allows an object to change its behavior when its internal state changes, without using large if-else or switch statements.
The object appears to change its class at runtime.


**Problem It Solves**
* When an object:
* Has multiple states
* Each state has different behavior
* Behavior changes based on current state

Bad approach:

        if (state == A) { ... }
        else if (state == B) { ... }
        else if (state == C) { ... }


Better approach:
1. [ ] Each state is a separate class
2. [ ] Context delegates behavior to the current state

| Component             | Role                              |
| --------------------- | --------------------------------- |
| **Context**           | Holds current state               |
| **State (interface)** | Declares state-specific behavior  |
| **Concrete States**   | Implement behavior for each state |


                                            +----------------+
                                            |    Context     |
                                            |----------------|
                                            | - state        |
                                            |----------------|
                                            | request()      |
                                            +-------+--------+
                                                    |
                                                    v
                                            +----------------+
                                            |    State       |<<interface>>
                                            |----------------|
                                            | handle()       |
                                            +-------+--------+
                                                    |
                                        --------------------------------
                                        |               |              |
                                    +-----------+ +------------+ +------------+
                                    | StateA    | | StateB     | | StateC     |
                                    +-----------+ +------------+ +------------+

**When to Use State Pattern**
✅ **Use when:**
* Object has many conditional behaviors
* State transitions are explicit
* Want Open/Closed Principle

❌ **Avoid when:**
* Only 2–3 trivial states
* Behavior doesn’t really change
