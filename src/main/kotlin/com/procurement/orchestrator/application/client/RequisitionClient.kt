package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
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

interface RequisitionClient {

    suspend fun validatePcrData(
        id: CommandId,
        params: ValidatePcrDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createPcr(
        id: CommandId,
        params: CreatePcrAction.Params
    ): Result<Reply<CreatePcrAction.Result>, Fail.Incident>

    suspend fun setUnsuccessfulStateForLots(
        id: CommandId,
        params: SetUnsuccessfulStateForLotsAction.Params
    ): Result<Reply<SetUnsuccessfulStateForLotsAction.Result>, Fail.Incident>

    suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createRelationToContractProcessStage(
        id: CommandId,
        params: CreateRelationToContractProcessStageAction.Params
    ): Result<Reply<CreateRelationToContractProcessStageAction.Result>, Fail.Incident>

    suspend fun getTenderState(
        id: CommandId,
        params: GetTenderStateAction.Params
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident>


    suspend fun checkLotsState(id: CommandId, params: CheckLotsStateAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkItemsDataForRfq(
        id: CommandId,
        params: CheckItemsDataForRfqAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun findProcurementMethodModalities(
        id: CommandId,
        params: FindProcurementMethodModalitiesAction.Params
    ): Result<Reply<FindProcurementMethodModalitiesAction.Result>, Fail.Incident>

    suspend fun getCurrency(
        id: CommandId,
        params: GetCurrencyAction.Params
    ): Result<Reply<GetCurrencyAction.Result>, Fail.Incident>

    suspend fun findItemsByLotIds(
        id: CommandId,
        params: FindItemsByLotIdsAction.Params
    ): Result<Reply<FindItemsByLotIdsAction.Result>, Fail.Incident>

    suspend fun validateRequirementResponses(
        id: CommandId,
        params: ValidateRequirementResponsesAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun setStateForLots(
        id: CommandId,
        params: SetStateForLotsAction.Params
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident>

    suspend fun findCriteriaAndTargetsForPacs(
        id: CommandId,
        params: FindCriteriaAndTargetsForPacsAction.Params
    ): Result<Reply<FindCriteriaAndTargetsForPacsAction.Result>, Fail.Incident>

    suspend fun fetOcidFromRelatedProcess(
        id: CommandId,
        params: GetOcidFromRelatedProcessAction.Params
    ): Result<Reply<GetOcidFromRelatedProcessAction.Result>, Fail.Incident>
}
