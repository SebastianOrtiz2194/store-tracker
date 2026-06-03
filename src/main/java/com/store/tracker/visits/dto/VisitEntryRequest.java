package com.store.tracker.visits.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for registering a customer's entry into the store.
 *
 * @param personName the visitor's name; must not be blank
 */
public record VisitEntryRequest(
    @NotBlank(message = "Person name cannot be blank") String personName
) {}
