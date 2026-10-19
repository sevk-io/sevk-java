package io.sevk.types;

import java.util.List;

public class BulkEmailRequest {
    public List<BulkEmailItem> emails;

    public BulkEmailRequest emails(List<BulkEmailItem> emails) {
        this.emails = emails;
        return this;
    }
}
