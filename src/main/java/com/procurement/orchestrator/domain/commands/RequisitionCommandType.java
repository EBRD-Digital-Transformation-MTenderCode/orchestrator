package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum RequisitionCommandType {

    CHECK_LOTS_STATUS("checkLotsStatus"),
    CHECK_LOT_AWARDED("checkLotAwarded"),
    CREATE_REQUESTS_FOR_EV_PANELS("createRequestsForEvPanels"),
    GET_ACTIVE_LOTS("getActiveLots"),
    GET_AWARD_CRITERIA_AND_CONVERSATIONS("getAwardCriteriaAndConversions"),
    GET_CURRENCY("getCurrency"),
    GET_LOTS_AUCTION("getLotsAuction"),
    GET_TENDER_OWNER("getTenderOwner"),
    SET_LOTS_UNSUCCESSFUL("setLotsStatusUnsuccessful"),
    SET_TENDER_STATUS_DETAILS("setTenderStatusDetails"),
    SET_TENDER_SUSPENDED("setTenderSuspended"),
    SET_TENDER_UNSUCCESSFUL("setTenderUnsuccessful"),
    SET_TENDER_UNSUSPENDED("setTenderUnsuspended");

    private static final Map<String, RequisitionCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final RequisitionCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    RequisitionCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static RequisitionCommandType fromValue(final String value) {
        final RequisitionCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(RequisitionCommandType.class.getName(), value, Arrays.toString(values()));
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
