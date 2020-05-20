package com.procurement.orchestrator.domain.model.submission

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class Submissions(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("details") @param:JsonProperty("details") val details: Details = Details()
) : ComplexObject<Submissions>, Serializable {

    override fun updateBy(src: Submissions) = Submissions(
        details = details updateBy src.details
    )
}
