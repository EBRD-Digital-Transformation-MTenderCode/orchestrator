package com.procurement.orchestrator.infrastructure.client.web.mdm

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.MdmClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.asSuccess
import com.procurement.orchestrator.infrastructure.bpms.repository.ErrorDescriptionRepository
import com.procurement.orchestrator.infrastructure.client.reply.EMPTY_REPLY_ID
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction
import com.procurement.orchestrator.infrastructure.model.Version

class HttpMdmClient(
    private val errorDescriptionRepository: ErrorDescriptionRepository
) : MdmClient {

    override suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident> =

        errorDescriptionRepository.load(codes = params.codes, language = params.language.toUpperCase())
            .orForwardFail { fail -> return fail }
            .map { error ->
                GetErrorDescriptionsAction.Result.Error(
                    code = error.code,
                    description = error.description
                )
            }
            .let { errors ->
                Reply.Success(
                    id = EMPTY_REPLY_ID,
                    version = Version.parse("2.0.0"),
                    result = Option.pure(GetErrorDescriptionsAction.Result(errors))
                )
            }
            .asSuccess()
}
