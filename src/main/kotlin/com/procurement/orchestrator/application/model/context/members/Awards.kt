package com.procurement.orchestrator.application.model.context.members

import com.procurement.orchestrator.domain.model.award.Award
import java.io.Serializable

class Awards(values: List<Award>) : List<Award> by values, Serializable {

    constructor(award: Award) : this(listOf(award))

    operator fun plus(other: Awards): Awards = Awards(this as List<Award> + other as List<Award>)

    operator fun plus(others: List<Award>): Awards = Awards(this as List<Award> + others)
}
