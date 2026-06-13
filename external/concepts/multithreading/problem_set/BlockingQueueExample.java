package internal.designPattern.external.concepts.multithreading.problem_set;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueExample {

    public static void main(String[] args) {
        int[] tasks = new int[]{1, 2, 4, 2, 4, 7, 8, 0, 9, 7, 3, 8, 6};
        BlockingQueue<Integer> bq = new ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            try {
                for(int task : tasks) {
                    bq.put(task);
                    System.out.println(Thread.currentThread().getName() + " produced : " + task);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                while(true){
                    int task = bq.take();
                    System.out.println(Thread.currentThread().getName() + " consumed : " + task);
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e){
                System.out.println(e.getStackTrace());
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}
