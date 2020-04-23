package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class ConfirmationRequests(
    values: List<ConfirmationRequest> = emptyList()
) : List<ConfirmationRequest> by values,
    IdentifiableObjects<ConfirmationRequest, ConfirmationRequests>,
    Serializable {

    override fun updateBy(src: ConfirmationRequests) = ConfirmationRequests(update(dst = this, src = src))
}
