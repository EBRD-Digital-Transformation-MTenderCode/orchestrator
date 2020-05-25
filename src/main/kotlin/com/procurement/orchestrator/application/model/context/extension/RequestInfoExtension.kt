package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success

private const val PATH = "requestInfo"
private const val TOKEN_ATTRIBUTE_NAME = "token"

fun RequestInfo.tryGetToken(): Result<Token, Fail.Incident.Bpms.Context.Missing> = this.token
    ?.let { success(it) }
    ?: failure(Fail.Incident.Bpms.Context.Missing(name = TOKEN_ATTRIBUTE_NAME, path = PATH))
