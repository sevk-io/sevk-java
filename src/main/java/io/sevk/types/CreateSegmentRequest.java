package io.sevk.types;

import java.util.List;

public class CreateSegmentRequest {
    public String name;
    public List<SegmentRule> rules;
    public LogicalOperator operator;
    public List<String> limitToTopics;
    public List<SegmentGroup> groups;

    public CreateSegmentRequest name(String name) {
        this.name = name;
        return this;
    }

    public CreateSegmentRequest rules(List<SegmentRule> rules) {
        this.rules = rules;
        return this;
    }

    public CreateSegmentRequest operator(LogicalOperator operator) {
        this.operator = operator;
        return this;
    }

    public CreateSegmentRequest limitToTopics(List<String> limitToTopics) {
        this.limitToTopics = limitToTopics;
        return this;
    }

    public CreateSegmentRequest groups(List<SegmentGroup> groups) {
        this.groups = groups;
        return this;
    }
}
