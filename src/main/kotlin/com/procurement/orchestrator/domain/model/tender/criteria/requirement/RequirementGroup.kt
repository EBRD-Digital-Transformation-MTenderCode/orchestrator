package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementDeserializer
import com.procurement.orchestrator.infrastructure.bind.criteria.requirement.RequirementSerializer
import java.io.Serializable

data class RequirementGroup(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementGroupId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonDeserialize(using = RequirementDeserializer::class)
    @JsonSerialize(using = RequirementSerializer::class)
    @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: Requirements = Requirements()
) : IdentifiableObject<RequirementGroup>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RequirementGroup
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RequirementGroup) = RequirementGroup(
        id = id,
        description = src.description or description,
        requirements = requirements updateBy src.requirements
    )
}
