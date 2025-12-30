package internal.designPattern.adapterPattern;

public class ThirdPartyPaymentGateway {

    public void makePayment(double value){
        System.out.println("Payment of $" + value + " processed via ThirdParty Gateway");
    }
}
