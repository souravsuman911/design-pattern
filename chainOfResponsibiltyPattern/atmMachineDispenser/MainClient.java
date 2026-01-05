package internal.designPattern.chainOfResponsibiltyPattern.atmMachineDispenser;

interface ICashDispenser {

    void dispense(int amount);
    void validate(int amount);
}

class CashDispenser implements ICashDispenser {

    int currencyDenomination = 1000;
    ICashDispenser nextCashDispenser;

    public CashDispenser(int currencyDenomination, ICashDispenser nextCashDispenser){
        this.currencyDenomination = currencyDenomination;
        this.nextCashDispenser = nextCashDispenser;
    }

    @Override
    public void dispense(int amount) {
        validate(amount);

        int remainingAmount = amount % currencyDenomination;
        int currencyCount = amount / currencyDenomination;
        int dispensedAmount = amount - remainingAmount;

        if(currencyCount > 0){
            System.out.println("Amount dispensed in " + currencyDenomination + " currency : " + dispensedAmount + ", total notes : " + currencyCount);
        }


        if(remainingAmount > 0){
            if (nextCashDispenser != null) {
                nextCashDispenser.dispense(remainingAmount);
            } else {
                System.out.println("No smaller denomination available for amount : " + remainingAmount);
            }
        }
    }

    @Override
    public void validate(int amount) {
        if(amount % 100 != 0){
            throw new IllegalArgumentException("Amount is Invalid");
        }
    }
}

class ThousandDispenser extends CashDispenser{
    public ThousandDispenser(int currencyDenomination, ICashDispenser nextCashDispenser){
        super(currencyDenomination, nextCashDispenser);
    }
}

class FiveHunderDispenser extends CashDispenser{
    public FiveHunderDispenser(int currencyDenomination, ICashDispenser nextCashDispenser){
        super(currencyDenomination, nextCashDispenser);
    }
}

class HundredDispenser extends CashDispenser{
    public HundredDispenser(int currencyDenomination, ICashDispenser nextCashDispenser){
        super(currencyDenomination, nextCashDispenser);
    }
}


public class MainClient {

    public static void main(String[] args) {
        ICashDispenser cashDispenser = new ThousandDispenser(1000,
                new FiveHunderDispenser(500,
                        new HundredDispenser(100, null)));

        cashDispenser.dispense(5000);
        System.out.println();

        cashDispenser.dispense(7700);
        System.out.println();

        cashDispenser.dispense(6340);
        System.out.println();
    }

}
