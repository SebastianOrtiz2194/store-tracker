# Store Tracker

Store Tracker is a microservice designed to manage store visits and purchases. It is built using Spring Boot and provides a RESTful API for seamless integration.

## Technologies

* Java 17
* Spring Boot 3.2.3
* Maven
* Security: Basic Authentication (Stateless)
* Database: H2 (Development) / External RDBMS (Production)
* Documentation: OpenAPI 3 / Swagger UI
* Testing: JUnit 5, Mockito, MockMvc, DataJpaTest

## Architecture

This project follows a standard layered architecture:
* Controllers for handling HTTP requests.
* Services for encapsulating business logic.
* Repositories for data access.

It also features centralized global exception handling using `@ControllerAdvice` and request payload validation via JSR-303 Bean Validation.

## Prerequisites

* Java JDK 17 or higher
* Maven 3.8+
* Git

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/SebastianOrtiz2194/store-tracker.git
   cd store-tracker
   ```

2. Compile and run tests:
   ```bash
   mvn clean test
   ```

3. Run the application:

   Development profile (uses in-memory H2 database):
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

   Production profile (requires external database configuration):
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
   ```

## Security

The service endpoints are protected by Basic Authentication. Default credentials for local testing:
* Username: admin
* Password: admin123

## API Documentation

Once the application is running, you can access the interactive Swagger UI documentation at:
http://localhost:8080/swagger-ui.html

## Monitoring

Basic health checks are available via Spring Boot Actuator:
http://localhost:8080/actuator/health

## Environment Variables (Production)

If running with the `prod` profile, ensure the following environment variables are set:
* `DB_URL`: Database connection string (e.g., PostgreSQL or MySQL)
* `DB_USERNAME`: Database user
* `DB_PASSWORD`: Database password
* `SERVER_PORT`: Server port (default is 8080)

## H2 Database Console

When running the `dev` profile, the H2 console is available for database inspection:
* URL: http://localhost:8080/h2-console
* JDBC URL: jdbc:h2:mem:testdb
* Username: sa
* Password: (leave blank)

## Testing with Postman

A Postman collection (`Store_Tracker_Postman_Collection.json`) is included in the root directory. You can import this into Postman to test the endpoints. Remember to configure Basic Auth with the credentials `admin` and `admin123` before executing the requests.
