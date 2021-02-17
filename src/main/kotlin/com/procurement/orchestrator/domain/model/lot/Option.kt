package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Option(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasOptions") @param:JsonProperty("hasOptions") val hasOptions: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("optionDetails") @param:JsonProperty("optionDetails") val optionDetails: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?

) : ComplexObject<Option>, Serializable {

    override fun updateBy(src: Option) = Option(
        hasOptions = src.hasOptions or hasOptions,
        optionDetails = src.optionDetails or optionDetails,
        description = src.description or description,
        period = period updateBy src.period
    )
}
