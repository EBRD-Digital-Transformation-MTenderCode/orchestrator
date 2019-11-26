package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum EvaluationCommandType {

    CREATE_AWARD("createAward"),
    EVALUATE_AWARD("evaluateAward"),
    CREATE_AWARDS("createAwards"),
    AWARD_BY_BID("awardByBid"),
    AWARDS_FOR_CANS("awardsForCans"),
    SET_FINAL_STATUSES("setFinalStatuses"),
    PREPARE_CANCELLATION("prepareCancellation"),
    AWARDS_CANCELLATION("awardsCancellation"),
    CREATE_AWARDS_BY_LOT_AUCTION("createAwardsByLotAuction"),
    CREATE_AWARDS_AUCTION("createAwardsAuction"),
    CREATE_AWARDS_AUCTION_END("createAwardsAuctionEnd"),
    CHECK_AWARD_VALUE("checkAwardValue"),
    END_AWARD_PERIOD("endAwardPeriod"),
    SET_INITIAL_AWARDS_STATUS("setInitialAwardsStatus"),
    GET_WIN_AWARD("getWinAward"),
    GET_EVALUATED_AWARDS("getEvaluatedAwards"),
    APPLY_EVALUATED_AWARDS("applyAwardingRes"),
    GET_AWARDS_FOR_AC("getAwardsForAc"),
    GET_LOT_FOR_CHECK("getLotForCheck"),
    GET_AWARD_ID_FOR_CHECK("getAwardIdForCheck"),
    FINAL_AWARDS_STATUS_BY_LOTS("finalAwardsStatusByLots"),
    GET_UNSUCCSESS_LOTS("getUnsuccsessLots"),
    SET_AWARD_FOR_EVALUATION("setAwardForEvaluation"),
    START_AWARD_PERIOD("startAwardPeriod"),
    CREATE_UNSUCCESSFUL_AWARDS("createUnsuccessfulAwards");


    private static final Map<String, EvaluationCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final EvaluationCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    EvaluationCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static EvaluationCommandType fromValue(final String value) {
        final EvaluationCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(EvaluationCommandType.class.getName(), value, Arrays.toString(values()));
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
