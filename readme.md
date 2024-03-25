# Statement Console Application
 This is a Java web application built with Spring Boot that allows users to view statements based on date and amount ranges, with authentication, access control, and database integration.

 ## Prerequisites
Before running the application, ensure you have the following installed:
```
Java 17
MySQL
Gradle
```
Setup Instructions
Clone the repository to your local machine:

```
git clone https://github.com/rinshadkv20/diffrenz-assignment.git
cd diffrenz-assignment
```
Update application.properties file located in src/main/resources with your MySQL database configurations:

properties
Copy code
```
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.username=db_username
spring.datasource.password=db_password
```
Build the project using Gradle:

```
./gradlew build
```
Running the Application
After building the project, you can run the application using the following Gradle command:

```
./gradlew bootRun
```
Once the application is running, you can access it in your web browser at http://localhost:8080.

### Initial Data Loading:
The application loads initial data into the database during every bootup from the data.sql file located in the [specify directory or provide a relative path].
Ensure that your database is configured properly to execute this script during bootup.
# Usage
Authentication
* Admin User: Username: admin, Password: admin,
* Regular User: Username: user, Password: user
* Regular User2: Username: user2, Password: user

### Viewing Statements
Admin Role: Can specify account number, date range, and amount range.
User Role: Can only specify date range. also can only see his account statement

#### Endpoints

GET api/statements: Endpoint for viewing statements.
Parameters:
* accountNumber: Optional. Specifies the account number.
* fromDate: Optional. Specifies the start date of the date range.
* toDate: Optional. Specifies the end date of the date range.
* amountFrom: Optional. Specifies the minimum amount.
* amountTo: Optional. Specifies the maximum amount.

#### Session Timeout
The session timeout is set to 5 minutes.

Logging and Exception Handling
Logging is implemented to track requests and errors.
Exceptions are handled gracefully with appropriate HTTP status codes.
Database Schema
The database schema scripts can be found in the database directory.

### Unit Tests
Unit test cases for critical components are located in the src/test directory.

