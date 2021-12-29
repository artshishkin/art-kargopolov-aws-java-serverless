package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.shyshkin.study.aws.serverless.cognito.service.CognitoUserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserHandlerTest {

    @Mock
    CognitoUserService cognitoUserService;

    @Mock
    APIGatewayProxyRequestEvent requestEvent;

    @Mock
    Context context;

    @Mock
    LambdaLogger loggerMock;

    @InjectMocks
    CreateUserHandler createUserHandler;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before All");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Before Each");
    }

    @AfterEach
    void tearDown() {
        System.out.println("After Each");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After All");
    }

    @Test
    @Disabled
    void failingTest() {

        fail("Intentionally failing test case");

    }

    @Test
    void testHandleRequest_whenValidRequestProvided_thenShouldReturnSuccessfulResponse() {
        // A A A

        // Arrange or Given
        String userDetailsJsonString = "{\n" +
                "  \"email\":\"kateshyshkina@mailinator.com\",\n" +
                "  \"password\":\"12345678\",\n" +
                "  \"firstName\":\"Kate\",\n" +
                "  \"lastName\":\"Shyshkina\"\n" +
                "}";
        given(requestEvent.getBody()).willReturn(userDetailsJsonString);

        when(context.getLogger()).thenReturn(loggerMock);

        JsonObject createUserResult = new JsonObject();
        createUserResult.addProperty("isSuccessful", true);
        createUserResult.addProperty("statusCode", 200);
        createUserResult.addProperty("cognitoUserId", "23931bbb-6564-4aab-b6c2-161e9bebf64e");
        createUserResult.addProperty("isConfirmed", false);

        when(cognitoUserService.createUser(any(JsonObject.class), anyString(), anyString())).thenReturn(createUserResult);

        // Act or When
        APIGatewayProxyResponseEvent responseEvent = createUserHandler.handleRequest(requestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject responseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();


        // Assert or Then
        verify(loggerMock, times(1)).log(anyString());

    }
}