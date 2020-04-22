package com.procurement.orchestrator.domain.model.classification

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class Classifications(
    values: List<Classification> = emptyList()
) : List<Classification> by values, IdentifiableObjects<Classification, Classifications> {

    override fun updateBy(src: Classifications) = Classifications(update(dst = this, src = src))
}
