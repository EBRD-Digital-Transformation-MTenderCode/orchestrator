package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OperationType {

    CREATE_EI("createEI"),
    UPDATE_EI("updateEI"),
    CREATE_FS("createFS"),
    UPDATE_FS("updateFS"),
    CREATE_CN("createCN"),
    CREATE_PN("createPN"),
    CREATE_PIN("createPIN"),
    UPDATE_CN("updateCN"),
    UPDATE_PN("updatePN"),
    CREATE_PIN_ON_PN("createPINonPN"),
    CREATE_CN_ON_PN("createCNonPN"),
    CREATE_CN_ON_PIN("createCNonPIN"),
    UPDATE_TENDER_PERIOD("updateTenderPeriod"),
    CREATE_ENQUIRY("createEnquiry"),
    ADD_ANSWER("addAnswer"),
    SUSPEND_TENDER("suspendTender"),//no message
    UNSUSPEND_TENDER("unsuspendTender"),//no message
    UNSUCCESSFUL_TENDER("tenderUnsuccessful"),//no message
    TENDER_PERIOD_END("tenderPeriodEnd"),
    TENDER_PERIOD_END_EV("tenderPeriodEndEv"),
    AWARD_BY_BID("awardByBid"),
    AWARD_BY_BID_EV("awardByBidEv"),
    AWARD_PERIOD_END("awardPeriodEnd"),
    AWARD_PERIOD_END_EV("awardPeriodEndEv"),
    STANDSTILL_PERIOD("standstillPeriod"),//no message
    STANDSTILL_PERIOD_EV("standstillPeriodEv"),//no message
    START_NEW_STAGE("startNewStage");

    private static final Map<String, OperationType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final OperationType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    OperationType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static OperationType fromValue(final String value) {
        final OperationType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(OperationType.class.getName(), value, Arrays.toString(values()));
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
