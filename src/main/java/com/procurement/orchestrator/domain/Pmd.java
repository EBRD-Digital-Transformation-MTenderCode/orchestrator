package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Pmd {

    RT("RT"),
    OT("OT"),
    TEST_RT("TEST_RT"),
    TEST_OT("TEST_OT");

    private static final Map<String, Pmd> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final Pmd c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    Pmd(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static Pmd fromValue(final String value) {
        final Pmd constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(Pmd.class.getName(), value, Arrays.toString(values()));
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
