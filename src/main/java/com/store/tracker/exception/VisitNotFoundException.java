package com.store.tracker.exception;

/**
 * Thrown when a visit cannot be found by its ID.
 */
public class VisitNotFoundException extends RuntimeException {
    public VisitNotFoundException(String message) {
        super(message);
    }
}
