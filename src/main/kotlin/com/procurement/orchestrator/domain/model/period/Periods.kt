package com.procurement.orchestrator.domain.model.period

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Periods(values: List<Period> = emptyList()) : List<Period> by values,
                                                    ComplexObjects<Period, Periods>,
                                                    Serializable {

    override fun combineBy(src: Periods) = Periods(merge(dst = this, src = src))
}
