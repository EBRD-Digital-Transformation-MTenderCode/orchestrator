package com.procurement.orchestrator.domain.model.classification

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Classifications(
    values: List<Classification> = emptyList()
) : List<Classification> by values,
    IdentifiableObjects<Classification, Classifications>,
    Serializable {

    constructor(value: Classification) : this(listOf(value))

    override operator fun plus(other: Classifications) =
        Classifications(this as List<Classification> + other as List<Classification>)

    override operator fun plus(others: List<Classification>) = Classifications(this as List<Classification> + others)

    override fun updateBy(src: Classifications) = Classifications(update(dst = this, src = src))
}
