package internal.designPattern.external.concepts.multithreading.problem_set;

public class ImplementsRunnable {

    static class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new Task(), "Task A");
        Thread t2 = new Thread(new Task(), "Task B");

        t1.start();
        t2.start();
    }
}

/*
Task A
Task B
 */
