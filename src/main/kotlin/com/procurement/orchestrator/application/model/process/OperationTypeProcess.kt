package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class OperationTypeProcess(@JsonValue override val key: String) : EnumElementProvider.Key {

    ADD_GENERATED_DOCUMENT("addGeneratedDocument"),
    APPLY_QUALIFICATION_PROTOCOL("applyQualificationProtocol"),
    AWARD_CONSIDERATION("awardConsideration"),
    COMPLETE_QUALIFICATION("completeQualification"),
    COMPLETE_SOURCING("completeSourcing"),
    CREATE_AWARD("createAward"),
    CREATE_PCR("createPcr"),
    CREATE_SUBMISSION("createSubmission"),
    DECLARE_NON_CONFLICT_OF_INTEREST("declareNonConflictOfInterest"),
    DIVIDE_LOT("divideLot"),
    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    LOT_CANCELLATION("lotCancellation"),
    OUTSOURCING_PN("outsourcingPN"),
    PCR_PROTOCOL("pcrProtocol"),
    QUALIFICATION("qualification"),
    QUALIFICATION_CONSIDERATION("qualificationConsideration"),
    QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST("qualificationDeclareNonConflictOfInterest"),
    QUALIFICATION_PROTOCOL("qualificationProtocol"),
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
    WITHDRAW_BID("withdrawBid"),
    WITHDRAW_PCR_PROTOCOL("withdrawPcrProtocol"),
    WITHDRAW_QUALIFICATION_PROTOCOL("withdrawQualificationProtocol"),
    WITHDRAW_SUBMISSION("withdrawSubmission");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationTypeProcess>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
