package com.procurement.orchestrator.domain.model.tender.conversion

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Conversions(values: List<Conversion> = emptyList()) : List<Conversion> by values,
                                                            IdentifiableObjects<Conversion, Conversions>,
                                                            Serializable {

    override fun updateBy(src: Conversions) = Conversions(update(dst = this, src = src))
}
