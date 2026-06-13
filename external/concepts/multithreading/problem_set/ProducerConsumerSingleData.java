package internal.designPattern.external.concepts.multithreading.problem_set;

public class ProducerConsumerSingleData {
    public int data;
    public boolean isDataAvailable;

    public synchronized void produce() throws InterruptedException{
        while(true){
            while(isDataAvailable){
                wait();
            }

            data = (int) (Math.random() * 100);
            Thread.sleep(1000);
            System.out.println("Data produced : " + data);
            isDataAvailable = true;
            notifyAll();
        }
    }

    public synchronized void consume() throws InterruptedException{
        while(true){
            while(!isDataAvailable){
                wait();
            }

            Thread.sleep(1000);
            System.out.println("Data consumed : " + data);
            isDataAvailable = false;
            notifyAll();
        }
    }

    public static void main(String[] args) {
        ProducerConsumerSingleData obj = new ProducerConsumerSingleData();
        Thread t1 = new Thread(() -> {
            try {
                obj.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "P_THREAD");

        Thread t2 = new Thread(() -> {
            try {
                obj.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "C_THREAD");

        t1.start();
        t2.start();
    }
}
