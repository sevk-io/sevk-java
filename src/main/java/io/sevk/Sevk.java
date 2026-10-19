package io.sevk;

import io.sevk.resources.Activity;
import io.sevk.resources.Audiences;
import io.sevk.resources.Broadcasts;
import io.sevk.resources.Contacts;
import io.sevk.resources.Domains;
import io.sevk.resources.Emails;
import io.sevk.resources.Inbound;
import io.sevk.resources.Outbound;
import io.sevk.resources.Segments;
import io.sevk.resources.Subscriptions;
import io.sevk.resources.Templates;
import io.sevk.resources.Topics;
import io.sevk.resources.Usage;
import io.sevk.resources.Webhooks;

/**
 * sevk Java SDK - Official Java client for the sevk email platform.
 *
 * <pre>{@code
 * Sevk sevk = new Sevk("sevk_your_api_key");
 *
 * // Send an email
 * SendEmailRequest request = new SendEmailRequest()
 *     .to("recipient@example.com")
 *     .from("sender@yourdomain.com")
 *     .subject("Hello!")
 *     .html("<h1>Hello World!</h1>");
 *
 * SendEmailResponse response = sevk.emails().send(request);
 * }</pre>
 */
public class Sevk {
    private final Contacts contacts;
    private final Audiences audiences;
    private final Templates templates;
    private final Broadcasts broadcasts;
    private final Domains domains;
    private final Topics topics;
    private final Segments segments;
    private final Subscriptions subscriptions;
    private final Emails emails;
    private final Webhooks webhooks;
    private final Activity activity;
    private final Inbound inbound;
    private final Outbound outbound;
    private final Usage usage;

    /**
     * Create a new sevk client with the given API key.
     *
     * @param apiKey Your sevk API key
     */
    public Sevk(String apiKey) {
        this(apiKey, new SevkOptions());
    }

    /**
     * Create a new sevk client with the given API key and options.
     *
     * @param apiKey  Your sevk API key
     * @param options Configuration options
     */
    public Sevk(String apiKey, SevkOptions options) {
        SevkClient client = new SevkClient(apiKey, options);
        this.contacts = new Contacts(client);
        this.audiences = new Audiences(client);
        this.templates = new Templates(client);
        this.broadcasts = new Broadcasts(client);
        this.domains = new Domains(client);
        this.topics = new Topics(client);
        this.segments = new Segments(client);
        this.subscriptions = new Subscriptions(client);
        this.emails = new Emails(client);
        this.webhooks = new Webhooks(client);
        this.activity = new Activity(client);
        this.inbound = new Inbound(client);
        this.outbound = new Outbound(client);
        this.usage = new Usage(client);
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

    /**
     * Get the Webhooks resource for managing webhooks.
     *
     * @return Webhooks resource
     */
    public Webhooks webhooks() {
        return webhooks;
    }

    /**
     * Get the Activity resource for retrieving the activity audit log.
     *
     * @return Activity resource
     */
    public Activity activity() {
        return activity;
    }

    /**
     * Get the Inbound resource for reading incoming mail and managing
     * inbound routes, blocklist, quota, and per-domain security.
     *
     * @return Inbound resource
     */
    public Inbound inbound() {
        return inbound;
    }

    /**
     * Get the Outbound resource for managing the project's outbound
     * monthly cap and pause / resume state.
     *
     * @return Outbound resource
     */
    public Outbound outbound() {
        return outbound;
    }

    /**
     * Get the Usage resource for reading project plan caps, balance,
     * outbound price, resource counters, and email send-limit stats.
     *
     * @return Usage resource
     */
    public Usage usage() {
        return usage;
    }

}
