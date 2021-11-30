# art-kargopolov-aws-java-serverless
AWS Serverless REST APIs - Introduction for Java Developers - Tutorial from Sergey Kargopolov (Udemy)

####  Section 4: AWS API Gateway

#####  10. Import from Swagger or Open API 3

-  [Amazon API Gateway Documentation](https://docs.aws.amazon.com/apigateway/index.html)
-  [Choosing between HTTP APIs and REST APIs](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-vs-rest.html)

Steps
-  Amazon API Gateway
-  Create API
-  REST API
-  Import
-  Protocol: REST
-  Create new API -> [Example API](docs/pet-store-swagger.json)
-  Endpoint type: Edge optimized
-  Fail on Warnings
-  Import

####  Section 5: Creating Mock API Endpoints

#####  18. Creating a new API

Steps
-  Amazon API Gateway
-  Create API
-  REST API
-  Build
-  Protocol: REST
-  Create new API -> New API
    -  API name: `Users Mock API`
    -  Endpoint Type: Edge Optimized
-  Create API    

#####  19. Creating a new Resource

-  Actions -> Create Resource
   -  Resource Name: `users`
   -  CORS: off (for now)
   -  Create Resource

#####  20. Creating HTTP Method

-  `/users`
-  Actions -> Create Method -> POST
   -  Integration type: Mock
-  Actions -> Create Method -> GET
   -  Integration type: Mock

#####  21. Returning Mock Data

-  `/users` -> GET -> Integration Response
-  response 200 -> expand -> Mapping Templates
-  Content type -> application/json ->  
```json
[
   {
      "firstname": "Art",
      "lastname": "Shyshkin"
   },
   {
      "firstname": "Kate",
      "lastname": "Shyshkina"
   }
]
```
-  Save
-  Test it:
   -  `/users` -> GET -> Test -> Test

#####  22. Path parameter

-  `/users` -> Create Resource
   -  Name: Single User
   -  Path: `/{userId}`
   -  Enable CORS: true
   -  Create Resource
   -  (will create OPTIONS method for preflight request for CORS)
-  create method GET

#####  23. Reading the path parameter

-  `/users` -> `/{userId}` -> GET -> 
-  Integration Response -> status 200 -> Mapping Templates -> application/json 
```json
{
    "userId":"$input.params('userId')",
    "firstName":"Art",
    "lastName":"Shyshkin"
}
```
-  Test it
   -  Test
   -  Path -> {userId}: 123
```json
{
    "userId":"123",
    "firstName":"Art",
    "lastName":"Shyshkin"
}
```

#####  24. Query String Parameters

-  `/users` -> GET -> 
-  Method Request -> URL Query String Parameters ->
-  Add -> 
   -  `count`

#####  25. Reading Query String Parameter in a Mapping template

-  `/users` -> GET ->
-  Integration Response -> 200 -> Mapping Templates -> application/json

```json
{
   "users":[
     {
        "firstname": "Art",
        "lastname": "Shyshkin"
     },
     {
        "firstname": "Kate",
        "lastname": "Shyshkina"
     }
   ],
   "count": $input.params('count')
}
```
-  Test it
   -  `/users` -> GET ->
   -  Query String -> count=3 -> Test -> OK

#####  26. Deploying Mock API

-  Actions -> Deploy API
-  Development Stage -> New Stage
   -  Stage name: dev (any name)
   -  Deploy
-  Invoke:
   -  Stages -> dev -> `/users` -> GET -> 
   -  Invoke URL: `https://bfvrrtg4l2.execute-api.eu-north-1.amazonaws.com/dev/users`
   -  Use Postman or IntelliJ Http Client

#####  27. Documenting API

-  API: Users Mock API -> Documentation
-  Create Documentation Part ->
   -  Type: Method
   -  Path: /users
   -  Method: GET
```json
{
    "description": "Returns a list of all users"
}
```
-  Save
-  Create Documentation Part ->
   -  Type: Path Parameter
   -  Path: /users/{userId}
   -  Method: GET
   -  Name: userId
```json
{
    "description": "The id of user to return. The value of userId is UUID."
}
```
-  Save
-  Resources
   -  `/users` -> GET -> Book icon (View documentation) ->
   -  Edit -> Save
-  `/users` -> GET -> URL Query String Parameters -> count -> Book icon (Documentation button)   
```json
{
    "description": "The 'count' Query String Parameter is used to limit the number of users to return."
}
```
-  Documentation -> Already 3 parts
-  Publish Documentation ->
   -  Stage: dev
   -  Version: 1.0
   -  Description: First variant of documentation
   -  Publish

####   Section 6: Exporting API

#####  29. Export API and Test with Swagger

-  Deploy the latest changes
   -  Resources -> Actions -> Deploy API
   -  Deployment stage: dev
-  Stages -> dev
   -  Export
      -  Export as `OpenAPI 3`
      -  YAML -> Editor
-  Go to [swagger.io](https://swagger.io/)
   -  Tools -> [Swagger Editor](https://swagger.io/tools/swagger-editor/) ->
   -  Live Demo -> Insert content of [exported documentation](docs/Users Mock API-dev-oas30.yaml)
-  For me it does not return proper content as expected(((
   -  return body is empty
   -  curl works fine

#####  30. Export API with Gateway Extensions

-  Stages -> dev
   -  Export
      -  Export as `Swagger`
      -  Export as Swagger + API Gateway Extensions
      -  YAML -> Editor -> copy
-  APIs -> Create new API
   -  Import from Swagger or Open API 3
   -  Edge optimized
   -  Import
-  View new API
-  Delete API

#####  31. Export API and Test with Postman

-  Stages -> dev
   -  Export
      -  Export as `Swagger`
      -  Export as Swagger + Postman Extensions
      -  YAML -> Editor -> copy
-  Postman
   -  Import -> Raw text -> paste
   -  Continue
   -  Generate collection from imported APIs
   -  Import
-  Collections -> Users Mock API
   -  GET `/users/:userId`

####  Section 7: Validating HTTP Request

#####  33. Validating Request Parameters & Headers

-  `/users` -> GET -> Method Request ->
-  Request Validator: `Validate query string parameters and headers`
-  HTTP Request Headers
   -  Add header -> Authorization -> tick
   -  Required -> true
-  Test it
   -  set Request Params: count=2
   -  headers -> do nothing
   -  Test ->
      -  400 Bad Request
      -  `Response Body`
      -  `{`
      -  `"message": "Missing required request parameters: [Authorization]"`
      -  `}`
      -  `Response Headers`
      -  `{"x-amzn-ErrorType":"BadRequestException"}`   
   -  set header
      -  `Authorization:FooBuzzBar
   -  Test -> OK      `

#####  34. Validating Request Body - Creating a Model

-  API: Users Mock API
-  Models -> [JSON Schema](http://json-schema.org/)
-  [Getting started](http://json-schema.org/learn/getting-started-step-by-step)
-  Create
   -  Model name: `CreateUserModel`
   -  Content type: `application/json`
   -  Model description: A model for create new user http POST request
      
```json
{
   "$schema": "http://json-schema.org/draft-04/schema#",
   "title": "Create User Model Schema",
   "type": "object",
   "properties": {
      "firstName": {
         "type": "string",
         "description": "User's First Name"
      },
      "lastName": {
         "type": "string",
         "description": "User's Last Name"
      },
      "email": {
         "type": "string",
         "description": "User's Email"
      },
      "password": {
         "type": "string",
         "description": "User's Password"
      },
      "repeatPassword": {
         "type": "string",
         "description": "User's Password Repeat"
      },
      "age": {
         "type": "integer",
         "description": "User's age"
      }      
   },
   "required": [ "firstName", "lastName", "email", "password", "repeatPassword" ]
}
```
-  Create Model

#####  35. Associate Model with HTTP Request

-  Resources -> `/users` -> Add Method -> POST
-  Method Request ->
   -  Request Validator: Validate body 
   -  Request Body -> Add Model 
      -  Content type: application/json
      -  Model name: CreateUserModel 

#####  36. Validating Request Body - Trying how it works

-  Test it
-  Empty request body -> Test
   -  `Status: 400`
   -  `Latency: 151 ms`
   -  `Response Body`
   -  `{`
   -  `"message": "Invalid request body"`
   -  `}`
   -  `Response Headers`
   -  `{"x-amzn-ErrorType":"BadRequestException"}`
   -  Logs: `Mon Nov 29 13:31:04 UTC 2021 : Request body does not match model schema for content type application/json: [Unknown error parsing request body]` 
-  Some properties provided (firstname and lastName)
   -  Http Response is the same, Logs differs 
   -  `Mon Nov 29 13:31:51 UTC 2021 : Request body does not match model schema for content type application/json: [object has missing required properties (["email","password","repeatPassword"])]` 
-  All required properties -> OK
```json
{
    "firstName":"Art",
    "lastName":"Shyshkin",
    "email":"d.art.shishkin@gmail.com",
    "password":"123",
    "repeatPassword":"321"
}
```

####  Section 8: Introduction to Lambda

#####  39. Anatomy of a Java Lambda function handler

Request interface
```java
public interface RequestHandler<I, O>{
    public O handleRequest(I input, Context context);
}
```
RequestStream interface

```java
import java.io.IOException;

public interface RequestStreamHandler<I, O> {
   public O handleRequest(I inputS, O output, Context context) throws IOException;
}
```

####  Section 9: Create Java Lambda using Maven and IDE

#####  49. Creating function in AWS Console

-  Lambda Console -> Create function -> Author from scratch
-  Function Name: GetUser
-  Runtime: Java 11 (Corretto)
-  Create a new role with basic Lambda permissions 

#####  50. Deploying Lambda function

-  `mvn clean package`
-  Lambda -> Upload JAR
-  Runtime settings -> Edit
   -  Handler: `net.shyshkin.study.aws.serverless.photoapp.users.GetUserHandler`
   
#####  51. Testing Lambda function with Input Template

-  Test
-  Template: apigateway-aws-proxy
-  Name: GetUserTemplate
-  Change
```
  "pathParameters": {
    "proxy": "/path/to/resource",
    "userId": "123456"
  },
```
-  Test -> OK

#####  52. Create new API project

-  Name:  `Photo App Users API` 

#####  53. Assigning Lambda function to API Endpoint

-  create resource `/users`
-  create sub-resource `/{userId}`
-  create method GET
-  Lambda
-  Use Lambda Proxy integration: **true**
-  Save -> Add permissions -> ok
-  Test it -> with random userId

####  Section 10: AWS SAM - Tools to create & deploy Lambda functions

#####  59. Creating a new project with SAM

-  `sam init`
-  `1 - AWS Quick Start Templates`
-  `1 - Zip (artifact is a zip uploaded to S3)`
-  `5 - java11`
-  `1 - maven`
-  Project name: `photo-app-users-rest-api-sam`
-  `1 - Hello World Example: Maven`

#####  67. Invoke Remote Lambda function from local computer

From directory where `template.yml` is located
-  `sam build` - build and package application
-  `sam local invoke CreateNewUserFunction --event events/event.json`

#####  68.1 Debug Lambda function locally

-  `sam local invoke CreateNewUserFunction --event events/event.json -d 5858`
-  `d` - debug port number
-  IntelliJ -> Add Configuration -> New -> Remote JVM Debug
   -  Name: SAM Local (any name)
   -  Port: 5858
   -  Use module classpath: PhotoAppUsersApi
-  Add debug breakpoint
-  Debug

