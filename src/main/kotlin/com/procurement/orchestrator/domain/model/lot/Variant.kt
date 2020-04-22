package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Variant(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("variantDetails") @param:JsonProperty("variantDetails") val variantDetails: String? = null
) : ComplexObject<Variant>, Serializable {

    override fun updateBy(src: Variant) = Variant(
        hasVariants = src.hasVariants or hasVariants,
        variantDetails = src.variantDetails or variantDetails
    )
}
