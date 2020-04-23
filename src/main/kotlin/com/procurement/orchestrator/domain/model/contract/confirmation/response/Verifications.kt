package com.procurement.orchestrator.domain.model.contract.confirmation.response

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Verifications(values: List<Verification> = emptyList()) : List<Verification> by values,
                                                                ComplexObjects<Verification, Verifications>,
                                                                Serializable {

    constructor(value: Verification) : this(listOf(value))

    override operator fun plus(other: Verifications) =
        Verifications(this as List<Verification> + other as List<Verification>)

    override operator fun plus(others: List<Verification>) = Verifications(this as List<Verification> + others)

    override fun combineBy(src: Verifications) = Verifications(merge(dst = this, src = src))
}
