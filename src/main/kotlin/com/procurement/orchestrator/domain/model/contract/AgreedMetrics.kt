package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class AgreedMetrics(values: List<AgreedMetric> = emptyList()) : List<AgreedMetric> by values,
                                                                IdentifiableObjects<AgreedMetric, AgreedMetrics>,
                                                                Serializable {

    override fun updateBy(src: AgreedMetrics) = AgreedMetrics(update(dst = this, src = src))
}
