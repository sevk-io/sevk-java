package io.sevk.types;

import java.util.Map;

public class SubscribeRequest {
    public String email;
    public String audienceId;
    public Map<String, Object> data;

    public SubscribeRequest email(String email) {
        this.email = email;
        return this;
    }

    public SubscribeRequest audienceId(String audienceId) {
        this.audienceId = audienceId;
        return this;
    }

    public SubscribeRequest data(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
