package com.procurement.orchestrator.domain.model.process.master.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import com.procurement.orchestrator.domain.model.or
import java.io.Serializable

data class Candidate(
    @get:JsonProperty("organizations") @param:JsonProperty("organizations") val organizations: Organizations,
    @get:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
) : ComplexObject<Candidate>, Serializable {

    override fun updateBy(src: Candidate) = Candidate(
        owner = src.owner or owner,
        organizations = organizations updateBy src.organizations
    )
}
