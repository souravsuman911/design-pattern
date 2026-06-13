package internal.designPattern.external.concepts.multithreading.problem_set;

class MyThread extends Thread {
    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName());
    }
}
public class EntendsThread {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();

        t1.run();
        t1.start();
    }
}

/*
main
Thread-0
 */
