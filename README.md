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

#####  85. Reading with Query String Parameters & Headers

-  API Gateway -> `/users` -> POST -> Integration Request
-  mapping Templates -> application/json
  -  append `"secretKey": "$util.escapeJavaScript($input.params('userSecretKey'))"` to template
-  Test it
  -  as **Header**
    -  `userSecretKey:123456header`
  -  as **RequestParameter**
    -  `userSecretKey=123456param`
  -  as **both** then
    -  got `"secretKey": "123456param"` -> Param has more privilege
  -  if neither header nor param have `userSecretKey`
    -  `"secretKey": ""`
-  `sam build`
-  `sam deploy`
-  `sam logs --name PostHandlerFunction --stack-name data-transformation-example --tail`
-  API Gateway -> Deploy API -> Prod
-  curl

####  Section 12: Error Responses

#####  87. Project source code(Proxy Integration)

#####  89. Customize the Default API Gateway Error Response

1. Make query string parameters required
    -  API Gateway -> URL Query String Parameters -> Add
        -  dividend -> required        
        -  divisor -> required
    -  Request Validator -> Validate Query String Parameters and Headers
2.  Test it
    -  Test without params
    -  `{"message": "Missing required request parameters: [divisor, dividend]"}`
3.  Gateway API -> Gateway Responses
    -  Default 4XX
    -  application/json
    -  Edit
    -  Response headers
        -  `art-app-header`: `'I know that you did something bad!'` - hardcoded STATIC_VALUE
        -  `art-app-error-msg`: `context.error.message` - context variables 
        -  `api-key`: `method.request.header.api-key` - incoming request parameter
    -  Response templates
        -  application/json
        -  Response template body
        -  use [docs](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
```json
{
  "message":$context.error.messageString,
  "responseType": "$context.error.responseType",
  "apiId": "$context.apiId",
  "method": "$context.httpMethod",
  "path": "$context.path",
  "time": "$context.requestTime",
  "stage": "$context.stage"
}
```    

#####  90. Trying how it works

1.  Test in Gateway Client
    -  with correct params: `dividend=2&divisor=3` -> OK
    -  with empty request params -> custom 400 error
2.  Deploy API -> to Prod
3.  Test in external client (Postman or IntelliJ)
    -  `GET https://v95tcmrgmd.execute-api.eu-north-1.amazonaws.com/Prod/divide?dividend=2&divisor=3` -> OK
    -  `GET https://v95tcmrgmd.execute-api.eu-north-1.amazonaws.com/Prod/divide` - 400
```
HTTP/1.1 400 Bad Request

x-amzn-RequestId: dd611f3d-7937-4380-8711-29975cda16d9
art-app-header: something wrong
art-app-error-msg: "Missing required request parameters: [divisor, dividend]"
x-amzn-ErrorType: BadRequestException
api-key: someCoolSecret
x-amz-apigw-id: JrtEUESLAi0F5hg=
X-Cache: Error from cloudfront
Via: 1.1 94faae20b0f122c4555025f52a2fd745.cloudfront.net (CloudFront)
X-Amz-Cf-Pop: FRA6-C1
X-Amz-Cf-Id: oEW1u9D_q0RgaZI8kyS6oxCfpG42legVyury3E_ZF9zPtZq5FfahtA==
```

```json
{
  "message": "Missing required request parameters: [divisor, dividend]",
  "responseType": "BAD_REQUEST_PARAMETERS",
  "apiId": "v95tcmrgmd",
  "method": "GET",
  "path": "/Prod/divide",
  "time": "01/Dec/2021:18:42:35 +0000",
  "stage": "Prod"
}
```

#####  91. Proxy Integration. Causing a 500 Server Error.

-  modify code to throw exceptions
-  deploy
-  invoke
    -  `dividend=6&divisor=2` -> ok
    -  `dividend=6&divisor=0` -> 500 {"errorMessage":"/ by zero","errorType":"java.lang.ArithmeticException"...
    -  `dividend=NAN&divisor=2` -> 500 {"errorMessage":"For input string: \"NAN\"","errorType":"java.lang.NumberFormatException"...
-  `sam logs -n DivisionExampleFunction --stack-name error-response-example --start-time "5mins ago" --tail`
-  customize error message
    -  API Gateway -> Gateway Responses -> 5XX -> customize like in #89

#####  95. Deploy and disable Proxy Integration

-  `sam build`
-  `sam deploy`
-  API Gateway -> `/divide` -> POST -> Integration Request
-  Use Lambda Proxy integration -> uncheck
-  To import changes to SAM:
    -  Deploy API -> Prod
    -  Export -> OpenAPI 3 ->
    -  Export as OpenAPI 3 + API Gateway Extensions
    -  `My Division REST API-Prod-oas30-apigateway.yaml`
    -  modify SAM template to include OpenAPI 3  
-  `sam delete`
-  `sam build`
-  `sam deploy` -> OK
-  **BUT**
-  URIs give wrong URLs of lambdas
    -  `uri: "arn:aws:apigateway:eu-north-1:lambda:path/2015-03-31/functions/arn:aws:lambda:eu-north-1:392971033516:function:error-response-example-DivisionExampleNonProxyFunc-2a7DMsQiu9kq/invocations"`
    -  `error-response-example-DivisionExampleNonProxyFunc-2a7DMsQiu9kq` does not match real -> need to be fixed

#####  96. Response code & Lambda Error Regex for 500 status code

-  API Gateway -> `/divide` -> POST -> Method Response
-  Add Responses:
    -  200, 500
-  Test
    -  with body: `{"dividend":"20","divisor":"3"}`
        -  OK
    -  with empty request body
        -  Status: 200
        -  "errorMessage": "Exception: null"
        -  "errorType": "net.shyshkin.study.aws.serverless.error.MyException"
-  Integration response
    -  Add 500
    -  Lambda Error RegEx: `.*Exception.*`

#####  97. Mapping template for 500 status code

-  Integration Response -> 500
-  Mapping Templates -> Add -> application/json
```json
{
  "errorMessage":$input.json('$.errorMessage'),
  "type":$input.json('$.errorType'),
  "requestTime": "$context.requestTime",
  "stackTrace":$input.json('$.stackTrace'),
}
```

#####  98. Lambda Error Regex for Different Error

-  Test with: `{"dividend":6,"divisor":0}`
    -  "errorMessage":"Exception: / by zero"
    -  modify Lambda Error Regex to `.*(by zero|Exception).*`
-  Test with: `{"dividend":"fff","divisor":2}`
    -  "errorMessage":"Exception: For input string: \"fff\"",
    -  "type":"net.shyshkin.study.aws.serverless.error.MyException"
    -  modify Lambda Error Regex to `.*(error|by zero|Exception).*`

#####  99. Handle multiple exceptions

-  Because we wrapped Exception in our own Exception
-  Simplify Lambda Error Regex to `Exception: .*`

####  12 Refactor data-transformation-example

#####  12.1 Create Request and Response Models

-  CreateUserRequestModel
```json
{
   "$schema": "http://json-schema.org/draft-04/schema#",
   "title": "Create User Model Request Schema",
   "type": "object",
   "properties": {
      "userFirstName": {
         "type": "string",
         "description": "User's First Name"
      },
      "userLastName": {
         "type": "string",
         "description": "User's Last Name"
      },
      "userEmail": {
         "type": "string",
         "description": "User's Email"
      },
      "userPassword": {
         "type": "string",
         "description": "User's Password"
      },
      "userRepeatPassword": {
         "type": "string",
         "description": "User's Password Repeat"
      },
      "userAge": {
         "type": "integer",
         "description": "User's age"
      }      
   },
   "required": [ "userFirstName", "userLastName", "userEmail", "userPassword", "userRepeatPassword" ]
}
```
-  CreateUserResponseModel
    -  copy from `CreateUserResponseMappingTemplate`
-  CreateUserLambdaInput
    -  copy from `CreateUserRequestMappingTemplate`
```json
{
   "$schema": "http://json-schema.org/draft-04/schema#",
   "title": "Create User Lambda Input Model Schema",
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
-  CreateUserLambdaOutput
```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "title": "Create User Output Lambda Model Schema",
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

#####  12.2 Provide New Models into API Gateway

-  CreateUserRequestModel
    -  Method Request -> Request Body -> Add model -> application/json -> CreateUserRequestModel
    -  Request Validator -> Validate body
-  CreateUserResponseModel
    -  Method Response -> 200
    -  Replace `CreateUserResponseMappingTemplate` with `CreateUserResponseModel`
-  Delete unused
    -  CreateUserRequestMappingTemplate
    -  CreateUserResponseMappingTemplate

#####  12.3 Generate OpenAPI 3 Specification

-  Deploy API -> Prod   
-  Export -> Export as OpenAPI 3 + API Gateway Extensions

#####  12.4 Include OpenAPI 3 Specification into SAM

-  add `AWS::Serverless::Api` resource to SAM template
-  resolve URIs in OpenAPI 3 document

#####  12.5 Test how it works

-  `sam delete`
-  `sam build`
-  `sam deploy`
-  curl -> OK
-  Missing `CreateUserLambdaInput` and `CreateUserLambdaOutput` in API Gateway Models 

####  Section 13: Lambda Function Versions

#####  103. Publishing a new version

-  before deploy new version
    -  `Code version Latest. Pred-production Deployment version: $LATEST`
-  deploy new version
-  Lambda console
    -  `lambda-functions-play-LambdaVersionFunction-6D8bnnn9xq7S`
    -  Versions -> `This function does not have any published versions.`
    -  Publish new version
-  Versions -> 1
    -  ARN with version: `:1`

#####  104. Assign new version to API endpoint

-  API Gateway console -> Integration Request
-  Lambda Function -> add `:1`
-  Test it in Gateway test client
    -  `Code version '1'. Pred-production Deployment version: 1`
-  Delete version:
    -  API Gateway console -> Integration Request -> Lambda Function -> edit
-  Test it in Gateway test client
    -  `Code version '1'. Pred-production Deployment version: $LATEST`

#####  105. Publish version #2

-  Make API Gateway use lambda version 1
-  Change Lambda Function source code
-  build deploy
-  Lambda -> Publish new version
    -  Description: Code version 2
-  Test in Gateway test client:
    -  `Code version '1'. Pred-production Deployment version: 1`
-  Test in curl (Prod stage)
    -  `Code version '2'. Pred-production Deployment version: $LATEST`
-  Change Integration Request to Lambda version 2
-  Test in Gateway client
    -  `Code version '2'. Pred-production Deployment version: 2`

####  Section 14: Lambda Function Aliases

#####  107. Creating an Alias

-  Lambda console -> Aliases -> 
-  Create alias
    -  Name: prod
    -  Description: Points to the function version that is used in production
    -  Version: 1
    -  Save
-  Function ARN ends with `:prod`
-  Create new alias
    -  Name: dev
    -  Description: Points to the $LATEST version of Lambda
    -  Version: $LATEST
    -  Save

#####  108. Using Alias in API Gateway

-  Integration Request
    -  make use of prod version
    -  test -> `Code version '1'. Pred-production Deployment version: 1`
-  Deploy API with `prod` alias to Prod Stage       
-  Integration Request
    -  make use of dev version
    -  test -> `Code version '2'. Pred-production Deployment version: $LATEST`
-  Deploy API with `dev` lambda to Dev Stage
    -  create new Stage -> `Dev`
    

