package io.sevk;

import io.sevk.resources.*;

/**
 * Sevk Java SDK - Official Java client for the Sevk email platform.
 *
 * <pre>{@code
 * Sevk sevk = new Sevk("your-api-key");
 *
 * // Send an email
 * SendEmailRequest request = new SendEmailRequest()
 *     .to("recipient@example.com")
 *     .from("sender@yourdomain.com")
 *     .subject("Hello!")
 *     .html("<h1>Hello World!</h1>");
 *
 * Email email = sevk.emails().send(request);
 * }</pre>
 */
public class Sevk {
    private final SevkClient client;
    private final Contacts contacts;
    private final Audiences audiences;
    private final Templates templates;
    private final Broadcasts broadcasts;
    private final Domains domains;
    private final Topics topics;
    private final Segments segments;
    private final Subscriptions subscriptions;
    private final Emails emails;

    /**
     * Create a new Sevk client with the given API key.
     *
     * @param apiKey Your Sevk API key
     */
    public Sevk(String apiKey) {
        this(apiKey, new SevkOptions());
    }

    /**
     * Create a new Sevk client with the given API key and options.
     *
     * @param apiKey  Your Sevk API key
     * @param options Configuration options
     */
    public Sevk(String apiKey, SevkOptions options) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new SevkException("API key is required", 401, "invalid_api_key");
        }

        this.client = new SevkClient(apiKey, options);
        this.contacts = new Contacts(client);
        this.audiences = new Audiences(client);
        this.templates = new Templates(client);
        this.broadcasts = new Broadcasts(client);
        this.domains = new Domains(client);
        this.topics = new Topics(client);
        this.segments = new Segments(client);
        this.subscriptions = new Subscriptions(client);
        this.emails = new Emails(client);
    }

    /**
     * Get the Contacts resource for managing contacts.
     *
     * @return Contacts resource
     */
    public Contacts contacts() {
        return contacts;
    }

    /**
     * Get the Audiences resource for managing audiences.
     *
     * @return Audiences resource
     */
    public Audiences audiences() {
        return audiences;
    }

    /**
     * Get the Templates resource for managing email templates.
     *
     * @return Templates resource
     */
    public Templates templates() {
        return templates;
    }

    /**
     * Get the Broadcasts resource for managing broadcasts.
     *
     * @return Broadcasts resource
     */
    public Broadcasts broadcasts() {
        return broadcasts;
    }

    /**
     * Get the Domains resource for managing domains.
     *
     * @return Domains resource
     */
    public Domains domains() {
        return domains;
    }

    /**
     * Get the Topics resource for managing topics.
     *
     * @return Topics resource
     */
    public Topics topics() {
        return topics;
    }

    /**
     * Get the Segments resource for managing segments.
     *
     * @return Segments resource
     */
    public Segments segments() {
        return segments;
    }

    /**
     * Get the Subscriptions resource for managing subscriptions.
     *
     * @return Subscriptions resource
     */
    public Subscriptions subscriptions() {
        return subscriptions;
    }

    /**
     * Get the Emails resource for sending emails.
     *
     * @return Emails resource
     */
    public Emails emails() {
        return emails;
    }
}
