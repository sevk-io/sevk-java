package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Broadcasts resource for managing email broadcasts.
 */
public class Broadcasts {
    private final SevkClient client;

    public Broadcasts(SevkClient client) {
        this.client = client;
    }

    /**
     * List all broadcasts.
     */
    public List<Broadcast> list() {
        return list(null);
    }

    /**
     * List broadcasts with pagination.
     */
    public List<Broadcast> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        BroadcastListResponse response = client.get("/broadcasts", queryParams, BroadcastListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a broadcast by ID.
     */
    public Broadcast get(String id) {
        return client.get("/broadcasts/" + id, Broadcast.class);
    }
}
