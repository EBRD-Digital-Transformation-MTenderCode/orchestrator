package com.procurement.orchestrator.domain.model.contract.observation

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Dimensions(
    @field:JsonProperty("requirementClassIdPR") @param:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
) : ComplexObject<Dimensions>, Serializable {

    override fun updateBy(src: Dimensions) = Dimensions(
        requirementClassIdPR = src.requirementClassIdPR or requirementClassIdPR

    )
}
