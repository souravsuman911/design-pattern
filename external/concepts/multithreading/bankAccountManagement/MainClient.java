package internal.designPattern.external.concepts.multithreading.bankAccountManagement;

interface IBankAccount {
    void deposit(double amount);
    void withdraw(double amount);
}

class BankAccount implements IBankAccount {
    private double balance;

    public BankAccount(double initialBalance){
        this.balance = initialBalance;
        System.out.println("Account created with balance : " + balance);
    }


    @Override
    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Current thread : " + Thread.currentThread().getName());
        System.out.println("Amount Deposited : " + amount + " | Current Balance : " + balance);
        notifyAll();
    }

    @Override
    public synchronized void withdraw(double amount) {

        // added to expire the
        long timeout = 5000;
        long expTime = System.currentTimeMillis() + timeout;
        long remainingTime = timeout;

        while(balance < amount && remainingTime > 0){
            try {
                System.out.println("Time remaining to expire : " + remainingTime);
                wait(remainingTime);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
            remainingTime = expTime - System.currentTimeMillis();
        }

        if(balance < amount){
            System.out.println("Current thread : " + Thread.currentThread().getName());
            System.out.println("Insufficient amount");
            return;
        }

        balance -= amount;
        System.out.println("Current thread : " + Thread.currentThread().getName());
        System.out.println("Amount withdrawal : " + amount + " | Current Balance : " + balance);

    }
}

class DepositTask implements Runnable {

    double amount;
    IBankAccount bankAccount;

    public DepositTask(double amount, IBankAccount bankAccount){
        this.amount = amount;
        this.bankAccount = bankAccount;
    }

    @Override
    public void run() {
        bankAccount.deposit(amount);
    }
}

class WithdrawTask implements Runnable {
    double amount;
    IBankAccount bankAccount;

    public WithdrawTask(double amount, IBankAccount bankAccount) {
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
        IBankAccount bankAccount = new BankAccount(10000);
        Thread t1 = new Thread(new WithdrawTask(10000, bankAccount), "ATM withdrawal");
        Thread t2 = new Thread(new WithdrawTask(1000, bankAccount), "UPI withdrawal");

        t1.start();
        t2.start();

        Thread.sleep(5000);

        Thread t3 = new Thread(new DepositTask(3000, bankAccount), "NEFT deposit");
        t3.start();
    }
}
