package com.procurement.orchestrator.domain.model.contract

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RelatedProcesses(
    values: List<RelatedProcess> = emptyList()
) : List<RelatedProcess> by values,
    IdentifiableObjects<RelatedProcess, RelatedProcesses>,
    Serializable {

    constructor(value: RelatedProcess) : this(listOf(value))

    override operator fun plus(other: RelatedProcesses) =
        RelatedProcesses(this as List<RelatedProcess> + other as List<RelatedProcess>)

    override operator fun plus(others: List<RelatedProcess>) = RelatedProcesses(this as List<RelatedProcess> + others)

    override fun updateBy(src: RelatedProcesses) = RelatedProcesses(update(dst = this, src = src))
}
