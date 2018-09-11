package com.procurement.orchestrator.domain.dto.commnds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MdmCommandType {

    PROCESS_EI_DATA("processEiData"),
    PROCESS_FS_DATA("processFsData"),
    PROCESS_TENDER_DATA("processTenderData"),
    PROCESS_BID_DATA("processBidData"),
    PROCESS_ENQUIRY_DATA("processEnquiryData");

    private static final Map<String, MdmCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final MdmCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    MdmCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static MdmCommandType fromValue(final String value) {
        final MdmCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(MdmCommandType.class.getName(), value, Arrays.toString(values()));
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
