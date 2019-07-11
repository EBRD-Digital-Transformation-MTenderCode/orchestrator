package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AgentResponseCommandType {
    LAUNCH_AC_VERIFICATION("launchAcVerification"),
    TREASURY_APPROVING("treasuryApprovingAc");

    private static final Map<String, AgentResponseCommandType> CONSTANTS =
        new HashMap<String, AgentResponseCommandType>() {{
            for (final AgentResponseCommandType item : AgentResponseCommandType.values()) {
                put(item.value, item);
            }
        }};

    private final String value;

    AgentResponseCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AgentResponseCommandType fromValue(final String value) {
        final AgentResponseCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(AgentResponseCommandType.class.getName(), value, Arrays.toString(values()));
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
