package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.ComplexObjects
import com.procurement.orchestrator.domain.model.ComplexObjects.Companion.merge
import java.io.Serializable

class Options(values: List<Option> = emptyList()) : List<Option> by values,
                                                    ComplexObjects<Option, Options>,
                                                    Serializable {

    constructor(value: Option) : this(listOf(value))

    override operator fun plus(other: Options) = Options(this as List<Option> + other as List<Option>)

    override operator fun plus(others: List<Option>) = Options(this as List<Option> + others)

    override fun combineBy(src: Options) = Options(merge(dst = this, src = src))
}
