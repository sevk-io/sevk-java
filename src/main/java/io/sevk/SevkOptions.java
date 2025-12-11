package io.sevk;

/**
 * Configuration options for the Sevk client.
 */
public class SevkOptions {
    private String baseUrl = "https://api.sevk.io";
    private int timeout = 30000;

    /**
     * Create default options.
     */
    public SevkOptions() {
    }

    /**
     * Get the base URL for API requests.
     *
     * @return Base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Set the base URL for API requests.
     *
     * @param baseUrl Base URL
     * @return this for chaining
     */
    public SevkOptions baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Get the request timeout in milliseconds.
     *
     * @return Timeout in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the request timeout in milliseconds.
     *
     * @param timeout Timeout in milliseconds
     * @return this for chaining
     */
    public SevkOptions timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
