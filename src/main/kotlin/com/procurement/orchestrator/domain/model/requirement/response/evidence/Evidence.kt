package com.procurement.orchestrator.domain.model.requirement.response.evidence

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.document.DocumentReference
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Evidence(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: EvidenceId,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedDocument") @param:JsonProperty("relatedDocument") val relatedDocument: DocumentReference? = null
) : IdentifiableObject<Evidence>, Serializable {

    override fun updateBy(src: Evidence) = Evidence(
        id = id,
        title = src.title,
        description = src.description or description,
        relatedDocument = src.relatedDocument or relatedDocument
    )
}
