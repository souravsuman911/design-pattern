package internal.designPattern.external.practice;

import java.util.Arrays;
import java.util.List;

class LogFormat {
    String logLevel;
    String httpMethod;
    String endpoint;
    String statusCode;
    long responseTime; // in ms

    public void logDetails(){
        System.out.println("Log level : " + logLevel);
        System.out.println("Http method : " + httpMethod);
        System.out.println("Endpoint : " + endpoint);
        System.out.println("Status Code : " + statusCode);
        System.out.println("Response Time : " + logLevel);
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public long getResponseTime() {
        return responseTime;
    }

    static class LogFormatBuilder {
        private LogFormat logFormat;

        LogFormatBuilder(){
            logFormat = new LogFormat();
        }

        public LogFormatBuilder withLogLevel(String logLevel){
            logFormat.logLevel = logLevel;
            return this;
        }

        public LogFormatBuilder withHttpMethod(String httpMethod){
            logFormat.httpMethod = httpMethod;
            return this;
        }

        public LogFormatBuilder withEndpoint(String endpoint){
            logFormat.endpoint = endpoint;
            return this;
        }

        public LogFormatBuilder withStatusCode(String statusCode){
            logFormat.statusCode = statusCode;
            return this;
        }

        public LogFormatBuilder withResponseTime(long responseTime){
            logFormat.responseTime = responseTime;
            return this;
        }

        public LogFormat build(){
            if(logFormat.logLevel == null || logFormat.logLevel.isEmpty()){
                System.out.println("Log level is mandatory");
            }

            if(logFormat.httpMethod == null || logFormat.httpMethod.isEmpty()){
                System.out.println("Http Method is mandatory");
            }

            if(logFormat.endpoint == null || logFormat.endpoint.isEmpty()){
                System.out.println("Endpoint is mandatory");
            }

            return logFormat;
        }
    }
}

public class HttpLogAnalyzer {

    public static LogFormat getObject(String logLevel, String httpMethod, String endpoint, String statusCode, long responseTime){
        return new LogFormat.LogFormatBuilder()
                .withLogLevel(logLevel)
                .withHttpMethod(httpMethod)
                .withEndpoint(endpoint)
                .withStatusCode(statusCode)
                .withResponseTime(responseTime)
                .build();
    }

    public static void main(String[] args) {
        LogFormat l1 = getObject("INFO", "GET", "/api/users", "200", 120);
        LogFormat l2 = getObject("INFO", "GET", "/api/orders", "404", 60);
        LogFormat l3 = getObject("INFO", "GET", "/api/users", "500", 95);
        LogFormat l4 = getObject("INFO", "GET", "/api/orders", "200", 180);
        LogFormat l5 = getObject("INFO", "POST", "/api/orders", "200", 310);
        LogFormat l6 = getObject("DEBUG", "OPTIONS", "/api/orders", "204", 0);
        LogFormat l7 = getObject("INFO", "POST", "/api/users", "201", 250);

        List<LogFormat> logFormats = Arrays.asList(l1, l2, l3, l4, l5, l6, l7);

        // requests per endpoint - sorted in desc wrt count


    }
}
