package internal.designPattern.external.concepts.multithreading.problem_set;

public class ThreadCreation {

    static class MyThread extends Thread {
        @Override
        public void run(){
            System.out.println(Thread.currentThread().getName() + " " +  " Says Hello!");
        }
    }

    static class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " " +  " Says Hello!");
        }
    }


    public static void main(String[] args) {

         // 1.a
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " " +  " Says Hello!");
        }, "THREAD_01");
        t1.start();

        //1.b
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName() + " " +  " Says Hello!");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        Thread t2 = new Thread(runnable, "THREAD_02");
        t2.start();

        //1.c
        MyThread t3 = new MyThread();
        t3.setName("Thread_03");
        t3.start();

        //1.d
        MyRunnable r1 = new MyRunnable();
        Thread t4 = new Thread(r1, "Thread_04");
        t4.start();



    }
}

/*
Thread_03  Says Hello!
Thread_04  Says Hello!
THREAD_01  Says Hello!
THREAD_02  Says Hello! // after 5 secs
 */
