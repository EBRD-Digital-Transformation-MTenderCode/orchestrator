package com.procurement.orchestrator.infrastructure.client.web.evaluation.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.award.AwardId
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.party.PartyId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.client.web.Target

abstract class CreateRequirementResponseAction :
    FunctionalAction<CreateRequirementResponseAction.Params, CreateRequirementResponseAction.Result> {
    override val version: String = "2.0.0"
    override val name: String = "createRequirementResponse"
    override val target: Target<Result> = Target.Single(typeRef = Result::class.java)

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
                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("responderer") @param:JsonProperty("responderer") val responderer: Responder?

            ) {

                class RelatedTenderer(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId
                )

                class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                )

                class Responder(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: PartyId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
                )
            }
        }
    }

    class Result(
        @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
    ) {
        class Award(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
            @field:JsonProperty("requirementResponse") @param:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse
        ) {

            class RequirementResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId.Permanent,

                @JsonDeserialize(using = RequirementValueDeserializer::class)
                @JsonSerialize(using = RequirementValueSerializer::class)
                @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

                @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
                @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
                @field:JsonProperty("responderer") @param:JsonProperty("responderer") val responderer: Responder

            ) {

                class RelatedTenderer(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId
                )

                class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
                )

                class Responder(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: PartyId,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                )
            }
        }
    }
}
