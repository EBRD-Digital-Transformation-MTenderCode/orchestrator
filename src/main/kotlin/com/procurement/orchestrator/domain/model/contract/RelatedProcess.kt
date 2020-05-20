package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class RelatedProcess(
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RelatedProcessScheme,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RelatedProcessId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relationship") @param:JsonProperty("relationship") val relationship: RelatedProcessTypes = RelatedProcessTypes(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: RelatedProcessIdentifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null
) : IdentifiableObject<RelatedProcess>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RelatedProcess
            && this.scheme == other.scheme
            && this.id == other.id

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun updateBy(src: RelatedProcess) = RelatedProcess(
        scheme = scheme,
        id = id,
        relationship = relationship combineBy src.relationship,
        identifier = src.identifier or identifier,
        uri = src.uri or uri
    )
}
