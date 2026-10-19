package io.sevk.types;

import java.util.Date;

public class BroadcastEmail {
    public String id;
    public String messageId;
    public String to;
    public String subject;
    public EmailStatus status;
    public EmailType type;
    public Date openedAt;
    public Date clickedAt;
    public Date unsubscribedAt;
    public Date createdAt;
    public BroadcastEmailContact contact;
}
