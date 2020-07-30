package com.procurement.orchestrator.infrastructure.client.web

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.CommandV1
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import java.net.URL

interface WebClientV1 {
    suspend fun <C, D, R> call(
        url: URL,
        command: CommandV1<C, D>,
        target: Target<R>
    ): Result<Reply<R>, Fail.Incident>

    suspend fun <C, D> call(url: URL, command: CommandV1<C, D>): Result<Reply<Unit>, Fail.Incident>
}
