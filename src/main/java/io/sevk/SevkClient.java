package io.sevk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Internal HTTP client for Sevk API requests.
 */
public class SevkClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;

    /**
     * Create a new SevkClient.
     *
     * @param apiKey  API key
     * @param options Configuration options
     */
    public SevkClient(String apiKey, SevkOptions options) {
        this.apiKey = apiKey;
        this.baseUrl = options.getBaseUrl();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .build();
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }

    /**
     * Make a GET request.
     *
     * @param path         API path
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T get(String path, Type responseType) {
        return get(path, null, responseType);
    }

    /**
     * Make a GET request with query parameters.
     *
     * @param path         API path
     * @param params       Query parameters
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T get(String path, Map<String, String> params, Type responseType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .get()
                .build();

        return executeRequest(request, responseType);
    }

    /**
     * Make a POST request.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T post(String path, Object body, Type responseType) {
        String json = body != null ? gson.toJson(body) : "{}";
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        return executeRequest(request, responseType);
    }

    /**
     * Make a PUT request.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T put(String path, Object body, Type responseType) {
        String json = body != null ? gson.toJson(body) : "{}";
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .put(requestBody)
                .build();

        return executeRequest(request, responseType);
    }

    /**
     * Make a PATCH request.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T patch(String path, Object body, Type responseType) {
        String json = body != null ? gson.toJson(body) : "{}";
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .patch(requestBody)
                .build();

        return executeRequest(request, responseType);
    }

    /**
     * Make a DELETE request.
     *
     * @param path API path
     */
    public void delete(String path) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .delete()
                .build();

        executeRequest(request, Void.class);
    }

    /**
     * Execute an HTTP request and handle the response.
     */
    private <T> T executeRequest(Request request, Type responseType) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                handleError(response.code(), responseBody);
            }

            if (responseType == Void.class || responseBody.isEmpty()) {
                return null;
            }

            return gson.fromJson(responseBody, responseType);
        } catch (IOException e) {
            throw new SevkException("Network error: " + e.getMessage(), 0, "network_error", e);
        }
    }

    /**
     * Handle API error responses.
     */
    private void handleError(int statusCode, String responseBody) {
        String message = "API error";
        String errorType = "api_error";

        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if (json.has("message")) {
                message = json.get("message").getAsString();
            }
            if (json.has("error")) {
                errorType = json.get("error").getAsString();
            }
        } catch (Exception ignored) {
            message = responseBody.isEmpty() ? "Unknown error" : responseBody;
        }

        // Include status code in message for easier testing
        String fullMessage = statusCode + ": " + message;
        throw new SevkException(fullMessage, statusCode, errorType);
    }

    /**
     * Get the Gson instance for JSON serialization.
     *
     * @return Gson instance
     */
    public Gson getGson() {
        return gson;
    }
}
