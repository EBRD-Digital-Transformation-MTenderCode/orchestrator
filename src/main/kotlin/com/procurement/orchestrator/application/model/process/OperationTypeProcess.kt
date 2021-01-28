package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class OperationTypeProcess(@JsonValue override val key: String) : EnumElementProvider.Key {

    APPLY_QUALIFICATION_PROTOCOL("applyQualificationProtocol"),
    COMPLETE_QUALIFICATION("completeQualification"),
    CREATE_AWARD("createAward"),
    COMPLETE_SOURCING("completeSourcing"),
    CREATE_PCR("createPcr"),
    CREATE_SUBMISSION("createSubmission"),
    DECLARE_NON_CONFLICT_OF_INTEREST("declareNonConflictOfInterest"),
    DIVIDE_LOT("divideLot"),
    LOT_CANCELLATION("lotCancellation"),
    OUTSOURCING_PN("outsourcingPN"),
    QUALIFICATION("qualification"),
    QUALIFICATION_CONSIDERATION("qualificationConsideration"),
    QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST("qualificationDeclareNonConflictOfInterest"),
    QUALIFICATION_PROTOCOL("qualificationProtocol"),
    PCR_PROTOCOL("pcrProtocol"),
    RELATION_AP("relationAP"),
    START_AWARD_PERIOD("startAwardPeriod"),
    START_SECOND_STAGE("startSecondStage"),
    SUBMISSION_PERIOD_END("submissionPeriodEnd"),
    SUBMIT_BID("submitBid"),
    SUBMIT_BID_IN_PCR("submitBidInPcr"),
    TENDER_CANCELLATION("tenderCancellation"),
    TENDER_OR_LOT_AMENDMENT_CANCELLATION("tenderOrLotAmendmentCancellation"),
    TENDER_OR_LOT_AMENDMENT_CONFIRMATION("tenderOrLotAmendmentConfirmation"),
    UPDATE_AWARD("updateAward"),
    WITHDRAW_QUALIFICATION_PROTOCOL("withdrawQualificationProtocol"),
    WITHDRAW_SUBMISSION("withdrawSubmission");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationTypeProcess>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
