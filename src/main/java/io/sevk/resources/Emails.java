package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.markup.Renderer;
import io.sevk.types.BulkEmailItem;
import io.sevk.types.BulkEmailRequest;
import io.sevk.types.BulkEmailResponse;
import io.sevk.types.Email;
import io.sevk.types.SendEmailRequest;
import io.sevk.types.SendEmailResponse;
import io.sevk.types.ScheduledEmailMutationResponse;
import io.sevk.types.UpdateScheduledEmailRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Emails resource for sending emails.
 */
public class Emails {
    private final SevkClient client;

    public Emails(SevkClient client) {
        this.client = client;
    }

    private static SendEmailRequest wireEmail(SendEmailRequest req) {
        SendEmailRequest wire = new SendEmailRequest();
        wire.from = req.from;
        wire.to = req.to;
        wire.subject = req.subject;
        wire.html = req.markup != null && !req.markup.isEmpty() ? Renderer.render(req.markup) : req.html;
        wire.text = req.text;
        wire.replyTo = req.replyTo;
        wire.cc = req.cc;
        wire.bcc = req.bcc;
        wire.attachments = req.attachments;
        wire.tracking = req.tracking;
        wire.headers = req.headers;
        wire.scheduledAt = req.scheduledAt;

        return wire;
    }

    private static BulkEmailItem wireEmail(BulkEmailItem req) {
        BulkEmailItem wire = new BulkEmailItem();
        wire.from = req.from;
        wire.to = req.to;
        wire.subject = req.subject;
        wire.html = req.markup != null && !req.markup.isEmpty() ? Renderer.render(req.markup) : req.html;
        wire.text = req.text;
        wire.replyTo = req.replyTo;
        wire.cc = req.cc;
        wire.bcc = req.bcc;
        wire.attachments = req.attachments;
        wire.tracking = req.tracking;
        wire.headers = req.headers;

        return wire;
    }

    /**
     * Retrieves a sent email and its current delivery state.
     */
    public Email get(String id) {
        return client.get("/emails/" + id, Email.class);
    }

    /**
     * moves an unattempted scheduled email to a new delivery time.
     */
    public ScheduledEmailMutationResponse update(String id, UpdateScheduledEmailRequest request) {
        return client.patch("/emails/" + id, request, ScheduledEmailMutationResponse.class);
    }

    /**
     * cancels an unattempted scheduled email.
     */
    public ScheduledEmailMutationResponse cancel(String id) {
        return client.post("/emails/" + id + "/cancel", null, ScheduledEmailMutationResponse.class);
    }

    /**
     * Sends one transactional email, rendering local markup when supplied.
     */
    public SendEmailResponse send(SendEmailRequest request) {
        return send(request, null);
    }

    /**
     * Sends one transactional email with optional retry idempotency.
     */
    public SendEmailResponse send(SendEmailRequest request, SendOptions options) {
        return client.post("/emails", wireEmail(request), SendEmailResponse.class, idempotencyHeaders(options));
    }

    /**
     * Sends up to 100 transactional emails and returns per-item results.
     */
    public BulkEmailResponse sendBulk(BulkEmailRequest request) {
        return sendBulk(request, null);
    }

    /**
     * Sends a transactional email batch with optional retry idempotency.
     */
    public BulkEmailResponse sendBulk(BulkEmailRequest request, SendOptions options) {
        BulkEmailRequest wire = new BulkEmailRequest();

        if (request.emails == null) {
            wire.emails = null;
        } else {
            wire.emails = new ArrayList<>();

            for (BulkEmailItem email : request.emails) {
                wire.emails.add(email == null ? null : wireEmail(email));
            }
        }

        return client.post("/emails/bulk", wire, BulkEmailResponse.class, idempotencyHeaders(options));
    }

    private static Map<String, String> idempotencyHeaders(SendOptions options) {
        if (options == null || options.idempotencyKey == null) {
            return null;
        }

        return Collections.singletonMap("Idempotency-Key", options.idempotencyKey);
    }

    /**
     * Per-call options for send/sendBulk.
     */
    public static class SendOptions {
        public String idempotencyKey;

        /** Sets the key used to make a retried send safe. */
        public SendOptions idempotencyKey(String key) {
            this.idempotencyKey = key;

            return this;
        }
    }
}
