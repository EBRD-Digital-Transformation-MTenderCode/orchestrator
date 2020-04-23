package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.contract.confirmation.RelatedPerson
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Request(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequestId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedPerson") @param:JsonProperty("relatedPerson") val relatedPerson: RelatedPerson? = null
) : IdentifiableObject<Request>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Request
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Request) = Request(
        id = id,
        title = src.title or title,
        description = src.description or description,
        relatedPerson = relatedPerson updateBy src.relatedPerson
    )
}
