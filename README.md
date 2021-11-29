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

   