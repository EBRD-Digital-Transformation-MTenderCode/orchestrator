package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.lot.LotId
import java.io.Serializable
import java.time.LocalDateTime

data class Document(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("url") @param:JsonProperty("url") val url: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("datePublished") @param:JsonProperty("datePublished") val datePublished: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("dateModified") @param:JsonProperty("dateModified") val dateModified: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("format") @param:JsonProperty("format") val format: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("language") @param:JsonProperty("language") val language: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList(),

    //TODO type
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedConfirmations") @param:JsonProperty("relatedConfirmations") val relatedConfirmations: List<String> = emptyList()
) : Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Document
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()
}
