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

    /**
     * Create a new broadcast.
     */
    public Broadcast create(CreateBroadcastRequest request) {
        return client.post("/broadcasts", request, Broadcast.class);
    }

    /**
     * Update a broadcast.
     */
    public Broadcast update(String id, UpdateBroadcastRequest request) {
        return client.put("/broadcasts/" + id, request, Broadcast.class);
    }

    /**
     * Delete a broadcast.
     */
    public void delete(String id) {
        client.delete("/broadcasts/" + id);
    }

    /**
     * Send a broadcast.
     */
    public Broadcast send(String id) {
        return client.post("/broadcasts/" + id + "/send", null, Broadcast.class);
    }

    /**
     * Send a broadcast with scheduling options.
     */
    public Broadcast send(String id, SendBroadcastRequest request) {
        return client.post("/broadcasts/" + id + "/send", request, Broadcast.class);
    }

    /**
     * Cancel a scheduled broadcast.
     */
    public Broadcast cancel(String id) {
        return client.post("/broadcasts/" + id + "/cancel", null, Broadcast.class);
    }

    /**
     * Send a test email for a broadcast.
     */
    public void sendTest(String id, SendTestRequest request) {
        client.post("/broadcasts/" + id + "/test", request, Void.class);
    }

    /**
     * Get broadcast analytics.
     */
    public BroadcastAnalytics getAnalytics(String id) {
        return client.get("/broadcasts/" + id + "/analytics", BroadcastAnalytics.class);
    }

    /**
     * Get broadcast status.
     */
    public BroadcastStatus getStatus(String id) {
        return client.get("/broadcasts/" + id + "/status", BroadcastStatus.class);
    }

    /**
     * Get broadcast emails.
     */
    public List<BroadcastEmail> getEmails(String id) {
        return getEmails(id, null);
    }

    /**
     * Get broadcast emails with pagination.
     */
    public List<BroadcastEmail> getEmails(String id, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
        }
        BroadcastEmailListResponse response = client.get("/broadcasts/" + id + "/emails", queryParams, BroadcastEmailListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Estimate broadcast cost.
     */
    public BroadcastCostEstimate estimateCost(String id) {
        return client.get("/broadcasts/" + id + "/estimate-cost", BroadcastCostEstimate.class);
    }

    /**
     * List active broadcasts.
     */
    public List<Broadcast> listActive() {
        BroadcastListResponse response = client.get("/broadcasts/active", BroadcastListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }
}
