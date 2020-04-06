package com.procurement.orchestrator.infrastructure.client.web.notice.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class CreateRecordAction : ProceduralAction<CreateRecordAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "createRecord"

    class Params(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("data") @param:JsonProperty("data") val data: String
    )
}
