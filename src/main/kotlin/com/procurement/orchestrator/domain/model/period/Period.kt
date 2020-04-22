package com.procurement.orchestrator.domain.model.period

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable
import java.time.LocalDateTime

data class Period(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime? = null,

    //TODO not using
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maxExtentDate") @param:JsonProperty("maxExtentDate") val maxExtentDate: LocalDateTime? = null,

    //TODO not using
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("durationInDays") @param:JsonProperty("durationInDays") val durationInDays: Int? = null
) : ComplexObject<Period>, Serializable {

    override fun updateBy(src: Period) = Period(
        startDate = src.startDate or startDate,
        endDate = src.endDate or endDate,
        maxExtentDate = src.maxExtentDate or maxExtentDate,
        durationInDays = src.durationInDays or durationInDays
    )
}
