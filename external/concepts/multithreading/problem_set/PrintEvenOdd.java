package internal.designPattern.external.concepts.multithreading.problem_set;

public class PrintEvenOdd {

    static int num = 1;
    static int LIMIT = 10;

    public synchronized void printOdd() throws InterruptedException{
        while(num < LIMIT){
            while(num % 2 == 0){
                wait();
            }

            System.out.println(Thread.currentThread().getName() + " " + num ++);
            Thread.sleep(1000);
            notify();
        }
    }

    public synchronized void printEven() throws InterruptedException{
        while(num < LIMIT){
            while(num % 2 != 0){
                wait();
            }

            System.out.println(Thread.currentThread().getName() + " " + num ++);
            Thread.sleep(1000);
            notify();
        }
    }

    public static void main(String[] args) {
        PrintEvenOdd obj = new PrintEvenOdd();

        Thread t1 = new Thread(() -> {
            try {
                obj.printOdd();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "ODD_THREAD");

        Thread t2 = new Thread(() -> {
            try {
                obj.printEven();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "EVEN_THREAD");

        t1.start();
        t2.start();
    }
}
