package com.procurement.orchestrator.domain.model.requirement.response

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RequirementResponses(
    values: List<RequirementResponse> = emptyList()
) : List<RequirementResponse> by values,
    IdentifiableObjects<RequirementResponse, RequirementResponses>,
    Serializable {

    constructor(value: RequirementResponse) : this(listOf(value))

    override operator fun plus(other: RequirementResponses) =
        RequirementResponses(this as List<RequirementResponse> + other as List<RequirementResponse>)

    override operator fun plus(others: List<RequirementResponse>) =
        RequirementResponses(this as List<RequirementResponse> + others)

    override fun updateBy(src: RequirementResponses) = RequirementResponses(update(dst = this, src = src))
}
