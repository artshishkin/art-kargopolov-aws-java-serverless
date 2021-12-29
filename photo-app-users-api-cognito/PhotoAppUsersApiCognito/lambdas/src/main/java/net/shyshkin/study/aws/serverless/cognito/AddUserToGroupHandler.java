package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shyshkin.study.aws.serverless.cognito.service.CognitoUserService;
import net.shyshkin.study.aws.serverless.cognito.service.KMSUserService;
import net.shyshkin.study.aws.serverless.cognito.service.SerializerService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.Map;

public class AddUserToGroupHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String userPoolId;

    public AddUserToGroupHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.userPoolId = KMSUserService.MY_COGNITO_POOL_ID;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        var logger = context.getLogger();

        try {

            String body = input.getBody();

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            String userName = input.getPathParameters().get("userName");
            String userGroup = jsonObject.get("userGroup").getAsString();

            JsonObject addUserToGroupResult = cognitoUserService.addUserToGroup(userName, userGroup, userPoolId);
            response.withStatusCode(addUserToGroupResult.get("statusCode").getAsInt())
                    .withBody(addUserToGroupResult.toString());
        } catch (AwsServiceException ex) {
            String errorMessage = ex.awsErrorDetails().errorMessage();
            logger.log(errorMessage);

            var errorResponse = new ErrorResponse(errorMessage);

            var respBody = SerializerService.instance().toJson(errorResponse);
            response
                    .withStatusCode(ex.statusCode())
                    .withBody(respBody);
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            logger.log(errorMessage);

            var errorResponse = new ErrorResponse(errorMessage);

            var respBody = SerializerService.instance().toJson(errorResponse);
            response
                    .withStatusCode(500)
                    .withBody(respBody);
        }
        return response;
    }
}
