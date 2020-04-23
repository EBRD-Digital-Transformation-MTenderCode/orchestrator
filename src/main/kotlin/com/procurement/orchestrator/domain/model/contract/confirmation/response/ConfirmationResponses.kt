package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import com.procurement.orchestrator.domain.model.IdentifiableObjects

class ConfirmationResponses(
    values: List<ConfirmationResponse> = emptyList()
) : List<ConfirmationResponse> by values, IdentifiableObjects<ConfirmationResponse, ConfirmationResponses> {

    override fun updateBy(src: ConfirmationResponses) = ConfirmationResponses(merge(dst = this, src = src))
}
