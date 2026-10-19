package io.sevk.types;

import java.util.Date;

public class InboundQuota {
    public String projectId;
    public Date createdAt;
    public Date updatedAt;
    public Integer monthlyEmailLimit;
    public int monthlyEmailsUsed;
    public Integer freeMonthlyInboundEmails;
    public int freeEmailsUsed;
    public double inboundEmailPrice;
    public Date resetAt;
    public Date pausedAt;
    public String pausedReason;
}
