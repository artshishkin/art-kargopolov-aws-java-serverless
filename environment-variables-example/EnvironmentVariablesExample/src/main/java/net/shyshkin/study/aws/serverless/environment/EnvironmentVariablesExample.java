package net.shyshkin.study.aws.serverless.environment;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.Base64;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class EnvironmentVariablesExample implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new Gson();

    // These variables will hold your decrypted key. Decryption happens on first
    // invocation when the container is initialized and never again for
    // subsequent invocations.
    private static final String MY_VARIABLE = decryptKey("MY_VARIABLE");
    private static final String MY_COGNITO_USER_POOL_ID = decryptKey("MY_COGNITO_USER_POOL_ID");
    private static final String MY_COGNITO_CLIENT_APP_SECRET = decryptKey("MY_COGNITO_CLIENT_APP_SECRET");
    private static final String MY_GLOBAL_VARIABLE = decryptKey("MY_GLOBAL_VARIABLE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String, String> environment = System.getenv();
        String json = gson.toJson(environment);

        context.getLogger().log("MY_VARIABLE is: " + MY_VARIABLE);
        context.getLogger().log("MY_COGNITO_USER_POOL_ID is: " + MY_COGNITO_USER_POOL_ID);
        context.getLogger().log("MY_COGNITO_CLIENT_APP_SECRET is: " + MY_COGNITO_CLIENT_APP_SECRET);
        context.getLogger().log("MY_GLOBAL_VARIABLE is: " + MY_GLOBAL_VARIABLE);

        return response
                .withBody("{\"MY_VARIABLE\":\"" + MY_VARIABLE + "\"}")
                .withStatusCode(200);

    }

    private static String decryptKey(String key) {
        System.out.println("Decrypting key: " + key);
        byte[] encryptedKey = Base64.decode(System.getenv(key));
        Map<String, String> encryptionContext = new HashMap<>();
        encryptionContext.put("LambdaFunctionName",
                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey))
                .withEncryptionContext(encryptionContext);

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), StandardCharsets.UTF_8);
    }

}
