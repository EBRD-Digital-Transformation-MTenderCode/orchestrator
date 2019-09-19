package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ProcurerCommandType {

    CHECK_CRITERIA("checkCriteria"),
    CREATE_CRITERIA("createCriteria");

    private static final Map<String, ProcurerCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ProcurerCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ProcurerCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProcurerCommandType fromValue(final String value) {
        final ProcurerCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(ProcurerCommandType.class.getName(), value, Arrays.toString(values()));
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
