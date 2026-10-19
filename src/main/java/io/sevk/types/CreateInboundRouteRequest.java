package io.sevk.types;

public class CreateInboundRouteRequest {
    public String name;
    public String pattern;
    public String target;
    public Integer priority;
    public Boolean enabled;

    public CreateInboundRouteRequest name(String name) {
        this.name = name;
        return this;
    }

    public CreateInboundRouteRequest pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public CreateInboundRouteRequest target(String target) {
        this.target = target;
        return this;
    }

    public CreateInboundRouteRequest priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public CreateInboundRouteRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
