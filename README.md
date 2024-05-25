Expose the following API's:
User registration API (Public) (done)
Admin API that returns all users (Authenticated with role admin only) (done)
Authentication API with username & encrypted password (Public) (done)
User Info API (Authenticated with role user only) (done)
Get Data API (Authenticated with role user only) (done)
Input: VIN (done)
This API will call two external services through REST (done)
Will log request in the DB and return a unique request ID (done)

Points to take care of
Application resiliency: Backend must be resilient in case external services are down or have errors. (done)
Authentication must use JWT (done)
Protect against API spamming (rate limiting) (done)
Auditing: be able to tell if an issue occurred with any external service. (done)
Application must be dockerized. (done)
Unit test (done)
Deliverable
Complete Spring Boot Application Code in github (done)
One page document outlining what was done and what needs to be done (done)
Can add whatever extra considerations that were not implemented :

due to time constraints 
Mapper for mapping DTO to Entity and vice versa especially for User and UserDTO and not returning password in UserDTO
some hard coded values in the code
admin user created in the code
application.properties file for configuration used the default did not create separate for dev, test and prod
Migration scripts for DB and Using Flyway or Liquibase
Swagger for API documentation
Logging using log4j2
Exception handling enhancement
Security enhancement
first time using rate limiting and resiliency in the code so not sure if it is implemented correctly

note: I have not added the above points in the code as I have not implemented them, 
but I am aware of them and can implement them if required.
```
