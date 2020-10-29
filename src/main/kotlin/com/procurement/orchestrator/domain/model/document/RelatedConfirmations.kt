package com.procurement.orchestrator.domain.model.document

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RelatedConfirmations(
    values: List<RelatedConfirmation> = emptyList()
) : List<RelatedConfirmation> by values,
    ComplexObjects<RelatedConfirmation, RelatedConfirmations>,
    Serializable {

    @JsonCreator
    constructor(vararg values: RelatedConfirmation) : this(values.toList())

    override operator fun plus(other: RelatedConfirmations) =
        RelatedConfirmations(this as List<RelatedConfirmation> + other as List<RelatedConfirmation>)

    override operator fun plus(others: List<RelatedConfirmation>) =
        RelatedConfirmations(this as List<RelatedConfirmation> + others)

    override fun combineBy(src: RelatedConfirmations) = RelatedConfirmations()
}
