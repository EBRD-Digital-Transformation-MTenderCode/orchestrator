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
    OperationTypeProcess.CREATE_SUBMISSION -> Initiator.PLATFORM
    OperationTypeProcess.DECLARE_NON_CONFLICT_OF_INTEREST,
    OperationTypeProcess.LOT_CANCELLATION -> Initiator.PLATFORM
    OperationTypeProcess.TENDER_CANCELLATION -> Initiator.PLATFORM
    OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
    OperationTypeProcess.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> Initiator.PLATFORM
    OperationTypeProcess.WITHDRAW_SUBMISSION -> Initiator.PLATFORM
}
