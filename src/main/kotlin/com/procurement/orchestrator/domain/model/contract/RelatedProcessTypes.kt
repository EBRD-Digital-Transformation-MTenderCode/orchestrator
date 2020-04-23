package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge

class RelatedProcessTypes(values: List<RelatedProcessType> = emptyList()) : List<RelatedProcessType> by values,
    ComplexObjects<RelatedProcessType, RelatedProcessTypes> {

    override fun combineBy(src: RelatedProcessTypes) = RelatedProcessTypes(merge(dst = this, src = src))
}
