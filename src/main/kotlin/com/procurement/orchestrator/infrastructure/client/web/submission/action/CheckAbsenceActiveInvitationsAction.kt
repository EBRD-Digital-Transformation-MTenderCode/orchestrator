package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckAbsenceActiveInvitationsAction : ProceduralAction<CheckAbsenceActiveInvitationsAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkAbsenceActiveInvitations"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid
    )
}
