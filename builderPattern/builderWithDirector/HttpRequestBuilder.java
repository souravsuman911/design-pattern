package internal.designPattern.builderPattern.builderWithDirector;

public class HttpRequestBuilder {

    private HttpRequest httpRequest;

    public HttpRequestBuilder(){
        httpRequest = new HttpRequest();
    }

    public HttpRequestBuilder withURL(String url){
        httpRequest.url = url;
        return this;
    }

    public HttpRequestBuilder withMethod(String method){
        httpRequest.method = method;
        return this;
    }

    public HttpRequestBuilder withHeader(String key, String value){
        httpRequest.headers.put(key, value);
        return this;
    }

    public HttpRequestBuilder withQueryParam(String key, String value){
        httpRequest.queryParam.put(key, value);
        return this;
    }

    public HttpRequestBuilder withBody(String body){
        httpRequest.body = body;
        return this;
    }

    public HttpRequestBuilder withTimeout(int timeout){
        httpRequest.timeout = timeout;
        return this;
    }

    public HttpRequest build(){
        // centralized validation logic
        if(httpRequest.url == null || httpRequest.url.isEmpty()){
            throw new RuntimeException("URL cannot be empty");
        }

        if(httpRequest.method == null || httpRequest.method.isEmpty()){
            throw new RuntimeException("Method cannot be empty");
        }

        return httpRequest;
    }
}
