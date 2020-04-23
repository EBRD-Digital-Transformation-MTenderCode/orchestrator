package com.procurement.orchestrator.domain.model.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class LegalProceeding(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: LegalProceedId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<LegalProceeding>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is LegalProceeding
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: LegalProceeding) = LegalProceeding(
        id = id,
        title = src.title or title,
        uri = src.uri or uri
    )
}
