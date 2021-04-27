package com.procurement.orchestrator.domain.model.masterdata.access

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class Access(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("buyers") @param:JsonProperty("buyers") val buyers: Buyers = Buyers()

) : ComplexObject<Access>, Serializable {

    override fun updateBy(src: Access) = Access(
        buyers = buyers updateBy src.buyers
    )
}
