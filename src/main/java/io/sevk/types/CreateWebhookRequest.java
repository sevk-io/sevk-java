package io.sevk.types;

import java.util.List;

public class CreateWebhookRequest {
    public String url;
    public List<WebhookEventType> events;
    public Boolean enabled;

    public CreateWebhookRequest url(String url) {
        this.url = url;
        return this;
    }

    public CreateWebhookRequest events(List<WebhookEventType> events) {
        this.events = events;
        return this;
    }

    public CreateWebhookRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
