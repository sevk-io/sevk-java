package io.sevk.types;

import java.util.Date;

public class BroadcastStatus {
    public String id;
    public String name;
    public BroadcastLifecycleStatus status;
    public Integer totalContacts;
    public Integer emailsSent;
    public Integer failedEmails;
    public Integer pendingEmails;
    public Date createdAt;
    public Date sentAt;
    public Date completedAt;
    public Date scheduledAt;
    public Date sendingStartedAt;
    public String step;
    public String stepLabel;
    public Integer stepIndex;
    public Integer stepTotal;
    public Double stepProgress;
    public BroadcastStatusWarning warning;
    public BroadcastStatusProgress progress;
    public BroadcastStatusSchedule schedule;
    public BroadcastStatusCompletion completion;
}
