package internal.designPattern.external.concepts.multithreading.problem_set;

public class ThreadSync {

    public boolean taskFinished;

    public boolean turnA;
    public boolean turnB;
    public boolean turnC;

    public ThreadSync(){
        turnA = true;
        turnB = false;
        turnC = false;
        taskFinished = false;
    }

    public synchronized void printA() throws InterruptedException {
        while(!turnA && !taskFinished){
            wait();
        }

        if(taskFinished){
            return;
        }

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName());
        turnA = false;
        turnB = true;
        notifyAll();
    }

    public synchronized void printB() throws InterruptedException {
        while(!turnB && !taskFinished){
            wait();
        }

        if(taskFinished){
            return;
        }

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName());
        turnB = false;
        turnC = true;
        notifyAll();
    }

    public synchronized void printC() throws InterruptedException {
        while(!turnC && !taskFinished){
            wait();
        }

        if(taskFinished){
            return;
        }

        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName());
        turnC = false;
        turnA = true;
        notifyAll();
    }

    public synchronized void completeAllTask(){
        taskFinished = true;
        notifyAll();
    }

    public static void main(String[] args) {
        ThreadSync ts = new ThreadSync();
        Thread t1 = new Thread(() -> {
            try {
                while(!ts.taskFinished){
                    ts.printA();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "A");

        Thread t2 = new Thread(() -> {
            try {
                while(!ts.taskFinished){
                    ts.printB();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "B");

        Thread t3 = new Thread(() -> {
            try {
                while(!ts.taskFinished){
                    ts.printC();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "C");

        Thread t4 = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ts.completeAllTask();
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
