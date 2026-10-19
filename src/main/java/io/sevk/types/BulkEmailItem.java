package io.sevk.types;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class BulkEmailItem {
    public String from;
    public String to;
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

    public BulkEmailItem to(String to) {
        this.to = to;
        return this;
    }

    public BulkEmailItem from(String from) {
        this.from = from;
        return this;
    }

    public BulkEmailItem subject(String subject) {
        this.subject = subject;
        return this;
    }

    public BulkEmailItem html(String html) {
        this.html = html;
        return this;
    }

    public BulkEmailItem text(String text) {
        this.text = text;
        return this;
    }

    public BulkEmailItem markup(String markup) {
        this.markup = markup;
        return this;
    }

    public BulkEmailItem replyTo(String replyTo) {
        this.replyTo = StringOrList.of(replyTo);
        return this;
    }

    public BulkEmailItem replyTo(List<String> replyTo) {
        this.replyTo = StringOrList.of(replyTo);
        return this;
    }

    public BulkEmailItem cc(String cc) {
        this.cc = StringOrList.of(cc);
        return this;
    }

    public BulkEmailItem cc(List<String> cc) {
        this.cc = StringOrList.of(cc);
        return this;
    }

    public BulkEmailItem bcc(String bcc) {
        this.bcc = StringOrList.of(bcc);
        return this;
    }

    public BulkEmailItem bcc(List<String> bcc) {
        this.bcc = StringOrList.of(bcc);
        return this;
    }

    public BulkEmailItem attachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public BulkEmailItem tracking(EmailTracking tracking) {
        this.tracking = tracking;
        return this;
    }

    public BulkEmailItem headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
}
