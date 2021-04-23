package com.procurement.orchestrator.domain.model.mdm

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Tenderer(
    @get:JsonProperty("organizations") @param:JsonProperty("organizations") val organizations: Organizations,
    @get:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner
) : ComplexObject<Tenderer>, Serializable {

    override fun updateBy(src: Tenderer) = Tenderer(
        owner = src.owner or owner,
        organizations = organizations updateBy src.organizations
    )
}
