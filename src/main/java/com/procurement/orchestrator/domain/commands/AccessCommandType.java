package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AccessCommandType {

    CREATE_AP("createAp"),
    CREATE_CN("createCn"),
    CREATE_CN_ON_PIN("createCnOnPin"),
    CREATE_CN_ON_PN("createCnOnPn"),
    CREATE_FE("createFE"),
    CREATE_PIN("createPin"),
    CREATE_PIN_ON_PN("createPinOnPn"),
    CREATE_PN("createPn"),

    AMEND_FE("amendFE"),
    UPDATE_AP("updateAp"),
    UPDATE_CN("updateCn"),
    UPDATE_PN("updatePn"),

    SET_TENDER_SUSPENDED("setTenderSuspended"),
    SET_TENDER_UNSUSPENDED("setTenderUnsuspended"),
    SET_TENDER_UNSUCCESSFUL("setTenderUnsuccessful"),
    SET_TENDER_PRECANCELLATION("setTenderPreCancellation"),
    SET_TENDER_CANCELLATION("setTenderCancellation"),
    SET_TENDER_STATUS_DETAILS("setTenderStatusDetails"),
    GET_TENDER_OWNER("getTenderOwner"),
    START_NEW_STAGE("startNewStage"),

    GET_AP_TITLE_AND_DESCRIPTION("getAPTitleAndDescription"),
    GET_ITEMS_BY_LOT("getItemsByLot"),
    GET_ITEMS_BY_LOTS("getItemsByLots"),
    GET_ACTIVE_LOTS("getActiveLots"),
    GET_CRITERIA_FOR_TENDERER("getCriteriaForTenderer"),
    GET_LOT("getLot"),
    GET_LOTS_AUCTION("getLotsAuction"),
    GET_LOTS_FOR_AUCTION("getLotsForAuction"),
    GET_AWARD_CRITERIA("getAwardCriteria"),
    GET_DATA_FOR_AC("getDataForAc"),
    GET_MAIN_PROCUREMENT_CATEGORY("getMainProcurementCategory"),
    SET_LOTS_SD_UNSUCCESSFUL("setLotsStatusDetailsUnsuccessful"),
    SET_LOTS_SD_AWARDED("setLotsStatusDetailsAwarded"),
    SET_LOTS_UNSUCCESSFUL("setLotsStatusUnsuccessful"),
    COMPLETE_LOTS("completeLots"),
    SET_LOTS_INITIAL_STATUS("setLotInitialStatus"),
    SET_FINAL_STATUSES("setFinalStatuses"),

    CHECK_AWARD("checkAward"),
    CHECK_EXISTENCE_ITEMS_AND_LOTS("checkExistenceItemsAndLots"),
    CHECK_FE_DATA("checkFEData"),
    CHECK_LOT_STATUS("checkLotStatus"),
    CHECK_LOT_ACTIVE("checkLotActive"),
    CHECK_LOT_AWARDED("checkLotAwarded"),
    CHECK_LOTS_STATUS("checkLotsStatus"),
    CHECK_BID("checkBid"),
    CHECK_ITEMS("checkItems"),
    CHECK_TOKEN("checkToken"),
    CHECK_BUDGET_SOURCES("checkBudgetSources"),
    VALIDATE_OWNER_AND_TOKEN("validateOwnerAndToken"),
    CHECK_CN_ON_PN("checkCnOnPn"),
    GET_AWARD_CRITERIA_AND_CONVERSATIONS("getAwardCriteriaAndConversions"),
    CREATE_REQUESTS_FOR_EV_PANELS("createRequestsForEvPanels"),
    CHECK_RESPONSES("checkResponses");

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
