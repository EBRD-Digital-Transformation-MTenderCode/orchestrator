package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class AgreedMetrics(
    values: List<AgreedMetric> = emptyList()
) : List<AgreedMetric> by values, IdentifiableObjects<AgreedMetric, AgreedMetrics> {

    override fun updateBy(src: AgreedMetrics) = AgreedMetrics(update(dst = this, src = src))
}
