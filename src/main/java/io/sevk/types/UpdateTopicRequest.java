package io.sevk.types;

public class UpdateTopicRequest {
    public String name;
    public String description;
    public UpdateTopicRequest name(String name) {
        this.name = name;
        return this;
    }

    public UpdateTopicRequest description(String description) {
        this.description = description;
        return this;
    }
}
