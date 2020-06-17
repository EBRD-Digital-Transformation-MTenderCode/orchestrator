package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.model.process.OperationTypeProcess
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class ValidateRequirementResponsesAction :
    FunctionalAction<ValidateRequirementResponsesAction.Params, ValidateRequirementResponsesAction.Result> {

    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateRequirementResponses"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponse: List<RequirementResponse>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("organizationIds") @param:JsonProperty("organizationIds") val organizationIds: List<OrganizationId>,

        @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: OperationTypeProcess
    ) {

        class RequirementResponse(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedCandidate") @param:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate?
        ) {

            class Requirement(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
            )

            class RelatedCandidate(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String?
            )
        }
    }

    class Result(values: List<RequirementResponse>) : List<Result.RequirementResponse> by values, Serializable {

        class RequirementResponse(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue,

            @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
            @field:JsonProperty("relatedCandidate") @param:JsonProperty("relatedCandidate") val relatedCandidate: RelatedCandidate
        ) : Serializable {

            class Requirement(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
            ) : Serializable

            class RelatedCandidate(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: OrganizationId,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String
            ) : Serializable
        }
    }
}
