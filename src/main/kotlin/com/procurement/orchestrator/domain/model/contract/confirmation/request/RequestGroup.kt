package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import java.io.Serializable

data class RequestGroup(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequestGroupId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("requests") @param:JsonProperty("requests") val requests: Requests = Requests()
) : IdentifiableObject<RequestGroup>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RequestGroup
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RequestGroup) = RequestGroup(
        id = id,
        requests = requests updateBy src.requests
    )
}
