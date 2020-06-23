package com.procurement.orchestrator.infrastructure.client.web

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import okhttp3.HttpUrl

interface RestClient {
    suspend fun  call(url: HttpUrl): Result<CallResponse, Fail.Incident>
}

data class CallResponse(
    val code: Int,
    val content: String
)
