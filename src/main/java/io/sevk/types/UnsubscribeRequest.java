package io.sevk.types;

public class UnsubscribeRequest {
    public String email;
    public String contactId;
    public String broadcastId;

    public UnsubscribeRequest email(String email) {
        this.email = email;
        return this;
    }

    public UnsubscribeRequest contactId(String contactId) {
        this.contactId = contactId;
        return this;
    }

    public UnsubscribeRequest broadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
        return this;
    }
}
