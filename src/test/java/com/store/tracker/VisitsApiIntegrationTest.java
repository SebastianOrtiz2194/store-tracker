package com.store.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test that boots the whole application against a real PostgreSQL
 * database provided by Testcontainers.
 *
 * <p>It exists to close the gap left by the H2-based slice tests: dialect
 * differences, datasource wiring, security, JSON serialization and transaction
 * boundaries are only exercised together here. The test is skipped
 * automatically when no Docker daemon is available, so it never blocks a
 * developer working offline; CI runs it against the default PostgreSQL image.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class VisitsApiIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    void connectsToPostgresRatherThanTheInMemoryDatabase() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL()).startsWith("jdbc:postgresql:");
        }
    }

    @Test
    void registersEntryAndExitThenExposesTheVisitInHistory() {
        var client = restTemplate.withBasicAuth("admin", "test-password");

        ResponseEntity<Map> entry = client.postForEntity(
                "/api/visits/enter", Map.of("personName", "Ana Integration"), Map.class);
        assertThat(entry.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> entryData = data(entry);
        long visitId = ((Number) entryData.get("id")).longValue();
        assertThat(entryData.get("personName")).isEqualTo("Ana Integration");

        assertThat(activeVisits(client)).hasSize(1);

        var leaveRequest = Map.of(
                "purchasedItems", List.of(Map.of("name", "Coffee", "price", 3.5, "quantity", 2)));
        var jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> exit = client.exchange(
                "/api/visits/{id}/leave", HttpMethod.PUT,
                new HttpEntity<>(leaveRequest, jsonHeaders), Map.class, visitId);
        assertThat(exit.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Number) data(exit).get("totalSpent")).doubleValue()).isEqualTo(7.0);

        assertThat(activeVisits(client)).isEmpty();

        ResponseEntity<Map> secondExit = client.exchange(
                "/api/visits/{id}/leave", HttpMethod.PUT,
                new HttpEntity<>(leaveRequest, jsonHeaders), Map.class, visitId);
        assertThat(secondExit.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<Map> history = client.getForEntity(
                "/api/visits?from={from}", Map.class, LocalDateTime.now().minusDays(1).toString());
        assertThat(((Number) data(history).get("totalElements")).longValue()).isEqualTo(1);
        List<Object> visits = pageItems(history);
        assertThat(visits).anySatisfy(visit -> assertThat(((Number) ((Map<?, ?>) visit).get("id")).longValue())
                .isEqualTo(visitId));
    }

    @Test
    void rejectsRequestsWithoutCredentials() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/visits", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private static List<Object> activeVisits(TestRestTemplate client) {
        return items(client.getForEntity("/api/visits/active", Map.class));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> data(ResponseEntity<Map> response) {
        return (Map<String, Object>) response.getBody().get("data");
    }

    @SuppressWarnings("unchecked")
    private static List<Object> items(ResponseEntity<Map> response) {
        return (List<Object>) response.getBody().get("data");
    }

    @SuppressWarnings("unchecked")
    private static List<Object> pageItems(ResponseEntity<Map> response) {
        return (List<Object>) data(response).get("items");
    }
}
