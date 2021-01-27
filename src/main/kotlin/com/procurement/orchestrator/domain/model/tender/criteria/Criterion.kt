package com.procurement.orchestrator.domain.model.tender.criteria

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.classification.Classification
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.tender.criteria.requirement.RequirementGroups
import java.io.Serializable

data class Criterion(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CriterionId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("source") @param:JsonProperty("source") val source: CriteriaSource? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: RequirementGroups = RequirementGroups(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriteriaRelatesTo? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification? = null
) : IdentifiableObject<Criterion>,
    Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Criterion
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Criterion) = Criterion(
        id = id,
        title = src.title or title,
        source = src.source or source,
        description = src.description or description,
        requirementGroups = requirementGroups updateBy src.requirementGroups,
        relatesTo = src.relatesTo or relatesTo,
        relatedItem = src.relatedItem or relatedItem
    )
}
