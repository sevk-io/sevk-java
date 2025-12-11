package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

/**
 * Emails resource for sending emails.
 */
public class Emails {
    private final SevkClient client;

    public Emails(SevkClient client) {
        this.client = client;
    }

    /**
     * Send an email.
     */
    public Email send(SendEmailRequest request) {
        return client.post("/emails", request, Email.class);
    }
}
