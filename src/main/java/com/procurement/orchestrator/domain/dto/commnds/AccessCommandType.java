package com.procurement.orchestrator.domain.dto.commnds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AccessCommandType {

    CHECK_BID("checkBid"),
    CHECK_ITEMS("checkItems"),
    CHECK_TOKEN("checkToken"),
    CHECK_LOT_GET_ITEMS("checkLotGetItems");

    private static final Map<String, AccessCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final AccessCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    AccessCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static AccessCommandType fromValue(final String value) {
        final AccessCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(AccessCommandType.class.getName(), value, Arrays.toString(values()));
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
