package net.shyshkin.study.aws.serverless.sam.photoapp.users;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class PostHandlerTest {
    @Test
    public void successfulResponse() {
        PostHandler postHandler = new PostHandler();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("firstName", "Art");
        requestBody.addProperty("lastName", "Shyshkin");

        var requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setBody(requestBody.toString());

        APIGatewayProxyResponseEvent result = postHandler.handleRequest(requestEvent, null);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"firstName\""));
        assertTrue(content.contains("\"Art\""));
        assertTrue(content.contains("\"lastName\""));
        assertTrue(content.contains("\"Shyshkin\""));
        assertTrue(content.contains("\"userId\""));

        Gson gson = new Gson();
        UserDetails userDetails = gson.fromJson(content, UserDetails.class);

        assertEquals("Art", userDetails.firstName);
        assertEquals("Shyshkin", userDetails.lastName);
        assertNotNull(UUID.fromString(userDetails.userId));

    }
}
