package com.procurement.orchestrator.domain.model.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import com.procurement.orchestrator.domain.model.period.Periods
import java.io.Serializable

data class RecurrentProcurement(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("isRecurrent") @param:JsonProperty("isRecurrent") val isRecurrent: Boolean? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("dates") @param:JsonProperty("dates") val dates: Periods = Periods(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null
) : ComplexObject<RecurrentProcurement>, Serializable {

    override fun updateBy(src: RecurrentProcurement) = RecurrentProcurement(
        isRecurrent = src.isRecurrent or isRecurrent,
        dates = src.dates combineBy dates,
        description = src.description or description
    )
}
