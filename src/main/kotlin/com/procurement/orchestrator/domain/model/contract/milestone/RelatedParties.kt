package com.procurement.orchestrator.domain.model.contract.milestone

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RelatedParties(values: List<RelatedParty> = emptyList()) : List<RelatedParty> by values,
                                                                 IdentifiableObjects<RelatedParty, RelatedParties>,
                                                                 Serializable {

    constructor(value: RelatedParty) : this(listOf(value))

    override operator fun plus(other: RelatedParties) =
        RelatedParties(this as List<RelatedParty> + other as List<RelatedParty>)

    override operator fun plus(others: List<RelatedParty>) = RelatedParties(this as List<RelatedParty> + others)

    override fun updateBy(src: RelatedParties) = RelatedParties(update(dst = this, src = src))
}
