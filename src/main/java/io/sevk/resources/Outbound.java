package io.sevk.resources;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.sevk.SevkClient;
import io.sevk.types.OutboundQuota;
import io.sevk.types.UpdateOutboundQuotaRequest;

/**
 * Outbound resource. Read and adjust the project's monthly outbound
 * sending cap, and pause / resume transactional sending.
 *
 * Pause affects POST /emails, POST /emails/bulk, and SMTP relay.
 * Broadcasts run on a separate balance check and are NOT affected.
 */
public class Outbound {
    private final SevkClient client;

    public Outbound(SevkClient client) {
        this.client = client;
    }

    /**
     * Returns outbound quota settings and current usage counters.
     */
    public OutboundQuota getQuota() {
        return client.get("/outbound-settings/quota", OutboundQuota.class);
    }

    /**
     * Updates the monthly cap and clears a cap-exhausted pause when possible.
     */
    public OutboundQuota updateQuota(UpdateOutboundQuotaRequest request) {
        JsonObject body = new JsonObject();

        if (request != null) {
            if (request.monthlyEmailLimit == null) {
                body.add("monthlyEmailLimit", JsonNull.INSTANCE);
            } else {
                body.addProperty("monthlyEmailLimit", request.monthlyEmailLimit);
            }
        }

        return client.put("/outbound-settings/quota", body, OutboundQuota.class);
    }

    /**
     * Clears an outbound pause while preserving usage and the configured cap.
     */
    public OutboundQuota resetQuota() {
        return client.post("/outbound-settings/quota/reset", null, OutboundQuota.class);
    }

    /**
     * Resets custom-limit usage while preserving the cap and monthly usage.
     */
    public OutboundQuota resetLimit() {
        return client.post("/outbound-settings/quota/reset-limit", null, OutboundQuota.class);
    }

    /**
     * Pauses transactional sending until resetQuota is called.
     */
    public OutboundQuota pauseQuota() {
        return client.post("/outbound-settings/quota/pause", null, OutboundQuota.class);
    }
}
