package com.procurement.orchestrator.domain.model.tender.criteria.requirement

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class RequirementGroups(
    values: List<RequirementGroup> = emptyList()
) : List<RequirementGroup> by values,
    IdentifiableObjects<RequirementGroup, RequirementGroups>,
    Serializable {

    constructor(value: RequirementGroup) : this(listOf(value))

    override operator fun plus(other: RequirementGroups) =
        RequirementGroups(this as List<RequirementGroup> + other as List<RequirementGroup>)

    override operator fun plus(others: List<RequirementGroup>) =
        RequirementGroups(this as List<RequirementGroup> + others)

    override fun updateBy(src: RequirementGroups) = RequirementGroups(update(dst = this, src = src))
}
