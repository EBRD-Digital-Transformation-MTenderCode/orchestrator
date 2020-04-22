package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class RequirementGroups(
    values: List<RequirementGroup> = emptyList()
) : List<RequirementGroup> by values, IdentifiableObjects<RequirementGroup, RequirementGroups> {

    override fun updateBy(src: RequirementGroups) = RequirementGroups(update(dst = this, src = src))
}
