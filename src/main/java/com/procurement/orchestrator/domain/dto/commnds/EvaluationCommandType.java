package com.procurement.orchestrator.domain.dto.commnds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum EvaluationCommandType {

    GET_UPDATED_AWARDS_FOR_CANS("GetUpdatedAwardsForCAN");

    private static final Map<String, EvaluationCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final EvaluationCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    EvaluationCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static EvaluationCommandType fromValue(final String value) {
        final EvaluationCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(EvaluationCommandType.class.getName(), value, Arrays.toString(values()));
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
