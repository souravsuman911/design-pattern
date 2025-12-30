package internal.designPattern.adapterPattern;

public class PaymentAdapter implements IPaymentProcessor{

    private ThirdPartyPaymentGateway paymentGateway;

    public PaymentAdapter(ThirdPartyPaymentGateway paymentGateway){
        this.paymentGateway = paymentGateway;
    }

    @Override
    public void pay(double amount) {
        paymentGateway.makePayment(amount);
    }
}
