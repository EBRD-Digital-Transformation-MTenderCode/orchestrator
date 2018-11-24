package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum DocGeneratorCommandType {

    GENERATE("generateACDoc");

    private static final Map<String, DocGeneratorCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final DocGeneratorCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    DocGeneratorCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static DocGeneratorCommandType fromValue(final String value) {
        final DocGeneratorCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(DocGeneratorCommandType.class.getName(), value, Arrays.toString(values()));
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
