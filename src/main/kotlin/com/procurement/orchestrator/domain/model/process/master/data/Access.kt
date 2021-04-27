package com.procurement.orchestrator.domain.model.process.master.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class Access(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("buyers") @param:JsonProperty("buyers") val buyers: Buyers = Buyers(),

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("procuringEntity") @param:JsonProperty("procuringEntity") val procuringEntity: ProcuringEntity? = null
) : ComplexObject<Access>, Serializable {

    override fun updateBy(src: Access) = Access(
        buyers = buyers updateBy src.buyers,
        procuringEntity = procuringEntity updateBy src.procuringEntity
    )
}
