package com.procurement.orchestrator.domain.model.award

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Awards(values: List<Award> = emptyList()) : List<Award> by values,
                                                  IdentifiableObjects<Award, Awards>,
                                                  Serializable {

    constructor(award: Award) : this(listOf(award))

    operator fun plus(other: Awards): Awards = Awards(this as List<Award> + other as List<Award>)

    operator fun plus(others: List<Award>): Awards = Awards(this as List<Award> + others)

    override fun updateBy(src: Awards) = Awards(update(dst = this, src = src))
}
