package com.procurement.orchestrator.infrastructure.client.web.extension

import okhttp3.Response

sealed class WebClientFail {
    class NetworkError(val exception: Exception) : WebClientFail()
    class ResponseError(val response: Response) : WebClientFail()
}
