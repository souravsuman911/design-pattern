package internal.designPattern.builderPattern.stepBuilderPattern;

import internal.designPattern.builderPattern.builderWithDirector.HttpRequestBuilder;

public class MainClient {
    public static void main(String[] args) {
        HttpRequest httpRequest = HttpRequestStepBuilder.getBuilder()
                .withURL("https://api.payment.com")
                .withMethod("POST")
                .withBody("{\"transactionId\", \"12345\"}")
                .withHeader("Content-type", "application/json")
                .withQueryParam("transactionType", "BILLED")
                .withTimeout(60)
                .build(); // need to be in this order

        httpRequest.execute();
    } 
}
