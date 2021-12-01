package net.shyshkin.study.aws.serverless.transformation.photoapp.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<Map<String, String>, Map<String, String>> {

    private final UUID lambdaId = UUID.randomUUID();

    public Map<String, String> handleRequest(final Map<String, String> input, final Context context) {

        var firstName = input.get("firstName");
        var lastName = input.get("lastName");
        var email = input.get("email");
        var password = input.get("password");
        var repeatPassword = input.get("repeatPassword");

        var secretKey = input.get("secretKey");

        if (context != null) {
            LambdaLogger logger = context.getLogger();
            logger.log("Handling Http Post request for /users API Endpoint by Lambda: " + lambdaId);
            logger.log("User firstName: " + firstName);
            logger.log("User lastName: " + lastName);
            logger.log("User email: " + email);
            logger.log("User secretKey: " + secretKey);
        }

        return Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "email", email,
                "id", UUID.randomUUID().toString()
        );
    }

}
