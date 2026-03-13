package com.store.tracker.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de entrada de una persona al establecimiento.
 */
public class VisitEntryRequest {
    @NotBlank(message = "El nombre de la persona no puede estar vacío")
    private String personName;

    public VisitEntryRequest() {}

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
}
