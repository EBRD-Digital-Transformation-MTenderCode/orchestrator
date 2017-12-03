package com.procurement.orchestrator.domain.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum ResponseMessageType {

    OK("May the Force be with you."),
    TRANSACTION_EXISTS("Transaction already exists."),
    SERVICE_EXCEPTION("One of the services is unavailable.");

    private static final Map<String, ResponseMessageType> CONSTANTS = new HashMap<>();

    static {
        for (final ResponseMessageType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    ResponseMessageType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static ResponseMessageType fromValue(final String value) {
        final ResponseMessageType constant = CONSTANTS.get(value);
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
