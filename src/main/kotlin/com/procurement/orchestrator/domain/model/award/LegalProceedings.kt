package com.procurement.orchestrator.domain.model.award

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class LegalProceedings(
    values: List<LegalProceeding> = emptyList()
) : List<LegalProceeding> by values,
    IdentifiableObjects<LegalProceeding, LegalProceedings>,
    Serializable {

    constructor(value: LegalProceeding) : this(listOf(value))

    override operator fun plus(other: LegalProceedings) =
        LegalProceedings(this as List<LegalProceeding> + other as List<LegalProceeding>)

    override operator fun plus(others: List<LegalProceeding>) = LegalProceedings(this as List<LegalProceeding> + others)

    override fun updateBy(src: LegalProceedings) = LegalProceedings(update(dst = this, src = src))
}
