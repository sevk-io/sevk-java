package io.sevk.types;

public class CreateAudienceRequest {
    public String name;
    public String description;
    public AudienceVisibility visibility;

    public CreateAudienceRequest name(String name) {
        this.name = name;
        return this;
    }

    public CreateAudienceRequest description(String description) {
        this.description = description;
        return this;
    }

    public CreateAudienceRequest visibility(AudienceVisibility visibility) {
        this.visibility = visibility;
        return this;
    }
}
