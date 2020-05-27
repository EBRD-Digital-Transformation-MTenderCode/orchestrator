package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.model.Version

abstract class AddRequirementResponseAction : ProceduralAction<AddRequirementResponseAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "addRequirementResponse"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
    ) {

        class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("requirementResponse") @param:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse?
        ) {

            class RequirementResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId.Permanent,

                @JsonDeserialize(using = RequirementValueDeserializer::class)
                @JsonSerialize(using = RequirementValueSerializer::class)
                @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

                @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
                @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
                @field:JsonProperty("responder") @param:JsonProperty("responder") val responder: Responder

            ) {

                class RelatedTenderer(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId
                )

                class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                )

                class Responder(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: PersonId,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                )
            }
        }
    }
}
