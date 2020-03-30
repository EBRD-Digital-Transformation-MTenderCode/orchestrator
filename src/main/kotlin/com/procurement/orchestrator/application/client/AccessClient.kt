package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonsStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction

interface AccessClient {

    suspend fun checkAccessToTender(params: CheckAccessToTenderAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkPersonsStructure(params: CheckPersonsStructureAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun getLotIds(params: GetLotIdsAction.Params): Result<Reply<GetLotIdsAction.Result>, Fail.Incident>

    suspend fun getLotStateByIds(params: GetLotStateByIdsAction.Params): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident>

    suspend fun responderProcessing(params: ResponderProcessingAction.Params): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident>
}
