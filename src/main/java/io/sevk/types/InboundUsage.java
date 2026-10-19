package io.sevk.types;

public class InboundUsage {
    public int emailsCount;
    public Integer monthlyEmailLimit;
    public int monthlyEmailsUsed;
    public Integer monthlyRemaining;
    public FreeEmailsBucket freeEmails;
    public String resetAt;
    public String pausedAt;
    public String pausedReason;
}
