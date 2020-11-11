package com.procurement.orchestrator.infrastructure.client.web

import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.infrastructure.bpms.model.ResultContext
import com.procurement.orchestrator.infrastructure.client.CommandV1
import com.procurement.orchestrator.infrastructure.client.reply.ParserV1
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.retry.RetryInfo
import com.procurement.orchestrator.infrastructure.client.web.extension.WebClientFail
import com.procurement.orchestrator.infrastructure.client.web.extension.awaitSuccessfulResponse
import com.procurement.orchestrator.infrastructure.client.web.extension.content
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.URL
import kotlin.coroutines.coroutineContext

class OkHttpWebClientV1(
    private val logger: Logger,
    private val transform: Transform
) : WebClientV1 {
    companion object {
        private val MEDIA_TYPE_JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        private var httpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build()
        private val retryInfo = RetryInfo()
    }

    override suspend fun <C, D, R> call(
        url: URL,
        command: CommandV1<C, D>,
        target: Target<R>
    ): Result<Reply<R>, Fail.Incident>  {
        val response = execute(url, command)
            .orForwardFail { fail -> return fail }
            .also { response ->
                if (logger.isDebugEnabled)
                    logger.debug(response)
            }
        return ParserV1.parse(response = response, transform = transform, target = target)
    }

    override suspend fun <C, D> call(url: URL, command: CommandV1<C, D>): Result<Reply<Unit>, Fail.Incident> {
        val response = execute(url, command)
            .orForwardFail { fail -> return fail }
            .also { response ->
                if (logger.isDebugEnabled)
                    logger.debug(response)
            }
        return ParserV1.parse(response = response, transform = transform)
    }

    private suspend fun <C, D> execute(url: URL, command: CommandV1<C, D>): Result<String, Fail.Incident> {
        val payload = transform.trySerialization(command)
            .orForwardFail { fail -> return fail }

        if (logger.isDebugEnabled)
            logger.debug(payload)

        val ctx = coroutineContext[ResultContext.Key]!!
        ctx.request(payload)

        val request = buildRequest(url, payload)
        val call = httpClient.newCall(request)
        val response = execute(call = call, retryInfo = retryInfo)
            .orReturnFail { webClientFail ->
                return when (webClientFail) {
                    is WebClientFail.NetworkError -> failure(
                        Fail.Incident.NetworkError(
                            exception = webClientFail.exception,
                            description = "Error of call by url: '$url' and payload: '$payload'."
                        )
                    )
                    is WebClientFail.ResponseError -> failure(
                        Fail.Incident.ResponseError(description = "Error of call by url: '$url' and payload: '$payload'. Reason: ${webClientFail.response.content}.")
                    )
                }
            }
            .content

        ctx.response(response)
        return success(response)
    }

    private tailrec suspend fun execute(call: Call, retryInfo: RetryInfo): Result<Response, WebClientFail> =
        when (val result = call.awaitSuccessfulResponse()) {
            is Result.Success -> result
            is Result.Failure -> when (result.error) {
                is WebClientFail.ResponseError -> result
                is WebClientFail.NetworkError -> {
                    if (retryInfo.attempts.nonNext)
                        result
                    else {
                        delay(retryInfo.delayTime.current)
                        execute(call = call.clone(), retryInfo = retryInfo.next())
                    }
                }
            }
        }

    private fun buildRequest(url: URL, payload: String): Request = Builder()
        .url(url)
        .post(payload.toRequestBody(MEDIA_TYPE_JSON))
        .build()
}
