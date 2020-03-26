package com.procurement.orchestrator.infrastructure.client.web.notice.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import java.time.LocalDateTime

abstract class UpdateRecordAction : ProceduralAction<UpdateRecordAction.Params> {
    override val version: String = "2.0.0"
    override val name: String = "updateRecord"

    class Params(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime,
        @field:JsonProperty("data") @param:JsonProperty("data") val data: String
    )
}
