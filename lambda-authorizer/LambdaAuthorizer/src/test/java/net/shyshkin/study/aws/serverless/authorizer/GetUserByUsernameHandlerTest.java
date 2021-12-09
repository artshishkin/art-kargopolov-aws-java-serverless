package net.shyshkin.study.aws.serverless.authorizer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import static org.junit.Assert.*;

public class GetUserByUsernameHandlerTest {
//  @Test
  public void successfulResponse() {
    GetUserByUsernameHandler getUserByUsernameHandler = new GetUserByUsernameHandler();
    APIGatewayProxyResponseEvent result = getUserByUsernameHandler.handleRequest(null, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));
  }
}
