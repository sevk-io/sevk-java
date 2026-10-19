package io.sevk.types;

public class UpdateAudienceRequest {
    public String name;
    public String description;
    public AudienceVisibility visibility;

    public UpdateAudienceRequest name(String name) {
        this.name = name;
        return this;
    }

    public UpdateAudienceRequest description(String description) {
        this.description = description;
        return this;
    }

    public UpdateAudienceRequest visibility(AudienceVisibility visibility) {
        this.visibility = visibility;
        return this;
    }
}
