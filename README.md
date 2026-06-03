# Store Tracker

> Spring Modulith tracking customer visits and purchases in retail stores.

A Spring Boot service that records customer entries and exits at a retail store, captures the items purchased during each visit, and exposes a small REST API for querying visit history and who's currently inside the store.

The project is currently organized as a layered monolith and is being migrated to a [Spring Modulith](https://spring.io/projects/spring-modulith): a single deployable with a clear, ArchUnit-enforced module boundary around the `visits` domain. The package layout reflects the first step of that migration, and a second bounded context (e.g. inventory, customers, billing) can be extracted into its own service with minimal refactoring.

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

The service follows a three-layer design:

```
HTTP Request
     │
     ▼
┌──────────────┐
│  Controller  │  VisitController  (@RestController, @Valid request bodies)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Service    │  VisitServiceImpl  (@Transactional boundaries, business rules)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Repository  │  VisitRepository  (Spring Data JPA)
└──────┬───────┘
       │
       ▼
   Database     H2 (dev) / PostgreSQL (prod)
```

Cross-cutting concerns:

- **`SecurityConfig`** — stateless Basic Auth. Swagger UI and `/actuator/health/**` are public; all other routes require authentication.
- **`GlobalExceptionHandler`** — `@ControllerAdvice` that maps validation and domain exceptions to the standard [`ApiResponse`](#api) envelope.
- **`VisitMapper` / `PurchasedItemMapper`** — hand-rolled entity↔DTO conversion. No MapStruct dependency.
- **`ApiResponse<T>`** — uniform JSON envelope with `success`, `message`, `data`.
- **`JpaConfig`** — enables JPA auditing so `@CreatedDate` and `@LastModifiedDate` are populated automatically.
- **`ApplicationProperties`** — type-safe configuration bound to the `app.*` prefix.

## Project Structure

```
src/main/java/com/store/tracker/
├── TrackerApplication.java        # entry point
├── config/
│   ├── ApplicationProperties.java # @ConfigurationProperties(prefix = "app")
│   ├── JpaConfig.java             # @EnableJpaAuditing
│   └── SecurityConfig.java        # basic auth, ADMIN_PASSWORD env
├── controller/
│   └── VisitController.java
├── service/
│   ├── VisitService.java          # interface
│   └── impl/VisitServiceImpl.java
├── repository/
│   └── VisitRepository.java
├── entity/
│   ├── Visit.java
│   └── PurchasedItem.java
├── dto/
│   ├── ApiResponse.java
│   ├── PurchasedItemDto.java
│   ├── VisitEntryRequest.java
│   ├── VisitLeaveRequest.java
│   └── VisitResponse.java
├── mapper/
│   ├── VisitMapper.java
│   └── PurchasedItemMapper.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── VisitNotFoundException.java

src/test/java/com/store/tracker/
├── controller/VisitControllerTest.java   # @WebMvcTest, 8 cases
├── service/impl/VisitServiceImplTest.java # Mockito + AssertJ, 7 cases
└── repository/VisitRepositoryTest.java   # @DataJpaTest, 5 cases
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

- **`dev`** — H2 in-memory, SQL logging, H2 console enabled, debug-level app logs
- **`prod`** — PostgreSQL, `ddl-auto: validate`, error messages hidden, info-level logs
- **`local`** — personal overrides layered on top of `dev`. Backed by a gitignored `application-local.yml`; see [Local Profile](#local-profile).

### Local Profile

For a fully IDE-driven dev loop without environment variables, create `src/main/resources/application-local.yml` (already covered by `.gitignore`) and activate the `local` profile in your run configuration:

- **Environment variable**: `SPRING_PROFILES_ACTIVE=local`
- **Program argument**: `--spring.profiles.active=local`

The file is auto-loaded by Spring Boot and may override any value that would otherwise come from the environment — `ADMIN_PASSWORD`, datasource credentials, H2 console settings, etc. Suggested starter content:

```yaml
ADMIN_PASSWORD: devpass

spring:
  datasource:
    password: local-dev-password
```

The CI workflow is unaffected because it supplies the same values via environment variables, which take priority over YAML. **Do not commit this file**; it is gitignored for a reason. Do not use its values in any deployed environment.

## API

All endpoints live under `/api/visits` and require HTTP Basic auth. Responses follow the [`ApiResponse<T>`](#response-envelope) envelope.

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

The project ships with a 20-test suite covering all three layers:

| Test class | Slice | What it covers |
|------------|-------|----------------|
| `VisitControllerTest` | `@WebMvcTest` | Endpoint behavior, request validation, security (401/404 paths) |
| `VisitServiceImplTest` | Mockito + AssertJ | Service logic, exception paths, list-returning methods |
| `VisitRepositoryTest` | `@DataJpaTest` | `findAll`, `findByExitTimeIsNull`, `save`, empty-list edges |

Run the full suite:

```bash
mvn test
```

## Operational Notes

- **H2 Console (dev only)** — <http://localhost:8080/h2-console>. JDBC URL `jdbc:h2:mem:storedb`, user `sa`, password from `H2_PASSWORD`.
- **Actuator health** — <http://localhost:8080/actuator/health>. Unauthenticated for orchestrator probes.
- **Postman collection** — `Store_Tracker_Postman_Collection.json` is included for quick API exploration. Update the Basic Auth password in Postman to match the `ADMIN_PASSWORD` you set at startup.
- **Architecture diagrams** — see [`docs/images/`](docs/images/) for the ER and high-level diagrams.

## Roadmap

The project is being incrementally evolved from a layered monolith to a **Spring Modulith** with a clear extraction path:

1. Add `spring-modulith` dependencies and a `ModularityTests` boundary-enforcement test.
2. Move the visit domain into its own package module (`com.store.tracker.visits`).
3. Once a second bounded context emerges (inventory, customers, billing, …), extract that module into its own service.
