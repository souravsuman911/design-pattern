**Observer Pattern** defines a one-to-many dependency so that when one object changes state, 
all its dependents are notified/updated automatically.



                        ┌──────────────────────────────┐
                        │          <<interface>>       │
                        │            Subject           │
                        ├──────────────────────────────┤
                        │ + registerObserver(o)        │
                        │ + removeObserver(o)          │
                        │ + notifyObservers()          │
                        └───────────────▲──────────────┘
                                        │ implements
                        ┌──────────────────────────────┐
                        │        ConcreteSubject       │
                        ├──────────────────────────────┤
                        │ - observers : List<Observer> │
                        │ - state                      │
                        ├──────────────────────────────┤
                        │ + getState()                 │
                        │ + setState()                 │
                        └───────────────┬──────────────┘
                                        │ notifies
                                        │                      
                                        ▼                      
                              ┌────────────────────┐         
                              │   <<interface>>    │          
                              │      Observer      │          
                              ├────────────────────┤          
                              │ + update(state)    │          
                              └─────────▲──────────┘          
                          ┌───--------────┴──-----──────┐
                          │ implements                  │ implements
                ┌────────────────────┐          ┌────────────────────┐
                │  ConcreteObserver  │          │  ConcreteObserver  │
                ├────────────────────┤          ├────────────────────┤
                │ - observerState    │          │ - observerState    │
                ├────────────────────┤          ├────────────────────┤
                │ + update(state)    │          │ + update(state)    │
                └────────────────────┘          └────────────────────┘

In pattern terms:
*     Subject → YouTube Channel
*     Observers → Subscribers
*     Observers register and unregister themselves.
*     The subject does not care who they are, only that they follow a common interface.

🔹 What each part means
* Subject
    Maintains a list of observers
        Provides methods to:
        add
        remove
        notify observers
        "The Publisher"

* ConcreteSubject
    Actual object whose state changes
    Calls notify() when state changes
    “The YouTube channel”

* Observer
    Interface with update() method
    Subject depends only on this interface
    “Anyone who wants updates”

* ConcreteObserver
    Implements Observer
    Reacts when update() is called
    “A specific subscriber”

🔹 Execution Flow
    Subscriber attaches to Channel
    Channel state changes
    Channel calls notify()
    notify() calls update() on all observers
    Subscribers react

🔹 Why Observer is powerful
    ✔ Loose coupling (publisher doesn’t know subscriber details)
    ✔ Easy to add/remove observers at runtime
    ✔ Event-driven design

🔹 Real-world usages

    YouTube / Instagram notifications
    Stock price updates
    Event listeners in UI frameworks


🧠 One-line takeaway
Observer = “Don’t call me, I’ll call you.”
