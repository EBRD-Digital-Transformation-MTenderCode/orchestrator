package com.procurement.orchestrator.infrastructure.client.web.extension

import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

val Response.contentType: String
    get() = body?.contentType()?.type ?: "N/A"

val Response.content: String
    get() = body?.charStream()?.readText() ?: ""

suspend fun Call.awaitSuccessfulResponse(): Result<Response, WebClientFail> = suspendCoroutine { con ->
    val callback = object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful)
                con.resume(success(response))
            else
                con.resume(failure(WebClientFail.ResponseError(response = response)))
        }

        override fun onFailure(call: Call, e: IOException) {
            con.resume(failure(WebClientFail.NetworkError(exception = e)))
        }
    }

    enqueue(callback)
}


suspend fun Call.awaitAnyResponse(): Result<Response, WebClientFail.NetworkError> =
    suspendCoroutine { con ->
        val callback = object : Callback {
            override fun onResponse(call: Call, response: Response) {
                con.resume(success(response))
            }

            override fun onFailure(call: Call, e: IOException) {
                con.resume(failure(WebClientFail.NetworkError(exception = e)))
            }
        }

        enqueue(callback)
    }
