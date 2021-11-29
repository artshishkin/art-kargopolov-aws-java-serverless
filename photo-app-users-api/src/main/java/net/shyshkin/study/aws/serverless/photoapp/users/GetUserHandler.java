package net.shyshkin.study.aws.serverless.photoapp.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        String userId = input.getPathParameters().get("userId");

        JsonObject returnValue = new JsonObject();
        returnValue.addProperty("firstName", "Art");
        returnValue.addProperty("lastNAme", "Shyshkin");
        returnValue.addProperty("userId", userId);

        var responseEvent = new APIGatewayProxyResponseEvent();

        responseEvent
                .withStatusCode(200)
                .withBody(returnValue.toString());

        return responseEvent;
    }
}
