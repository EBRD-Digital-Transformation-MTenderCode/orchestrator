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

    SUSPEND_TENDER("suspendTender"),
    UNSUSPEND_TENDER("unsuspendTender"),
    UNSUCCESSFUL_TENDER("unsuccessfulTender"),
    PREPARE_CANCELLATION("prepareCancellation"),
    TENDER_CANCELLATION("tenderCancellation"),
    START_NEW_STAGE("startNewStage"),

    GET_LOTS("getLots"),
    UPDATE_LOT_STATUS_DETAILS("updateLotStatusDetails"),
    UPDATE_LOT_STATUS_DETAILS_BY_BID("updateLotStatusDetailsById"),
    UPDATE_LOTS("updateLots"),
    UPDATE_LOTS_EV("updateLotsEv"),
    CHECK_LOT_STATUS("checkLotStatus"),
    CHECK_LOT_GET_ITEMS("checkLotGetItems"),
    CHECK_BID("checkBid"),
    CHECK_ITEMS("checkItems"),
    CHECK_TOKEN("checkToken");

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
