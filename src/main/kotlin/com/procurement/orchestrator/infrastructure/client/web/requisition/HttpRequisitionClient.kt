package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckItemsDataForRfqAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckLotsStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreateRelationToContractProcessStageAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindCriteriaAndTargetsForPacsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindItemsByLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindProcurementMethodModalitiesAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetCurrencyAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetOcidFromRelatedProcessAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.SetStateForLotsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.SetUnsuccessfulStateForLotsAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidateRequirementResponsesAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpRequisitionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    RequisitionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun validatePcrData(
        id: CommandId,
        params: ValidatePcrDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.ValidatePcrData.build(id = id, params = params)
    )

    override suspend fun createPcr(
        id: CommandId,
        params: CreatePcrAction.Params
    ): Result<Reply<CreatePcrAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CreatePcr.build(id = id, params = params),
        target = RequisitionCommands.CreatePcr.target
    )

    override suspend fun setUnsuccessfulStateForLots(
        id: CommandId,
        params: SetUnsuccessfulStateForLotsAction.Params
    ): Result<Reply<SetUnsuccessfulStateForLotsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.SetUnsuccessfulStateForLots.build(id = id, params = params),
        target = RequisitionCommands.SetUnsuccessfulStateForLots.target
    )

    override suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CheckTenderState.build(id = id, params = params)
    )

    override suspend fun createRelationToContractProcessStage(
        id: CommandId,
        params: CreateRelationToContractProcessStageAction.Params
    ): Result<Reply<CreateRelationToContractProcessStageAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CreateRelationToContractProcessStage.build(id = id, params = params),
        target = RequisitionCommands.CreateRelationToContractProcessStage.target
    )

    override suspend fun getTenderState(
        id: CommandId,
        params: GetTenderStateAction.Params
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.GetTenderState.build(id = id, params = params),
        target = RequisitionCommands.GetTenderState.target
    )

    override suspend fun checkLotsState(
        id: CommandId,
        params: CheckLotsStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CheckLotsState.build(id = id, params = params)
    )

    override suspend fun checkItemsDataForRfq(
        id: CommandId,
        params: CheckItemsDataForRfqAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CheckItemsDataForRfq.build(id = id, params = params)
    )

    override suspend fun findProcurementMethodModalities(
        id: CommandId,
        params: FindProcurementMethodModalitiesAction.Params
    ): Result<Reply<FindProcurementMethodModalitiesAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.FindProcurementMethodModalities.build(id = id, params = params),
        target = RequisitionCommands.FindProcurementMethodModalities.target
    )

    override suspend fun getCurrency(
        id: CommandId,
        params: GetCurrencyAction.Params
    ): Result<Reply<GetCurrencyAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.GetCurrency.build(id = id, params = params),
        target = RequisitionCommands.GetCurrency.target
    )

    override suspend fun findItemsByLotIds(
        id: CommandId,
        params: FindItemsByLotIdsAction.Params
    ): Result<Reply<FindItemsByLotIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.FindItemsByLotIds.build(id = id, params = params),
        target = RequisitionCommands.FindItemsByLotIds.target
    )

    override suspend fun validateRequirementResponses(
        id: CommandId,
        params: ValidateRequirementResponsesAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.ValidateRequirementResponses.build(id = id, params = params)
    )

    override suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CheckAccessToTender.build(id = id, params = params)
    )

    override suspend fun setStateForLots(
        id: CommandId,
        params: SetStateForLotsAction.Params
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.SetStateForLots.build(id = id, params = params),
        target = RequisitionCommands.SetStateForLots.target
    )

    override suspend fun findCriteriaAndTargetsForPacs(
        id: CommandId,
        params: FindCriteriaAndTargetsForPacsAction.Params
    ): Result<Reply<FindCriteriaAndTargetsForPacsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.FindCriteriaAndTargetsForPacs.build(id = id, params = params),
        target = RequisitionCommands.FindCriteriaAndTargetsForPacs.target
    )

    override suspend fun fetOcidFromRelatedProcess(
        id: CommandId,
        params: GetOcidFromRelatedProcessAction.Params
    ): Result<Reply<GetOcidFromRelatedProcessAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.GetOcidFromRelatedProcess.build(id = id, params = params),
        target = RequisitionCommands.GetOcidFromRelatedProcess.target
    )
}
