package com.procurement.orchestrator.domain.model.amendment

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Amendments(values: List<Amendment> = emptyList()) : List<Amendment> by values,
                                                          IdentifiableObjects<Amendment, Amendments>,
                                                          Serializable {

    constructor(amendment: Amendment) : this(listOf(amendment))

    operator fun plus(values: Amendments) = Amendments(this as List<Amendment> + values as List<Amendment>)

    operator fun plus(values: List<Amendment>) = Amendments(this as List<Amendment> + values)

    override fun updateBy(src: Amendments) = Amendments(update(dst = this, src = src))
}
