package internal.designPattern.statePattern;

public class SoldOutState implements IVendingState{

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
