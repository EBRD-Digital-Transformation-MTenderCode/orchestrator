package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.contract.ConfirmationRequestId
import java.io.Serializable

data class ConfirmationRequest(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ConfirmationRequestId,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("type") @param:JsonProperty("type") val type: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    //TODO
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("source") @param:JsonProperty("source") val source: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requestGroups") @param:JsonProperty("requestGroups") val requestGroups: List<RequestGroup> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ConfirmationRequest
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
