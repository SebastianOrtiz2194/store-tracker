package com.store.tracker;

import com.store.tracker.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class TrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackerApplication.class, args);
    }
}
