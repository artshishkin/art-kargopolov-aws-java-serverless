package net.shyshkin.study.aws.serverless.cognito.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class CognitoUserService {

    private static final Logger log = LoggerFactory.getLogger(CognitoUserService.class);

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

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

    /**
     * @param appClientId     Client ID of application to pass to Cognito service
     * @param appClientSecret Client Secret
     * @param email           User's Email
     * @param password        User's Password
     * @return Json result of authentication
     */
    public JsonObject loginUser(String appClientId,
                                String appClientSecret,
                                String email,
                                String password) {

        String secretHash = calculateSecretHash(appClientId, appClientSecret, email);

        Map<String, String> authParams = Map.of(
                "USERNAME", email,
                "PASSWORD", password,
                "SECRET_HASH", secretHash
        );
        var authRequest = InitiateAuthRequest.builder()
                .clientId(appClientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        var authResponse = cognitoIdentityProviderClient.initiateAuth(authRequest);

        JsonObject loginUserResult = new JsonObject();
        loginUserResult.addProperty("isSuccessful", authResponse.sdkHttpResponse().isSuccessful());
        loginUserResult.addProperty("statusCode", authResponse.sdkHttpResponse().statusCode());
        if (authResponse.sdkHttpResponse().isSuccessful()) {
            var authenticationResult = authResponse.authenticationResult();
            loginUserResult.addProperty("accessToken", authenticationResult.accessToken());
            loginUserResult.addProperty("idToken", authenticationResult.idToken());
            loginUserResult.addProperty("refreshToken", authenticationResult.refreshToken());
        }
        return loginUserResult;
    }

    public JsonObject addUserToGroup(String userName, String groupName, String userPoolId) {

        log.info("addUserToGroup({}, {}, {})", userName, groupName, userPoolId);

        AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest.builder()
                .username(userName)
                .groupName(groupName)
                .userPoolId(userPoolId)
                .build();

        log.info("Start calling cognitoIdentityProviderClient.adminAddUserToGroup");

        AdminAddUserToGroupResponse toGroupResponse = cognitoIdentityProviderClient.adminAddUserToGroup(request);

        log.info("Finished adding user to the group: {}", toGroupResponse);

        JsonObject addToGroupResult = new JsonObject();
        addToGroupResult.addProperty("isSuccessful", toGroupResponse.sdkHttpResponse().isSuccessful());
        addToGroupResult.addProperty("statusCode", toGroupResponse.sdkHttpResponse().statusCode());

        return addToGroupResult;
    }

    public JsonObject getUser(String accessToken) {

        if (accessToken != null && accessToken.length() > 10)
            log.info("getUser({}...{})", accessToken.substring(0, 4), accessToken.substring(accessToken.length() - 4));
        else
            log.info("getUser({})", accessToken);

        GetUserRequest getUserRequest = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        log.info("Start calling cognitoIdentityProviderClient.getUser");

        var getUserResponse = cognitoIdentityProviderClient.getUser(getUserRequest);

        log.info("Finished getting user data: {}", getUserResponse);

        JsonObject getUserResult = new JsonObject();
        getUserResult.addProperty("isSuccessful", getUserResponse.sdkHttpResponse().isSuccessful());
        getUserResult.addProperty("statusCode", getUserResponse.sdkHttpResponse().statusCode());

        if (getUserResponse.sdkHttpResponse().isSuccessful()) {
            JsonObject userDetails = new JsonObject();
            getUserResult.addProperty("username", getUserResponse.username());
            getUserResponse
                    .userAttributes()
                    .forEach(attribute -> userDetails.addProperty(attribute.name(), attribute.value()));
            getUserResult.add("user", userDetails);
        }
        return getUserResult;
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
