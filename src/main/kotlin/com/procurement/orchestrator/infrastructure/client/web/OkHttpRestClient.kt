package com.procurement.orchestrator.infrastructure.client.web

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.client.retry.RetryInfo
import com.procurement.orchestrator.infrastructure.client.web.extension.WebClientFail
import com.procurement.orchestrator.infrastructure.client.web.extension.awaitAnyResponse
import com.procurement.orchestrator.infrastructure.client.web.extension.content
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.Response
import kotlin.coroutines.coroutineContext

class OkHttpRestClient(
    private val logger: Logger
) : RestClient {

    companion object {
        private var httpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build()
        private val retryInfo = RetryInfo()
    }

    private tailrec suspend fun execute(call: Call, retryInfo: RetryInfo): Result<Response, WebClientFail> =
        when (val result = call.awaitAnyResponse()) {
            is Result.Success -> result
            is Result.Failure ->
                if (retryInfo.attempts.nonNext)
                    result
                else {
                    delay(retryInfo.delayTime.current)
                    execute(call = call.clone(), retryInfo = retryInfo.next())
                }
        }

    override suspend fun call(url: HttpUrl): Result<CallResponse, Fail.Incident> {

        val ctx = coroutineContext[ResultContext.Key]!!

        val request = buildRequestMdm(url)
        val call = httpClient.newCall(request)
        val response = execute(call = call, retryInfo = retryInfo)
            .orReturnFail { webClientFail ->
                return when (webClientFail) {
                    is WebClientFail.NetworkError  -> failure(
                        Fail.Incident.NetworkError(description = webClientFail.toString())
                    )
                    is WebClientFail.ResponseError -> failure(
                        Fail.Incident.ResponseError(description = webClientFail.response.content)
                    )
                }
            }

        val callResponse = CallResponse(
            code = response.code,
            content = response.content
        )
        if (logger.isDebugEnabled)
            logger.debug(callResponse.content)

        ctx.response(callResponse.content)
        return success(callResponse)
    }


    private fun buildRequestMdm(url: HttpUrl): Request = Builder()
        .url(url)
        .build()

}


