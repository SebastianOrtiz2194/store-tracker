package com.store.tracker.exception;

/**
 * Thrown when a visit cannot be found by its ID.
 */
public class VisitNotFoundException extends RuntimeException {

    /**
     * @param message a human-readable description of the failure
     */
    public VisitNotFoundException(String message) {
        super(message);
    }

    /**
     * @param visitId the identifier that could not be resolved
     */
    public VisitNotFoundException(Long visitId) {
        super("Visit not found with ID: " + visitId);
    }
}
