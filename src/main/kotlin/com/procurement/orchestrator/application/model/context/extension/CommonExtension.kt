package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.functional.asSuccess

fun <T> List<T>.getElementIfOnlyOne(name: String, path: String? = null): Result<T, Fail.Incident.Bpms.Context> {
    if (this.isEmpty())
        return failure(Fail.Incident.Bpms.Context.Empty(name = name, path = path))

    return if (this.size != 1)
        failure(
            Fail.Incident.Bpms.Context.ExpectedNumber(
                name = name,
                path = path,
                expected = 1,
                actual = this.size
            )
        )
    else
        success(this[0])
}

fun <T> List<T>.getIfNotEmpty(name: String, path: String? = null): Result<List<T>, Fail.Incident.Bpms.Context> =
    if (this.isEmpty())
        failure(Fail.Incident.Bpms.Context.Empty(name = name, path = path))
    else
        this.asSuccess()
