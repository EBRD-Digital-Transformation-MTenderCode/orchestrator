package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.ProcurementMethod
import com.procurement.orchestrator.infrastructure.model.Version

abstract class AnalizeQualificationForInvitationAction : ProceduralAction<AnalizeQualificationForInvitationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "analizeQualification"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
        @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: ProcurementMethod
    )
}
