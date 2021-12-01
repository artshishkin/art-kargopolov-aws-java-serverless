package net.shyshkin.study.aws.serverless.transformation.photoapp.users;

import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class PostHandlerTest {
    @Test
    public void successfulResponse() {
        PostHandler postHandler = new PostHandler();

        var requestBody = Map.of(
                "firstName", "Art",
                "lastName", "Shyshkin",
                "email", "bla@example.com",
                "password", "123",
                "repeatPassword", "321"
        );

        var result = postHandler.handleRequest(requestBody, null);

        assertEquals("Art", result.get("firstName"));
        assertEquals("Shyshkin", result.get("lastName"));
        assertNotNull(UUID.fromString(result.get("id")));
        assertNull(result.get("password"));
        assertNull(result.get("repeatPassword"));
    }
}
