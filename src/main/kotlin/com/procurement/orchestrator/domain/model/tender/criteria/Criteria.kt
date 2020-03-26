package com.procurement.orchestrator.domain.model.tender.criteria

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroup
import java.io.Serializable

data class Criteria(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CriteriaId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Criteria
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
