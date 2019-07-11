package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AgentRequestCommandType {
    SEND_AC_FOR_VERIFICATION("sendAcForVerification");

    private static final Map<String, AgentRequestCommandType> CONSTANTS =
        new HashMap<String, AgentRequestCommandType>() {{
            for (final AgentRequestCommandType item : AgentRequestCommandType.values()) {
                put(item.value, item);
            }
        }};

    private final String value;

    AgentRequestCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AgentRequestCommandType fromValue(final String value) {
        final AgentRequestCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(AgentRequestCommandType.class.getName(), value, Arrays.toString(values()));
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
