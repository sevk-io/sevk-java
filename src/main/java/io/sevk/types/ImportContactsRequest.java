package io.sevk.types;

import java.util.List;

public class ImportContactsRequest {
    public List<ImportContactEntry> contacts;
    public String audienceId;
    public String topicId;

    public ImportContactsRequest contacts(List<ImportContactEntry> contacts) {
        this.contacts = contacts;
        return this;
    }

    public ImportContactsRequest audienceId(String audienceId) {
        this.audienceId = audienceId;
        return this;
    }

    public ImportContactsRequest topicId(String topicId) {
        this.topicId = topicId;
        return this;
    }
}
