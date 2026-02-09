package internal.designPattern.external.interview;


public class IbmR1 {

    public static int getNthFibonacii(int n){
        if(n == 0 || n == 1){
            return n;
        }

        return getNthFibonacii(n - 1) + getNthFibonacii(n - 2);
    }

    public static int fib(int n){
        if(n == 0 || n == 1){
            return n;
        }

        int prev = 0;
        int curr = 1;

        for(int i = 2; i <= n; i ++){
            int next = prev + curr;
            prev = curr;
            curr = next;
        }

        return curr;
    }

    public static void main(String[] args) {
        int n = 1;
//        System.out.println(getNthFibonacii(n));
        System.out.println(fib(3));
    }

}
