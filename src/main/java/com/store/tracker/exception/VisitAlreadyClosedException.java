package com.store.tracker.exception;

/**
 * Thrown when an operation requires an open visit but the visit has already been
 * closed. A visit can be closed exactly once.
 */
public class VisitAlreadyClosedException extends RuntimeException {

    /**
     * @param visitId the identifier of the visit that is already closed
     */
    public VisitAlreadyClosedException(Long visitId) {
        super("Visit with ID " + visitId + " is already closed");
    }
}
