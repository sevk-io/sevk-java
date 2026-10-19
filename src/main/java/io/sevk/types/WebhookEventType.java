package io.sevk.types;

import com.google.gson.annotations.SerializedName;

public enum WebhookEventType {
    @SerializedName("email.sent")
    EMAIL_SENT("email.sent"),
    @SerializedName("email.delivered")
    EMAIL_DELIVERED("email.delivered"),
    @SerializedName("email.opened")
    EMAIL_OPENED("email.opened"),
    @SerializedName("email.clicked")
    EMAIL_CLICKED("email.clicked"),
    @SerializedName("email.bounced")
    EMAIL_BOUNCED("email.bounced"),
    @SerializedName("email.complained")
    EMAIL_COMPLAINED("email.complained"),
    @SerializedName("email.rejected")
    EMAIL_REJECTED("email.rejected"),
    @SerializedName("email.delayed")
    EMAIL_DELAYED("email.delayed"),
    @SerializedName("email.rendering_failed")
    EMAIL_RENDERING_FAILED("email.rendering_failed"),
    @SerializedName("contact.subscribed")
    CONTACT_SUBSCRIBED("contact.subscribed"),
    @SerializedName("contact.unsubscribed")
    CONTACT_UNSUBSCRIBED("contact.unsubscribed"),
    @SerializedName("broadcast.started")
    BROADCAST_STARTED("broadcast.started"),
    @SerializedName("broadcast.finished")
    BROADCAST_FINISHED("broadcast.finished"),
    @SerializedName("email.received")
    EMAIL_RECEIVED("email.received");

    private final String wireValue;

    WebhookEventType(String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }
}
