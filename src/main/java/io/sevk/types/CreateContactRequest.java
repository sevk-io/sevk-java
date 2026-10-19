package io.sevk.types;

import java.util.Map;

public class CreateContactRequest {
    public String email;
    public Boolean subscribed;
    public Map<String, Object> data;

    public CreateContactRequest email(String email) {
        this.email = email;
        return this;
    }

    public CreateContactRequest subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public CreateContactRequest data(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
