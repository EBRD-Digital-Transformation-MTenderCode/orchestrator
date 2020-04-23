package com.procurement.orchestrator.domain.model.tender.conversion

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Conversions(values: List<Conversion> = emptyList()) : List<Conversion> by values,
                                                            IdentifiableObjects<Conversion, Conversions>,
                                                            Serializable {

    constructor(value: Conversion) : this(listOf(value))

    override operator fun plus(other: Conversions) = Conversions(this as List<Conversion> + other as List<Conversion>)

    override operator fun plus(others: List<Conversion>) = Conversions(this as List<Conversion> + others)

    override fun updateBy(src: Conversions) = Conversions(update(dst = this, src = src))
}
