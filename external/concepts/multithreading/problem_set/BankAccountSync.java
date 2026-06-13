package internal.designPattern.external.concepts.multithreading.problem_set;

public class BankAccountSync {

    public class Account {
        public int accountId;
        public double balance;

        public Account(){
            this.accountId = (int)(Math.random() * 10000);
            this.balance = 0.0;
        }
    }

    public BankAccountSync(){
        this.account = new Account();
    }

    public Account account;

    public synchronized void deposit(double amount) throws InterruptedException {
        account.balance += amount;
        Thread.sleep(3000);
        System.out.println("Amount deposited : " + amount + ", through " + Thread.currentThread().getName() + ", Available Balance : " + account.balance);
        notifyAll();
    }

    public synchronized void withdraw(double amount) throws InterruptedException {
        while (account.balance < amount){
            System.out.println("Sufficient Amount Unavailable..");
            wait();
        }

        Thread.sleep(1000);
        account.balance -= amount;
        System.out.println("Amount deducted " + amount + ", through " + Thread.currentThread().getName() + ". Available Balance : " + account.balance);
        notifyAll();
    }

    public static void main(String[] args) {

        BankAccountSync bankAccountSync = new BankAccountSync();

        Thread t1 = new Thread(() -> {
            try {
                bankAccountSync.deposit(9000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "BANK_DEPOSIT");

        Thread t2 = new Thread(() -> {
            try {
                bankAccountSync.deposit(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "PF_DEPOSIT");

        Thread t3 = new Thread(() -> {
            try {
                bankAccountSync.withdraw(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "ATM_TXN");

        Thread t4 = new Thread(() -> {
            try {
                bankAccountSync.withdraw(9000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "UPI");

        Thread t5 = new Thread(() -> {
            try {
                bankAccountSync.deposit(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "REWARDS");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

    }
}
