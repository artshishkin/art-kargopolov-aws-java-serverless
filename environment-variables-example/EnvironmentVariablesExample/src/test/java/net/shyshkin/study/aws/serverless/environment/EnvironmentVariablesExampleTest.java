package net.shyshkin.study.aws.serverless.environment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class EnvironmentVariablesExampleTest {

    @Mock
    Context context;

    LambdaLogger lambdaLogger = new MyLogger();

    @Test
    @Disabled("Fail because of KMS")
    public void successfulResponse() {

        //given
        EnvironmentVariablesExample environmentVariablesExample = new EnvironmentVariablesExample();
        given(context.getLogger()).willReturn(lambdaLogger);

        //when
        APIGatewayProxyResponseEvent result = environmentVariablesExample.handleRequest(null, context);

        //then
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        String content = result.getBody();
        System.out.println(content);
        assertNotNull(content);
    }

    private static class MyLogger implements LambdaLogger {

        @Override
        public void log(String message) {
            System.out.println(message);
        }

        @Override
        public void log(byte[] message) {
            log(String.valueOf(message));
        }
    }
}
