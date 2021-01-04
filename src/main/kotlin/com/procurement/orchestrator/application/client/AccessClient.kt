package com.procurement.orchestrator.application.client

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.CalculateAPValueAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckEqualityCurrenciesAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckExistenceFaAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckExistenceSignAuctionAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckLotsStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonesStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckRelationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateCriteriaForProcuringEntityAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRelationToOtherProcessAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.DivideLotAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindAuctionsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotsValueAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetMainProcurementCategoryAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetOrganizationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetQualificationCriteriaAndMethodAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderCurrencyAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.OutsourcingPnAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForLotsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateLotsDataAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRelatedTenderClassificationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRequirementResponsesAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.VerifyRequirementResponseAction

interface AccessClient {

    suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    @Deprecated(message = "Using validateRequirementResponses")
    suspend fun checkPersonsStructure(
        id: CommandId,
        params: CheckPersonesStructureAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun verifyRequirementResponse(
        id: CommandId,
        params: VerifyRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun findLotIds(
        id: CommandId,
        params: FindLotIdsAction.Params
    ): Result<Reply<FindLotIdsAction.Result>, Fail.Incident>

    suspend fun getLotStateByIds(
        id: CommandId,
        params: GetLotStateByIdsAction.Params
    ): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident>

    suspend fun responderProcessing(
        id: CommandId,
        params: ResponderProcessingAction.Params
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident>

    suspend fun setStateForTender(
        id: CommandId,
        params: SetStateForTenderAction.Params
    ): Result<Reply<SetStateForTenderAction.Result>, Fail.Incident>

    suspend fun getTenderState(
        id: CommandId,
        params: GetTenderStateAction.Params
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident>

    suspend fun getTenderCurrency(
        id: CommandId,
        params: GetTenderCurrencyAction.Params
    ): Result<Reply<GetTenderCurrencyAction.Result>, Fail.Incident>

    suspend fun setStateForLots(
        id: CommandId,
        params: SetStateForLotsAction.Params
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident>

    suspend fun getOrganization(
        id: CommandId,
        params: GetOrganizationAction.Params
    ): Result<Reply<GetOrganizationAction.Result>, Fail.Incident>

    suspend fun createCriteriaForProcuringEntity(
        id: CommandId,
        params: CreateCriteriaForProcuringEntityAction.Params
    ): Result<Reply<CreateCriteriaForProcuringEntityAction.Result>, Fail.Incident>

    suspend fun getQualificationCriteriaAndMethod(
        id: CommandId,
        params: GetQualificationCriteriaAndMethodAction.Params
    ): Result<Reply<GetQualificationCriteriaAndMethodAction.Result>, Fail.Incident>

    suspend fun validateRequirementResponses(
        id: CommandId,
        params: ValidateRequirementResponsesAction.Params
    ): Result<Reply<ValidateRequirementResponsesAction.Result>, Fail.Incident>

    suspend fun findCriteria(
        id: CommandId,
        params: FindCriteriaAction.Params
    ): Result<Reply<FindCriteriaAction.Result>, Fail.Incident>

    suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun findAuctions(
        id: CommandId,
        params: FindAuctionsAction.Params
    ): Result<Reply<FindAuctionsAction.Result>, Fail.Incident>

    suspend fun checkExistenceFa(
        id: CommandId,
        params: CheckExistenceFaAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun checkExistenceSignAuction(
        id: CommandId,
        params: CheckExistenceSignAuctionAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun outsourcingPn(
        id: CommandId,
        params: OutsourcingPnAction.Params
    ): Result<Reply<OutsourcingPnAction.Result>, Fail.Incident>

    suspend fun validateRelatedTenderClassification(
        id: CommandId,
        params: ValidateRelatedTenderClassificationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun createRelationToOtherProcess(
        id: CommandId,
        params: CreateRelationToOtherProcessAction.Params
    ): Result<Reply<CreateRelationToOtherProcessAction.Result>, Fail.Incident>

    suspend fun checkRelationDelegate(
        id: CommandId,
        params: CheckRelationAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun calculateAPValueAction(
        id: CommandId,
        params: CalculateAPValueAction.Params
    ): Result<Reply<CalculateAPValueAction.Result>, Fail.Incident>

    suspend fun checkEqualityCurrencies(
        id: CommandId,
        params: CheckEqualityCurrenciesAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun getMainProcurementCategory(
        id: CommandId,
        params: GetMainProcurementCategoryAction.Params
    ): Result<Reply<GetMainProcurementCategoryAction.Result>, Fail.Incident>

    suspend fun checkLotsState(id: CommandId, params: CheckLotsStateAction.Params): Result<Reply<Unit>, Fail.Incident>

    suspend fun getLotsValue(
        id: CommandId,
        params: GetLotsValueAction.Params
    ): Result<Reply<GetLotsValueAction.Result>, Fail.Incident>

    suspend fun validateLotsDataAction(
        id: CommandId,
        params: ValidateLotsDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident>

    suspend fun divideLot(
        id: CommandId,
        params: DivideLotAction.Params
    ): Result<Reply<DivideLotAction.Result>, Fail.Incident>
}
