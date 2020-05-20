package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RelatedConfirmations(
    values: List<RelatedConfirmation> = emptyList()
) : List<RelatedConfirmation> by values,
    ComplexObjects<RelatedConfirmation, RelatedConfirmations>,
    Serializable {

    constructor(value: RelatedConfirmation) : this(listOf(value))

    override operator fun plus(other: RelatedConfirmations) =
        RelatedConfirmations(this as List<RelatedConfirmation> + other as List<RelatedConfirmation>)

    override operator fun plus(others: List<RelatedConfirmation>) =
        RelatedConfirmations(this as List<RelatedConfirmation> + others)

    override fun combineBy(src: RelatedConfirmations) = RelatedConfirmations()
}
