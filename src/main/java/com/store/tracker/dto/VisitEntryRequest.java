package com.store.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record VisitEntryRequest(
    @NotBlank(message = "Person name cannot be blank") String personName
) {}
