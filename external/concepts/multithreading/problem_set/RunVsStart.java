package internal.designPattern.external.concepts.multithreading.problem_set;

public class RunVsStart {

    public static void main(String[] args) {
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

        t1.run();
        t1.start();
    }
}

//sleep() pauses current thread.
//Does not release lock.

/*
with 1 sec gap in each
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
 */
