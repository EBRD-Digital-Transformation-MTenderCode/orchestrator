package com.procurement.orchestrator.infrastructure.client.web.mdm

import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction

class HttpMdmClient(
    private val errorDescriptionRepository: ErrorDescriptionRepository
) : MdmClient {

    override suspend fun getErrorDescription(
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> =

        errorDescriptionRepository.load(codes = params.codes, language = params.language.toUpperCase())
            .doOnError { return Result.failure(it) }
            .get
            .map { error ->
                GetErrorDescriptionsAction.Result.Error(
                    code = error.code,
                    description = error.description
                )
            }
            .let { errors ->
                Reply(
                    id = EMPTY_REPLY_ID,
                    version = "",
                    status = Reply.Status.SUCCESS,
                    result = Reply.Result.Success(GetErrorDescriptionsAction.Result(errors))
                )
            }
            .asSuccess()
}
