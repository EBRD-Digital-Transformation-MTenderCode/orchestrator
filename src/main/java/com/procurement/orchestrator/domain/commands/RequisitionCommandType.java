package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum RequisitionCommandType {

    GET_ACTIVE_LOTS("getActiveLots"),
    GET_LOTS_AUCTION("getLotsAuction"),
    SET_LOTS_UNSUCCESSFUL("setLotsStatusUnsuccessful"),
    SET_TENDER_STATUS_DETAILS("setTenderStatusDetails"),
    SET_TENDER_UNSUCCESSFUL("setTenderUnsuccessful"),
    CREATE_REQUESTS_FOR_EV_PANELS("createRequestsForEvPanels");

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
