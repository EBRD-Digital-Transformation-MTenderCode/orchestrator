package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.model.document.Document
import com.procurement.orchestrator.domain.model.lot.LotId
import java.io.Serializable
import java.time.LocalDateTime

data class Amendment(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("type") @param:JsonProperty("type") val type: AmendmentType? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: AmendmentStatus? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: AmendmentRelatesTo? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("releaseId") @param:JsonProperty("releaseId") val releaseID: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amendsReleaseID") @param:JsonProperty("amendsReleaseID") val amendsReleaseID: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

    @Deprecated("Use 'relatesTo' & 'relatedItem' instead of this.")
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("changes") @param:JsonProperty("changes") val changes: List<Change> = emptyList(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Amendment
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
