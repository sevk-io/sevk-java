package io.sevk.types;

import java.util.List;

public class BulkUpdateContactRequest {
    public List<BulkUpdateContactEntry> contacts;

    public BulkUpdateContactRequest contacts(List<BulkUpdateContactEntry> contacts) {
        this.contacts = contacts;
        return this;
    }
}
