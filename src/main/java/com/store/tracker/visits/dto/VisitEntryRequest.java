package com.store.tracker.visits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for registering a customer's entry into the store.
 *
 * @param personName the visitor's name; must not be blank and at most 120 characters
 */
public record VisitEntryRequest(
    @NotBlank(message = "Person name cannot be blank")
    @Size(max = 120, message = "Person name must be at most 120 characters")
    String personName
) {}
