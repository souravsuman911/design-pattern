package internal.designPattern.builderPattern.builderWithDirector;

import internal.designPattern.builderPattern.simpleBuilderPattern.HttpRequest;
import internal.designPattern.builderPattern.simpleBuilderPattern.HttpRequestBuilder;

public class HttpBuilderDirector {

    public static HttpRequest createGetRequest(String url, int timeout){
        return new HttpRequestBuilder()
            .withURL(url)
            .withMethod("GET")
            .withTimeout(timeout)
            .build();
    }

    public static HttpRequest createPostRequest(String url, String jsonBody, int timeout){
        return new HttpRequestBuilder()
            .withURL(url)
            .withMethod("POST")
            .withHeader("Content-Type", "application/json")
            .withHeader("Accept", "application/json")
            .withBody(jsonBody)
            .withTimeout(timeout)
            .build();
    }

}
