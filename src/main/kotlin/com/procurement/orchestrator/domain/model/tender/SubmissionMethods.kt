package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class SubmissionMethods(
    values: List<SubmissionMethod> = emptyList()
) : List<SubmissionMethod> by values,
    ComplexObjects<SubmissionMethod, SubmissionMethods>,
    Serializable {

    @JsonCreator
    constructor(vararg values: SubmissionMethod) : this(values.toList())

    override operator fun plus(other: SubmissionMethods) =
        SubmissionMethods(this as List<SubmissionMethod> + other as List<SubmissionMethod>)

    override operator fun plus(others: List<SubmissionMethod>) =
        SubmissionMethods(this as List<SubmissionMethod> + others)

    override fun combineBy(src: SubmissionMethods) =
        SubmissionMethods(ComplexObjects.merge(dst = this, src = src))
}
