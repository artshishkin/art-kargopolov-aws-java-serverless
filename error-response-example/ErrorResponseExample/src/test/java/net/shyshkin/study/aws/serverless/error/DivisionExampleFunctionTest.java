package net.shyshkin.study.aws.serverless.error;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DivisionExampleFunctionTest {
    @Test
    public void successfulResponse() {
        DivisionExampleFunction divisionExampleFunction = new DivisionExampleFunction();

        var request = new APIGatewayProxyRequestEvent();
        Map<String, String> queryParameters = Map.of(
                "dividend", "3",
                "divisor", "4"
        );
        request.setQueryStringParameters(queryParameters);

        APIGatewayProxyResponseEvent result = divisionExampleFunction.handleRequest(request, null);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"dividend\""));
        assertTrue(content.contains("\"divisor\""));
    }
}
