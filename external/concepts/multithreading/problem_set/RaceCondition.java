package internal.designPattern.external.concepts.multithreading.problem_set;

public class RaceCondition {
    public int counter = 0;

    public void increment(){
        synchronized (this){
            counter ++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RaceCondition rc = new RaceCondition();

        Thread t1 = new Thread(() -> {
            for(int i = 0; i < 10_000_000; i ++){
                rc.increment();
            }
        }, "T1");

        Thread t2 = new Thread(() -> {
            for(int i = 0; i < 10_000_000; i ++){
                rc.increment();
            }
        }, "T2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(rc.counter);

        // Fix
        // 1. make increment() synchronized
        // 2. make counter AtomicInteger and use incrementAndGet()
    }
}
