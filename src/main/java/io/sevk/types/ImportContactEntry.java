package io.sevk.types;

import java.util.Map;

public class ImportContactEntry {
    public String email;
    public Boolean subscribed;
    public Map<String, Object> data;

    public ImportContactEntry email(String email) {
        this.email = email;
        return this;
    }

    public ImportContactEntry subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public ImportContactEntry data(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
