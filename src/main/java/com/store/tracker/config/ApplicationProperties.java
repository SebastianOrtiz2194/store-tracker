package com.store.tracker.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final Admin admin = new Admin();

    public Admin getAdmin() {
        return admin;
    }

    public static class Admin {

        @NotBlank
        private String username = "admin";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
