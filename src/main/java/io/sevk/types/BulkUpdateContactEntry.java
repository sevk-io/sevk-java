package io.sevk.types;

import java.util.Map;

public class BulkUpdateContactEntry {
    public String email;
    public Boolean subscribed;
    public Map<String, Object> data;
    private transient boolean dataSet;

    public BulkUpdateContactEntry email(String email) {
        this.email = email;
        return this;
    }

    public BulkUpdateContactEntry subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public BulkUpdateContactEntry data(Map<String, Object> data) {
        this.data = data;
        this.dataSet = true;
        return this;
    }

    public boolean isDataSet() {
        return dataSet;
    }
}
