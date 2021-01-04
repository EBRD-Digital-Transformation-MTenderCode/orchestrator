package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.IdentifiableObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class ProcessMasterData(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mdm") @param:JsonProperty("mdm") val mdm: Mdm?
) : IdentifiableObject<ProcessMasterData>, Serializable {

    override fun updateBy(src: ProcessMasterData) = ProcessMasterData(
        mdm = src.mdm or mdm
    )
}