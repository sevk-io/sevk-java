package io.sevk.types;

import java.util.Date;
import java.util.List;

public class InboundEmail {
    public String id;
    public String messageId;
    public String fromAddress;
    public String toAddress;
    public String domainId;
    public List<String> ccAddresses;
    public String subject;
    public InboundEmailStatus status;
    public Boolean isSpam;
    public Boolean isVirus;
    public Double spamScore;
    public Long rawSize;
    public Date receivedAt;
    public Date createdAt;
    public String inReplyTo;
    public List<String> references;
    public Object headers;
    public String projectId;
    public Date processedAt;
    public Date updatedAt;
    public String bodyText;
    public String bodyHtml;
    public List<InboundAttachment> attachments;
    public String replyToEmailId;
    public String replyToBroadcastId;
    public InboundReplyToEmailRef replyToEmail;
    public InboundReplyToBroadcastRef replyToBroadcast;
}
