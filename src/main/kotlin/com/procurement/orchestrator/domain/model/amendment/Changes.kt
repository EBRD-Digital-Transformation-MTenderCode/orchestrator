package com.procurement.orchestrator.domain.model.amendment

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Changes(values: List<Change> = emptyList()) : List<Change> by values,
                                                    ComplexObjects<Change, Changes>,
                                                    Serializable {

    override fun combineBy(src: Changes) = Changes(merge(dst = this, src = src))
}
