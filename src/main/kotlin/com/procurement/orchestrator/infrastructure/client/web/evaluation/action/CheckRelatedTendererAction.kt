package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckRelatedTendererAction : ProceduralAction<CheckRelatedTendererAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkRelatedTenderer"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: AwardId,
        @field:JsonProperty("relatedTendererId") @param:JsonProperty("relatedTendererId") val relatedTendererId: OrganizationId?,
        @field:JsonProperty("requirementId") @param:JsonProperty("requirementId") val requirementId: RequirementId?,
        @field:JsonProperty("responderId") @param:JsonProperty("responderId") val responderId: PersonId?
    )
}
