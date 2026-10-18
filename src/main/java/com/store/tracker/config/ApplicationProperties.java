package com.store.tracker.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Type-safe binding for the application's own configuration under the {@code app}
 * prefix.
 *
 * <p>Binding fails fast at startup when a required value is missing or blank, so
 * the application never runs with half-configured credentials.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final Admin admin = new Admin();

    /**
     * @return the administrative credentials group; never {@code null}
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * Authentication details for the single administrative account.
     */
    public static class Admin {

        @NotBlank
        private String username = "admin";

        @NotBlank
        private String password;

        /**
         * @return the administrator's username
         */
        public String getUsername() {
            return username;
        }

        /**
         * @param username the administrator's username; must not be blank
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return the administrator's plain-text password, hashed once at startup
         */
        public String getPassword() {
            return password;
        }

        /**
         * @param password the administrator's plain-text password; must not be blank
         */
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
