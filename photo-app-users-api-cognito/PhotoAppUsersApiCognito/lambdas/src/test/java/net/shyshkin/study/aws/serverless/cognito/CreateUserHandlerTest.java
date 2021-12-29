package net.shyshkin.study.aws.serverless.cognito;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import net.shyshkin.study.aws.serverless.cognito.service.CognitoUserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreateUserHandlerTest {

    @Mock
    CognitoUserService cognitoUserService;

    @Mock
    APIGatewayProxyRequestEvent requestEvent;

    @Mock
    Context context;

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

        // Act or When

        // Assert or Then

    }
}