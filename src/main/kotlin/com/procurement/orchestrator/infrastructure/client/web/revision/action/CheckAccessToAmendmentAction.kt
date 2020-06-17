package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckAccessToAmendmentAction : ProceduralAction<CheckAccessToAmendmentAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkAccessToAmendment"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("amendmentId") @param:JsonProperty("amendmentId") val id: AmendmentId
    )
}
