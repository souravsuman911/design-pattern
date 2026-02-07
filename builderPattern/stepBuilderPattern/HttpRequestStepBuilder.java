package internal.designPattern.builderPattern.stepBuilderPattern;

public class HttpRequestStepBuilder implements IUrlStep, IMethodStep, IBodyStep, IOptionalStep{
    private HttpRequest httpRequest;
    HttpRequestStepBuilder(){
        httpRequest = new HttpRequest();
    }

    @Override
    public IMethodStep withURL(String url) {
        httpRequest.url = url;
        return this;
    }

    @Override
    public IBodyStep withMethod(String method) {
        httpRequest.method = method;
        return this;
    }

    @Override
    public IOptionalStep withBody(String body) {
        httpRequest.body = body;
        return this;
    }

    @Override
    public IOptionalStep withHeader(String key, String value) {
        httpRequest.headers.put(key, value);
        return this;
    }

    @Override
    public IOptionalStep withQueryParam(String key, String value) {
        httpRequest.queryParam.put(key, value);
        return this;
    }

    @Override
    public IOptionalStep withTimeout(int timeout) {
        httpRequest.timeout = timeout;
        return this;
    }

    @Override
    public HttpRequest build() {
        if(httpRequest.url == null || httpRequest.url.isEmpty()){
            throw new RuntimeException("URL cannot be empty");
        }
        return httpRequest;
    }

    //static method to start the building process
    public static IUrlStep getBuilder(){
        return new HttpRequestStepBuilder();
    }
}
