package com.quanta.bom.shared;

public enum RecordState {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    RecordState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
