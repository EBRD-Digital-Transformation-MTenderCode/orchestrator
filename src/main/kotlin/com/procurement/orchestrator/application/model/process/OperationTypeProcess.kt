package com.procurement.orchestrator.application.model.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class OperationTypeProcess(@JsonValue override val key: String) : EnumElementProvider.Key {

    APPLY_QUALIFICATION_PROTOCOL("applyQualificationProtocol"),
    CREATE_SUBMISSION("createSubmission"),
    DECLARE_NON_CONFLICT_OF_INTEREST("declareNonConflictOfInterest"),
    LOT_CANCELLATION("lotCancellation"),
    QUALIFICATION("qualification"),
    QUALIFICATION_CONSIDERATION("qualificationConsideration"),
    QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST("qualificationDeclareNonConflictOfInterest"),
    QUALIFICATION_PROTOCOL("qualificationProtocol"),
    START_SECOND_STAGE("startSecondStage"),
    SUBMISSION_PERIOD_END("submissionPeriodEnd"),
    TENDER_CANCELLATION("tenderCancellation"),
    TENDER_OR_LOT_AMENDMENT_CANCELLATION("tenderOrLotAmendmentCancellation"),
    TENDER_OR_LOT_AMENDMENT_CONFIRMATION("tenderOrLotAmendmentConfirmation"),
    WITHDRAW_QUALIFICATION_PROTOCOL("withdrawQualificationProtocol"),
    WITHDRAW_SUBMISSION("withdrawSubmission");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationTypeProcess>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
