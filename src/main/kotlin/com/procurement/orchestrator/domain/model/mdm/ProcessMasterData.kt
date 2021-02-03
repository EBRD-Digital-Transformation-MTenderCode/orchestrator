package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.scheme.RegistrationSchemes
import com.procurement.orchestrator.domain.model.updateBy
import java.io.Serializable

data class ProcessMasterData(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("mdm") @param:JsonProperty("mdm") val mdm: Mdm? = null,

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("registrationSchemes") @param:JsonProperty("registrationSchemes") val registrationSchemes: RegistrationSchemes = RegistrationSchemes()
) : ComplexObject<ProcessMasterData>, Serializable {

    override fun updateBy(src: ProcessMasterData) = ProcessMasterData(
        mdm = mdm updateBy src.mdm,
        registrationSchemes = registrationSchemes combineBy src.registrationSchemes
    )
}