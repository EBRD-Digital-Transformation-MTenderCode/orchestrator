package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindItemsByLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction

interface RequisitionClient {

    suspend fun validatePcrData(
        id: CommandId,
        params: ValidatePcrDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun findItemsByLotIds(
        id: CommandId,
        params: FindItemsByLotIdsAction.Params
    ): Result<Reply<FindItemsByLotIdsAction.Result>, Fail.Incident>
}
