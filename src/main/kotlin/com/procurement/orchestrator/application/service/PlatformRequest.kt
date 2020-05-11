package com.procurement.orchestrator.application.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.orchestrator.application.model.OperationId
import com.procurement.orchestrator.application.model.Owner
import com.procurement.orchestrator.application.model.PlatformId
import com.procurement.orchestrator.application.model.Token
import com.procurement.orchestrator.domain.fail.error.RequestErrors
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.infrastructure.extension.http.getOperationId
import com.procurement.orchestrator.infrastructure.extension.http.getPayload
import com.procurement.orchestrator.infrastructure.extension.http.getPlatformId
import com.procurement.orchestrator.infrastructure.extension.http.getToken
import javax.servlet.http.HttpServletRequest

class PlatformRequest private constructor(
    val operationId: OperationId,
    val platformId: PlatformId,
    val context: Context,
    val payload: String
) {

    class Context(
        @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: Cpid,
        @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: Ocid,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token? = null,
        @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: Owner,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String? = null,
        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String,
        @field:JsonProperty("processName") @param:JsonProperty("processName") val processName: String
    )

    companion object {

        fun tryCreate(
            servlet: HttpServletRequest,
            cpid: Cpid,
            ocid: Ocid,
            processName: String,
            id: String? = null
        ): Result<PlatformRequest, RequestErrors> {

            val platformId: PlatformId = servlet.getPlatformId()
                .orForwardFail { fail -> return fail }

            val operationId: OperationId = servlet.getOperationId()
                .orForwardFail { fail -> return fail }

            val token: Token = servlet.getToken()
                .orForwardFail { fail -> return fail }

            val payload: String = servlet.getPayload()
                .orForwardFail { fail -> return fail }

            return PlatformRequest(
                operationId = operationId,
                platformId = platformId,
                context = Context(
                    cpid = cpid,
                    ocid = ocid,
                    token = token,
                    owner = platformId,
                    id = id,
                    uri = servlet.requestURI,
                    processName = processName
                ),
                payload = payload
            )
                .asSuccess()
        }
    }
}
