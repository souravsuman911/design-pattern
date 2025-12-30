package internal.designPattern.builderPattern.simpleBuilderPattern;

import internal.designPattern.builderPattern.builderWithDirector.HttpBuilderDirector;

public class MainClient {
    public static void main(String[] args) {
        HttpRequest httpRequest = new HttpRequestBuilder()
                .withURL("https://api.payment.com")
                .withMethod("POST")
                .withHeader("Content-type", "application/json")
                .withQueryParam("transactionType", "BILLED")
                .withBody("{\"transactionId\", \"12345\"}")
                .withTimeout(60)
                .build();

        httpRequest.execute();

        HttpRequest httpRequest1 = HttpBuilderDirector.createGetRequest("https://api.example.com", 20);
        httpRequest1.execute();

        HttpRequest httpRequest2 = HttpBuilderDirector.createPostRequest("https://api.example.com", "{\"transactionId\", \"12345\"}", 60);
        httpRequest2.execute();
    }
}
