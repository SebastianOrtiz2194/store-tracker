# Store Tracker

A Spring Boot microservice that tracks customer visits to a retail store, recording entry and exit times along with purchased items. Built as a clean, layered REST API with proper validation, exception handling, and test coverage.

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA with H2 (dev) / external RDBMS (prod)
- Spring Security (stateless Basic Auth)
- Bean Validation (JSR-380)
- OpenAPI 3 / Swagger UI
- JUnit 5, Mockito, MockMvc, @DataJpaTest
- Maven

## Architecture

The project follows a standard three-layer structure:

```
Controller -> Service -> Repository
```

- **Controllers** handle HTTP requests and delegate to the service layer
- **Services** contain business logic and transaction boundaries
- **Repositories** manage data access through Spring Data JPA
- **Global exception handler** (`@ControllerAdvice`) ensures consistent error responses
- **Mapper** layer converts between entities and DTOs without external libraries

## Running Locally

### Prerequisites

- JDK 17+
- Maven 3.8+

### Build and Test

```bash
mvn clean test
```

### Run

Development mode (in-memory H2 database):

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Production mode (requires external database):

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/visits/enter` | Register a customer entry |
| PUT | `/api/visits/{id}/leave` | Register exit with purchased items |
| GET | `/api/visits` | Full visit history |
| GET | `/api/visits/active` | Visitors currently inside the store |

All endpoints require Basic Auth. Default credentials: `admin` / `admin123`

## Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

Health check: http://localhost:8080/actuator/health

## H2 Console (dev only)

When running with the `dev` profile:

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

## Postman Collection

Import `Store_Tracker_Postman_Collection.json` into Postman to test all endpoints. Configure Basic Auth with the default credentials before sending requests.

## Production Configuration

Set these environment variables when deploying with the `prod` profile:

| Variable | Description |
|----------|-------------|
| `DB_URL` | Database connection string |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `SERVER_PORT` | Server port (defaults to 8080) |
