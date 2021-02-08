package com.procurement.orchestrator.domain.model.scheme

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class Schemes(values: List<String> = emptyList()) : List<String> by values,
                                                        ComplexObjects<String, Schemes>,
                                                        Serializable {

    @JsonCreator
    constructor(value: String) : this(listOf(value))

    override operator fun plus(other: Schemes) = Schemes(this as List<String> + other as List<String>)

    override operator fun plus(others: List<String>) = Schemes(this as List<String> + others)

    override fun combineBy(src: Schemes) = Schemes(ComplexObjects.merge(dst = this, src = src))
}
