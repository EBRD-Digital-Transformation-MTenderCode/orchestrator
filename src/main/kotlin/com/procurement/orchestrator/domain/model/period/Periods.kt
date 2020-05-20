package com.procurement.orchestrator.domain.model.period

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Periods(values: List<Period> = emptyList()) : List<Period> by values,
                                                    ComplexObjects<Period, Periods>,
                                                    Serializable {

    constructor(value: Period) : this(listOf(value))

    override operator fun plus(other: Periods) = Periods(this as List<Period> + other as List<Period>)

    override operator fun plus(others: List<Period>) = Periods(this as List<Period> + others)

    override fun combineBy(src: Periods) = Periods(merge(dst = this, src = src))
}
