package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class ConfirmationResponse(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: ConfirmationResponseId,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("value") @param:JsonProperty("value") val value: String? = null,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("requestId") @field:JsonProperty("requestId") val requestId: String? = null,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("type") @field:JsonProperty("type") val type: ConfirmationResponseType? = null,

        @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("relatedPerson") @field:JsonProperty("relatedPerson") val relatedPerson: Person? = null
) : IdentifiableObject<ConfirmationResponse>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ConfirmationResponse
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: ConfirmationResponse) = ConfirmationResponse(
        id = id,
        value = src.value or value,
        type = src.type or type,
        requestId = src.requestId or requestId,
        relatedPerson = relatedPerson updateBy src.relatedPerson
    )
}
