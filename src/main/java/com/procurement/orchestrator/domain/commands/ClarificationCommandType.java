package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ClarificationCommandType {

    CREATE_ENQUIRY("createEnquiry"),
    CREATE_ANSWER("createAnswer"),
    CHECK_ENQUIRIES("checkEnquiries"),
    GET_PERIOD("getPeriod"),
    SAVE_PERIOD("savePeriod"),
    VALIDATE_PERIOD("validatePeriod"),
    CALCULATE_SAVE_PERIOD("calculateSavePeriod");

    private static final Map<String, ClarificationCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final ClarificationCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    ClarificationCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ClarificationCommandType fromValue(final String value) {
        final ClarificationCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(ClarificationCommandType.class.getName(), value, Arrays.toString(values()));
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
