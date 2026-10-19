package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.CreateWebhookRequest;
import io.sevk.types.CreatedWebhook;
import io.sevk.types.DeleteResponse;
import io.sevk.types.PaginationParams;
import io.sevk.types.RotateWebhookResponse;
import io.sevk.types.UpdateWebhookRequest;
import io.sevk.types.Webhook;
import io.sevk.types.WebhookEvent;
import io.sevk.types.WebhookEventDetail;
import io.sevk.types.WebhookEventType;
import io.sevk.types.WebhookEventsResponse;
import io.sevk.types.WebhookListResponse;
import io.sevk.types.WebhookTestResponse;
import java.util.ArrayList;
import java.util.Collections;
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

    public RotateWebhookResponse rotate(String id) {
        return client.post("/webhooks/" + id + "/rotate", null, RotateWebhookResponse.class);
    }

    /**
     * Lists configured webhooks and their recent delivery health.
     */
    public WebhookListResponse list() {
        return list(null);
    }

    public WebhookListResponse list(PaginationParams params) {
        Map<String, String> queryParams = new HashMap<>();

        if (params != null) {
            if (params.page != null) {
                queryParams.put("page", params.page.toString());
            }
            if (params.limit != null) {
                queryParams.put("limit", params.limit.toString());
            }
            if (params.after != null) {
                queryParams.put("after", params.after);
            }
            if (params.before != null) {
                queryParams.put("before", params.before);
            }
        }

        WebhookListResponse response = client.get("/webhooks", queryParams, WebhookListResponse.class);

        if (response == null) {
            response = new WebhookListResponse();
        }
        if (response.items == null) {
            response.items = Collections.emptyList();
        }

        return response;
    }

    /**
     * Retrieves a webhook and its recent delivery state.
     */
    public Webhook get(String id) {
        return client.get("/webhooks/" + id, Webhook.class);
    }

    /**
     * Creates a webhook and returns its usable whsec_ signing secret exactly once.
     */
    public CreatedWebhook create(CreateWebhookRequest request) {
        return client.post("/webhooks", request, CreatedWebhook.class);
    }

    /**
     * Updates a webhook while preserving omitted fields.
     */
    public Webhook update(String id, UpdateWebhookRequest request) {
        return client.put("/webhooks/" + id, request, Webhook.class);
    }

    /**
     * Deletes a webhook and stops future deliveries to its URL.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/webhooks/" + id, DeleteResponse.class);
    }

    /**
     * Sends a test event and returns the delivery result.
     */
    public WebhookTestResponse test(String id) {
        return client.post("/webhooks/" + id + "/test", null, WebhookTestResponse.class);
    }

    /**
     * Lists event names that can be assigned to a webhook.
     */
    public WebhookEventsResponse events() {
        return client.get("/webhooks/events", WebhookEventsResponse.class);
    }

    /** Flattens the webhook event catalog into event rows. */
    public List<WebhookEvent> eventItems() {
        WebhookEventsResponse response = events();
        if (response == null || response.items == null) {
            return Collections.emptyList();
        }

        List<WebhookEvent> result = new ArrayList<>();

        for (WebhookEventType name : response.items) {
            WebhookEvent event = new WebhookEvent();
            event.name = name;

            String eventName = name.wireValue();

            if (response.events != null && response.events.containsKey(eventName)) {
                WebhookEventDetail detail = response.events.get(eventName);

                if (detail != null) {
                    event.description = detail.description;
                    event.category = detail.category;
                }
            }

            result.add(event);
        }

        return result;
    }
}
