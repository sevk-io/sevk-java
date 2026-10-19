package io.sevk.types;

import java.util.Map;

public class WebhookListResponse extends ListResponse<Webhook> {
    public Map<String, WebhookEventDetail> availableEvents;
}
