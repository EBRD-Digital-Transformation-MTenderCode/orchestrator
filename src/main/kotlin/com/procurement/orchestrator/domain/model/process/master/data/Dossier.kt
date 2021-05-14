package com.procurement.orchestrator.domain.model.process.master.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.domain.model.ComplexObject
import java.io.Serializable

data class Dossier(
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("candidates") @param:JsonProperty("candidates") val candidates: Candidates = Candidates()
) : ComplexObject<Dossier>, Serializable {

    override fun updateBy(src: Dossier) = Dossier(
        candidates = candidates combineBy src.candidates
    )
}
