package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.SubscribeRequest;
import io.sevk.types.SubscribeResponse;
import io.sevk.types.UnsubscribeRequest;
import io.sevk.types.UnsubscribeResponse;

public class Subscriptions {
    private final SevkClient client;

    public Subscriptions(SevkClient client) {
        this.client = client;
    }

    /** Subscribes a contact and optionally adds it to an audience. */
    public SubscribeResponse subscribe(SubscribeRequest req) {
        return client.post("/subscriptions/subscribe", req, SubscribeResponse.class);
    }

    /** Globally unsubscribes a contact and optionally attributes the action. */
    public UnsubscribeResponse unsubscribe(UnsubscribeRequest req) {

        return client.post("/subscriptions/unsubscribe", req, UnsubscribeResponse.class);
    }
}
