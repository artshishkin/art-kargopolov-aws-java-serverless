package net.shyshkin.study.aws.serverless.error;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class DivisionExampleFunctionTest {

    @Mock
    Context context;

    @Test
    public void successfulResponse() {
        DivisionExampleFunction divisionExampleFunction = new DivisionExampleFunction();

        given(context.getFunctionVersion()).willReturn("mockVersion");

        var request = new APIGatewayProxyRequestEvent();
        Map<String, String> queryParameters = Map.of(
                "dividend", "3",
                "divisor", "4"
        );
        request.setQueryStringParameters(queryParameters);

        APIGatewayProxyResponseEvent result = divisionExampleFunction.handleRequest(request, context);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"dividend\""));
        assertTrue(content.contains("\"divisor\""));

        then(context).should().getFunctionVersion();
    }
}
