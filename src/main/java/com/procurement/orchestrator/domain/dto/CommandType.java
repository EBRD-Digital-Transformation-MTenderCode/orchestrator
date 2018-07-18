package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    CREATE_EI("createEi"),
    CREATE_FS("createFs"),
    CREATE_TENDER("createTender");;

    private static final Map<String, CommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final CommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static CommandType fromValue(final String value) {
        final CommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(CommandType.class.getName(), value, Arrays.toString(values()));
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
