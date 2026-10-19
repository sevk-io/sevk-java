package io.sevk.types;

import java.util.List;

public class PreviewSegmentRequest {
    public List<SegmentRule> rules;
    public LogicalOperator operator;
    public List<String> limitToTopics;
    public List<SegmentGroup> groups;

    public PreviewSegmentRequest rules(List<SegmentRule> rules) {
        this.rules = rules;
        return this;
    }

    public PreviewSegmentRequest operator(LogicalOperator operator) {
        this.operator = operator;
        return this;
    }

    public PreviewSegmentRequest limitToTopics(List<String> limitToTopics) {
        this.limitToTopics = limitToTopics;
        return this;
    }

    public PreviewSegmentRequest groups(List<SegmentGroup> groups) {
        this.groups = groups;
        return this;
    }
}
