package io.sevk.types;

import java.util.Map;

public class UpdateContactRequest {
    public String email;
    public Boolean subscribed;
    public Map<String, Object> data;
    private transient boolean dataSet;

    public UpdateContactRequest email(String email) {
        this.email = email;
        return this;
    }

    public UpdateContactRequest subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public UpdateContactRequest data(Map<String, Object> data) {
        this.data = data;
        this.dataSet = true;
        return this;
    }

    public boolean isDataSet() {
        return dataSet;
    }
}
