package net.shyshkin.study.aws.serverless.environment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class EnvironmentVariablesExample implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String, String> environment = System.getenv();
        String json = gson.toJson(environment);

        String myVariable = System.getenv("MY_VARIABLE");
        context.getLogger().log("MY_VARIABLE is: " + myVariable);
        context.getLogger().log("MY_COGNITO_USER_POOL_ID is: " + System.getenv("MY_COGNITO_USER_POOL_ID"));
        context.getLogger().log("MY_COGNITO_CLIENT_APP_SECRET is: " + System.getenv("MY_COGNITO_CLIENT_APP_SECRET"));
        context.getLogger().log("MY_GLOBAL_VARIABLE is: " + System.getenv("MY_GLOBAL_VARIABLE"));

        return response
                .withBody("{}")
                .withStatusCode(200);

    }
}
