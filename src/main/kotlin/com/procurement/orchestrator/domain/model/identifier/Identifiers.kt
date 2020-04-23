package com.procurement.orchestrator.domain.model.identifier

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Identifiers(values: List<Identifier> = emptyList()) : List<Identifier> by values,
                                                            IdentifiableObjects<Identifier, Identifiers>,
                                                            Serializable {

    override fun updateBy(src: Identifiers) = Identifiers(update(dst = this, src = src))
}
