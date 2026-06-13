package internal.designPattern.external.concepts.multithreading.problem_set;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer {
    public Queue<Integer> queue;
    public static final int SIZE = 3;
    public volatile boolean productionFinshed;

    ProducerConsumer(){
        queue = new LinkedList<>();
        productionFinshed = false;
    }

    public synchronized void produce(int task) throws InterruptedException {
        while(queue.size() == SIZE){
            wait();
        }

        queue.offer(task);
        System.out.println(Thread.currentThread().getName() + " produced : " + task + " | " + queue.size());
        notifyAll();
    }

    public synchronized void consume() throws InterruptedException {
        while(queue.isEmpty() && !productionFinshed){
            wait();
        }

        if(queue.isEmpty() && productionFinshed){
            return;
        }

        System.out.println(Thread.currentThread().getName() + " consume : " + queue.poll() + " | " + queue.size());
        notifyAll();
    }

    public synchronized void markProductionComplete() {
        productionFinshed = true;
        notifyAll();
    }

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();
        int[] tasks = new int[]{1, 2, 4, 2, 4, 7, 8, 0, 9, 7, 3, 8, 6};

        Thread t1 = new Thread(() -> {
            try {
                for(int task : tasks){
                    pc.produce(task);
                    Thread.sleep(1000);
                }
                pc.markProductionComplete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Producer");

        Thread t2 = new Thread(() -> {
            try {
                while(!pc.productionFinshed){
                    pc.consume();
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Consumer");

        t1.start();
        t2.start();
    }
}
