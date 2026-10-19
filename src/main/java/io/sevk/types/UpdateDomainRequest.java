package io.sevk.types;

public class UpdateDomainRequest {
    public String email;
    public String senderName;
    public Boolean clickTracking;
    public Boolean openTracking;
    public TlsPolicy tlsPolicy;
    public Boolean inboundEnabled;
    public String inboundPrefix;

    public UpdateDomainRequest inboundPrefix(String inboundPrefix) {
        this.inboundPrefix = inboundPrefix;
        return this;
    }

    public UpdateDomainRequest email(String email) {
        this.email = email;
        return this;
    }

    public UpdateDomainRequest senderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public UpdateDomainRequest clickTracking(Boolean clickTracking) {
        this.clickTracking = clickTracking;
        return this;
    }

    public UpdateDomainRequest openTracking(Boolean openTracking) {
        this.openTracking = openTracking;
        return this;
    }

    public UpdateDomainRequest tlsPolicy(TlsPolicy tlsPolicy) {
        this.tlsPolicy = tlsPolicy;
        return this;
    }

    public UpdateDomainRequest inboundEnabled(Boolean inboundEnabled) {
        this.inboundEnabled = inboundEnabled;
        return this;
    }
}
