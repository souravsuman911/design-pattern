package internal.designPattern.statePattern;

public class IdleState implements IVendingState{

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
