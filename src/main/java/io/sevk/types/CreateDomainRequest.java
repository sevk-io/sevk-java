package io.sevk.types;

public class CreateDomainRequest {
    public String domain;
    public String email;
    public String senderName;
    public String region;

    public CreateDomainRequest domain(String domain) {
        this.domain = domain;
        return this;
    }

    public CreateDomainRequest email(String email) {
        this.email = email;
        return this;
    }

    public CreateDomainRequest senderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public CreateDomainRequest region(String region) {
        this.region = region;
        return this;
    }
}
