package com.procurement.orchestrator.domain.model.document

import com.procurement.orchestrator.domain.model.ComplexObjects

class RelatedConfirmations(
    values: List<RelatedConfirmation> = emptyList()
) : List<RelatedConfirmation> by values, ComplexObjects<RelatedConfirmation, RelatedConfirmations> {

    override fun combineBy(src: RelatedConfirmations) = RelatedConfirmations()
}
