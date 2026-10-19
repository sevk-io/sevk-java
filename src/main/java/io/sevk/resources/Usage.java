package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.ProjectUsage;

/**
 * Usage resource. Read the project's plan caps, balance, outbound
 * price, current resource counts, and email send-limit stats.
 */
public class Usage {
    private final SevkClient client;

    public Usage(SevkClient client) {
        this.client = client;
    }

    /**
     * Returns current limits, resource counts, and email usage.
     */
    public ProjectUsage get() {
        return client.get("/limits", ProjectUsage.class);
    }
}
