package io.sevk.types;

public class SegmentRule {
    public String field;
    public SegmentRuleOperator operator;
    public Object value;
    public String groupId;
    public LogicalOperator groupOperator;

    public SegmentRule field(String field) {
        this.field = field;
        return this;
    }

    public SegmentRule operator(SegmentRuleOperator operator) {
        this.operator = operator;
        return this;
    }

    public SegmentRule value(Object value) {
        this.value = value;
        return this;
    }

    public SegmentRule groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public SegmentRule groupOperator(LogicalOperator groupOperator) {
        this.groupOperator = groupOperator;
        return this;
    }
}
