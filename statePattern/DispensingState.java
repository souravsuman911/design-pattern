package internal.designPattern.statePattern;

public class DispensingState implements IVendingState{

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
