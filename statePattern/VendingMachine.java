package internal.designPattern.statePattern;

public class VendingMachine {
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
