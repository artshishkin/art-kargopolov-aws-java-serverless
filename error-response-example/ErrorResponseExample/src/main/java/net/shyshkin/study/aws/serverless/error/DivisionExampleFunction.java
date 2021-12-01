package net.shyshkin.study.aws.serverless.error;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionExampleFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String, String> queryStringParameters = input.getQueryStringParameters();

        int dividend = Integer.parseInt(queryStringParameters.get("dividend"));
        int divisor = Integer.parseInt(queryStringParameters.get("divisor"));

        response.withStatusCode(200).withBody(
                "{"
                        + "\"dividend\":" + dividend + ","
                        + "\"divisor\":" + divisor +
                        "}"
        );

        return response;
    }

}
