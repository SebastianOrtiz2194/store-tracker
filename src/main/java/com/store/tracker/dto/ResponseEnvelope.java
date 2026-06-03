package com.store.tracker.dto;

/**
 * Standard envelope for every API response produced by the service.
 * Wraps the payload with a success flag and a human-readable message.
 * Response timing is conveyed by HTTP {@code Date} / {@code Last-Modified} headers.
 *
 * @param <T>     the type of the wrapped payload
 * @param success {@code true} when the operation succeeded, {@code false} otherwise
 * @param message a short human-readable description of the outcome
 * @param data    the response payload; {@code null} on error responses
 */
public record ResponseEnvelope<T>(
    boolean success,
    String message,
    T data
) {
    /**
     * Builds a successful response with the given payload and message.
     *
     * @param <T>     the payload type
     * @param data    the payload to wrap
     * @param message the success message
     * @return a new {@link ResponseEnvelope} marked as successful
     */
    public static <T> ResponseEnvelope<T> success(T data, String message) {
        return new ResponseEnvelope<>(true, message, data);
    }

    /**
     * Builds an error response carrying only a message.
     *
     * @param <T>     the (unused) payload type
     * @param message the error message
     * @return a new {@link ResponseEnvelope} marked as failed with a {@code null} payload
     */
    public static <T> ResponseEnvelope<T> error(String message) {
        return new ResponseEnvelope<>(false, message, null);
    }
}
