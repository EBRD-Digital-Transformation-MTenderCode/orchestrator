package com.procurement.orchestrator.domain.model.organization.person

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
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
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents()
) : IdentifiableObject<BusinessFunction>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is BusinessFunction
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: BusinessFunction) = BusinessFunction(
        id = id,
        type = src.type or type,
        jobTitle = src.jobTitle or jobTitle,
        period = period updateBy src.period,
        documents = documents updateBy src.documents
    )
}
