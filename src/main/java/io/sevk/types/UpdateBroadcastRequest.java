package io.sevk.types;

import java.util.List;

public class UpdateBroadcastRequest {
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
    private transient boolean audienceIdSet;
    private transient boolean topicIdSet;
    private transient boolean segmentIdSet;
    private transient boolean replyToSet;
    private transient boolean scheduledAtSet;
    private transient boolean openTrackingSet;
    private transient boolean clickTrackingSet;
    private transient boolean variantsSet;
    private transient boolean abTestSet;

    public UpdateBroadcastRequest name(String name) {
        this.name = name;
        return this;
    }

    public UpdateBroadcastRequest subject(String subject) {
        this.subject = subject;
        return this;
    }

    public UpdateBroadcastRequest body(String body) {
        this.body = body;
        return this;
    }

    public UpdateBroadcastRequest style(ContentStyle style) {
        this.style = style;
        return this;
    }

    public UpdateBroadcastRequest targetType(BroadcastTargetType targetType) {
        this.targetType = targetType;
        return this;
    }

    public UpdateBroadcastRequest audienceId(String audienceId) {
        this.audienceId = audienceId;
        this.audienceIdSet = true;
        return this;
    }

    public UpdateBroadcastRequest topicId(String topicId) {
        this.topicId = topicId;
        this.topicIdSet = true;
        return this;
    }

    public UpdateBroadcastRequest segmentId(String segmentId) {
        this.segmentId = segmentId;
        this.segmentIdSet = true;
        return this;
    }

    public UpdateBroadcastRequest senderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public UpdateBroadcastRequest email(String email) {
        this.email = email;
        return this;
    }

    public UpdateBroadcastRequest replyTo(String replyTo) {
        this.replyTo = replyTo;
        this.replyToSet = true;
        return this;
    }

    public UpdateBroadcastRequest replyToInbound(Boolean replyToInbound) {
        this.replyToInbound = replyToInbound;
        return this;
    }

    public UpdateBroadcastRequest domainId(String domainId) {
        this.domainId = domainId;
        return this;
    }

    public UpdateBroadcastRequest includeListUnsubscribeHeader(Boolean v) {
        this.includeListUnsubscribeHeader = v;
        return this;
    }

    public UpdateBroadcastRequest scheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
        this.scheduledAtSet = true;
        return this;
    }

    public UpdateBroadcastRequest openTracking(Boolean openTracking) {
        this.openTracking = openTracking;
        this.openTrackingSet = true;
        return this;
    }

    public UpdateBroadcastRequest clickTracking(Boolean clickTracking) {
        this.clickTracking = clickTracking;
        this.clickTrackingSet = true;
        return this;
    }

    public UpdateBroadcastRequest variants(List<VariantInput> variants) {
        this.variants = variants;
        this.variantsSet = true;
        return this;
    }

    public UpdateBroadcastRequest abTest(AbTestConfigInput abTest) {
        this.abTest = abTest;
        this.abTestSet = true;
        return this;
    }

    public boolean isAudienceIdSet() {
        return audienceIdSet;
    }

    public boolean isTopicIdSet() {
        return topicIdSet;
    }

    public boolean isSegmentIdSet() {
        return segmentIdSet;
    }

    public boolean isReplyToSet() {
        return replyToSet;
    }

    public boolean isScheduledAtSet() {
        return scheduledAtSet;
    }

    public boolean isOpenTrackingSet() {
        return openTrackingSet;
    }

    public boolean isClickTrackingSet() {
        return clickTrackingSet;
    }

    public boolean isVariantsSet() {
        return variantsSet;
    }

    public boolean isAbTestSet() {
        return abTestSet;
    }
}
