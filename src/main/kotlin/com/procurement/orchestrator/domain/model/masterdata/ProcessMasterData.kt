package com.procurement.orchestrator.domain.model.masterdata

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.masterdata.access.Access
import com.procurement.orchestrator.domain.model.masterdata.mdm.Mdm
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class ProcessMasterData(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mdm") @param:JsonProperty("mdm") val mdm: Mdm? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("access") @param:JsonProperty("access") val access: Access? = null
) : ComplexObject<ProcessMasterData>, Serializable {

    override fun updateBy(src: ProcessMasterData) = ProcessMasterData(
        mdm = mdm updateBy src.mdm,
        access = access updateBy src.access
    )
}