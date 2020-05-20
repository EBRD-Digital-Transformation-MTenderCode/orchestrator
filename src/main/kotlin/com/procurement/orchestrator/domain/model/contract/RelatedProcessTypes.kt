package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class RelatedProcessTypes(
    values: List<RelatedProcessType> = emptyList()
) : List<RelatedProcessType> by values,
    ComplexObjects<RelatedProcessType, RelatedProcessTypes>,
    Serializable {

    constructor(value: RelatedProcessType) : this(listOf(value))

    override operator fun plus(other: RelatedProcessTypes) =
        RelatedProcessTypes(this as List<RelatedProcessType> + other as List<RelatedProcessType>)

    override operator fun plus(others: List<RelatedProcessType>) =
        RelatedProcessTypes(this as List<RelatedProcessType> + others)

    override fun combineBy(src: RelatedProcessTypes) = RelatedProcessTypes(merge(dst = this, src = src))
}
