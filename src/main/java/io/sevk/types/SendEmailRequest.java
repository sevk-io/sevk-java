package io.sevk.types;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class SendEmailRequest {
    public String from;
    public StringOrList to;
    public String subject;
    public String html;
    public String text;
    public transient String markup;
    @SerializedName("reply_to")
    public StringOrList replyTo;
    public StringOrList cc;
    public StringOrList bcc;
    public List<EmailAttachment> attachments;
    public EmailTracking tracking;
    public Map<String, String> headers;
    public String scheduledAt;

    public SendEmailRequest to(String to) {
        this.to = StringOrList.of(to);
        return this;
    }

    public SendEmailRequest to(List<String> to) {
        this.to = StringOrList.of(to);
        return this;
    }

    public SendEmailRequest from(String from) {
        this.from = from;
        return this;
    }

    public SendEmailRequest subject(String subject) {
        this.subject = subject;
        return this;
    }

    public SendEmailRequest html(String html) {
        this.html = html;
        return this;
    }

    public SendEmailRequest text(String text) {
        this.text = text;
        return this;
    }

    public SendEmailRequest markup(String markup) {
        this.markup = markup;
        return this;
    }

    public SendEmailRequest replyTo(String replyTo) {
        this.replyTo = StringOrList.of(replyTo);
        return this;
    }

    public SendEmailRequest replyTo(List<String> replyTo) {
        this.replyTo = StringOrList.of(replyTo);
        return this;
    }

    public SendEmailRequest cc(String cc) {
        this.cc = StringOrList.of(cc);
        return this;
    }

    public SendEmailRequest cc(List<String> cc) {
        this.cc = StringOrList.of(cc);
        return this;
    }

    public SendEmailRequest bcc(String bcc) {
        this.bcc = StringOrList.of(bcc);
        return this;
    }

    public SendEmailRequest bcc(List<String> bcc) {
        this.bcc = StringOrList.of(bcc);
        return this;
    }

    public SendEmailRequest attachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public SendEmailRequest tracking(EmailTracking tracking) {
        this.tracking = tracking;
        return this;
    }

    public SendEmailRequest headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public SendEmailRequest scheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
        return this;
    }
}
