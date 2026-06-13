package internal.designPattern.external.concepts.multithreading.problem_set;

import java.util.concurrent.locks.ReentrantLock;

public class DeadlockReentrantLock {

    static ReentrantLock pen = new ReentrantLock();
    static ReentrantLock paper = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            pen.lock();

            try{
                System.out.println("T1 acquired Pen");
                Thread.sleep(1000);

                paper.lock();
                try{
                    System.out.println("T2 acquired Paper");
                }
                finally {
                    paper.unlock();
                }
            }
            catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
            finally {
                pen.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            paper.lock();

            try {
                System.out.println("T2 acquired Paper");
                Thread.sleep(1000);

                pen.lock();
                try {
                    System.out.println("T2 acquired Pen");
                }
                finally {
                    pen.unlock();
                }
            }
            catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
            finally {
                paper.unlock();
            }
        });

        t1.start();
        t2.start();
    }
}
