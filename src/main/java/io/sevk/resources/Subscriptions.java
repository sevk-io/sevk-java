package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

public class Subscriptions {
    private final SevkClient client;

    public Subscriptions(SevkClient client) {
        this.client = client;
    }

    public void subscribe(SubscribeRequest req) {
        client.post("/subscriptions/subscribe", req, Void.class);
    }

    public void unsubscribe(UnsubscribeRequest req) {
        client.post("/subscriptions/unsubscribe", req, Void.class);
    }
}
