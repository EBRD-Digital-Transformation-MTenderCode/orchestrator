package com.procurement.orchestrator.domain.model.contract.confirmation.request

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RequestGroups(values: List<RequestGroup> = emptyList()) : List<RequestGroup> by values,
                                                                IdentifiableObjects<RequestGroup, RequestGroups>,
                                                                Serializable {

    constructor(value: RequestGroup) : this(listOf(value))

    override operator fun plus(other: RequestGroups) =
        RequestGroups(this as List<RequestGroup> + other as List<RequestGroup>)

    override operator fun plus(others: List<RequestGroup>) = RequestGroups(this as List<RequestGroup> + others)

    override fun updateBy(src: RequestGroups) = RequestGroups(update(dst = this, src = src))
}
