package com.procurement.orchestrator.infrastructure.bpms.delegate.notifier

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.domain.EnumElementProvider

enum class Initiator(@JsonValue override val key: String) : EnumElementProvider.Key {

    PLATFORM("platform"),
    BPE("bpe");

    override fun toString(): String = key

    companion object : EnumElementProvider<Initiator>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

fun initiator(operationType: OperationTypeProcess): Initiator = when (operationType) {
    OperationTypeProcess.ADD_GENERATED_DOCUMENT -> Initiator.BPE
    OperationTypeProcess.APPLY_CONFIRMATIONS -> Initiator.PLATFORM
    OperationTypeProcess.APPLY_QUALIFICATION_PROTOCOL -> Initiator.PLATFORM
    OperationTypeProcess.AWARD_CONSIDERATION -> Initiator.PLATFORM
    OperationTypeProcess.COMPLETE_QUALIFICATION -> Initiator.PLATFORM
    OperationTypeProcess.COMPLETE_SOURCING -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_AWARD -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_BUYER -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_INVITED_CANDIDATE -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_CONTRACT -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_PCR -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_RFQ -> Initiator.PLATFORM
    OperationTypeProcess.CREATE_SUBMISSION -> Initiator.PLATFORM
    OperationTypeProcess.DECLARE_NON_CONFLICT_OF_INTEREST,
    OperationTypeProcess.DIVIDE_LOT -> Initiator.PLATFORM
    OperationTypeProcess.ISSUING_FRAMEWORK_CONTRACT -> Initiator.PLATFORM
    OperationTypeProcess.LOT_CANCELLATION -> Initiator.PLATFORM
    OperationTypeProcess.NEXT_STEP_AFTER_BUYERS_CONFIRMATION -> Initiator.PLATFORM
    OperationTypeProcess.NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION -> Initiator.PLATFORM
    OperationTypeProcess.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION -> Initiator.PLATFORM
    OperationTypeProcess.OUTSOURCING_PN -> Initiator.PLATFORM
    OperationTypeProcess.PCR_PROTOCOL -> Initiator.PLATFORM
    OperationTypeProcess.QUALIFICATION -> Initiator.PLATFORM
    OperationTypeProcess.QUALIFICATION_CONSIDERATION -> Initiator.PLATFORM
    OperationTypeProcess.QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST -> Initiator.PLATFORM
    OperationTypeProcess.QUALIFICATION_PROTOCOL -> Initiator.PLATFORM
    OperationTypeProcess.RELATION_AP -> Initiator.PLATFORM
    OperationTypeProcess.START_AWARD_PERIOD -> Initiator.PLATFORM
    OperationTypeProcess.START_SECOND_STAGE -> Initiator.PLATFORM
    OperationTypeProcess.SUBMISSION_PERIOD_END -> Initiator.BPE
    OperationTypeProcess.SUBMIT_BID -> Initiator.PLATFORM
    OperationTypeProcess.SUBMIT_BID_IN_PCR -> Initiator.PLATFORM
    OperationTypeProcess.TENDER_CANCELLATION -> Initiator.PLATFORM
    OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
    OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> Initiator.PLATFORM
    OperationTypeProcess.UPDATE_AWARD -> Initiator.PLATFORM
    OperationTypeProcess.WITHDRAW_BID -> Initiator.PLATFORM
    OperationTypeProcess.WITHDRAW_PCR_PROTOCOL -> Initiator.PLATFORM
    OperationTypeProcess.WITHDRAW_QUALIFICATION_PROTOCOL -> Initiator.PLATFORM
    OperationTypeProcess.WITHDRAW_SUBMISSION -> Initiator.PLATFORM
}
