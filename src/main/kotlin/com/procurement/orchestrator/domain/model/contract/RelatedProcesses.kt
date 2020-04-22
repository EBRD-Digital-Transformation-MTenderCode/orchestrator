package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class RelatedProcesses(
    values: List<RelatedProcess> = emptyList()
) : List<RelatedProcess> by values, IdentifiableObjects<RelatedProcess, RelatedProcesses> {

    override fun updateBy(src: RelatedProcesses) = RelatedProcesses(update(dst = this, src = src))
}
