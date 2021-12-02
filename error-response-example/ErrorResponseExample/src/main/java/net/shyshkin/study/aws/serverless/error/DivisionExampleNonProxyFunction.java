package net.shyshkin.study.aws.serverless.error;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionExampleNonProxyFunction implements RequestHandler<Map<String, String>, Map<String, Integer>> {

    public Map<String, Integer> handleRequest(final Map<String, String> input, final Context context) {

        Map<String, Integer> response = new HashMap<>();

        try {
            int dividend = Integer.parseInt(input.get("dividend"));
            int divisor = Integer.parseInt(input.get("divisor"));
            int result = dividend / divisor;

            response.put("dividend", dividend);
            response.put("divisor", divisor);
            response.put("result", result);
        } catch(Exception ex) {
            throw new MyException("Exception: " + ex.getMessage(), ex.getCause());
        }

        return response;
    }
}
