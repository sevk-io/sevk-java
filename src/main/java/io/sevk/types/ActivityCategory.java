package io.sevk.types;

public enum ActivityCategory {
    EMAIL("EMAIL"),
    SUBSCRIPTION("SUBSCRIPTION"),
    ALL("all");

    private final String wireValue;

    ActivityCategory(String wireValue) {
        this.wireValue = wireValue;
    }

    public String wireValue() {
        return wireValue;
    }
}
