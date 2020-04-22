package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects

class SubmissionMethodRationales(
    values: List<SubmissionMethodRationale> = emptyList()
) : List<SubmissionMethodRationale> by values, ComplexObjects<SubmissionMethodRationale, SubmissionMethodRationales> {

    override fun combineBy(src: SubmissionMethodRationales) =
        SubmissionMethodRationales(ComplexObjects.merge(dst = this, src = src))
}
