package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.*

interface MdmClient {

    suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident>

    suspend fun enrichCountry(
        id: CommandId,
        params: EnrichCountryAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetCountry.Result, Fail.Incident>
    ): Result<GetCountry.Result, Fail.Incident>

    suspend fun enrichRegion(
        id: CommandId,
        params: EnrichRegionAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetRegion.Result, Fail.Incident>
    ): Result<GetRegion.Result, Fail.Incident>

    suspend fun enrichLocality(
        id: CommandId,
        params: EnrichLocalityAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<GetLocality.Result, Fail.Incident>
    ): Result<GetLocality.Result, Fail.Incident>

}
