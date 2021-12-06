package net.shyshkin.study.aws.serverless.cognito.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CognitoUserService {

    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String region) {
        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient
                .builder()
                .region(Region.of(region))
                .build();
    }

    public JsonObject createUser(JsonObject user, String appClientId, String appClientSecret) {

        String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();
        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();


        String userId = UUID.randomUUID().toString();

        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
        AttributeType nameAttribute = AttributeType.builder().name("name").value(firstName + " " + lastName).build();

        AttributeType userIdAttribute = AttributeType.builder().name("custom:userId").value(userId).build();

        String secretHash = calculateSecretHash(appClientId, appClientSecret, email);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(email)
                .password(password)
                .userAttributes(emailAttribute, nameAttribute, userIdAttribute)
                .clientId(appClientId)
                .secretHash(secretHash)     // Generate client secret was checked
                .build();

        SignUpResponse signUpResponse = cognitoIdentityProviderClient.signUp(signUpRequest);

        JsonObject createUserResult = new JsonObject();
        createUserResult.addProperty("isSuccessful", signUpResponse.sdkHttpResponse().isSuccessful());
        createUserResult.addProperty("statusCode", signUpResponse.sdkHttpResponse().statusCode());
        createUserResult.addProperty("cognitoUserId", signUpResponse.userSub());
        createUserResult.addProperty("isConfirmed", signUpResponse.userConfirmed());

        return createUserResult;
    }

    public JsonObject confirmUserSignup(String appClientId,
                                        String appClientSecret,
                                        String email,
                                        String confirmationCode) {

        String secretHash = calculateSecretHash(appClientId, appClientSecret, email);

        var confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(appClientId)
                .secretHash(secretHash)
                .username(email)
                .confirmationCode(confirmationCode)
                .build();

        var confirmSignUpResponse = cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);

        JsonObject confirmUserResult = new JsonObject();
        confirmUserResult.addProperty("isSuccessful", confirmSignUpResponse.sdkHttpResponse().isSuccessful());
        confirmUserResult.addProperty("statusCode", confirmSignUpResponse.sdkHttpResponse().statusCode());

        return confirmUserResult;
    }

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }


}
