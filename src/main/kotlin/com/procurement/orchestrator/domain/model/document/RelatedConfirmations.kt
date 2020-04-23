package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RelatedConfirmations(
    values: List<RelatedConfirmation> = emptyList()
) : List<RelatedConfirmation> by values,
    ComplexObjects<RelatedConfirmation, RelatedConfirmations>,
    Serializable {

    override fun combineBy(src: RelatedConfirmations) = RelatedConfirmations()
}
