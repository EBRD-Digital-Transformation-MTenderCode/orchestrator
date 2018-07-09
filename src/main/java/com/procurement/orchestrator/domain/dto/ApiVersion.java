package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ApiVersion {
    V_0_1("0.1");

    private static final Map<String, ApiVersion> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ApiVersion c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ApiVersion(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ApiVersion fromValue(final String value) {
        final ApiVersion constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(ApiVersion.class.getName(), value, Arrays.toString(values()));
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
