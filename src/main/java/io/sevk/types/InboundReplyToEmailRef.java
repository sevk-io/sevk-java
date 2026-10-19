package io.sevk.types;

import java.util.Date;

public class InboundReplyToEmailRef {
    public String id;
    public String messageId;
    public String subject;
    public String to;
    public String from;
    public EmailStatus status;
    public Date createdAt;
    public String broadcastId;
    public String contactId;
}
