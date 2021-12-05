package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateUserHandlerTest {
    @Test
    public void successfulResponse() {
        CreateUserHandler createUserHandler = new CreateUserHandler();
        APIGatewayProxyResponseEvent result = createUserHandler.handleRequest(null, null);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertEquals("{}", content);
    }
}
