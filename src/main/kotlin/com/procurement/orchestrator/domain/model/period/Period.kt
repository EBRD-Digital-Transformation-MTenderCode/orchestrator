package com.procurement.orchestrator.domain.model.period

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
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
) : Serializable
