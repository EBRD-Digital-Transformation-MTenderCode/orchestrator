package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.FunctionalAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.organization.OrganizationId
import com.procurement.orchestrator.domain.model.person.PersonId
import com.procurement.orchestrator.domain.model.qualification.QualificationId
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.client.web.Target
import com.procurement.orchestrator.infrastructure.model.Version
import java.io.Serializable

abstract class DoDeclarationAction : FunctionalAction<DoDeclarationAction.Params, DoDeclarationAction.Result> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "doDeclaration"
    override val target: Target<Result> = Target.single()

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) {
        data class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>
        ) {
            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

                @JsonDeserialize(using = RequirementValueDeserializer::class)
                @JsonSerialize(using = RequirementValueSerializer::class)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue?,
                @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
                @param:JsonProperty("responder") @field:JsonProperty("responder") val responder: Responder
            ) {
                data class RelatedTenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId
                )

                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                )

                data class Responder(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String?
                )
            }
        }
    }

    class Result(
        @param:JsonProperty("qualifications") @field:JsonProperty("qualifications") val qualifications: List<Qualification>
    ) : Serializable {
        data class Qualification(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: QualificationId,
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>
        ) : Serializable {
            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

                @JsonDeserialize(using = RequirementValueDeserializer::class)
                @JsonSerialize(using = RequirementValueSerializer::class)
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementResponseValue,
                @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
                @param:JsonProperty("responder") @field:JsonProperty("responder") val responder: Responder
            ) : Serializable {
                data class RelatedTenderer(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: OrganizationId
                ) : Serializable

                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
                ) : Serializable

                data class Responder(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
                    @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                ) : Serializable
            }
        }
    }
}
