package com.procurement.orchestrator.domain.model.classification

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject

import com.procurement.orchestrator.domain.model.identifier.IdentifierId
import com.procurement.orchestrator.domain.model.identifier.IdentifierScheme
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Classification(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: IdentifierScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: IdentifierId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<Classification>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Classification
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: Classification) = Classification(
        scheme = scheme,
        id = id,
        description = src.description or description,
        uri = src.uri or uri
    )
}
