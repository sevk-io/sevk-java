package io.sevk.types;

public class ProjectUsage {
    public String id;
    public String name;
    public Integer audienceLimit;
    public Integer contactLimit;
    public Integer broadcastLimit;
    public Integer domainLimit;
    public Integer webhookLimit;
    public String storageLimit;
    public String storageUsed;
    public String balance;
    public String outboundEmailPrice;
    public String inboundEmailPrice;
    public Integer monthlyOutboundEmailLimit;
    public Integer outboundRateLimit;
    public Integer freeMonthlyOutboundEmails;
    public Integer freeMonthlyInboundEmails;
    public Integer audiencesCount;
    public Integer contactsCount;
    public Integer broadcastsCount;
    public Integer monthlyBroadcastsUsed;
    public Integer domainsCount;
    public Integer webhooksCount;
    public int inboundEmailsCount;
    public InboundUsage inbound;
    public OutboundUsage outbound;
}
