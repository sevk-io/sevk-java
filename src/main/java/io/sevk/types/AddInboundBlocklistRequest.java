package io.sevk.types;

public class AddInboundBlocklistRequest {
    public InboundBlocklistType type;
    public String value;
    public String reason;

    public AddInboundBlocklistRequest type(InboundBlocklistType type) {
        this.type = type;
        return this;
    }

    public AddInboundBlocklistRequest value(String value) {
        this.value = value;
        return this;
    }

    public AddInboundBlocklistRequest reason(String reason) {
        this.reason = reason;
        return this;
    }
}
