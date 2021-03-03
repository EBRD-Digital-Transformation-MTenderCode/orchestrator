package com.procurement.orchestrator.infrastructure.message.docgenerator

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class DocumentGeneratorEvent(@JsonValue override val key: String) : EnumElementProvider.Key {

    CONTRACT_FINALIZATION("contractFinalization"),
    DOCUMENT_GENERATED("documentGenerated");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentGeneratorEvent>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
