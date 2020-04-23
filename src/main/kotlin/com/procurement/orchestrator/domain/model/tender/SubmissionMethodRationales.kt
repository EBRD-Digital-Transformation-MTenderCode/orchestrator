package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class SubmissionMethodRationales(
    values: List<SubmissionMethodRationale> = emptyList()
) : List<SubmissionMethodRationale> by values,
    ComplexObjects<SubmissionMethodRationale, SubmissionMethodRationales>,
    Serializable {

    constructor(value: SubmissionMethodRationale) : this(listOf(value))

    override operator fun plus(other: SubmissionMethodRationales) =
        SubmissionMethodRationales(this as List<SubmissionMethodRationale> + other as List<SubmissionMethodRationale>)

    override operator fun plus(others: List<SubmissionMethodRationale>) =
        SubmissionMethodRationales(this as List<SubmissionMethodRationale> + others)

    override fun combineBy(src: SubmissionMethodRationales) =
        SubmissionMethodRationales(ComplexObjects.merge(dst = this, src = src))
}
