package com.procurement.orchestrator.domain.model.requirement.response

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RequirementResponses(
    values: List<RequirementResponse> = emptyList()
) : List<RequirementResponse> by values,
    IdentifiableObjects<RequirementResponse, RequirementResponses>,
    Serializable {

    constructor(requirementResponse: RequirementResponse) : this(listOf(requirementResponse))

    override fun updateBy(src: RequirementResponses) = RequirementResponses(update(dst = this, src = src))
}
