package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shyshkin.study.aws.serverless.cognito.service.CognitoUserService;
import net.shyshkin.study.aws.serverless.cognito.service.KMSUserService;
import net.shyshkin.study.aws.serverless.cognito.service.SerializerService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String appClientId;
    private final String appClientSecret;

    public CreateUserHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientId = KMSUserService.MY_COGNITO_POOL_APP_CLIENT_ID;
        this.appClientSecret = KMSUserService.MY_COGNITO_POOL_APP_CLIENT_SECRET;
    }

    protected CreateUserHandler(CognitoUserService cognitoUserService,
                             String appClientId,
                             String appClientSecret) {
        this.cognitoUserService = cognitoUserService;
        this.appClientId = appClientId;
        this.appClientSecret = appClientSecret;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String requestBody = input.getBody();

        LambdaLogger logger = context.getLogger();
        logger.log("Request body: " + requestBody);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            var userDetails = JsonParser.parseString(requestBody).getAsJsonObject();

            JsonObject createUserResult = cognitoUserService.createUser(userDetails, appClientId, appClientSecret);
            response
                    .withStatusCode(200)
                    .withBody(createUserResult.toString());
        } catch (AwsServiceException ex) {
            String errorMessage = ex.awsErrorDetails().errorMessage();
            logger.log(errorMessage);

            var errorResponse = new ErrorResponse(errorMessage);
            var respBody = SerializerService.instance().toJson(errorResponse);

            response
                    .withStatusCode(ex.awsErrorDetails().sdkHttpResponse().statusCode())
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
