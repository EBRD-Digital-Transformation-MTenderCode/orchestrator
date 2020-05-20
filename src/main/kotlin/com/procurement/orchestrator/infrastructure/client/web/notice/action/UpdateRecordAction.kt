package com.procurement.orchestrator.infrastructure.client.web.notice.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.infrastructure.model.Version
import java.time.LocalDateTime

abstract class UpdateRecordAction : ProceduralAction<UpdateRecordAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "updateRecord"

    class Params(
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("data") @param:JsonProperty("data") val data: String
    )
}
