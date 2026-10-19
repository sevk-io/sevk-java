package io.sevk.types;

import java.util.List;

public class SendTestRequest {
    public List<String> emails;

    public SendTestRequest emails(List<String> emails) {
        this.emails = emails;
        return this;
    }
}
