package com.procurement.orchestrator.application.model.context.extension

import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.application.model.context.members.RequestInfo
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asFailure
import com.procurement.orchestrator.domain.functional.asSuccess

private const val TOKEN_ATTRIBUTE_NAME = "requestInfo.token"

fun RequestInfo.tryGetToken(): Result<Token, Fail.Incident.Bpms.Context.Missing> {
    val token = this.token
        ?: return Fail.Incident.Bpms.Context.Missing(name = TOKEN_ATTRIBUTE_NAME)
            .asFailure()
    return token.asSuccess()
}

