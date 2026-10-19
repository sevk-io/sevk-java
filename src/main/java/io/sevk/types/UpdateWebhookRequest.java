package io.sevk.types;

import java.util.List;

public class UpdateWebhookRequest {
    public String url;
    public List<WebhookEventType> events;
    public Boolean enabled;

    public UpdateWebhookRequest url(String url) {
        this.url = url;
        return this;
    }

    public UpdateWebhookRequest events(List<WebhookEventType> events) {
        this.events = events;
        return this;
    }

    public UpdateWebhookRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
