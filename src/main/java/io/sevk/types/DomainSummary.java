package io.sevk.types;

import java.util.Date;

/** non-dns domain metadata returned by list operations. */
public class DomainSummary {
    public String id;
    public String domain;
    public String email;
    public String senderName;
    public String region;
    public Boolean verified;
    public Boolean clickTracking;
    public Boolean openTracking;
    public TlsPolicy tlsPolicy;
    public Boolean inboundEnabled;
    public Boolean inboundVerified;
    public String inboundPrefix;
    public Date createdAt;
    public Date updatedAt;
    public String projectId;
}
