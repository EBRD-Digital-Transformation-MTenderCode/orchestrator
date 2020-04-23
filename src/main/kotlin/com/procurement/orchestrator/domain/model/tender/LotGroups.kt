package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class LotGroups(values: List<LotGroup> = emptyList()) : List<LotGroup> by values,
                                                        IdentifiableObjects<LotGroup, LotGroups>,
                                                        Serializable {

    override fun updateBy(src: LotGroups) = LotGroups(update(dst = this, src = src))
}
