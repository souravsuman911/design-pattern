package internal.designPattern.external.concepts.multithreading.problem_set;

public class Solutions {


    public static void printHello_1() throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello");
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        //1. print hello using thread
        printHello_1();
    }
}
