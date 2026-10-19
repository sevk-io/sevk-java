package io.sevk.types;

import java.util.List;
import java.util.Map;

public class WebhookEventsResponse {
    public List<WebhookEventType> items;
    public Map<String, WebhookEventDetail> events;
}
