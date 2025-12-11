package io.sevk.types;

import java.util.Date;
import java.util.Map;

/**
 * Represents a contact in Sevk.
 */
public class Contact {
    private String id;
    private String email;
    private boolean subscribed;
    private Map<String, Object> metadata;
    private Date createdAt;
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
