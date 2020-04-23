package com.procurement.orchestrator.domain.model.contract.observation

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Observations(values: List<Observation> = emptyList()) : List<Observation> by values,
                                                              IdentifiableObjects<Observation, Observations>,
                                                              Serializable {

    override fun updateBy(src: Observations) = Observations(update(dst = this, src = src))
}
