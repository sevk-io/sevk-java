package io.sevk.types;

public class UpdateInboundRouteRequest {
    public String name;
    public String pattern;
    public String target;
    public Integer priority;
    public Boolean enabled;

    public UpdateInboundRouteRequest name(String name) {
        this.name = name;
        return this;
    }

    public UpdateInboundRouteRequest pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public UpdateInboundRouteRequest target(String target) {
        this.target = target;
        return this;
    }

    public UpdateInboundRouteRequest priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public UpdateInboundRouteRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
