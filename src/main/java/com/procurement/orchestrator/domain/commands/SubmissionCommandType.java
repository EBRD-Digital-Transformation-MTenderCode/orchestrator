package com.procurement.orchestrator.domain.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SubmissionCommandType {
    APPLY_EVALUATED_AWARDS("applyAwardingRes"),
    BIDS_CANCELLATION("bidsCancellation"),
    BIDS_WITHDRAWN("bidsWithdrawn"),
    BID_WITHDRAWN("bidWithdrawn"),
    CHECK_PERIOD("checkPeriod"),
    CHECK_PERIOD_END_DATE("checkPeriodEndDate"),
    CHECK_TOKEN_OWNER("checkTokenOwner"),
    COPY_BIDS("copyBids"),
    CREATE_BID("createBid"),
    EXTEND_TENDER_PERIOD("extendTenderPeriod"),
    FINAL_BIDS_STATUS_BY_LOTS("finalBidsStatusByLots"),
    GET_BIDS("getBids"),
    GET_BIDS_AUCTION("getBidsAuction"),
    GET_BIDS_BY_LOTS("getBidsByLots"),
    GET_BIDS_FOR_EVALUATION("getBidsForEvaluation"),
    GET_DOCS_OF_CONSIDERED_BID("getDocsOfConsideredBid"),
    GET_PERIOD("getPeriod"),
    OPEN_BIDS_FOR_PUBLISHING("openBidsForPublishing"),
    OPEN_BID_DOCS("openBidDocs"),
    PREPARE_BIDS_CANCELLATION("prepareBidsCancellation"),
    SAVE_NEW_PERIOD("saveNewPeriod"),
    SAVE_PERIOD("savePeriod"),
    SET_BIDS_FINAL_STATUSES("setBidsFinalStatuses"),
    SET_INITIAL_BIDS_STATUS("setInitialBidsStatus"),
    UPDATE_BID("updateBid"),
    UPDATE_BIDS_BY_LOTS("updateBidsByLots"),
    UPDATE_BID_BY_AWARD_STATUS("updateBidBAwardStatus"),
    UPDATE_BID_DOCS("updateBidDocs"),
    VALIDATE_PERIOD("validatePeriod");

    private static final Map<String, SubmissionCommandType> CONSTANTS = new HashMap<>();
    private final String value;

    static {
        for (final SubmissionCommandType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    SubmissionCommandType(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static SubmissionCommandType fromValue(final String value) {
        final SubmissionCommandType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new EnumException(SubmissionCommandType.class.getName(), value, Arrays.toString(values()));
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
