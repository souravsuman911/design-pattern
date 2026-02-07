package internal.designPattern.adapterPattern;

/*
    Scenario
    Application expects a PaymentProcessor(Target)
    Third-party gateway provides ThirdPartyPaymentGateway(Adaptee)
    Interfaces don’t match → Adapter needed
 */

class ThirdPartyPaymentGateway {

    public void makePayment(double value){
        System.out.println("Payment of $" + value + " processed via ThirdParty Gateway");
    }
}

interface IPaymentProcessor {
    public void pay(double amount);
}

class PaymentAdapter implements IPaymentProcessor{

    private ThirdPartyPaymentGateway paymentGateway;
    public PaymentAdapter(ThirdPartyPaymentGateway paymentGateway){
        this.paymentGateway = paymentGateway;
    }

    @Override
    public void pay(double amount) {
        paymentGateway.makePayment(amount);
    }
}

public class PaymentClientService {
    public static void main(String[] args) {
        IPaymentProcessor paymentProcessor = new PaymentAdapter(new ThirdPartyPaymentGateway());
        paymentProcessor.pay(5000.00);
    }
}
