package com.procurement.orchestrator.infrastructure.client.web.storage.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.document.DocumentId
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class CheckRegistrationAction : ProceduralAction<CheckRegistrationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkRegistration"

    class Params(
        @field:JsonProperty("datePublished") @param:JsonProperty("datePublished") val datePublished: LocalDateTime,
        @field:JsonProperty("documentIds") @param:JsonProperty("documentIds") val ids: List<DocumentId>
    )
}
