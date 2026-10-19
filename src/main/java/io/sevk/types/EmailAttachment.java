package io.sevk.types;

public class EmailAttachment {
    public String filename;
    public String content;
    public String contentType;
    public String cid;

    public EmailAttachment filename(String filename) {
        this.filename = filename;
        return this;
    }

    public EmailAttachment content(String content) {
        this.content = content;
        return this;
    }

    public EmailAttachment contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public EmailAttachment cid(String cid) {
        this.cid = cid;
        return this;
    }
}
