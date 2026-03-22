package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Webhooks resource for managing webhooks.
 */
public class Webhooks {
    private final SevkClient client;

    public Webhooks(SevkClient client) {
        this.client = client;
    }

    /**
     * List all webhooks.
     */
    public List<Webhook> list() {
        return list(null);
    }

    /**
     * List webhooks with pagination.
     */
    public List<Webhook> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        WebhookListResponse response = client.get("/webhooks", queryParams, WebhookListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a webhook by ID.
     */
    public Webhook get(String id) {
        return client.get("/webhooks/" + id, Webhook.class);
    }

    /**
     * Create a new webhook.
     */
    public Webhook create(CreateWebhookRequest request) {
        return client.post("/webhooks", request, Webhook.class);
    }

    /**
     * Update a webhook.
     */
    public Webhook update(String id, UpdateWebhookRequest request) {
        return client.put("/webhooks/" + id, request, Webhook.class);
    }

    /**
     * Delete a webhook.
     */
    public void delete(String id) {
        client.delete("/webhooks/" + id);
    }

    /**
     * Test a webhook.
     */
    public WebhookTestResponse test(String id) {
        return client.post("/webhooks/" + id + "/test", null, WebhookTestResponse.class);
    }

    /**
     * List available webhook event types.
     */
    public List<WebhookEvent> listEvents() {
        WebhookEventsResponse response = client.get("/webhooks/events", WebhookEventsResponse.class);
        if (response == null || response.items == null) {
            return java.util.Collections.emptyList();
        }
        List<WebhookEvent> result = new java.util.ArrayList<>();
        for (String name : response.items) {
            WebhookEvent event = new WebhookEvent();
            event.name = name;
            if (response.events != null && response.events.containsKey(name)) {
                WebhookEventDetail detail = response.events.get(name);
                if (detail != null) {
                    event.description = detail.description;
                }
            }
            result.add(event);
        }
        return result;
    }
}
