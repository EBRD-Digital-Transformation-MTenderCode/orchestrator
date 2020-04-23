package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Variants(values: List<Variant> = emptyList()) : List<Variant> by values,
                                                      ComplexObjects<Variant, Variants>,
                                                      Serializable {

    override fun combineBy(src: Variants) = Variants(merge(dst = this, src = src))
}
