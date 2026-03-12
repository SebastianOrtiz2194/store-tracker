package com.store.tracker.dto;

/**
 * DTO para la solicitud de entrada de una persona al establecimiento.
 */
public class VisitEntryRequest {
    private String personName;

    public VisitEntryRequest() {}

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
}
