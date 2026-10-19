package io.sevk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.sevk.types.SegmentRule;
import io.sevk.types.SegmentRuleOperator;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Internal HTTP client for sevk API requests.
 */
public class SevkClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String USER_AGENT = "sevk-java/" + SdkVersion.VALUE;

    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final Gson gsonNulls;

    private static final TypeAdapterFactory NULL_PRESERVING_MAP_ADAPTER = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!Map.class.isAssignableFrom(type.getRawType())) {
                return null;
            }

            TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) {
                        delegate.write(out, null);
                        return;
                    }

                    boolean serializeNulls = out.getSerializeNulls();
                    out.setSerializeNulls(true);
                    try {
                        delegate.write(out, value);
                    } finally {
                        out.setSerializeNulls(serializeNulls);
                    }
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    return delegate.read(in);
                }
            };
        }
    };

    private static final TypeAdapterFactory SEGMENT_RULE_ADAPTER = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != SegmentRule.class) {
                return null;
            }

            TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }

                    SegmentRule rule = (SegmentRule) value;
                    out.beginObject();
                    out.name("field").value(rule.field);
                    out.name("operator");
                    gson.toJson(rule.operator, SegmentRuleOperator.class, out);
                    out.name("value");
                    if (rule.value == null) {
                        boolean serializeNulls = out.getSerializeNulls();
                        out.setSerializeNulls(true);
                        out.nullValue();
                        out.setSerializeNulls(serializeNulls);
                    } else {
                        gson.toJson(rule.value, rule.value.getClass(), out);
                    }
                    if (rule.groupId != null) {
                        out.name("groupId").value(rule.groupId);
                    }
                    if (rule.groupOperator != null) {
                        out.name("groupOperator");
                        gson.toJson(rule.groupOperator, rule.groupOperator.getClass(), out);
                    }
                    out.endObject();
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    return delegate.read(in);
                }
            };
        }
    };

    /**
     * Creates an authenticated JSON client used by the resource classes.
     *
     * @param apiKey  API key
     * @param options Configuration options
     */
    public SevkClient(String apiKey, SevkOptions options) {
        this.apiKey = apiKey;
        this.baseUrl = options.getBaseUrl().replaceAll("/+$", "");
        this.httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder().header("User-Agent", USER_AGENT).build()))
                .retryOnConnectionFailure(false)
                .followRedirects(false)
                .followSslRedirects(false)
                .callTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(options.getTimeout(), TimeUnit.MILLISECONDS)
                .build();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new UtcDateAdapter())
                .registerTypeAdapterFactory(NULL_PRESERVING_MAP_ADAPTER)
                .registerTypeAdapterFactory(SEGMENT_RULE_ADAPTER)
                .create();
        this.gsonNulls = new GsonBuilder()
                .registerTypeAdapter(Date.class, new UtcDateAdapter())
                .registerTypeAdapterFactory(NULL_PRESERVING_MAP_ADAPTER)
                .registerTypeAdapterFactory(SEGMENT_RULE_ADAPTER)
                .serializeNulls()
                .create();
    }

    String serializeBody(Object body) {
        if (body == null) {
            return "{}";
        }

        try {
            if (body instanceof JsonElement) {
                return gsonNulls.toJson((JsonElement) body);
            }

            return gson.toJson(body);
        } catch (IllegalArgumentException | JsonIOException | StackOverflowError error) {
            throw new SevkException("Request body must be JSON serializable", 0, "SERIALIZATION_ERROR", error);
        }
    }

    public JsonObject serializeObject(Object body) {
        try {
            return gson.toJsonTree(body).getAsJsonObject();
        } catch (IllegalArgumentException | JsonIOException | StackOverflowError error) {
            throw new SevkException("Request body must be JSON serializable", 0, "SERIALIZATION_ERROR", error);
        }
    }

    /**
     * Sends a GET request to an API path.
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
     * Sends a GET request with query parameters to an API path.
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
     * Sends a POST request with a JSON body.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T post(String path, Object body, Type responseType) {
        return post(path, body, responseType, null);
    }

    /**
     * Sends a POST request with a JSON body and additional request headers.
     */
    public <T> T post(String path, Object body, Type responseType, Map<String, String> extraHeaders) {
        String json = serializeBody(body);
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(requestBody);

        if (extraHeaders != null) {
            for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        return executeRequest(builder.build(), responseType);
    }

    /**
     * Sends a PUT request with a JSON body.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T put(String path, Object body, Type responseType) {
        String json = serializeBody(body);
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
     * Sends a PATCH request with a JSON body.
     *
     * @param path         API path
     * @param body         Request body
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T patch(String path, Object body, Type responseType) {
        String json = serializeBody(body);
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
     * Sends a DELETE request when no response body is expected.
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
     * Sends a DELETE request and parses its response body.
     *
     * @param path         API path
     * @param responseType Response type class
     * @param <T>          Response type
     * @return Response object
     */
    public <T> T delete(String path, Type responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .delete()
                .build();

        return executeRequest(request, responseType);
    }

    private <T> T executeRequest(Request request, Type responseType) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                handleError(response, responseBody);
            }

            if (responseType == Void.class) {
                return null;
            }
            if (response.code() != 204 && responseBody.trim().isEmpty()) {
                throw invalidResponse(response.code(), responseBody);
            }

            try {
                JsonElement root = response.code() == 204 ? new JsonObject() : JsonParser.parseString(responseBody);
                if (!root.isJsonObject()) {
                    throw invalidResponse(response.code(), responseBody);
                }
                T parsed = gson.fromJson(root, responseType);
                if (parsed == null) {
                    throw invalidResponse(response.code(), responseBody);
                }
                return parsed;
            } catch (JsonParseException error) {
                throw invalidResponse(response.code(), responseBody);
            }
        } catch (IOException e) {
            String code = e instanceof InterruptedIOException ? "TIMEOUT" : "NETWORK_ERROR";
            throw new SevkException(e.getMessage(), 0, code, e);
        }
    }

    private static SevkException invalidResponse(int statusCode, String responseBody) {
        return new SevkException(
                "Expected a JSON response body but received empty or malformed JSON",
                statusCode,
                "INVALID_RESPONSE",
                Map.of(),
                null,
                responseBody,
                null
        );
    }

    private void handleError(Response response, String responseBody) {
        throw buildError(response.code(), response.header("Retry-After"), responseBody);
    }

    static SevkException buildError(int responseStatus, String retryAfterHeader, String responseBody) {
        int statusCode = responseStatus;
        String message = null;
        String code = "UNKNOWN_ERROR";
        Map<String, List<String>> fields = new HashMap<>();
        JsonElement details = null;

        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            // Unwrap the nested `{ "error": { ... } }` envelope when present.
            if (root.has("error") && root.get("error").isJsonObject()) {
                root = root.getAsJsonObject("error");
            }
            if (root.has("message") && root.get("message").isJsonPrimitive()
                    && root.getAsJsonPrimitive("message").isString()) {
                message = root.get("message").getAsString();
            }
            if (root.has("code") && root.get("code").isJsonPrimitive()
                    && root.getAsJsonPrimitive("code").isString()) {
                code = root.get("code").getAsString();
            }
            if (root.has("details") && !root.get("details").isJsonNull()) {
                details = root.get("details").deepCopy();
            }
            if (root.has("fields") && root.get("fields").isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("fields").entrySet()) {
                    List<String> values = new ArrayList<>();
                    JsonElement value = entry.getValue();
                    if (value.isJsonArray()) {
                        for (JsonElement item : value.getAsJsonArray()) {
                            values.add(item.isJsonPrimitive() ? item.getAsString() : item.toString());
                        }
                    } else {
                        values.add(value.isJsonPrimitive() ? value.getAsString() : value.toString());
                    }
                    fields.put(entry.getKey(), values);
                }
            }
        } catch (Exception ignored) {
        }

        if (message == null || message.isEmpty()) {
            String trimmedBody = responseBody == null ? "" : responseBody.trim();
            message = trimmedBody.isEmpty() ? "HTTP " + statusCode : trimmedBody;
        }

        Integer retryAfter = parseRetryAfter(retryAfterHeader);

        return new SevkException(message, statusCode, code, fields, retryAfter, responseBody, details);
    }

    private static Integer parseRetryAfter(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (!trimmed.matches("\\d+")) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
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
