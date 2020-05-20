package com.procurement.orchestrator.domain.model.contract.observation

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Observations(values: List<Observation> = emptyList()) : List<Observation> by values,
                                                              IdentifiableObjects<Observation, Observations>,
                                                              Serializable {

    constructor(value: Observation) : this(listOf(value))

    override operator fun plus(other: Observations) =
        Observations(this as List<Observation> + other as List<Observation>)

    override operator fun plus(others: List<Observation>) = Observations(this as List<Observation> + others)

    override fun updateBy(src: Observations) = Observations(update(dst = this, src = src))
}
