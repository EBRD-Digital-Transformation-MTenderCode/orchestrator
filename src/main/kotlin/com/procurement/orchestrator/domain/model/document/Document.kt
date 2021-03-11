package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.lot.RelatedLots
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable
import java.time.LocalDateTime

data class Document(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String? = null,

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
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: RelatedLots = RelatedLots(),

    //TODO type
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedConfirmations") @param:JsonProperty("relatedConfirmations") val relatedConfirmations: RelatedConfirmations = RelatedConfirmations()
) : IdentifiableObject<Document>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Document
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Document) = Document(
        id = id,
        documentType = src.documentType or documentType,
        title = src.title or title,
        description = src.description or description,
        url = src.description or description,
        datePublished = src.datePublished or datePublished,
        dateModified = src.dateModified or dateModified,
        format = src.format or format,
        language = src.language or language,
        relatedLots = relatedLots combineBy src.relatedLots,
        relatedConfirmations = relatedConfirmations combineBy src.relatedConfirmations
    )
}
