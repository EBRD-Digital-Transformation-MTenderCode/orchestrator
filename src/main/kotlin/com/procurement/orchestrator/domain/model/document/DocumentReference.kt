package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import java.io.Serializable

data class DocumentReference(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId
) : IdentifiableObject<DocumentReference>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is DocumentReference
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: DocumentReference) = DocumentReference(
        id = id
    )
}
