package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.*

interface MdmClient {

    suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident>

    suspend fun enrichCountry(
        id: CommandId,
        params: EnrichCountryAction.Params
    ): Result<GetCountry.Result, Fail.Incident>

    suspend fun enrichRegion(
        id: CommandId,
        params: EnrichRegionAction.Params
    ): Result<GetRegion.Result, Fail.Incident>

    suspend fun enrichLocality(
        id: CommandId,
        params: EnrichLocalityAction.Params
    ): Result<GetLocality.Result, Fail.Incident>

}
