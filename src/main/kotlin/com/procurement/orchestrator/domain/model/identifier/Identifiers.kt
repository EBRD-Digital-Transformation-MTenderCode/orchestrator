package com.procurement.orchestrator.domain.model.identifier

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Identifiers(values: List<Identifier> = emptyList()) : List<Identifier> by values,
                                                            IdentifiableObjects<Identifier, Identifiers>,
                                                            Serializable {

    constructor(value: Identifier) : this(listOf(value))

    override operator fun plus(other: Identifiers) = Identifiers(this as List<Identifier> + other as List<Identifier>)

    override operator fun plus(others: List<Identifier>) = Identifiers(this as List<Identifier> + others)

    override fun updateBy(src: Identifiers) = Identifiers(update(dst = this, src = src))
}
