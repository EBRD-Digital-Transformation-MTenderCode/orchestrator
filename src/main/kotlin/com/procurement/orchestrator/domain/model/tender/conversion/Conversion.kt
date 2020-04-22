package com.procurement.orchestrator.domain.model.tender.conversion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.tender.conversion.coefficient.Coefficients
import java.io.Serializable

data class Conversion(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: Coefficients = Coefficients()
) : IdentifiableObject<Conversion>, Serializable {

    override fun equals(other: Any?): Boolean = if (this === other)
        true
    else
        other is Conversion
            && this.id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun updateBy(src: Conversion) = Conversion(
        id = id,
        relatesTo = src.relatesTo or relatesTo,
        relatedItem = src.relatedItem or relatedItem,
        rationale = src.rationale or rationale,
        description = src.description or description,
        coefficients = coefficients updateBy src.coefficients
    )
}
