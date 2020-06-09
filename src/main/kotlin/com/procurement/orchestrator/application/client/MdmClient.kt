package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichCountryDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichLocalityDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.mdm.MdmEnrichRegionDelegate
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.CallResponse
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichCountryAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichLocalityAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.EnrichRegionAction
import com.procurement.orchestrator.infrastructure.client.web.mdm.action.GetErrorDescriptionsAction

interface MdmClient {

    suspend fun getErrorDescription(
        id: CommandId,
        params: GetErrorDescriptionsAction.Params
    ): Result<Reply<GetErrorDescriptionsAction.Result>, Fail.Incident>

    suspend fun enrichCountry(
        id: CommandId,
        params: EnrichCountryAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<MdmEnrichCountryDelegate.ResponseEvent, Fail.Incident>
    ): Result<MdmEnrichCountryDelegate.ResponseEvent, Fail.Incident>

    suspend fun enrichRegion(
        id: CommandId,
        params: EnrichRegionAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<MdmEnrichRegionDelegate.ResponseEvent, Fail.Incident>
    ): Result<MdmEnrichRegionDelegate.ResponseEvent, Fail.Incident>

    suspend fun enrichLocality(
        id: CommandId,
        params: EnrichLocalityAction.Params,
        handler: (response: CallResponse, transform: Transform) -> Result<MdmEnrichLocalityDelegate.ResponseEvent, Fail.Incident>
    ): Result<MdmEnrichLocalityDelegate.ResponseEvent, Fail.Incident>

}
