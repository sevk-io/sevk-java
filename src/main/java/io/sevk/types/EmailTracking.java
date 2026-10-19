package io.sevk.types;

public class EmailTracking {
    public Boolean open;
    public Boolean click;

    public EmailTracking open(Boolean open) {
        this.open = open;
        return this;
    }

    public EmailTracking click(Boolean click) {
        this.click = click;
        return this;
    }
}
