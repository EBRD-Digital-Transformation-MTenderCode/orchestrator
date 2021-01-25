package com.procurement.orchestrator.domain.model.tender.criteria.requirement.eligible.evidence

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.DocumentReference
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class EligibleEvidence(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: EligibleEvidenceId,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonProperty("type") @param:JsonProperty("type") val type: EligibleEvidenceType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedDocument") @param:JsonProperty("relatedDocument") val relatedDocument: DocumentReference? = null
) : IdentifiableObject<EligibleEvidence>, Serializable {

    override fun updateBy(src: EligibleEvidence) = EligibleEvidence(
        id = id,
        title = src.title,
        description = src.description or description,
        type = src.type,
        relatedDocument = src.relatedDocument or relatedDocument
    )
}
