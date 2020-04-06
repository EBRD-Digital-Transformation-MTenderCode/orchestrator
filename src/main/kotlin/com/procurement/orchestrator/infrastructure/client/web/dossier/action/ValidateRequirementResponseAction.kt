package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.application.service.ProceduralAction
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.requirement.RequirementId
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponseId
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import com.procurement.orchestrator.infrastructure.model.Version

abstract class ValidateRequirementResponseAction : ProceduralAction<ValidateRequirementResponseAction.Params> {
    override val version: Version = Version.parse("2.0.0")
    override val name: String = "validateRequirementResponse"

    class Params(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("requirementResponse") @param:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse
    ) {

        class RequirementResponse(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId.Temporal,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue? = null,

            @field:JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement? = null
        ) {

            class Requirement(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
            )
        }
    }
}
