package com.procurement.orchestrator.application.service.events

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.document.DocumentInitiator

sealed class DocumentGeneratorEvent {

    data class DocumentGenerated(
        val cpid: Cpid,
        val ocid: Ocid.SingleStage,
        val documentInitiator: DocumentInitiator,
        val processName: String,
        val objectId: String,
        val data: JsonNode
    ): DocumentGeneratorEvent() {

        data class Document(
            val id: String
        )
    }

}


