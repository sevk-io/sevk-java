package io.sevk.types;

public class VariantInput {
    public String label;
    public String subject;
    public String body;
    public String fromName;

    public VariantInput label(String label) {
        this.label = label;
        return this;
    }

    public VariantInput subject(String subject) {
        this.subject = subject;
        return this;
    }

    public VariantInput body(String body) {
        this.body = body;
        return this;
    }

    public VariantInput fromName(String fromName) {
        this.fromName = fromName;
        return this;
    }
}
