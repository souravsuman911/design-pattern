package internal.designPattern.builderPattern.builderWithDirector;

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
    } 
}
