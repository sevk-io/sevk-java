package io.sevk.types;

public class OutboundUsage {
    public Integer monthlyEmailLimit;
    public Integer adminMonthlyEmailLimit;
    public Integer effectiveMonthlyEmailLimit;
    public int monthlyEmailsUsed;
    public int limitEmailsUsed;
    public Integer monthlyRemaining;
    public Integer rateLimit;
    public FreeEmailsBucket freeEmails;
    public String resetAt;
    public String pausedAt;
    public String pausedReason;
}
