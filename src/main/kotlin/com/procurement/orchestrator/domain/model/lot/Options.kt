package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class Options(values: List<Option>) : List<Option> by values, ComplexObjects<Option, Options> {

    override fun combineBy(src: Options) = Options(merge(dst = this, src = src))
}
