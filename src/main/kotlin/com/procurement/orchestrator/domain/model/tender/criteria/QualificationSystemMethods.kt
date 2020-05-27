package com.procurement.orchestrator.domain.model.tender.criteria

import com.procurement.orchestrator.domain.model.ComplexObjects
import java.io.Serializable

class QualificationSystemMethods(
    values: List<QualificationSystemMethod> = emptyList()
) : List<QualificationSystemMethod> by values,
    ComplexObjects<QualificationSystemMethod, QualificationSystemMethods>,
    Serializable {

    constructor(value: QualificationSystemMethod) : this(listOf(value))

    override operator fun plus(other: QualificationSystemMethods) =
        QualificationSystemMethods(this as List<QualificationSystemMethod> + other as List<QualificationSystemMethod>)

    override operator fun plus(others: List<QualificationSystemMethod>) =
        QualificationSystemMethods(this as List<QualificationSystemMethod> + others)

    override fun combineBy(src: QualificationSystemMethods) =
        QualificationSystemMethods(ComplexObjects.merge(dst = this, src = src))
}
