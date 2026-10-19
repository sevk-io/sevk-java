package io.sevk;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exception thrown by sevk API operations.
 */
public class SevkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String code;
    private final HashMap<String, List<String>> fields;
    private final Integer retryAfter;
    private final String responseBody;
    private final String details;

    /**
     * Create a new SevkException.
     *
     * @param message    Error message
     * @param statusCode HTTP status code
     * @param code       Machine-readable error code (e.g. "NOT_FOUND", "VALIDATION_ERROR")
     */
    public SevkException(String message, int statusCode, String code) {
        this(message, statusCode, code, Collections.emptyMap(), null);
    }

    /**
     * Create a new SevkException with validation fields and a retry hint.
     *
     * @param message    Error message
     * @param statusCode HTTP status code
     * @param code       Machine-readable error code
     * @param fields     Field-level validation errors (422), field name -> messages
     * @param retryAfter Seconds to wait before retrying (Retry-After on a 429), or null
     */
    public SevkException(String message, int statusCode, String code, Map<String, List<String>> fields, Integer retryAfter) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
        this.fields = fields != null ? new HashMap<>(fields) : new HashMap<>();
        this.retryAfter = retryAfter;
        this.responseBody = null;
        this.details = null;
    }

    public SevkException(
        String message,
        int statusCode,
        String code,
        Map<String, List<String>> fields,
        Integer retryAfter,
        String responseBody,
        JsonElement details
    ) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
        this.fields = fields != null ? new HashMap<>(fields) : new HashMap<>();
        this.retryAfter = retryAfter;
        this.responseBody = responseBody;
        this.details = details == null ? null : details.toString();
    }

    /**
     * Create a new SevkException with a cause.
     *
     * @param message    Error message
     * @param statusCode HTTP status code
     * @param code       Machine-readable error code
     * @param cause      The cause of this exception
     */
    public SevkException(String message, int statusCode, String code, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.code = code;
        this.fields = new HashMap<>();
        this.retryAfter = null;
        this.responseBody = null;
        this.details = null;
    }

    /**
     * Get the HTTP status code.
     *
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the machine-readable error code from the API envelope.
     *
     * @return error code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the field-level validation errors (422), field name -> messages.
     *
     * @return validation fields (empty when not a validation error)
     */
    public Map<String, List<String>> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    /**
     * Get the Retry-After value in seconds from a 429 response, when present.
     *
     * @return retry-after seconds, or null
     */
    public Integer getRetryAfter() {
        return retryAfter;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public JsonElement getDetails() {
        return details == null ? null : JsonParser.parseString(details);
    }

    /**
     * Check if this is a not found error (404).
     *
     * @return true if not found
     */
    public boolean isNotFound() {
        return statusCode == 404;
    }

    /**
     * Check if this is an unauthorized error (401).
     *
     * @return true if unauthorized
     */
    public boolean isUnauthorized() {
        return statusCode == 401;
    }

    /**
     * Check if this is a forbidden error (403).
     *
     * @return true if forbidden
     */
    public boolean isForbidden() {
        return statusCode == 403;
    }

    /**
     * Check if this is a bad request error (400).
     *
     * @return true if bad request
     */
    public boolean isBadRequest() {
        return statusCode == 400;
    }

    /**
     * Check if this is a conflict error (409).
     *
     * @return true if conflict
     */
    public boolean isConflict() {
        return statusCode == 409;
    }

    /**
     * Check if this is a validation error (422, or a 400 carrying field errors).
     *
     * @return true if validation error
     */
    public boolean isValidationError() {
        return statusCode == 422 || (statusCode == 400 && ("VALIDATION_ERROR".equals(code) || !fields.isEmpty()));
    }

    /**
     * Check if this is a rate limit error (429).
     *
     * @return true if rate limited
     */
    public boolean isRateLimited() {
        return statusCode == 429;
    }

    /**
     * Check if this is a server error (5xx).
     *
     * @return true if server error
     */
    public boolean isServerError() {
        return statusCode >= 500 && statusCode < 600;
    }
}
