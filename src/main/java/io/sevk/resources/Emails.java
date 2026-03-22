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
     * Get an email by ID.
     *
     * @param id Email ID
     * @return Email details
     */
    public Email get(String id) {
        return client.get("/emails/" + id, Email.class);
    }

    /**
     * Send an email with optional attachments.
     *
     * @param request Email request with optional attachments (max 10, 10MB total)
     * @return Email response with id or ids
     */
    public Email send(SendEmailRequest request) {
        return client.post("/emails", request, Email.class);
    }

    /**
     * Send multiple emails in bulk (max 100).
     *
     * @param request Bulk email request containing list of emails
     * @return Bulk email response with success/failed counts
     */
    public BulkEmailResponse sendBulk(BulkEmailRequest request) {
        return client.post("/emails/bulk", request, BulkEmailResponse.class);
    }
}
