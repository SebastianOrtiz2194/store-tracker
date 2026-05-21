package com.store.tracker.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for registering a visitor's entry to the store.
 */
public class VisitEntryRequest {
    @NotBlank(message = "Person name cannot be blank")
    private String personName;

    public VisitEntryRequest() {}

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
}
