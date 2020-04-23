package com.procurement.orchestrator.domain.model.award

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Awards(values: List<Award> = emptyList()) : List<Award> by values,
                                                  IdentifiableObjects<Award, Awards>,
                                                  Serializable {

    constructor(value: Award) : this(listOf(value))

    override operator fun plus(other: Awards) = Awards(this as List<Award> + other as List<Award>)

    override operator fun plus(others: List<Award>) = Awards(this as List<Award> + others)

    override fun updateBy(src: Awards) = Awards(update(dst = this, src = src))
}
