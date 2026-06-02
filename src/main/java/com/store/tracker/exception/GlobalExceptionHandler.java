package com.store.tracker.exception;

import com.store.tracker.dto.ResponseEnvelope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler that ensures all error responses
 * follow the standard {@link ResponseEnvelope} format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles bean validation errors (@Valid).
     * Returns a map of failed fields and their error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseEnvelope<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = "Validation error: " + errors.keySet().stream().collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseEnvelope<>(false, message, errors));
    }

    /**
     * Handles VisitNotFoundException when a requested visit does not exist.
     */
    @ExceptionHandler(VisitNotFoundException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleVisitNotFoundException(VisitNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseEnvelope.error(ex.getMessage()));
    }

    /**
     * Catch-all handler for any unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseEnvelope.error("An unexpected error occurred: " + ex.getMessage()));
    }
}
