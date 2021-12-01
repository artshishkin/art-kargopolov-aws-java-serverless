# art-kargopolov-aws-java-serverless

AWS Serverless REST APIs - Introduction for Java Developers - Tutorial from Sergey Kargopolov (Udemy)

#### Section 4: AWS API Gateway

##### 10. Import from Swagger or Open API 3

- [Amazon API Gateway Documentation](https://docs.aws.amazon.com/apigateway/index.html)
- [Choosing between HTTP APIs and REST APIs](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-vs-rest.html)

Steps

- Amazon API Gateway
- Create API
- REST API
- Import
- Protocol: REST
- Create new API -> [Example API](docs/pet-store-swagger.json)
- Endpoint type: Edge optimized
- Fail on Warnings
- Import

#### Section 5: Creating Mock API Endpoints

##### 18. Creating a new API

Steps

- Amazon API Gateway
- Create API
- REST API
- Build
- Protocol: REST
- Create new API -> New API
    - API name: `Users Mock API`
    - Endpoint Type: Edge Optimized
- Create API

##### 19. Creating a new Resource

- Actions -> Create Resource
    - Resource Name: `users`
    - CORS: off (for now)
    - Create Resource

##### 20. Creating HTTP Method

- `/users`
- Actions -> Create Method -> POST
    - Integration type: Mock
- Actions -> Create Method -> GET
    - Integration type: Mock

##### 21. Returning Mock Data

- `/users` -> GET -> Integration Response
- response 200 -> expand -> Mapping Templates
- Content type -> application/json ->

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

- Save
- Test it:
    - `/users` -> GET -> Test -> Test

##### 22. Path parameter

- `/users` -> Create Resource
    - Name: Single User
    - Path: `/{userId}`
    - Enable CORS: true
    - Create Resource
    - (will create OPTIONS method for preflight request for CORS)
- create method GET

##### 23. Reading the path parameter

- `/users` -> `/{userId}` -> GET ->
- Integration Response -> status 200 -> Mapping Templates -> application/json

```json
{
  "userId": "$input.params('userId')",
  "firstName": "Art",
  "lastName": "Shyshkin"
}
```

- Test it
    - Test
    - Path -> {userId}: 123

```json
{
  "userId": "123",
  "firstName": "Art",
  "lastName": "Shyshkin"
}
```

##### 24. Query String Parameters

- `/users` -> GET ->
- Method Request -> URL Query String Parameters ->
- Add ->
    - `count`

##### 25. Reading Query String Parameter in a Mapping template

- `/users` -> GET ->
- Integration Response -> 200 -> Mapping Templates -> application/json

```json
{
  "users": [
    {
      "firstname": "Art",
      "lastname": "Shyshkin"
    },
    {
      "firstname": "Kate",
      "lastname": "Shyshkina"
    }
  ],
  "count": $input.params(
  'count'
  )
}
```

- Test it
    - `/users` -> GET ->
    - Query String -> count=3 -> Test -> OK

##### 26. Deploying Mock API

- Actions -> Deploy API
- Development Stage -> New Stage
    - Stage name: dev (any name)
    - Deploy
- Invoke:
    - Stages -> dev -> `/users` -> GET ->
    - Invoke URL: `https://bfvrrtg4l2.execute-api.eu-north-1.amazonaws.com/dev/users`
    - Use Postman or IntelliJ Http Client

##### 27. Documenting API

- API: Users Mock API -> Documentation
- Create Documentation Part ->
    - Type: Method
    - Path: /users
    - Method: GET

```json
{
  "description": "Returns a list of all users"
}
```

- Save
- Create Documentation Part ->
    - Type: Path Parameter
    - Path: /users/{userId}
    - Method: GET
    - Name: userId

```json
{
  "description": "The id of user to return. The value of userId is UUID."
}
```

- Save
- Resources
    - `/users` -> GET -> Book icon (View documentation) ->
    - Edit -> Save
- `/users` -> GET -> URL Query String Parameters -> count -> Book icon (Documentation button)

```json
{
  "description": "The 'count' Query String Parameter is used to limit the number of users to return."
}
```

- Documentation -> Already 3 parts
- Publish Documentation ->
    - Stage: dev
    - Version: 1.0
    - Description: First variant of documentation
    - Publish

#### Section 6: Exporting API

##### 29. Export API and Test with Swagger

- Deploy the latest changes
    - Resources -> Actions -> Deploy API
    - Deployment stage: dev
- Stages -> dev
    - Export
        - Export as `OpenAPI 3`
        - YAML -> Editor
- Go to [swagger.io](https://swagger.io/)
    - Tools -> [Swagger Editor](https://swagger.io/tools/swagger-editor/) ->
    - Live Demo -> Insert content of [exported documentation](docs/Users Mock API-dev-oas30.yaml)
- For me it does not return proper content as expected(((
    - return body is empty
    - curl works fine

##### 30. Export API with Gateway Extensions

- Stages -> dev
    - Export
        - Export as `Swagger`
        - Export as Swagger + API Gateway Extensions
        - YAML -> Editor -> copy
- APIs -> Create new API
    - Import from Swagger or Open API 3
    - Edge optimized
    - Import
- View new API
- Delete API

##### 31. Export API and Test with Postman

- Stages -> dev
    - Export
        - Export as `Swagger`
        - Export as Swagger + Postman Extensions
        - YAML -> Editor -> copy
- Postman
    - Import -> Raw text -> paste
    - Continue
    - Generate collection from imported APIs
    - Import
- Collections -> Users Mock API
    - GET `/users/:userId`

#### Section 7: Validating HTTP Request

##### 33. Validating Request Parameters & Headers

- `/users` -> GET -> Method Request ->
- Request Validator: `Validate query string parameters and headers`
- HTTP Request Headers
    - Add header -> Authorization -> tick
    - Required -> true
- Test it
    - set Request Params: count=2
    - headers -> do nothing
    - Test ->
        - 400 Bad Request
        - `Response Body`
        - `{`
        - `"message": "Missing required request parameters: [Authorization]"`
        - `}`
        - `Response Headers`
        - `{"x-amzn-ErrorType":"BadRequestException"}`
    - set header
        - `Authorization:FooBuzzBar
    - Test -> OK      `

##### 34. Validating Request Body - Creating a Model

- API: Users Mock API
- Models -> [JSON Schema](http://json-schema.org/)
- [Getting started](http://json-schema.org/learn/getting-started-step-by-step)
- Create
    - Model name: `CreateUserModel`
    - Content type: `application/json`
    - Model description: A model for create new user http POST request

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
  "required": [
    "firstName",
    "lastName",
    "email",
    "password",
    "repeatPassword"
  ]
}
```

- Create Model

##### 35. Associate Model with HTTP Request

- Resources -> `/users` -> Add Method -> POST
- Method Request ->
    - Request Validator: Validate body
    - Request Body -> Add Model
        - Content type: application/json
        - Model name: CreateUserModel

##### 36. Validating Request Body - Trying how it works

- Test it
- Empty request body -> Test
    - `Status: 400`
    - `Latency: 151 ms`
    - `Response Body`
    - `{`
    - `"message": "Invalid request body"`
    - `}`
    - `Response Headers`
    - `{"x-amzn-ErrorType":"BadRequestException"}`
    -
  Logs: `Mon Nov 29 13:31:04 UTC 2021 : Request body does not match model schema for content type application/json: [Unknown error parsing request body]`
- Some properties provided (firstname and lastName)
    - Http Response is the same, Logs differs
    - `Mon Nov 29 13:31:51 UTC 2021 : Request body does not match model schema for content type application/json: [object has missing required properties (["email","password","repeatPassword"])]`
- All required properties -> OK

```json
{
  "firstName": "Art",
  "lastName": "Shyshkin",
  "email": "d.art.shishkin@gmail.com",
  "password": "123",
  "repeatPassword": "321"
}
```

#### Section 8: Introduction to Lambda

##### 39. Anatomy of a Java Lambda function handler

Request interface

```java
public interface RequestHandler<I, O> {
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

#### Section 9: Create Java Lambda using Maven and IDE

##### 49. Creating function in AWS Console

- Lambda Console -> Create function -> Author from scratch
- Function Name: GetUser
- Runtime: Java 11 (Corretto)
- Create a new role with basic Lambda permissions

##### 50. Deploying Lambda function

- `mvn clean package`
- Lambda -> Upload JAR
- Runtime settings -> Edit
    - Handler: `net.shyshkin.study.aws.serverless.photoapp.users.GetUserHandler`

##### 51. Testing Lambda function with Input Template

- Test
- Template: apigateway-aws-proxy
- Name: GetUserTemplate
- Change

```
  "pathParameters": {
    "proxy": "/path/to/resource",
    "userId": "123456"
  },
```

- Test -> OK

##### 52. Create new API project

- Name:  `Photo App Users API`

##### 53. Assigning Lambda function to API Endpoint

- create resource `/users`
- create sub-resource `/{userId}`
- create method GET
- Lambda
- Use Lambda Proxy integration: **true**
- Save -> Add permissions -> ok
- Test it -> with random userId

#### Section 10: AWS SAM - Tools to create & deploy Lambda functions

##### 59. Creating a new project with SAM

- `sam init`
- `1 - AWS Quick Start Templates`
- `1 - Zip (artifact is a zip uploaded to S3)`
- `5 - java11`
- `1 - maven`
- Project name: `photo-app-users-rest-api-sam`
- `1 - Hello World Example: Maven`

##### 67. Invoke Remote Lambda function from local computer

From directory where `template.yml` is located

- `sam build` - build and package application
- `sam local invoke CreateNewUserFunction --event events/event.json`

##### 68.1 Debug Lambda function locally

- `sam local invoke CreateNewUserFunction --event events/event.json -d 5858`
- `d` - debug port number
- IntelliJ -> Add Configuration -> New -> Remote JVM Debug
    - Name: SAM Local (any name)
    - Port: 5858
    - Use module classpath: PhotoAppUsersApi
- Add debug breakpoint
- Debug

##### 68.2 Run Lambda function locally

- `sam local start-api`
- curl

```shell
curl --location --request POST 'http://localhost:3000/users' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Nazar",
    "lastName": "Shyshkin"
}'
```

##### 69. Deploy Lambda function to AWS

For the first time run

- `sam deploy --guided`
    - Stack Name: `PhotoAppUsersRestApi`
    - AWS Region: just Enter
    - Confirm changes before deploy: y
    - Allow SAM CLI IAM role creation: Y
    - ... may not have authorization defined. Ok?: y
    - Save to config?: y
    - SAM configuration file: enter (use default)
    - SAM config environment [default]: enter
    - Enter
    - Confirm changes: y

##### 70. Invoke public API Endpoint

```shell
curl --location --request POST 'https://89sldcq35g.execute-api.eu-north-1.amazonaws.com/Prod/users/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Nazar",
    "lastName": "Shyshkin"
}'
```

##### 71. Viewing logs

1. CloudWatchLogs
2. SAM

- `sam logs --name CreateNewUserFunction --stack-name PhotoAppUsersRestApi`
- live logs
- `sam logs --name CreateNewUserFunction --stack-name PhotoAppUsersRestApi --tail`

##### 72. Delete AWS SAM application

- from `samconfig.toml` copy stack name: `PhotoAppUsersRestApi`
- `sam delete PhotoAppUsersRestApi`
    - Are you sure? -> yes,yes

#### Section 11: Data Transformations

##### 77. Creating a new Request Model

- deploy (`sam deploy --guided`)
- API Gateway console ->
    - `data-transformation-example`
    - Models -> Create Model
        - Name: `CreateUserRequestMappingTemplate`
        - Content type: application/json

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
  "required": [
    "firstName",
    "lastName",
    "email",
    "password",
    "repeatPassword"
  ]
}
```

##### 78. Creating a new Response Model

- Name: `CreateUserResponseMappingTemplate`

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "title": "Create User Response Model Schema",
  "type": "object",
  "properties": {
    "firstName": {
      "type": "string"
    },
    "lastName": {
      "type": "string"
    },
    "email": {
      "type": "string"
    },
    "id": {
      "type": "string",
      "description": "Unique user identifier"
    }
  }
}
```

#####  79. Transforming HTTP Request Payload

-  API Gateway -> Resources ->
-  `/users` -> POST -> Integration Request
-  Uncheck `Use Lambda Proxy Integration`
-  Mapping Template
  -  Request body passthrough: `When there are no templates defined (recommended)` 
-  Add mapping template
  -  application/json
  -  Generate template -> CreateUserRequestMappingTemplate
  -  [Apache Velocity Template Language](https://velocity.apache.org/engine/1.7/user-guide.html)
  -  [Working with models and mapping templates](https://docs.aws.amazon.com/apigateway/latest/developerguide/models-mappings.html)
  -  [API Gateway mapping template and access logging variable reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
    
```json
#set($inputRoot = $input.path('$'))
{
  "firstName" : "$inputRoot.userFirstName",
  "lastName" : "$inputRoot.userLastName",
  "email" : "$inputRoot.userEmail",
  "password" : "$inputRoot.userPassword",
  "repeatPassword" : "$inputRoot.userRepeatPassword",
  "age" : $inputRoot.userAge
}
```
Escaping JavaScript (` We recommend that you use $util.escapeJavaScript to sanitize the result to avoid a potential injection attack`)
```json
#set($inputRoot = $input.path('$'))
{
"firstName" : "$util.escapeJavaScript($inputRoot.userFirstName)",
"lastName" : "$util.escapeJavaScript($inputRoot.userLastName)",
"email" : "$util.escapeJavaScript($inputRoot.userEmail)",
"password" : "$util.escapeJavaScript($inputRoot.userPassword)",
"repeatPassword" : "$util.escapeJavaScript($inputRoot.userRepeatPassword)",
"age" : $inputRoot.userAge
}
```

#####  81. Trying how it works

-  API Gateway -> Resources -> `/users` -> POST -> Test
-  Request Body
```json
{
  "userFirstName": "Arina",
  "userLastName": "Shyshkina",
  "userEmail": "d.art.shishkin@gmail.com",
  "userPassword": "123",
  "userRepeatPassword": "321",
  "userAge": 11
}
```
-  `{"x-amzn-ErrorType":"InternalServerErrorException"}`
-  Method request body before transformations:  correct
-  Endpoint request body after transformations: correct
```
Wed Dec 01 09:40:57 UTC 2021 : Endpoint response body before transformations: {"firstName":"Arina","lastName":"Shyshkina","id":"23fde9aa-c690-4710-8d38-6710995dcb09","email":"d.art.shishkin@gmail.com"}
Wed Dec 01 09:40:57 UTC 2021 : Execution failed due to configuration error: Output mapping refers to an invalid method response: 200
Wed Dec 01 09:40:57 UTC 2021 : Method completed with status: 500
```

#####  82. Response Mapping Template

-  API Gateway
   -  for study purpose modify CreateUserResponseMappingTemplate
```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "title": "Create User Response Model Schema",
  "type": "object",
  "properties": {
    "userFirstName": {
      "type": "string"
    },
    "userLastName": {
      "type": "string"
    },
    "userEmail": {
      "type": "string"
    },
    "userId": {
      "type": "string",
      "description": "Unique user identifier"
    }
  }
}
```  
-  `/users` -> POST -> Integration Response -> Mapping Template -> application/json   
-  Generate template: `CreateUserResponseMappingTemplate`
```json
#set($inputRoot = $input.path('$'))
{
  "userFirstName" : "$inputRoot.firstName",
  "userLastName" : "$inputRoot.lastName",
  "userEmail" : "$inputRoot.email",
  "userId" : "$inputRoot.id"
}
```

#####  83. Configure Method Response

-  Method Response -> Add Response -> 200
-  Response Body -> Add Response Model -> CreateUserResponseMappingTemplate -> tick

#####  84. Trying how the Response Mapping

-  API Gateway -> Resources -> `/users` -> POST -> Test
-  Request Body
```json
{
  "userFirstName": "Arina",
  "userLastName": "Shyshkina",
  "userEmail": "d.art.shishkin@gmail.com",
  "userPassword": "123",
  "userRepeatPassword": "321",
  "userAge": 11
}
```
-  Status: 200
-  Latency: 621 ms
-  TResponse Headers:
  -  `{"X-Amzn-Trace-Id":"Root=1-61a74cdf-52cf8d8517414ae522eed2b0;Sampled=0","Content-Type":"application/json"}`
-  Actions -> Deploy API -> Prod
-  curl it




