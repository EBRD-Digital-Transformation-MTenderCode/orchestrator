package com.procurement.orchestrator.domain.model.tender.conversion.coefficient

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Coefficients(
    values: List<Coefficient> = emptyList()
) : List<Coefficient> by values,
    IdentifiableObjects<Coefficient, Coefficients>,
    Serializable {

    override fun updateBy(src: Coefficients) = Coefficients(update(dst = this, src = src))
}
