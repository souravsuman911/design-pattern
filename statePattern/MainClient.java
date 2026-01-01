package internal.designPattern.statePattern;

public class MainClient {
    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine(2);

        vendingMachine.insertMoney();
        vendingMachine.selectItem();
        vendingMachine.itemDispensed();

        vendingMachine.insertMoney();
        vendingMachine.selectItem();
        vendingMachine.itemDispensed();

        vendingMachine.insertMoney();
        vendingMachine.selectItem();

    }
}

/*
A vending machine behaves differently depending on its state.

Typical States
IdleState – Waiting for money
HasMoneyState – Money inserted
DispensingState – Dispensing product
SoldOutState – No items left

Typical Operations
insertMoney()
selectItem()
itemDispensed()

        ┌─────────────┐
        │   Idle      │
        └─────┬───────┘
              │ insertMoney()
              ▼
       ┌──────────────┐
       │ Has Money    │
       └─────┬────────┘
             │ selectItem()
             ▼
       ┌──────────────┐
       │ Dispensing   │
       └─────┬────────┘
             │ itemDispensed()
             ▼
       ┌──────────────┐
       │ Idle /       │
       │ SoldOut      │
       └──────────────┘

________________________________________________
| Current State | Action      | Next State     |
| ------------- | ----------- | -------------- |
| Idle          | insertMoney | HasMoney       |
| Idle          | selectItem  | ❌ Invalid     |
| HasMoney      | selectItem  | Dispensing     |
| HasMoney      | ejectMoney  | Idle           |
| Dispensing    | dispense    | Idle / SoldOut |
| SoldOut       | insertMoney | ❌ Invalid     |
------------------------------------------------

Step-by-step Plan
Create a State interface
    * Declares all actions:
    * insertMoney(), selectItem(), dispense()

Create concrete state classes
    * IdleState
    * HasMoneyState
    * DispensingState
    * SoldOutState

Create VendingMachine (Context)
    * Holds current State
    * Delegates user actions to the current state
    * Manages inventory count

State objects control transitions
    * Each state decides what the next state is
 */
