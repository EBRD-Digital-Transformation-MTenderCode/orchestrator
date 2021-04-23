package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class Submission(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("tenderers") @param:JsonProperty("tenderers") val tenderers: Tenderers = Tenderers()
) : ComplexObject<Submission>, Serializable {

    override fun updateBy(src: Submission) = Submission(
        tenderers = tenderers combineBy src.tenderers
    )
}
