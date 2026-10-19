package com.store.tracker.exception;

/**
 * Thrown when a caller supplies an entry-time range whose lower bound is after
 * its upper bound.
 */
public class InvalidDateRangeException extends RuntimeException {

    /**
     * @param from the exclusive-or-equal lower bound supplied by the caller
     * @param to   the inclusive upper bound supplied by the caller
     */
    public InvalidDateRangeException(Object from, Object to) {
        super("Invalid date range: 'from' (" + from + ") must be before or equal to 'to' (" + to + ")");
    }
}
