package net.shyshkin.study.aws.serverless.cognito;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;

class CreateUserHandlerTest {

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

        // Act or When

        // Assert or Then

    }
}