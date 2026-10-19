package io.sevk.types;

import java.util.List;

public class UpdateSegmentRequest {
    public String name;
    public List<SegmentRule> rules;
    public LogicalOperator operator;
    public List<String> limitToTopics;
    public List<SegmentGroup> groups;

    public UpdateSegmentRequest name(String name) {
        this.name = name;
        return this;
    }

    public UpdateSegmentRequest rules(List<SegmentRule> rules) {
        this.rules = rules;
        return this;
    }

    public UpdateSegmentRequest operator(LogicalOperator operator) {
        this.operator = operator;
        return this;
    }

    public UpdateSegmentRequest limitToTopics(List<String> limitToTopics) {
        this.limitToTopics = limitToTopics;
        return this;
    }

    public UpdateSegmentRequest groups(List<SegmentGroup> groups) {
        this.groups = groups;
        return this;
    }
}
