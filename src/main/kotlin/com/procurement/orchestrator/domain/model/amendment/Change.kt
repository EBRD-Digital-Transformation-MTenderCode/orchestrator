package com.procurement.orchestrator.domain.model.amendment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Change(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("property") @param:JsonProperty("property") val property: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("formerValue") @param:JsonProperty("formerValue") val formerValue: Any? = null
) : ComplexObject<Change>, Serializable {

    override fun updateBy(src: Change) = Change(
        property = src.property or property,
        formerValue = src.formerValue or formerValue
    )
}
