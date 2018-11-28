package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AccessCommandType {

    CREATE_PIN("createPin"),
    CREATE_PN("createPn"),
    UPDATE_PN("updatePn"),
    CREATE_CN("createCn"),
    UPDATE_CN("updateCn"),
    CREATE_PIN_ON_PN("createPinOnPn"),
    CREATE_CN_ON_PIN("createCnOnPin"),
    CREATE_CN_ON_PN("createCnOnPn"),
    COMPLETE_TENDER("completeTender"),

    SET_TENDER_SUSPENDED("setTenderSuspended"),
    SET_TENDER_UNSUSPENDED("setTenderUnsuspended"),
    SET_TENDER_UNSUCCESSFUL("setTenderUnsuccessful"),
    SET_TENDER_PRECANCELLATION("setTenderPreCancellation"),
    SET_TENDER_CANCELLATION("setTenderCancellation"),
    SET_TENDER_STATUS_DETAILS("setTenderStatusDetails"),
    GET_TENDER_OWNER("getTenderOwner"),
    START_NEW_STAGE("startNewStage"),

    GET_LOTS("getLots"),
    GET_LOTS_AUCTION("getLotsAuction"),
    SET_LOTS_SD_UNSUCCESSFUL("setLotsStatusDetailsUnsuccessful"),
    SET_LOTS_SD_AWARDED("setLotsStatusDetailsAwarded"),
    SET_LOTS_UNSUCCESSFUL("setLotsStatusUnsuccessful"),
    SET_LOTS_UNSUCCESSFUL_EV("setLotsStatusUnsuccessfulEv"),

    CHECK_LOTS_STATUS_DETAILS("checkLotsStatusDetails"),
    CHECK_LOTS_STATUS("checkLotsStatus"),
    CHECK_BID("checkBid"),
    CHECK_ITEMS("checkItems"),
    CHECK_TOKEN("checkToken"),
    CHECK_BUDGET_SOURCES("checkBudgetSources");

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
