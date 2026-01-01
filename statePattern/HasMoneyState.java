package internal.designPattern.statePattern;

public class HasMoneyState implements IVendingState{

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
