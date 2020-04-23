package com.procurement.orchestrator.domain.model.contract.observation

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Observations(
    values: List<Observation> = emptyList()
) : List<Observation> by values, IdentifiableObjects<Observation, Observations> {

    override fun updateBy(src: Observations) = Observations(update(dst = this, src = src))
}
