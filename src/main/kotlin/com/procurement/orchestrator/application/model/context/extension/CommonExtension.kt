package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess

fun <T> List<T>.getElementIfOnlyOne(path: String): Result<T, Fail.Incident.Bpmn.Context> {
    if (this.isEmpty())
        return failure(Fail.Incident.Bpmn.Context.Empty(path = path))

    return if (this.size != 1)
        failure(
            Fail.Incident.Bpmn.Context.UnConsistency(
                name = path,
                description = "The attribute by the path '$path' should have only one element. Actually the attribute has ${this.size} elements."
            )
        )
    else
        success(this[0])
}

fun <T> List<T>.getElementIfNotEmpty(path: String): Result<List<T>, Fail.Incident.Bpmn.Context> =
    if (this.isEmpty())
        failure(Fail.Incident.Bpmn.Context.Empty(path = path))
    else
        this.asSuccess()
