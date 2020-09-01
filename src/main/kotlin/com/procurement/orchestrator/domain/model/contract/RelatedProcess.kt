package com.procurement.orchestrator.domain.model.contract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or

import java.io.Serializable

data class RelatedProcess(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RelatedProcessId,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relationship") @param:JsonProperty("relationship") val relationship: RelatedProcessTypes = RelatedProcessTypes(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: RelatedProcessScheme? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: RelatedProcessIdentifier? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String? = null

) : IdentifiableObject<RelatedProcess>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is RelatedProcess
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: RelatedProcess) = RelatedProcess(
        id = id,
        relationship = relationship combineBy src.relationship,
        scheme = src.scheme or scheme,
        identifier = src.identifier or identifier,
        uri = src.uri or uri
    )
}