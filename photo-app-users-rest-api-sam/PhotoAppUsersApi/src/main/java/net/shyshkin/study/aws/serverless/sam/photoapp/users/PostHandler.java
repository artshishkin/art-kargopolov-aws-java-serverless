package net.shyshkin.study.aws.serverless.sam.photoapp.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final UUID lambdaId = UUID.randomUUID();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        if (context != null) {
            context.getLogger().log("Handling Http Post request for /users API Endpoint by Lambda: " + lambdaId);
        }

        String body = input.getBody();

        Gson gson = new Gson();

        var userDetails = gson.fromJson(body, UserDetails.class);

        userDetails.userId = UUID.randomUUID().toString();

        String userDetailsJson = gson.toJson(userDetails);

        var responseEvent = new APIGatewayProxyResponseEvent();

        responseEvent
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(userDetailsJson);

        return responseEvent;
    }

}
