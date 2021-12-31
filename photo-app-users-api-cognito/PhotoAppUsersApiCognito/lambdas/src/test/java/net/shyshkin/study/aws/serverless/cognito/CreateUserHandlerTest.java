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
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.SdkHttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(context.getLogger()).thenReturn(loggerMock);
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

        JsonObject createUserResult = new JsonObject();
        createUserResult.addProperty("isSuccessful", true);
        createUserResult.addProperty("statusCode", 200);
        createUserResult.addProperty("cognitoUserId", "23931bbb-6564-4aab-b6c2-161e9bebf64e");
        createUserResult.addProperty("isConfirmed", false);

        when(cognitoUserService.createUser(any(), any(), any())).thenReturn(createUserResult);

        // Act or When
        APIGatewayProxyResponseEvent responseEvent = createUserHandler.handleRequest(requestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject responseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();

        // Assert or Then
        verify(loggerMock, times(1)).log(anyString());

        assertThat(responseBodyJson.get("isSuccessful").getAsBoolean()).isTrue();
        assertThat(responseBodyJson.get("statusCode").getAsInt()).isEqualTo(200);
        assertThat(responseBodyJson.get("cognitoUserId").getAsString()).isEqualTo("23931bbb-6564-4aab-b6c2-161e9bebf64e");
        assertThat(responseBodyJson.get("isConfirmed").getAsBoolean()).isFalse();

        assertThat(responseEvent.getStatusCode()).isEqualTo(200);

        verify(cognitoUserService).createUser(any(), any(), any());
    }

    @Test
    void testHandleRequest_whenEmptyRequestProvided_thenShouldReturnErrorMessage() {

        // Arrange
        given(requestEvent.getBody()).willReturn("");

        // Act
        APIGatewayProxyResponseEvent responseEvent = createUserHandler.handleRequest(requestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject responseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();

        // Assert
        verify(loggerMock, atLeastOnce()).log(anyString());

        assertThat(responseEvent.getStatusCode()).isEqualTo(500);
        assertThat(responseBodyJson.get("message")).isNotNull();
        assertThat(responseBodyJson.get("message").getAsString()).isNotEmpty();
    }

    @Test
    void testHandleRequest_whenAwsServiceExceptionTakesPlace_thenShouldReturnErrorMessage() {

        // Arrange
        when(requestEvent.getBody()).thenReturn("{}");

        String errorMessage = "Fake Exception took place";
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder()
                .errorMessage(errorMessage)
                .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                .build();
        AwsServiceException awsServiceException = AwsServiceException.builder()
                .awsErrorDetails(awsErrorDetails)
                .statusCode(500)
                .build();
        when(cognitoUserService.createUser(any(), any(), any())).thenThrow(awsServiceException);

        // Act
        APIGatewayProxyResponseEvent responseEvent = createUserHandler.handleRequest(requestEvent, context);
        String responseBody = responseEvent.getBody();
        JsonObject responseBodyJson = JsonParser.parseString(responseBody).getAsJsonObject();

        // Assert
        verify(loggerMock, atLeastOnce()).log(anyString());

        assertThat(responseEvent.getStatusCode()).isEqualTo(awsErrorDetails.sdkHttpResponse().statusCode());
        assertThat(responseBodyJson.get("message")).isNotNull();
        assertThat(responseBodyJson.get("message").getAsString()).isNotEmpty();
        assertThat(responseBodyJson.get("message").getAsString()).isEqualTo(errorMessage);
    }

}