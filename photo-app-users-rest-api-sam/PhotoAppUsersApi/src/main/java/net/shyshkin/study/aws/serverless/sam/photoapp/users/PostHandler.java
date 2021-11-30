package net.shyshkin.study.aws.serverless.sam.photoapp.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        String body = input.getBody();

        Gson gson = new Gson();

        Type typeOfT = new TypeToken<Map<String, String>>() {
        }.getType();

        Map<String, String> userDetails = gson.fromJson(body, typeOfT);

        userDetails.put("userId", UUID.randomUUID().toString());

        JsonObject returnValue = new JsonObject();

        returnValue.addProperty("firstName", userDetails.get("firstName"));
        returnValue.addProperty("lastName", userDetails.get("lastName"));
        returnValue.addProperty("userId", userDetails.get("userId"));

        var responseEvent = new APIGatewayProxyResponseEvent();

        responseEvent
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(returnValue.toString());

        return responseEvent;
    }

}
