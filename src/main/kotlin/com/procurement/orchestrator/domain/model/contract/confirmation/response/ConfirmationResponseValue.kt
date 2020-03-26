package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.contract.confirmation.RelatedPerson
import java.io.Serializable
import java.time.LocalDateTime

data class ConfirmationResponseValue(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ConfirmationResponseValueId,

    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    //TODO null
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("verification") @param:JsonProperty("verification") val verification: List<Verification> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is ConfirmationResponseValue
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
