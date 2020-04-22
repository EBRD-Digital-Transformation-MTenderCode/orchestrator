package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class RequestGroups(
    values: List<RequestGroup> = emptyList()
) : List<RequestGroup> by values, IdentifiableObjects<RequestGroup, RequestGroups> {

    override fun updateBy(src: RequestGroups) = RequestGroups(update(dst = this, src = src))
}
