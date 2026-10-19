package io.sevk.types;

import com.google.gson.annotations.SerializedName;

public enum WebhookEventCategory {
    @SerializedName("email")
    EMAIL,
    @SerializedName("subscription")
    SUBSCRIPTION,
    @SerializedName("broadcast")
    BROADCAST
}
