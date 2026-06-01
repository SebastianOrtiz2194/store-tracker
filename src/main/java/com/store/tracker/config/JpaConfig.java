package com.store.tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing so that {@code @CreatedDate} and {@code @LastModifiedDate}
 * fields on entities (such as {@link com.store.tracker.entity.Visit}) are
 * populated automatically.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
