package net.shyshkin.study.aws.serverless.cognito.service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KMSUserService {

    public static final String MY_COGNITO_POOL_APP_CLIENT_ID = decryptKey("MY_COGNITO_POOL_APP_CLIENT_ID");
    public static final String MY_COGNITO_POOL_APP_CLIENT_SECRET = decryptKey("MY_COGNITO_POOL_APP_CLIENT_SECRET");

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
