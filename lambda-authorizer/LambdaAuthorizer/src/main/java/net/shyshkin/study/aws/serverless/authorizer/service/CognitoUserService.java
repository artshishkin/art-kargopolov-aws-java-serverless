package net.shyshkin.study.aws.serverless.authorizer.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;

public class CognitoUserService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String region) {

        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }

    public JsonObject getUserByUsername(String userName, String poolId) {

        AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
                .username(userName)
                .userPoolId(poolId)
                .build();

        AdminGetUserResponse adminGetUserResponse = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);

        if (!adminGetUserResponse.sdkHttpResponse().isSuccessful()) {
            throw new IllegalArgumentException("Unsuccessful result. Status code: " + adminGetUserResponse.sdkHttpResponse().statusCode());
        }
        JsonObject userDetails = new JsonObject();

        adminGetUserResponse
                .userAttributes()
                .forEach(attribute -> userDetails.addProperty(attribute.name(), attribute.value()));

        return userDetails;
    }

}
