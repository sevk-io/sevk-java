package io.sevk.types;

public class UpdateScheduledEmailRequest {
    public String scheduledAt;

    public UpdateScheduledEmailRequest scheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
        return this;
    }
}
