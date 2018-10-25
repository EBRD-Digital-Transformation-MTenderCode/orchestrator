package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum RegulationCommandType {

    GET_TERMS("getTerms");

    private static final Map<String, RegulationCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final RegulationCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RegulationCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static RegulationCommandType fromValue(final String value) {
        final RegulationCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(RegulationCommandType.class.getName(), value, Arrays.toString(values()));
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
