package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ProcessInitiator(@JsonValue override val key: String) : EnumElementProvider.Key {

    ISSUING_FRAMEWORK_CONTRACT("issuingFrameworkContract"),
    NEXT_STEP_AFTER_BUYERS_CONFIRMATION("nextStepAfterBuyersConfirmation"),
    NEXT_STEP_AFTER_INVITED_CANDIDATES_CONFIRMATION("nextStepAfterInvitedCandidatesConfirmation"),
    NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION("nextStepAfterSuppliersConfirmation"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcessInitiator>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)

        fun tryCreate(name: String) = orNull(name)
    }
}
