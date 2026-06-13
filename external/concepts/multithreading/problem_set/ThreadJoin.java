package internal.designPattern.external.concepts.multithreading.problem_set;

public class ThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for(int i = 1; i <= 5; i ++){
                System.out.println(Thread.currentThread().getName() + " " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "THREAD_01");

        t1.run(); // new thread did not get created till here so main will execute first
        t1.start(); // then new thread will execute
        t1.join(); // main will not exit after t1.run(). it will wait for execution of t1 thread
                   // if we don't add this, main thread will and complete after t1.run()
                   // and will print "Main thread ends here"

        /*
        main 1
        main 2
        main 3
        main 4
        main 5
        THREAD_01 1
        THREAD_01 2
        THREAD_01 3
        THREAD_01 4
        THREAD_01 5
        Main thread ends here
         */

        t1.start(); // now new thread has been created
        t1.run(); // main thread abd current thread will run parallelly
        t1.join();

        /*
        main 1
        THREAD_01 1
        main 2
        THREAD_01 2
        main 3
        THREAD_01 3
        THREAD_01 4
        main 4
        THREAD_01 5
        main 5
        Main thread ends here
         */

        System.out.println("Main thread ends here");
    }
}
