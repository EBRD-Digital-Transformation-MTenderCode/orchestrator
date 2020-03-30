package com.procurement.orchestrator.domain.model.requirement.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.organization.OrganizationReference
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.requirement.RequirementReference
import com.procurement.orchestrator.domain.model.requirement.RequirementResponseValue
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.value.RequirementValueSerializer
import java.io.Serializable

data class RequirementResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonDeserialize(using = RequirementValueDeserializer::class)
    @JsonSerialize(using = RequirementValueSerializer::class)
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementResponseValue? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: RequirementReference? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: OrganizationReference? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("responder") @param:JsonProperty("responder") val responder: Responder?
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RequirementResponse
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
