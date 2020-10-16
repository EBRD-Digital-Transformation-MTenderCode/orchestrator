package com.procurement.orchestrator.domain.model.tender.target

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Targets(values: List<Target> = emptyList()) : List<Target> by values,
                                                    IdentifiableObjects<Target, Targets>,
                                                    Serializable {

    constructor(value: Target) : this(listOf(value))

    override operator fun plus(other: Targets) = Targets(this as List<Target> + other as List<Target>)

    override operator fun plus(others: List<Target>) = Targets(this as List<Target> + others)

    override fun updateBy(src: Targets) = Targets(update(dst = this, src = src))
}
