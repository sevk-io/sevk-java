package io.sevk.types;

import java.util.Date;
import java.util.List;

public class Segment {
    public String id;
    public String name;
    public List<SegmentRule> rules;
    public LogicalOperator operator;
    public List<String> limitToTopics;
    public List<SegmentGroup> groups;
    public String audienceId;
    public Date createdAt;
    public Date updatedAt;
    public Date lastRunAt;
    public Long lastRunCount;
}
