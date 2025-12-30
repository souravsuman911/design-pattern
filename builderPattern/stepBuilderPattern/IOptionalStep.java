package internal.designPattern.builderPattern.stepBuilderPattern;

public interface IOptionalStep {

    IOptionalStep withHeader(String key, String value);
    IOptionalStep withQueryParam(String key, String value);
    IOptionalStep withTimeout(int timeout);
    HttpRequest build();
}
