package com.procurement.orchestrator.domain.model.tender

import com.procurement.orchestrator.domain.model.ComplexObjects

class SubmissionMethods(
    values: List<SubmissionMethod> = emptyList()
) : List<SubmissionMethod> by values, ComplexObjects<SubmissionMethod, SubmissionMethods> {

    override fun combineBy(src: SubmissionMethods) =
        SubmissionMethods(ComplexObjects.merge(dst = this, src = src))
}
