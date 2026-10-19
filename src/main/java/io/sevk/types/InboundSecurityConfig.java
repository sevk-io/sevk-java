package io.sevk.types;

import java.util.Date;
import java.util.List;

public class InboundSecurityConfig {
    public String id;
    public String domainId;
    public Boolean dmarcStrict;
    public Boolean spfRequired;
    public Double spamThreshold;
    public Boolean catchAllEnabled;
    public List<String> acceptedRecipients;
    public Long maxAttachmentSize;
    public List<String> allowedMimeTypes;
    public Date createdAt;
    public Date updatedAt;
    public InboundSecurityConfigPlatform platform;
}
