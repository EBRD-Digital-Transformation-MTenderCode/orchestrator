package com.procurement.orchestrator.domain.model.organization.person

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.period.Period
import java.io.Serializable

data class BusinessFunction(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: BusinessFunctionId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("type") @param:JsonProperty("type") val type: BusinessFunctionType? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is BusinessFunction
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
