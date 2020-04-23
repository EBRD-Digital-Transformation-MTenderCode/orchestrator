package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.util.extension.getNewElements
import java.io.Serializable

class Verifications(values: List<Verification> = emptyList()) : List<Verification> by values,
                                                                ComplexObjects<Verification, Verifications>,
                                                                Serializable {

    override fun combineBy(src: Verifications): Verifications {
        val dstIds = this.toSet()
        val srcIds = src.toSet()
        val newIds = getNewElements(received = srcIds, known = dstIds)
        return Verifications(this + newIds)
    }
}
