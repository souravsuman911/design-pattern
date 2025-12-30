package internal.designPattern.builderPattern.stepBuilderPattern;

import java.util.HashMap;
import java.util.Map;

// fields and methods must be package-private
public class HttpRequest {
    String url;
    String method;
    Map<String, String> headers;
    Map<String, String> queryParam;
    String body;
    int timeout; // in seconds

    HttpRequest(){
        headers = new HashMap<>();
        queryParam = new HashMap<>();
        body = "";
    }

    // Method to execute the HTTP request
    // contains responsibility to execute the request after builder builds the object
    public void execute(){
        System.out.println("Executing " + method + " request to " + url);

        if(!queryParam.isEmpty()){
            System.out.println("Query Params");
            for(Map.Entry<String, String> param : queryParam.entrySet()){
                System.out.println("    " + param.getKey() + " : " + param.getValue());
            }
        }

        if(!headers.isEmpty()){
            System.out.println("Headers");
            for(Map.Entry<String, String> header : headers.entrySet()){
                System.out.println("    " + header.getKey() + " : " + header.getValue());
            }
        }

        if(!body.isEmpty()){
            System.out.println("Body");
            System.out.println("    " + body);
        }

        System.out.println("Timeout : " + timeout);
        System.out.println("Request executed successfully");
    }
}
