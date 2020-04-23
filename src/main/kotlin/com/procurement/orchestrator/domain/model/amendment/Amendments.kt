package com.procurement.orchestrator.domain.model.amendment

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Amendments(values: List<Amendment> = emptyList()) : List<Amendment> by values,
                                                          IdentifiableObjects<Amendment, Amendments>,
                                                          Serializable {

    constructor(value: Amendment) : this(listOf(value))

    override fun plus(other: Amendments) = Amendments(this as List<Amendment> + other as List<Amendment>)

    override operator fun plus(others: List<Amendment>) = Amendments(this as List<Amendment> + others)

    override fun updateBy(src: Amendments) = Amendments(update(dst = this, src = src))
}
