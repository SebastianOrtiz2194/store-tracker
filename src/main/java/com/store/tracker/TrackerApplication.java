package com.store.tracker;

import com.store.tracker.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Application entry point. Bootstraps the Spring context and binds the
 * {@link ApplicationProperties} configuration class.
 */
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class TrackerApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments forwarded to Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(TrackerApplication.class, args);
    }
}
