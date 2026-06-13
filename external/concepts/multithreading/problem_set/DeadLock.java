package internal.designPattern.external.concepts.multithreading.problem_set;

public class DeadLock {

    public final String resource1 = "pen";
    public final String resource2 = "paper";

    public static void main(String[] args) {
        DeadLock dl = new DeadLock();

        Thread t1 = new Thread(() -> {
            synchronized (dl.resource1) {
                System.out.println("T1 acquired Pen");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (dl.resource2) {
                    System.out.println("T1 acquired Paper");
                }
            }

        });

        Thread t2 = new Thread(() -> {
            synchronized (dl.resource2) {
                System.out.println("T2 acquired Paper");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (dl.resource1) {
                    System.out.println("T1 acquired Pen");
                }
            }

        });

        t1.start();
        t2.start();
    }



}
