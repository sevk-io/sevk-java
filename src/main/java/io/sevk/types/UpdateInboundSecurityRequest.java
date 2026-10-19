package io.sevk.types;

import java.util.List;

public class UpdateInboundSecurityRequest {
    public Boolean dmarcStrict;
    public Boolean spfRequired;
    public Boolean catchAllEnabled;
    public List<String> acceptedRecipients;
    public Long maxAttachmentSize;
    public List<String> allowedMimeTypes;
    private Double spamThreshold;
    private transient boolean spamThresholdPresent;

    public UpdateInboundSecurityRequest spamThreshold(double spamThreshold) {
        this.spamThreshold = spamThreshold;
        this.spamThresholdPresent = true;
        return this;
    }

    public UpdateInboundSecurityRequest clearSpamThreshold() {
        this.spamThreshold = null;
        this.spamThresholdPresent = true;
        return this;
    }

    public boolean hasSpamThreshold() {
        return spamThresholdPresent;
    }

    public Double getSpamThreshold() {
        return spamThreshold;
    }

    public UpdateInboundSecurityRequest dmarcStrict(Boolean dmarcStrict) {
        this.dmarcStrict = dmarcStrict;
        return this;
    }

    public UpdateInboundSecurityRequest spfRequired(Boolean spfRequired) {
        this.spfRequired = spfRequired;
        return this;
    }

    public UpdateInboundSecurityRequest catchAllEnabled(Boolean catchAllEnabled) {
        this.catchAllEnabled = catchAllEnabled;
        return this;
    }

    public UpdateInboundSecurityRequest maxAttachmentSize(Long maxAttachmentSize) {
        this.maxAttachmentSize = maxAttachmentSize;
        return this;
    }

    public UpdateInboundSecurityRequest acceptedRecipients(List<String> acceptedRecipients) {
        this.acceptedRecipients = acceptedRecipients;
        return this;
    }

    public UpdateInboundSecurityRequest allowedMimeTypes(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
        return this;
    }
}
