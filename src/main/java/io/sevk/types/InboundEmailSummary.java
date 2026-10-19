package io.sevk.types;

import java.util.Date;

public class InboundEmailSummary {
    public String id;
    public String messageId;
    public String fromAddress;
    public String toAddress;
    public String subject;
    public InboundEmailStatus status;
    public Boolean isSpam;
    public Boolean isVirus;
    public Double spamScore;
    public Long rawSize;
    public String domainId;
    public String replyToEmailId;
    public String replyToBroadcastId;
    public Date receivedAt;
    public Date createdAt;
    public InboundReplyEmailSummary replyToEmail;
    public InboundReplyBroadcastSummary replyToBroadcast;
}
