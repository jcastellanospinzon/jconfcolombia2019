package com.jcastellanospinzon.jconfco2019.serverlessawshelloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("statusCode", 200);
        response.put("isBase64Encoded", false);
        response.put("body", "Hello World!");
        return response;
    }

}
