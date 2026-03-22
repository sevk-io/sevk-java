package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Events resource for managing events.
 */
public class Events {
    private final SevkClient client;

    public Events(SevkClient client) {
        this.client = client;
    }

    /**
     * List all events.
     */
    public List<Event> list() {
        return list(null, null, null, null, null);
    }

    /**
     * List events with filters.
     */
    public List<Event> list(Integer page, Integer limit, String type, String from, String to) {
        Map<String, String> queryParams = new HashMap<>();
        if (page != null) queryParams.put("page", page.toString());
        if (limit != null) queryParams.put("limit", limit.toString());
        if (type != null) queryParams.put("type", type);
        if (from != null) queryParams.put("from", from);
        if (to != null) queryParams.put("to", to);
        EventListResponse response = client.get("/events", queryParams, EventListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * List events with ListParams.
     */
    public List<Event> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
        }
        EventListResponse response = client.get("/events", queryParams, EventListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get event stats.
     */
    public EventStats stats() {
        return client.get("/events/stats", EventStats.class);
    }
}
