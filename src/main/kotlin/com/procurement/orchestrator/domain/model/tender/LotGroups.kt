package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class LotGroups(values: List<LotGroup> = emptyList()) : List<LotGroup> by values,
                                                        IdentifiableObjects<LotGroup, LotGroups>,
                                                        Serializable {

    constructor(value: LotGroup) : this(listOf(value))

    override operator fun plus(other: LotGroups) = LotGroups(this as List<LotGroup> + other as List<LotGroup>)

    override operator fun plus(others: List<LotGroup>) = LotGroups(this as List<LotGroup> + others)

    override fun updateBy(src: LotGroups) = LotGroups(update(dst = this, src = src))
}
