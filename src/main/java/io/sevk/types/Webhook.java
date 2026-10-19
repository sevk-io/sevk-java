package io.sevk.types;

import java.util.Date;
import java.util.List;

public class Webhook {
    public String id;
    public String url;
    public List<WebhookEventType> events;
    public Boolean enabled;
    /** Masked placeholder (••••••••) outside the one-time create response. */
    public String secret;
    public Integer failureCount;
    public String lastFailureAt;
    public String lastDeliveryAt;
    public String disabledAt;
    public Date createdAt;
    public Date updatedAt;
    public String projectId;
}
