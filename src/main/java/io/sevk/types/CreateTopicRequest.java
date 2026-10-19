package io.sevk.types;

public class CreateTopicRequest {
    public String name;
    public String description;
    public CreateTopicRequest name(String name) {
        this.name = name;
        return this;
    }

    public CreateTopicRequest description(String description) {
        this.description = description;
        return this;
    }
}
