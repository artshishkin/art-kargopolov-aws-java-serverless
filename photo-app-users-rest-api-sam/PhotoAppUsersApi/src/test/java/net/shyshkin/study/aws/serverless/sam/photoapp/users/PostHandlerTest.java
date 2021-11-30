package net.shyshkin.study.aws.serverless.sam.photoapp.users;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class PostHandlerTest {
    @Test
    public void successfulResponse() {
        PostHandler postHandler = new PostHandler();
        APIGatewayProxyResponseEvent result = postHandler.handleRequest(null, null);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"message\""));
        assertTrue(content.contains("\"hello world\""));
        assertTrue(content.contains("\"location\""));
    }
}
