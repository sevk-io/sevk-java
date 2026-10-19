package io.sevk.types;

import java.util.List;

public class CreateBroadcastRequest {
    public String name;
    public String subject;
    public String body;
    public ContentStyle style;
    public BroadcastTargetType targetType;
    public String audienceId;
    public String topicId;
    public String segmentId;
    public String senderName;
    public String email;
    public String replyTo;
    public Boolean replyToInbound;
    public String domainId;
    public String scheduledAt;
    public Boolean includeListUnsubscribeHeader;
    public Boolean openTracking;
    public Boolean clickTracking;
    public List<VariantInput> variants;
    public AbTestConfigInput abTest;

    public CreateBroadcastRequest name(String name) {
        this.name = name;
        return this;
    }

    public CreateBroadcastRequest subject(String subject) {
        this.subject = subject;
        return this;
    }

    public CreateBroadcastRequest body(String body) {
        this.body = body;
        return this;
    }

    public CreateBroadcastRequest style(ContentStyle style) {
        this.style = style;
        return this;
    }

    public CreateBroadcastRequest targetType(BroadcastTargetType targetType) {
        this.targetType = targetType;
        return this;
    }

    public CreateBroadcastRequest audienceId(String audienceId) {
        this.audienceId = audienceId;
        return this;
    }

    public CreateBroadcastRequest topicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public CreateBroadcastRequest segmentId(String segmentId) {
        this.segmentId = segmentId;
        return this;
    }

    public CreateBroadcastRequest senderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public CreateBroadcastRequest email(String email) {
        this.email = email;
        return this;
    }

    public CreateBroadcastRequest replyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public CreateBroadcastRequest replyToInbound(Boolean replyToInbound) {
        this.replyToInbound = replyToInbound;
        return this;
    }

    public CreateBroadcastRequest domainId(String domainId) {
        this.domainId = domainId;
        return this;
    }

    public CreateBroadcastRequest scheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
        return this;
    }

    public CreateBroadcastRequest includeListUnsubscribeHeader(Boolean v) {
        this.includeListUnsubscribeHeader = v;
        return this;
    }

    public CreateBroadcastRequest openTracking(Boolean openTracking) {
        this.openTracking = openTracking;
        return this;
    }

    public CreateBroadcastRequest clickTracking(Boolean clickTracking) {
        this.clickTracking = clickTracking;
        return this;
    }

    public CreateBroadcastRequest variants(List<VariantInput> variants) {
        this.variants = variants;
        return this;
    }

    public CreateBroadcastRequest abTest(AbTestConfigInput abTest) {
        this.abTest = abTest;
        return this;
    }
}
