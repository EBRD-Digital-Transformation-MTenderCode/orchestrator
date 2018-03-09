package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Stage {
    EI("EI"),
    FS("FS"),
    PS("PS"),
    PQ("PQ"),
    EV("EV"),
    CT("CT"),
    PIN("PIN"),
    PN("PN");

    private static final Map<String, Stage> CONSTANTS = new HashMap<>();

    static {
        for (final Stage c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    Stage(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static Stage fromValue(final String value) {
        final Stage constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(
                    "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
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
