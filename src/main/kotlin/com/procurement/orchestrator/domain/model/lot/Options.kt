package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Options(values: List<Option> = emptyList()) : List<Option> by values,
                                                    ComplexObjects<Option, Options>,
                                                    Serializable {

    override fun combineBy(src: Options) = Options(merge(dst = this, src = src))
}
