# Store Tracker

A Spring Boot service that records customer entries and exits at a retail store, captures the items purchased during each visit, and exposes a REST API for querying visit history.

The service is organized as a [Spring Modulith](https://spring.io/projects/spring-modulith) with a single `visits` bounded context. Module boundaries are enforced by ArchUnit on every build. A second bounded context can be extracted into its own service with minimal refactoring.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API](#api)
- [Testing](#testing)
- [Operational Notes](#operational-notes)
- [Roadmap](#roadmap)

## Tech Stack

- **Java 21** (LTS)
- **Spring Boot 3.2.3** — Web, Data JPA, Validation, Security, Actuator
- **Spring Data JPA** with H2 (dev) / PostgreSQL (prod)
- **Spring Security** — stateless HTTP Basic
- **Bean Validation** (Jakarta / JSR-380)
- **springdoc-openapi 2.3.0** — OpenAPI 3 / Swagger UI
- **JUnit 5**, **Mockito**, **AssertJ**, **MockMvc**, `@DataJpaTest`
- **Maven** for build and dependency management

## Architecture

A [Spring Modulith](https://spring.io/projects/spring-modulith) with four logical modules verified at build time by `ModularityTests`:

```
Root  (TrackerApplication)
├── config      — security, JPA auditing, application properties
├── dto         — shared transport layer (ResponseEnvelope)
├── exception   — global error handling
└── visits      — bounded context: controller, service, repository, entity, DTO, mapper
```

Dependencies all flow inward: `visits` depends on `config`, `dto`, and `exception`. No module depends on another domain module, preventing cyclic coupling.

## Project Structure

```
src/main/java/com/store/tracker/
├── TrackerApplication.java
├── config/{ApplicationProperties, JpaConfig, SecurityConfig}.java
├── dto/{ResponseEnvelope}.java
├── exception/{GlobalExceptionHandler, VisitNotFoundException}.java
└── visits/
    ├── controller/VisitController.java
    ├── service/{VisitService, impl/VisitServiceImpl}.java
    ├── repository/VisitRepository.java
    ├── entity/{Visit, PurchasedItem}.java
    ├── dto/{PurchasedItemDto, VisitEntryRequest, VisitLeaveRequest, VisitResponse}.java
    └── mapper/{VisitMapper, PurchasedItemMapper}.java

src/test/java/com/store/tracker/
├── ModularityTests.java
└── visits/
    ├── controller/VisitControllerTest.java
    ├── repository/VisitRepositoryTest.java
    └── service/impl/VisitServiceImplTest.java
```

## Prerequisites

- JDK 21 or newer
- Maven 3.8 or newer

## Quick Start

### Build and test

```bash
mvn clean test
```

### Run in dev mode

In-memory H2 database, SQL logging enabled, H2 web console at `/h2-console`.

```bash
ADMIN_PASSWORD=devpass mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run in prod mode

Connects to a PostgreSQL instance configured via environment variables. See [Configuration](#configuration) for the full list.

```bash
ADMIN_PASSWORD=your-strong-password \
DB_URL=jdbc:postgresql://localhost:5432/store_tracker \
DB_USERNAME=tracker \
DB_PASSWORD=tracker-secret \
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Configuration

All credentials are environment-driven. There are no default secrets.

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `ADMIN_PASSWORD` | **yes** | — | Plain-text password for the `admin` user (BCrypt-hashed at startup) |
| `SPRING_PROFILES_ACTIVE` | no | `dev` | Active Spring profile |
| `DB_URL` | yes (prod) | `jdbc:postgresql://localhost:5432/store_tracker` | JDBC connection string |
| `DB_USERNAME` | yes (prod) | — | Database user |
| `DB_PASSWORD` | yes (prod) | — | Database password |
| `H2_PASSWORD` | no | `password` | H2 password (dev only) |
| `PORT` | no (prod) | `8080` | HTTP listen port |

### Profiles

- **`dev`** — H2 in-memory, SQL logging, H2 console enabled
- **`prod`** — PostgreSQL, `ddl-auto: validate`
- **`local`** — personal overrides via gitignored `application-local.yml`

## API

All endpoints live under `/api/visits` and require HTTP Basic auth. Responses follow the `ResponseEnvelope<T>` envelope.

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/visits/enter` | Register a customer entry |
| `PUT` | `/api/visits/{id}/leave` | Register exit with purchased items |
| `GET` | `/api/visits` | Full visit history |
| `GET` | `/api/visits/active` | Visitors currently inside the store |

### Response Envelope

```json
{
  "success": true,
  "message": "Entry registered successfully",
  "data": { }
}
```

Errors use the same envelope with `success: false` and a `null` data payload.

### Examples

**Register an entry**

```bash
curl -u admin:devpass -X POST http://localhost:8080/api/visits/enter \
  -H "Content-Type: application/json" \
  -d '{"personName":"Maria Gomez"}'
```

**Register an exit with purchases**

```bash
curl -u admin:devpass -X PUT http://localhost:8080/api/visits/1/leave \
  -H "Content-Type: application/json" \
  -d '{
    "purchasedItems": [
      {"name":"Coffee","price":3.5,"quantity":1},
      {"name":"Croissant","price":2.0,"quantity":2}
    ],
    "totalSpent": 7.5
  }'
```

**List active visits**

```bash
curl -u admin:devpass http://localhost:8080/api/visits/active
```

### Interactive Documentation

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI spec (JSON): <http://localhost:8080/api-docs>

## Testing

The project ships with a 21-test suite covering all three layers plus architecture enforcement:

| Test class | Slice | What it covers |
|------------|-------|----------------|
| `ModularityTests` | ArchUnit | Module boundary enforcement (no cycles, valid dependencies) |
| `VisitControllerTest` | `@WebMvcTest` | Endpoint behavior, request validation, security (401/404 paths) |
| `VisitServiceImplTest` | Mockito + AssertJ | Service logic, exception paths, list-returning methods |
| `VisitRepositoryTest` | `@DataJpaTest` | `findAll`, `findByExitTimeIsNull`, `save`, empty-list edges |

Run the full suite:

```bash
mvn test
```

## Operational Notes

- **H2 Console (dev only)** — <http://localhost:8080/h2-console>. JDBC URL `jdbc:h2:mem:storedb`, user `sa`, password from `H2_PASSWORD`.
- **Actuator health** — <http://localhost:8080/actuator/health>. Unauthenticated.
- **Postman collection** — `Store_Tracker_Postman_Collection.json` for quick API exploration. Update credentials to match the running instance.

## Roadmap

- [x] Spring Modulith dependencies and boundary-enforcement test
- [x] Visit domain moved into `com.store.tracker.visits` package
- [ ] Event-based communication between modules when a second bounded context emerges
- [ ] Extract a module into its own service

