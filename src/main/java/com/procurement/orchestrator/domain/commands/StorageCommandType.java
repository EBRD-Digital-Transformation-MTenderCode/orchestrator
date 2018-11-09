package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum StorageCommandType {

    VALIDATE("validate"),
    PUBLISH("publish");

    private static final Map<String, StorageCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final StorageCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    StorageCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static StorageCommandType fromValue(final String value) {
        final StorageCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(StorageCommandType.class.getName(), value, Arrays.toString(values()));
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
