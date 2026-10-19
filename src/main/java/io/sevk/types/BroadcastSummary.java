package io.sevk.types;

/** Broadcast projection returned by list, send, and cancel operations. */
public class BroadcastSummary extends BroadcastBase {
    public String estimatedCost;
    public String actualCost;
    public Long totalContacts;
    public Long sentCount;
    public Long failedCount;
    public Long deliveredCount;
    public Long openedCount;
    public Long clickedCount;
    public Long bouncedCount;
    public Long complainedCount;
    public Long unsubscribedCount;
}
