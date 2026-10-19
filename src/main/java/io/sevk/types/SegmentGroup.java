package io.sevk.types;

public class SegmentGroup {
    public String id;
    public LogicalOperator operator;

    public SegmentGroup id(String id) {
        this.id = id;
        return this;
    }

    public SegmentGroup operator(LogicalOperator operator) {
        this.operator = operator;
        return this;
    }
}
