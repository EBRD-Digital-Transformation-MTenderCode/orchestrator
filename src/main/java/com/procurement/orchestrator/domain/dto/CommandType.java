package com.procurement.orchestrator.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CommandType {
    PROCESS_EI_DATA("processEiData"),
    PROCESS_FS_DATA("processFsData"),
    PROCESS_TENDER_DATA("processTenderData"),
    PROCESS_BID_DATA("processBidData"),
    PROCESS_ENQUIRY_DATA("processEnquiryData"),
    CHECK_BID("checkBid"),
    CHECK_ITEMS("checkItems"),
    CHECK_TOKEN("checkToken"),
    CREATE_RELEASE("createRelease");

    private static final Map<String, CommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final CommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    CommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static CommandType fromValue(final String value) {
        final CommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(CommandType.class.getName(), value, Arrays.toString(values()));
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
