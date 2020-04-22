package com.procurement.orchestrator.domain.model.requirement

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class RequirementReference(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null
) : IdentifiableObject<RequirementReference>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RequirementReference
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RequirementReference) = RequirementReference(
        id = id,
        title = src.title or title
    )
}
