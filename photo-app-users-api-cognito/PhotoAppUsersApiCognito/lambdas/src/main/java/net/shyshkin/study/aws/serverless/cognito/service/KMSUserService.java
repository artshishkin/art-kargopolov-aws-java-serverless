package net.shyshkin.study.aws.serverless.cognito.service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class KMSUserService {

    private static final Logger log = LoggerFactory.getLogger(KMSUserService.class);

    public static final String MY_COGNITO_POOL_APP_CLIENT_ID = decryptKey("MY_COGNITO_POOL_APP_CLIENT_ID");
    public static final String MY_COGNITO_POOL_APP_CLIENT_SECRET = decryptKey("MY_COGNITO_POOL_APP_CLIENT_SECRET");
    public static final String MY_COGNITO_POOL_ID = System.getenv("MY_COGNITO_POOL_ID");;

    private static String decryptKey(String key) {

        log.info("Decrypting key: " + key);

        byte[] encryptedKey = Base64.decode(System.getenv(key));
//        Map<String, String> encryptionContext = new HashMap<>();
//        encryptionContext.put("LambdaFunctionName",
//                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey))
//                .withEncryptionContext(encryptionContext)
                //we did not encrypt through AWS Console with using Function Name
                ;

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), StandardCharsets.UTF_8);
    }

}
