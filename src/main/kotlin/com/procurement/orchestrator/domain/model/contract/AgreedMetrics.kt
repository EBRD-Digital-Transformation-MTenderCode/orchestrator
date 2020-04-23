package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class AgreedMetrics(values: List<AgreedMetric> = emptyList()) : List<AgreedMetric> by values,
                                                                IdentifiableObjects<AgreedMetric, AgreedMetrics>,
                                                                Serializable {

    constructor(value: AgreedMetric) : this(listOf(value))

    override operator fun plus(other: AgreedMetrics) =
        AgreedMetrics(this as List<AgreedMetric> + other as List<AgreedMetric>)

    override operator fun plus(others: List<AgreedMetric>) = AgreedMetrics(this as List<AgreedMetric> + others)

    override fun updateBy(src: AgreedMetrics) = AgreedMetrics(update(dst = this, src = src))
}
