package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum DossierCommandType {

    CHECK_CRITERIA("checkCriteria"),
    CREATE_CRITERIA("createCriteria"),
    CHECK_RESPONSES("checkResponses"),
    CREATE_REQUESTS_FOR_EV_PANELS("createRequestsForEvPanels"),
    GET_PRE_QUALIFICATION_PERIOD_END("getPreQualificationPeriodEnd"),
    VALIDATE_PERIOD("validatePeriod"),
    CHECK_PERIOD("checkPeriod"),
    SAVE_PERIOD("savePeriod"),
    EXTEND_SUBMISSION_PERIOD("extendSubmissionPeriod");

    private static final Map<String, DossierCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final DossierCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    DossierCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static DossierCommandType fromValue(final String value) {
        final DossierCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(DossierCommandType.class.getName(), value, Arrays.toString(values()));
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
