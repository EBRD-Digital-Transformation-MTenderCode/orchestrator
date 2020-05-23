package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.context.CamundaContext
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result

fun CamundaContext.Request.tryGetId(): Result<String, Fail> = this.id
    ?.let { Result.success(it) }
    ?: Result.failure(
        Fail.Incident.Bpe(
            description = "Missing required 'id' attribute in 'request' attribute of camunda context."
        )
    )
