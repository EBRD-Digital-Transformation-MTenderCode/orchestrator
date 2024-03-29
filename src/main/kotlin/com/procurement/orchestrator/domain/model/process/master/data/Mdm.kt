package com.procurement.orchestrator.domain.model.process.master.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.scheme.RegistrationSchemes
import com.procurement.orchestrator.domain.model.tender.criteria.Criteria
import java.io.Serializable

data class Mdm(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: Criteria = Criteria(),

    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("registrationSchemes") @param:JsonProperty("registrationSchemes") val registrationSchemes: RegistrationSchemes = RegistrationSchemes()
) : ComplexObject<Mdm>, Serializable {

    override fun updateBy(src: Mdm) = Mdm(
        criteria = criteria updateBy src.criteria,
        registrationSchemes = registrationSchemes combineBy src.registrationSchemes
    )
}
