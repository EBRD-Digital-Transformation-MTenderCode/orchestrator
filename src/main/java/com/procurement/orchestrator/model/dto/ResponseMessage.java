package com.procurement.orchestrator.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum ResponseMessage {

    OK("May the Force be with you."),
    OPERATION_EXCEPTION("You will find only what you bring in.");

    private static final Map<String, ResponseMessage> CONSTANTS = new HashMap<>();

    static {
        for (final ResponseMessage c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    ResponseMessage(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ResponseMessage fromValue(final String value) {
        final ResponseMessage constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
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
