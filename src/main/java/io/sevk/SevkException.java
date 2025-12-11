package io.sevk;

/**
 * Exception thrown by Sevk API operations.
 */
public class SevkException extends RuntimeException {
    private final int statusCode;
    private final String errorType;

    /**
     * Create a new SevkException.
     *
     * @param message    Error message
     * @param statusCode HTTP status code
     * @param errorType  Error type (e.g., "not_found", "validation_error")
     */
    public SevkException(String message, int statusCode, String errorType) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    /**
     * Create a new SevkException with a cause.
     *
     * @param message    Error message
     * @param statusCode HTTP status code
     * @param errorType  Error type
     * @param cause      The cause of this exception
     */
    public SevkException(String message, int statusCode, String errorType, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorType = errorType;
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
     * Get the error type.
     *
     * @return Error type
     */
    public String getErrorType() {
        return errorType;
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
     * Check if this is a validation error (422).
     *
     * @return true if validation error
     */
    public boolean isValidationError() {
        return statusCode == 422;
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
