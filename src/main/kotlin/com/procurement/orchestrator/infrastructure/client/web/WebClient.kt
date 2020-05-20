package com.procurement.orchestrator.infrastructure.client.web

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.Command
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import java.net.URL

interface WebClient {
    suspend fun <T, R> call(
        url: URL,
        command: Command<T>,
        target: Target<R>
    ): Result<Reply<R>, Fail.Incident>

    suspend fun <T> call(url: URL, command: Command<T>): Result<Reply<Unit>, Fail.Incident>
}
