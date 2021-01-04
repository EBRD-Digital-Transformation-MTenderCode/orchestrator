package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import java.io.Serializable

data class Mdm(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: Criteria = Criteria()
) : IdentifiableObject<Mdm>, Serializable {

    override fun updateBy(src: Mdm) = Mdm(
        criteria = criteria updateBy src.criteria
    )
}
