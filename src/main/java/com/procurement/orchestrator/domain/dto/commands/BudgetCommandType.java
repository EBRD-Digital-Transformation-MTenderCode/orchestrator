package com.procurement.orchestrator.domain.dto.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum BudgetCommandType {

    CREATE_EI("createEi"),
    UPDATE_EI("updateEi"),
    CREATE_FS("createFs"),
    UPDATE_FS("updateFs"),
    CHECK_FS("checkFs");

    private static final Map<String, BudgetCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final BudgetCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    BudgetCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static BudgetCommandType fromValue(final String value) {
        final BudgetCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(BudgetCommandType.class.getName(), value, Arrays.toString(values()));
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
