package io.sevk.types;

public class BroadcastRecipientEstimateRequest {
    public BroadcastTargetType targetType;
    public String audienceId;
    public String topicId;
    public String segmentId;

    public BroadcastRecipientEstimateRequest targetType(BroadcastTargetType targetType) {
        this.targetType = targetType;
        return this;
    }

    public BroadcastRecipientEstimateRequest audienceId(String audienceId) {
        this.audienceId = audienceId;
        return this;
    }

    public BroadcastRecipientEstimateRequest topicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public BroadcastRecipientEstimateRequest segmentId(String segmentId) {
        this.segmentId = segmentId;
        return this;
    }
}
