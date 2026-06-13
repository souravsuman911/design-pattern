package internal.designPattern.external.concepts.multithreading.problem_set;

import java.util.concurrent.locks.ReentrantLock;

public class BankAccountLock {

    private static Double balance = 0.0;
    private static final ReentrantLock lock = new ReentrantLock();

    public static void deposit(Double amount){
        lock.lock();

        try {
            Thread.sleep(3000);
            balance += amount;
            System.out.println("Amount deposited : " + amount + ", through " + Thread.currentThread().getName() +  ", Available Balance : " + balance);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void withdraw(Double amount){
        lock.lock();

        try {
            if(balance < amount){
                System.out.println("Insufficient balance");
                return;
            }

            Thread.sleep(3000);
            balance -= amount;
            System.out.println("Amount deducted : " + amount + ", through " + Thread.currentThread().getName() +  ", Available Balance : " + balance);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            deposit(9000.0);
        }, "BANK_DEPOSIT");

        Thread t2 = new Thread(() -> {
            deposit(5000.0);
        }, "PF_DEPOSIT");

        Thread t3 = new Thread(() -> {
            withdraw(6000.0);
        }, "ATM_TXN");

        Thread t4 = new Thread(() -> {
            withdraw(9000.0);
        }, "UPI");

        Thread t5 = new Thread(() -> {
            deposit(4000.0);
        }, "REWARDS");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}
