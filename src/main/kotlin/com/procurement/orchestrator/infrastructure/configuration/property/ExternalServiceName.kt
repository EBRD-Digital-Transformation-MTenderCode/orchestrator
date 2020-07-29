package com.procurement.orchestrator.infrastructure.configuration.property

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.EnumElementProvider

enum class ExternalServiceName(override val key: String) : EnumElementProvider.Key {
    ACCESS("eAccess"),
    CLARIFICATION("eClarification"),
    CONTRACTING("eContracting"),
    DOSSIER("eDossier"),
    EVALUATION("eEvaluation"),
    MDM("MDM"),
    NOTICE("eNotice"),
    QUALIFICATION("eQualification"),
    REVISION("eRevision"),
    STORAGE("Storage"),
    SUBMISSION("eSubmisssion");

    override fun toString(): String = key

    companion object : EnumElementProvider<ExternalServiceName>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ExternalServiceName.orThrow(name)
    }
}
