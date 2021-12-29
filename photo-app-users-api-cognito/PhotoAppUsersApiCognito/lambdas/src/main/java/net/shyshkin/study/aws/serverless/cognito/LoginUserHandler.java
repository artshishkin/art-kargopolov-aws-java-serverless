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

public class LoginUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String appClientId;
    private final String appClientSecret;

    public LoginUserHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientId = KMSUserService.MY_COGNITO_POOL_APP_CLIENT_ID;
        this.appClientSecret = KMSUserService.MY_COGNITO_POOL_APP_CLIENT_SECRET;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        var logger = context.getLogger();

        try {

            String body = input.getBody();

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();

            JsonObject loginResult = cognitoUserService.loginUser(appClientId, appClientSecret, email, password);
            response.withStatusCode(loginResult.get("statusCode").getAsInt())
                    .withBody(loginResult.toString());
        } catch (AwsServiceException ex) {
            String errorMessage = ex.awsErrorDetails().errorMessage();
            logger.log(errorMessage);

            var errorResponse = new ErrorResponse(errorMessage);

            var respBody = SerializerService.instance().toJson(errorResponse);
            response
                    .withStatusCode(ex.statusCode())
                    .withBody(respBody)
            ;
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            logger.log(errorMessage);

            var errorResponse = new ErrorResponse(errorMessage);

            var respBody = SerializerService.instance().toJson(errorResponse);
            response
                    .withStatusCode(500)
                    .withBody(respBody)
            ;
        }


        return response;
    }
}
