package com.store.tracker.exception;

import com.store.tracker.dto.ResponseEnvelope;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Translates exceptions into the standard {@link ResponseEnvelope} contract.
 *
 * <p>Client mistakes are reported as {@code 4xx} with a message that describes the
 * problem; unexpected failures are reported as a generic {@code 500} carrying a
 * reference id, while the full stack trace is logged server-side only.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles a missing visit (404).
     *
     * @param ex the raised exception
     * @return an error envelope
     */
    @ExceptionHandler(VisitNotFoundException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleVisitNotFound(VisitNotFoundException ex) {
        log.warn("visit_not_found message={}", ex.getMessage());
        return envelope(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles a second exit for the same visit (409).
     *
     * @param ex the raised exception
     * @return an error envelope
     */
    @ExceptionHandler(VisitAlreadyClosedException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleVisitAlreadyClosed(VisitAlreadyClosedException ex) {
        log.warn("visit_already_closed message={}", ex.getMessage());
        return envelope(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles an inverted date range (400).
     *
     * @param ex the raised exception
     * @return an error envelope
     */
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleInvalidDateRange(InvalidDateRangeException ex) {
        log.warn("invalid_date_range message={}", ex.getMessage());
        return envelope(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles a path or query parameter that cannot be converted to its target
     * type, such as a non-numeric visit id (400).
     *
     * @param ex the raised exception
     * @return an error envelope
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("type_mismatch parameter={}", ex.getName());
        return envelope(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'");
    }

    /**
     * Handles constraint violations raised outside the controller binding layer (400).
     *
     * @param ex the raised exception
     * @return an error envelope
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("constraint_violation message={}", ex.getMessage());
        return envelope(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
    }

    /**
     * Handles Bean Validation failures on a request body (400).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return validationFailure(fieldErrors(ex));
    }

    /**
     * Handles validation failures on method parameters such as path variables (400).
     */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getAllValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            String fallbackKey = parameterName == null ? "parameter" : parameterName;
            result.getResolvableErrors().forEach(error -> {
                // Cascaded body violations surface as field errors whose field name
                // ("purchasedItems") is far more useful than the parameter name.
                String key = error instanceof FieldError fieldError && !fieldError.getField().isEmpty()
                        ? fieldError.getField()
                        : fallbackKey;
                errors.putIfAbsent(key, error.getDefaultMessage());
            });
        });
        return validationFailure(errors);
    }

    /**
     * Handles a missing or unparseable request body (400).
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("unreadable_request_body message={}", ex.getMessage());
        return body(HttpStatus.BAD_REQUEST, "Malformed or missing request body");
    }

    /**
     * Handles an unsupported request content type (415).
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return body(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content type '" + ex.getContentType() + "' is not supported; use application/json");
    }

    /**
     * Handles a supported path addressed with the wrong HTTP method (405).
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (ex.getSupportedHttpMethods() != null) {
            responseHeaders.setAllow(ex.getSupportedHttpMethods());
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(responseHeaders)
                .body(ResponseEnvelope.error(
                        "HTTP method " + ex.getMethod() + " is not supported for this endpoint"));
    }

    /**
     * Handles an unknown path (404).
     */
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return body(HttpStatus.NOT_FOUND, "Resource not found");
    }

    /**
     * Handles a request for a path with no matching handler (404).
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return body(HttpStatus.NOT_FOUND, "Resource not found");
    }

    /**
     * Last-resort handler. Logs the failure with a reference id and returns a
     * generic message so internal details never reach the caller (500).
     *
     * @param ex the unexpected exception
     * @return an error envelope carrying the reference id
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseEnvelope<Void>> handleUnexpected(Exception ex) {
        String reference = UUID.randomUUID().toString();
        log.error("unhandled_exception reference={}", reference, ex);
        return envelope(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Reference: " + reference);
    }

    private static Map<String, String> fieldErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            errors.putIfAbsent(field, error.getDefaultMessage());
        });
        return errors;
    }

    private static ResponseEntity<Object> validationFailure(Map<String, String> errors) {
        String message = errors.isEmpty()
                ? "Validation failed"
                : "Validation error: " + String.join(", ", errors.keySet());
        return ResponseEntity.badRequest().body(new ResponseEnvelope<>(false, message, errors));
    }

    private static ResponseEntity<Object> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ResponseEnvelope.error(message));
    }

    private static ResponseEntity<ResponseEnvelope<Void>> envelope(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ResponseEnvelope.error(message));
    }
}
