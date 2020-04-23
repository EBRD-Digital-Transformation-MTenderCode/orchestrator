package com.procurement.orchestrator.domain.model.contract.milestone

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class RelatedParties(
    values: List<RelatedParty> = emptyList()
) : List<RelatedParty> by values, IdentifiableObjects<RelatedParty, RelatedParties> {

    override fun updateBy(src: RelatedParties) = RelatedParties(update(dst = this, src = src))
}
