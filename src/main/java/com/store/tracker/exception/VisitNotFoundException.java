package com.store.tracker.exception;

/**
 * Excepción personalizada para cuando no se encuentra una visita por su ID.
 */
public class VisitNotFoundException extends RuntimeException {
    public VisitNotFoundException(String message) {
        super(message);
    }
}
