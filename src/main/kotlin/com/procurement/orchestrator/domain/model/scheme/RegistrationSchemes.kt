package com.procurement.orchestrator.domain.model.scheme

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class RegistrationSchemes(values: List<RegistrationScheme> = emptyList()) : List<RegistrationScheme> by values,
                                                        ComplexObjects<RegistrationScheme, RegistrationSchemes>,
                                                        Serializable {

    constructor(value: RegistrationScheme) : this(listOf(value))

    override operator fun plus(other: RegistrationSchemes) = RegistrationSchemes(this as List<RegistrationScheme> + other as List<RegistrationScheme>)

    override operator fun plus(others: List<RegistrationScheme>) = RegistrationSchemes(this as List<RegistrationScheme> + others)

    override fun combineBy(src: RegistrationSchemes) = RegistrationSchemes(ComplexObjects.merge(dst = this, src = src))
}
