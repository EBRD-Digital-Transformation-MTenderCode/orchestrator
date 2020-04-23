package com.procurement.orchestrator.domain.model.amendment

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Changes(values: List<Change> = emptyList()) : List<Change> by values,
                                                    ComplexObjects<Change, Changes>,
                                                    Serializable {

    constructor(value: Change) : this(listOf(value))

    override operator fun plus(other: Changes) = Changes(this as List<Change> + other as List<Change>)

    override operator fun plus(others: List<Change>) = Changes(this as List<Change> + others)

    override fun combineBy(src: Changes) = Changes(merge(dst = this, src = src))
}
