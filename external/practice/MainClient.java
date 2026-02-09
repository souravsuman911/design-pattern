package internal.designPattern.external.practice;

interface IBankAccount {
    void deposit(Double amount);
    void withdraw(Double amount);
}

class BankAccount implements IBankAccount {

    private Double balance;

    public BankAccount(Double balance){
        this.balance = balance;
    }

    @Override
    public synchronized void deposit(Double amount) {
        balance += amount;
        System.out.println("Current Thread : " + Thread.currentThread().getName());
        System.out.println("Amount deposited : " + amount + " | Current Balance : " + balance);
        notifyAll();
    }

    @Override
    public synchronized void withdraw(Double amount) {
        while(balance < amount){
            try{
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        balance -= amount;
        System.out.println("Current Thread : " + Thread.currentThread().getName());
        System.out.println("Amount withdrawn : " + amount + " | Current Balance : " + balance);
    }
}

class DepositTask implements Runnable {

    private Double amount;
    private IBankAccount bankAccount;

    public DepositTask(Double amount, IBankAccount bankAccount){
        this.amount = amount;
        this.bankAccount = bankAccount;
    }

    @Override
    public void run() {
        bankAccount.deposit(amount);
    }
}

class WithDrawTask implements Runnable {

    private Double amount;
    private IBankAccount bankAccount;

    public WithDrawTask(Double amount, IBankAccount bankAccount){
        this.amount = amount;
        this.bankAccount = bankAccount;
    }

    @Override
    public void run() {
        bankAccount.withdraw(amount);
    }
}

public class MainClient {
    public static void main(String[] args) throws InterruptedException {
        IBankAccount bankAccount = new BankAccount(10000.00);
        Thread t1 = new Thread(new DepositTask(1000.00, bankAccount), "NEFT Txn");
        t1.start();
        Thread t2 = new Thread(new WithDrawTask(10000.00, bankAccount), "ATM Withdrawal");
        Thread t3 = new Thread(new WithDrawTask(5000.00, bankAccount), "Autopay Deduction");
        t2.start();
        t3.start();

        Thread.sleep(5000);

        Thread t4 = new Thread(new DepositTask(6000.00, bankAccount), "Chequebook request");
        t4.start();
    }
}
