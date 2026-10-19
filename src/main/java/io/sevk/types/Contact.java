package io.sevk.types;

import java.util.Date;
import java.util.Map;

/**
 * Represents a contact in sevk.
 */
public class Contact {
    private String id;
    private String email;
    private boolean subscribed;
    private Map<String, Object> data;
    private Date createdAt;
    private Date updatedAt;
    private String projectId;
    private Date lastEventAt;
    private Date unsubscribedAt;
    private ContactUnsubscribeActor unsubscribedBy;
    private Boolean resubscriptionLocked;
    private Long bounceCount;
    private Date lastBouncedAt;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getProjectId() {
        return projectId;
    }

    public Date getLastEventAt() {
        return lastEventAt;
    }

    public Date getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public void setUnsubscribedAt(Date unsubscribedAt) {
        this.unsubscribedAt = unsubscribedAt;
    }

    public ContactUnsubscribeActor getUnsubscribedBy() {
        return unsubscribedBy;
    }

    public void setUnsubscribedBy(ContactUnsubscribeActor unsubscribedBy) {
        this.unsubscribedBy = unsubscribedBy;
    }

    public Boolean getResubscriptionLocked() {
        return resubscriptionLocked;
    }

    public void setResubscriptionLocked(Boolean resubscriptionLocked) {
        this.resubscriptionLocked = resubscriptionLocked;
    }

    public Long getBounceCount() {
        return bounceCount;
    }

    public void setBounceCount(Long bounceCount) {
        this.bounceCount = bounceCount;
    }

    public Date getLastBouncedAt() {
        return lastBouncedAt;
    }

    public void setLastBouncedAt(Date lastBouncedAt) {
        this.lastBouncedAt = lastBouncedAt;
    }
}
