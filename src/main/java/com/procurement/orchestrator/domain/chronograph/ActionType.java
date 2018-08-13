package com.procurement.orchestrator.domain.chronograph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum ActionType {

    SCHEDULE("SCHEDULE"),
    CANCEL("CANCEL"),
    REPLACE("REPLACE");

    private static final Map<String, ActionType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ActionType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ActionType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActionType fromValue(final String value) {
        final ActionType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        }
        return constant;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
