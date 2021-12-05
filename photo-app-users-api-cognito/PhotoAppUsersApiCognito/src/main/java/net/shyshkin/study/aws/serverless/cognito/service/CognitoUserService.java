package net.shyshkin.study.aws.serverless.cognito.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

import java.util.UUID;

public class CognitoUserService {

    public JsonObject createUser(JsonObject user, String appClientId) {
        JsonObject storedUserDetails = new JsonObject();

        String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();
        String firstName = user.get("firstName").getAsString();
        String lastName = user.get("lastName").getAsString();


        String userId = UUID.randomUUID().toString();

        AttributeType emailAttribute = AttributeType.builder().name("email").value(email).build();
        AttributeType nameAttribute = AttributeType.builder().name("name").value(firstName + " " + lastName).build();

        AttributeType userIdAttribute = AttributeType.builder().name("customer::userId").value(userId).build();

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(email)
                .password(password)
                .userAttributes(emailAttribute, nameAttribute, userIdAttribute)
                .clientId(appClientId)
                .secretHash("")
                .build();

        return storedUserDetails;
    }

}
