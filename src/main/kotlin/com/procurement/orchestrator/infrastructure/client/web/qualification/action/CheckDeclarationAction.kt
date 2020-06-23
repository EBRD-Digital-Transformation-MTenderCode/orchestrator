package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementDataType
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroupId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.model.Version

abstract class CheckDeclarationAction : ProceduralAction<CheckDeclarationAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "checkDeclaration"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("qualificationId") @field:JsonProperty("qualificationId") val qualificationId: QualificationId,
        @param:JsonProperty("requirementResponse") @field:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse,
        @param:JsonProperty("criteria") @field:JsonProperty("criteria") val criteria: List<Criteria>
    ) {
        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
            @param:JsonProperty("relatedTendererId") @field:JsonProperty("relatedTendererId") val relatedTendererId: OrganizationId,
            @param:JsonProperty("responderId") @field:JsonProperty("responderId") val responderId: IdentifierId,
            @param:JsonProperty("requirementId") @field:JsonProperty("requirementId") val requirementId: RequirementId
        )

        data class Criteria(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("requirementGroups") @field:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
        ) {
            data class RequirementGroup(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementGroupId,
                @param:JsonProperty("requirements") @field:JsonProperty("requirements") val requirements: List<Requirement>
            ) {
                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId,
                    @param:JsonProperty("dataType") @field:JsonProperty("dataType") val dataType: RequirementDataType
                )
            }
        }
    }
}
