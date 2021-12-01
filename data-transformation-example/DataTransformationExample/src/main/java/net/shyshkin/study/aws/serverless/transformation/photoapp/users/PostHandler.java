package net.shyshkin.study.aws.serverless.transformation.photoapp.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<Map<String, String>, Map<String, String>> {

    private final UUID lambdaId = UUID.randomUUID();

    public Map<String, String> handleRequest(final Map<String, String> input, final Context context) {

        if (context != null) {
            context.getLogger().log("Handling Http Post request for /users API Endpoint by Lambda: " + lambdaId + " and request body " + input);
        }
        var result = new HashMap<>(input);
        result.put("userId", UUID.randomUUID().toString());
        return result;
    }

}
