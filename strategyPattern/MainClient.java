package internal.designPattern.strategyPattern;

// switch algorithms at run time
public class MainClient {

    public interface PaymentStrategy{
        public void pay(Double amt);
    }

    public static class CardPay implements PaymentStrategy{
        @Override
        public void pay(Double amt) {
            System.out.println("Card Payment successful : " + amt);
        }
    }

    public static class CashPay implements PaymentStrategy{
        @Override
        public void pay(Double amt) {
            System.out.println("Cash Payment successful : " + amt);
        }
    }

    public static class UpiPay implements PaymentStrategy{
        @Override
        public void pay(Double amt) {
            System.out.println("UPI Payment successful : " + amt);
        }
    }

    public static class PaymentService{
        private PaymentStrategy strategy;

        PaymentService(PaymentStrategy strategy){
            this.strategy = strategy;
        }

        public void processPayment(double amt){
            strategy.pay(amt);
        }
    }

    public static void main(String[] args) {
        PaymentService service = new PaymentService(new UpiPay());
        service.processPayment(100.0);
    }
}
