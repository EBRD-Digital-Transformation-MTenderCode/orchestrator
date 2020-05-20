package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@Deprecated("Never using")
data class ContractTerm(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ContractTermId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("agreedMetrics") @param:JsonProperty("agreedMetrics") val agreedMetrics: List<AgreedMetric> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ContractTerm
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
