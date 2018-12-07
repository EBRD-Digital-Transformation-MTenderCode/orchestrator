package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AgentCommandType {

    VERIFICATION("verificationAc"),
    TREASURY_APPROVING("treasuryApprovingAc");

    private static final Map<String, AgentCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final AgentCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    AgentCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AgentCommandType fromValue(final String value) {
        final AgentCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(AgentCommandType.class.getName(), value, Arrays.toString(values()));
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
