package io.sevk.types;

import java.util.Date;

public class Email {
    public String id;
    public String messageId;
    public String providerMessageId;
    public String from;
    public String to;
    public String subject;
    public String body;
    public String text;
    public EmailStatus status;
    public EmailType type;
    public String contactId;
    public String broadcastId;
    public String broadcastVariantId;
    public String projectId;
    public Date openedAt;
    public Date clickedAt;
    public Date unsubscribedAt;
    public Date scheduledAt;
    public Date cancelledAt;
    public Date createdAt;
    public Date updatedAt;
    public String userAgent;
}
