package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckExistenceFaAction : ProceduralAction<CheckExistenceFaAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkExistenceFA"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid
    )

}
