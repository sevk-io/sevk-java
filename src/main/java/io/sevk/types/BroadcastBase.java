package io.sevk.types;

import java.util.Date;

/** Fields guaranteed on every persisted broadcast projection. */
public class BroadcastBase {
    public String id;
    public String name;
    public String subject;
    public String body;
    public ContentStyle style;
    public BroadcastLifecycleStatus status;
    public BroadcastTargetType targetType;
    public String audienceId;
    public String topicId;
    public String segmentId;
    public String email;
    public String senderName;
    public String replyTo;
    public Boolean replyToInbound;
    public String domainId;
    public Date scheduledAt;
    public Date sentAt;
    public Date completedAt;
    public String projectId;
    public Date createdAt;
    public Date updatedAt;
    public Date delivered;
    public Boolean includeListUnsubscribeHeader;
    public Boolean openTracking;
    public Boolean clickTracking;
    public BroadcastAudienceRef audience;
    public BroadcastTopicRef topic;
    public BroadcastSegmentRef segment;
    public BroadcastDomainRef domain;
}
