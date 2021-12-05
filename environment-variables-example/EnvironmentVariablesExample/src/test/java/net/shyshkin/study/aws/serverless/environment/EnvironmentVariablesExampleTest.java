package net.shyshkin.study.aws.serverless.environment;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnvironmentVariablesExampleTest {
    @Test
    public void successfulResponse() {
        EnvironmentVariablesExample environmentVariablesExample = new EnvironmentVariablesExample();
        APIGatewayProxyResponseEvent result = environmentVariablesExample.handleRequest(null, null);
        assertEquals(500, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
//    assertTrue(content.contains("\"message\""));
//    assertTrue(content.contains("\"hello world\""));
//    assertTrue(content.contains("\"location\""));
    }
}
