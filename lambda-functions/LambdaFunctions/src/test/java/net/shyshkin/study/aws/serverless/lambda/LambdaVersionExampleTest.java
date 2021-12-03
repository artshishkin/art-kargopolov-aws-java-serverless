package net.shyshkin.study.aws.serverless.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import static org.junit.Assert.*;

public class LambdaVersionExampleTest {
//  @Test
  public void successfulResponse() {
    LambdaVersionExample lambdaVersionExample = new LambdaVersionExample();
    APIGatewayProxyResponseEvent result = lambdaVersionExample.handleRequest(null, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));
  }
}
