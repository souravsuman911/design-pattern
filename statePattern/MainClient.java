package internal.designPattern.statePattern;

class VendingMachine {
    private int itemCount;
    private IVendingState currentState;

    private IdleState idleState;
    private HasMoneyState hasMoneyState;
    private DispensingState dispensingState;
    private SoldOutState soldOutState;

    public VendingMachine(int itemCount){
        this.itemCount = itemCount;
        this.idleState = new IdleState(this);
        this.hasMoneyState = new HasMoneyState(this);
        this.dispensingState = new DispensingState(this);
        this.soldOutState = new SoldOutState(this);
        currentState = itemCount > 0 ? idleState : soldOutState;
    }

    public void insertMoney(){
        currentState.insertMoney();
    }

    public void selectItem(){
        currentState.selectItem();
    }

    public void itemDispensed(){
        currentState.itemDispensed();
    }

    public void setNextState(IVendingState currentState){
        this.currentState = currentState;
    }

    public void releaseItem(){
        if(itemCount > 0){
            itemCount --;
            System.out.println("Item released, items remaining : " + itemCount);
        }
    }

    public boolean isSoldOut(){
        return itemCount == 0;
    }

    public IdleState getIdleState() {
        return idleState;
    }

    public HasMoneyState getHasMoneyState() {
        return hasMoneyState;
    }

    public DispensingState getDispensingState() {
        return dispensingState;
    }

    public SoldOutState getSoldOutState() {
        return soldOutState;
    }
}

interface IVendingState {
    void insertMoney();
    void selectItem();
    void itemDispensed();
}

class IdleState implements IVendingState{

    private VendingMachine vendingMachine;

    public IdleState(VendingMachine vendingMachine){
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertMoney() {
        System.out.println("Money inserted");
        vendingMachine.setNextState(vendingMachine.getHasMoneyState());
    }

    @Override
    public void selectItem() {
        System.out.println("Kindly insert money first");
    }

    @Override
    public void itemDispensed() {
        System.out.println("No money inserted");
    }
}

class HasMoneyState implements IVendingState{

    VendingMachine vendingMachine;

    public HasMoneyState(VendingMachine vendingMachine){
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertMoney() {
        System.out.println("Money already inserted");
    }

    @Override
    public void selectItem() {
        System.out.println("Item selected");
        vendingMachine.setNextState(vendingMachine.getDispensingState());
    }

    @Override
    public void itemDispensed() {
        System.out.println("Please insert money first");
    }
}

class DispensingState implements IVendingState{

    VendingMachine vendingMachine;

    public DispensingState(VendingMachine vendingMachine){
        this.vendingMachine = vendingMachine;
    }
    @Override
    public void insertMoney() {
        System.out.println("Item dispensing");
    }

    @Override
    public void selectItem() {
        System.out.println("Item dispensing");
    }

    @Override
    public void itemDispensed() {
        System.out.println("Item dispensed");
        vendingMachine.releaseItem();

        if(vendingMachine.isSoldOut()){
            vendingMachine.setNextState(vendingMachine.getSoldOutState());
        }
        else{
            vendingMachine.setNextState(vendingMachine.getIdleState());
        }
    }
}

class SoldOutState implements IVendingState{

    private VendingMachine vendingMachine;

    public SoldOutState(VendingMachine vendingMachine){
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void insertMoney() {
        if(!vendingMachine.isSoldOut()){
            vendingMachine.setNextState(vendingMachine.getIdleState());
        }
        else{
            System.out.println("Item out of stock");
        }
    }

    @Override
    public void selectItem() {
        System.out.println("Item out of stock");
    }

    @Override
    public void itemDispensed() {
        System.out.println("Item out of stock");

    }
}

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
