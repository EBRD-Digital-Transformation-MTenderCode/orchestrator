package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Country {
    UA("UA"),
    MD("MD");

    private static final Map<String, Country> CONSTANTS = new HashMap<>();

    static {
        for (final Country c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    Country(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static Country fromValue(final String value) {
        final Country constant = CONSTANTS.get(value);
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
