package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum QualificationCommandType {

    CREATE_AWARDS("createAwards"),
    AWARD_BY_BID("awardByBid"),
    CHECK_AWARDED("checkAwarded"),
    SET_FINAL_STATUSES("setFinalStatuses"),
    PREPARE_CANCELLATION("prepareCancellation"),
    AWARDS_CANCELLATION("awardsCancellation");

    private static final Map<String, QualificationCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final QualificationCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    QualificationCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static QualificationCommandType fromValue(final String value) {
        final QualificationCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(QualificationCommandType.class.getName(), value, Arrays.toString(values()));
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
