package io.sevk.types;

public class ActiveBroadcast {
    public String id;
    public String name;
    public BroadcastLifecycleStatus status;
    public int sentCount;
    public int failedCount;
    public int totalContacts;
    public String scheduledAt;
    public ActiveBroadcastAbTest abTest;
}
