package com.procurement.orchestrator.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.procurement.orchestrator.exception.EnumException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OperationType {

    ACTIVATION_AC("activationAC"),
    ADD_ANSWER("addAnswer"),
    AMEND_FE("amendFE"),
    AUCTION_PERIOD_END("auctionPeriodEnd"),
    AWARD_BY_BID("awardByBid"),
    AWARD_BY_BID_EV("awardByBidEv"),
    AWARD_PERIOD_END("awardPeriodEnd"),
    AWARD_PERIOD_END_EV("awardPeriodEndEv"),
    BID_WITHDRAWN("bidWithdrawn"),
    BUYER_SIGNING_AC("buyerSigningAC"),
    CANCEL_CAN("cancelCan"),
    CANCEL_CAN_CONTRACT("cancelCanContract"),
    CANCEL_PLAN("cancelPlan"),
    CANCEL_STANDSTILL("cancellationStandstillPeriod"),
    CANCEL_TENDER("cancelTender"),
    CANCEL_TENDER_EV("cancelTenderEv"),
    CONFIRM_CAN("confirmCan"),
    CREATE_AC("createAC"),
    CREATE_AP("createAP"),
    CREATE_AWARD("createAward"),
    CREATE_BID("createBid"),
    CREATE_CAN("createCan"),
    CREATE_CN("createCN"),
    CREATE_CN_ON_PIN("createCNonPIN"),
    CREATE_CN_ON_PN("createCNonPN"),
    CREATE_EI("createEI"),
    CREATE_ENQUIRY("createEnquiry"),
    CREATE_FS("createFS"),
    CREATE_NEGOTIATION_CN_ON_PN("createNegotiationCnOnPn"),
    CREATE_PIN("createPIN"),
    CREATE_PIN_ON_PN("createPINonPN"),
    CREATE_PN("createPN"),
    CREATE_PROTOCOL("createProtocol"),
    DO_AWARD_CONSIDERATION("doAwardConsideration"),
    END_AWARD_PERIOD("endAwardPeriod"),
    END_CONTRACT_PROCESS("endContractingProcess"),
    EVALUATE_AWARD("evaluateAward"),
    FINAL_UPDATE_AC("finalUpdateAC"),
    ISSUING_AC("issuingAC"),
    OUTSOURCING_PN("outsourcingPN"),
    PROCESS_AC_CLARIFICATION("processAcClarification"),
    PROCESS_AC_REJECTION("processAcRejection"),
    QUALIFICATION("qualification"),
    QUALIFICATION_CONSIDERATION("qualificationConsideration"),
    RELATION_AP("relationAP"),
    STANDSTILL_PERIOD("standstillPeriod"),
    START_AWARD_PERIOD("startAwardPeriod"),
    START_NEW_STAGE("startNewStage"),
    SUBMISSION_PERIOD_END("submissionPeriodEnd"),
    SUPPLIER_SIGNING_AC("supplierSigningAC"),
    SUSPEND_TENDER("suspendTender"),//no message
    TENDER_EVALUATED("tenderEvaluated"),
    TENDER_PERIOD_END("tenderPeriodEnd"),
    TENDER_PERIOD_END_AUCTION("tenderPeriodEndAuction"),
    TENDER_PERIOD_END_EV("tenderPeriodEndEv"),
    TREASURY_APPROVING_AC("treasuryApprovingAC"),
    UNSUCCESSFUL_TENDER("tenderUnsuccessful"),//no message
    UNSUSPEND_TENDER("unsuspendTender"),//no message
    UPDATE_AC("updateAC"),
    UPDATE_AP("updateAP"),
    UPDATE_BID("updateBid"),
    UPDATE_BID_DOCS("updateBidDocs"),
    UPDATE_CAN_DOCS("updateCanDocs"),
    UPDATE_CN("updateCN"),
    UPDATE_EI("updateEI"),
    UPDATE_FS("updateFS"),
    UPDATE_PN("updatePN"),
    UPDATE_TENDER_PERIOD("updateTenderPeriod"),
    VERIFICATION_AC("verificationAC");

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
