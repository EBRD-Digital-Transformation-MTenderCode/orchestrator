package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RequirementGroups(
    values: List<RequirementGroup> = emptyList()
) : List<RequirementGroup> by values,
    IdentifiableObjects<RequirementGroup, RequirementGroups>,
    Serializable {

    override fun updateBy(src: RequirementGroups) = RequirementGroups(update(dst = this, src = src))
}
