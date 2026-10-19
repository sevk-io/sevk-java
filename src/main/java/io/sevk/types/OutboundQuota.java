package io.sevk.types;

import java.util.Date;

public class OutboundQuota {
    public String projectId;
    public Integer monthlyEmailLimit;
    public Integer adminMonthlyEmailLimit;
    public Integer effectiveMonthlyEmailLimit;
    public int monthlyEmailsUsed;
    public int limitEmailsUsed;
    public Integer monthlyRemaining;
    public Integer freeMonthlyEmails;
    public int freeEmailsUsed;
    public Integer rateLimit;
    public double emailPrice;
    public Date resetAt;
    public Date pausedAt;
    public String pausedReason;
    public Date createdAt;
    public Date updatedAt;
}
