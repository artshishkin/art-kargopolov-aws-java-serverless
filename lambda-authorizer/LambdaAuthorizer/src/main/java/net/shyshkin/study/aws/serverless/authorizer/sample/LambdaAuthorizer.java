package net.shyshkin.study.aws.serverless.authorizer.sample;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.List;

public class LambdaAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerOutput> {

    @Override
    public AuthorizerOutput handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        var requestContext = input.getRequestContext();

//        String arn = "*"; //allow any resource
        String arn = String.format(
                "arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                System.getenv("AWS_REGION"),
                requestContext.getAccountId(),
                requestContext.getApiId(),
                requestContext.getStage(),
                requestContext.getHttpMethod(),
                "*"
        );

        String userName = input.getPathParameters().get("userName");

        String effect = "Allow";
        if ("123".equals(userName)) {
            effect = "Deny";
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
