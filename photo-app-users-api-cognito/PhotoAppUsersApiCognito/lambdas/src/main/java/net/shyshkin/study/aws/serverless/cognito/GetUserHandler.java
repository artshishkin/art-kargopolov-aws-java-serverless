package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import net.shyshkin.study.aws.serverless.cognito.service.CognitoUserService;
import net.shyshkin.study.aws.serverless.cognito.service.SerializerService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.Map;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        var logger = context.getLogger();

        try {

            String accessToken = input.getHeaders().get("AccessToken");
            JsonObject getUserResult = new CognitoUserService(System.getenv("AWS_REGION")).getUser(accessToken);

            response.withBody(getUserResult.toString())
                    .withStatusCode(getUserResult.get("statusCode").getAsInt());

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
