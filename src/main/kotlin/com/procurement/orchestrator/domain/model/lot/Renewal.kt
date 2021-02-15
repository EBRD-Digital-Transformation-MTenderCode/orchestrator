package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Period
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Renewal(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("hasRenewals") @param:JsonProperty("hasRenewals") val hasRenewals: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maxNumber") @param:JsonProperty("maxNumber") val maxNumber: Int? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("renewalConditions") @param:JsonProperty("renewalConditions") val renewalConditions: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("minimumRenewals") @field:JsonProperty("minimumRenewals") val minimumRenewals: Long?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("maximumRenewals") @field:JsonProperty("maximumRenewals") val maximumRenewals: Long?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
) : ComplexObject<Renewal>, Serializable {

    override fun updateBy(src: Renewal) = Renewal(
        hasRenewals = src.hasRenewals or hasRenewals,
        maxNumber = src.maxNumber or maxNumber,
        renewalConditions = src.renewalConditions or renewalConditions,
        period = period updateBy src.period,
        description = src.description or description,
        maximumRenewals = src.maximumRenewals or maximumRenewals,
        minimumRenewals = src.minimumRenewals or minimumRenewals
    )
}
