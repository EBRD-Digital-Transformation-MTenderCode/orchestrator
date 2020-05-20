package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.Documents
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable
import java.time.LocalDateTime

data class Amendment(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AmendmentId,

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
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("changes") @param:JsonProperty("changes") val changes: Changes = Changes(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: Documents = Documents()
) : IdentifiableObject<Amendment>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Amendment
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Amendment) = Amendment(
        id = id,
        type = src.type or type,
        status = src.status or status,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        date = src.date or date,
        releaseID = src.releaseID or releaseID,
        amendsReleaseID = src.amendsReleaseID or amendsReleaseID,
        description = src.description or description,
        rationale = src.rationale or rationale,
        relatedLots = relatedLots combineBy src.relatedLots,
        changes = changes combineBy src.changes,
        documents = documents updateBy src.documents
    )
}
