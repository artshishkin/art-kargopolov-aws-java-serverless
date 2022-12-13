package net.shyshkin.study.aws.serverless.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import net.shyshkin.study.aws.serverless.authorizer.service.CognitoUserService;

import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class GetUserByUsernameHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;

    public GetUserByUsernameHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        String userName = input.getPathParameters().get("userName");

        String poolId = System.getenv("PHOTO_APP_USERS_POOL_ID");
        try {
            JsonObject userDetails = this.cognitoUserService.getUserByUsername(userName, poolId);

            return new APIGatewayProxyResponseEvent()
                    .withBody(userDetails.toString())
                    .withStatusCode(200)
                    .withHeaders(Map.of(
                            "Content-Type", "application/json",
                            "Access-Control-Allow-Origin", "*"
                    ));
        } catch (Exception ex) {
            context.getLogger().log(ex.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"" + ex.getMessage() + "\"}")
                    .withStatusCode(500)
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }
    }

}
