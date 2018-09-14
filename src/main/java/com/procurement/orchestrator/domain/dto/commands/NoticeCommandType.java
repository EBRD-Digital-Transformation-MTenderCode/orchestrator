package com.procurement.orchestrator.domain.dto.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum NoticeCommandType {

    CREATE_RELEASE("createRelease");

    private static final Map<String, NoticeCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final NoticeCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    NoticeCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static NoticeCommandType fromValue(final String value) {
        final NoticeCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(NoticeCommandType.class.getName(), value, Arrays.toString(values()));
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
