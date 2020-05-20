package com.procurement.orchestrator.infrastructure.client.web.storage.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable
import java.time.LocalDateTime

abstract class OpenAccessAction : FunctionalAction<OpenAccessAction.Params, OpenAccessAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "openAccess"
    override val target: Target<Result> = Target.plural()

    class Params(
        @field:JsonProperty("datePublished") @param:JsonProperty("datePublished") val datePublished: LocalDateTime,
        @field:JsonProperty("documentIds") @param:JsonProperty("documentIds") val ids: List<DocumentId>
    )

    class Result(values: List<Document>) : List<Result.Document> by values, Serializable {

        class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
            @field:JsonProperty("datePublished") @param:JsonProperty("datePublished") val datePublished: LocalDateTime,
            @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
        ) : Serializable
    }
}
