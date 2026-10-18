package com.store.tracker.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * Sets up stateless basic authentication for API endpoints.
 *
 * <p>Credentials are read from {@link ApplicationProperties} so that configuration
 * is bound once, validated at startup and shared with the rest of the context
 * instead of being resolved ad hoc.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(ApplicationProperties.class)
public class SecurityConfig {

    private final ApplicationProperties properties;

    /**
     * @param properties bound application configuration; must not be {@code null}
     */
    public SecurityConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    /**
     * Defines the stateless security filter chain: documentation and health
     * endpoints are public, everything else requires HTTP Basic authentication.
     *
     * @param http the security builder provided by Spring Security
     * @return the configured filter chain
     * @throws Exception if the filter chain cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (documentation and health checks)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Creates the in-memory administrator account, hashing the configured password.
     *
     * @param passwordEncoder the encoder used to hash the plain-text password
     * @return a user-details service holding the single administrator
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // In production, credentials would come from a database or LDAP
        ApplicationProperties.Admin admin = properties.getAdmin();
        UserDetails user = User.builder()
            .username(admin.getUsername())
            .password(passwordEncoder.encode(admin.getPassword()))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * @return the BCrypt password encoder used for all stored credentials
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
