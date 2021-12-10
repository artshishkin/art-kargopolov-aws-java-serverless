package net.shyshkin.study.aws.serverless.authorizer.sample;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.shyshkin.study.aws.serverless.authorizer.utils.JwtUtils;

import java.util.List;

public class LambdaAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerOutput> {

    @Override
    public AuthorizerOutput handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        var requestContext = input.getRequestContext();

//        String arn = "*"; //allow any resource
        String awsRegion = System.getenv("AWS_REGION");
        String arn = String.format(
                "arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                awsRegion,
                requestContext.getAccountId(),
                requestContext.getApiId(),
                requestContext.getStage(),
                requestContext.getHttpMethod(),
                "*"
        );

        String userName = input.getPathParameters().get("userName");

        String effect = "Allow";

        JwtUtils jwtUtils = new JwtUtils();

        String jwt = input.getHeaders()
                .get("Authorization")
                .replace("Bearer ", "")
                .trim();

        String userPoolId = System.getenv("PHOTO_APP_USERS_POOL_ID");
        String audience = System.getenv("PHOTO_APP_USERS_APP_CLIENT_ID");
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = jwtUtils.validateJwtForUser(jwt, awsRegion, userPoolId, userName, audience);
            userName = decodedJWT.getSubject();
        } catch (RuntimeException ex) {
            effect = "Deny";
            ex.printStackTrace();
        }

        var statement = Statement.builder()
                .action("execute-api:Invoke")
                .effect(effect)
                .resource(arn)
                .build();

        var policyDocument = PolicyDocument.builder()
                .version("2012-10-17")
                .statements(List.of(statement))
                .build();

        return AuthorizerOutput.builder()
                .policyDocument(policyDocument)
                .principalId(userName)
                .build();
    }

}
