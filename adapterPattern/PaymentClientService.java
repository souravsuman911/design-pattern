package internal.designPattern.adapterPattern;

/*
    Scenario
    Application expects a PaymentProcessor(Target)
    Third-party gateway provides ThirdPartyPaymentGateway(Adaptee)
    Interfaces don’t match → Adapter needed
 */

public class PaymentClientService {
    public static void main(String[] args) {
        IPaymentProcessor paymentProcessor = new PaymentAdapter(new ThirdPartyPaymentGateway());
        paymentProcessor.pay(5000.00);
    }
}
