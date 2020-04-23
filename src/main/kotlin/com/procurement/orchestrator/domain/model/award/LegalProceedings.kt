package com.procurement.orchestrator.domain.model.award

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class LegalProceedings(
    values: List<LegalProceeding> = emptyList()
) : List<LegalProceeding> by values,
    IdentifiableObjects<LegalProceeding, LegalProceedings>,
    Serializable {

    override fun updateBy(src: LegalProceedings) = LegalProceedings(update(dst = this, src = src))
}
