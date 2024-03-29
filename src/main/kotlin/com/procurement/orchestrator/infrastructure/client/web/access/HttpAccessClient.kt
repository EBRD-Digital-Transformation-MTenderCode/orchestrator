package com.procurement.orchestrator.infrastructure.client.web.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.access.action.AddClientsToPartiesInAPAction
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
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRelationToContractProcessStageAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRelationToOtherProcessAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CreateRfqAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.DefineTenderClassificationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.DivideLotAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindAuctionsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindCriteriaAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetBuyersOwnersAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetDataForContractAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetItemsByLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotsValueAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetMainProcurementCategoryAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetOrganizationsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetQualificationCriteriaAndMethodAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderCurrencyAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.OutsourcingPnAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.PersonesProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForLotsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.SetStateForTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateLotsDataForDivisionAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRelatedTenderClassificationAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRequirementResponsesAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ValidateRfqDataAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.VerifyRequirementResponseAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpAccessClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    AccessClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun findLotIds(
        id: CommandId,
        params: FindLotIdsAction.Params
    ): Result<Reply<FindLotIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.FindLotByIds.build(id = id, params = params),
        target = AccessCommands.FindLotByIds.target
    )

    override suspend fun getLotStateByIds(
        id: CommandId,
        params: GetLotStateByIdsAction.Params
    ): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetLotStateByIds.build(id = id, params = params),
        target = AccessCommands.GetLotStateByIds.target
    )

    override suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckAccessToTender.build(id = id, params = params)
    )

    override suspend fun verifyRequirementResponse(
        id: CommandId,
        params: VerifyRequirementResponseAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.VerifyRequirementResponse.build(id = id, params = params)
    )

    override suspend fun responderProcessing(
        id: CommandId,
        params: ResponderProcessingAction.Params
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ResponderProcessing.build(id = id, params = params),
        target = AccessCommands.ResponderProcessing.target
    )

    override suspend fun setStateForTender(
        id: CommandId,
        params: SetStateForTenderAction.Params
    ): Result<Reply<SetStateForTenderAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.SetStateForTender.build(id = id, params = params),
        target = AccessCommands.SetStateForTender.target
    )

    override suspend fun getTenderState(
        id: CommandId,
        params: GetTenderStateAction.Params
    ): Result<Reply<GetTenderStateAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetTenderState.build(id = id, params = params),
        target = AccessCommands.GetTenderState.target
    )

    override suspend fun getTenderCurrency(
        id: CommandId,
        params: GetTenderCurrencyAction.Params
    ): Result<Reply<GetTenderCurrencyAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetTenderCurrency.build(id = id, params = params),
        target = AccessCommands.GetTenderCurrency.target
    )

    override suspend fun setStateForLots(
        id: CommandId,
        params: SetStateForLotsAction.Params
    ): Result<Reply<SetStateForLotsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.SetStateForLots.build(id = id, params = params),
        target = AccessCommands.SetStateForLots.target
    )

    override suspend fun getOrganizations(
        id: CommandId,
        params: GetOrganizationsAction.Params
    ): Result<Reply<GetOrganizationsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetOrganizations.build(id = id, params = params),
        target = AccessCommands.GetOrganizations.target
    )

    override suspend fun createCriteriaForProcuringEntity(
        id: CommandId,
        params: CreateCriteriaForProcuringEntityAction.Params
    ): Result<Reply<CreateCriteriaForProcuringEntityAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CreateCriteriaForProcuringEntity.build(id = id, params = params),
        target = AccessCommands.CreateCriteriaForProcuringEntity.target
    )

    override suspend fun getQualificationCriteriaAndMethod(
        id: CommandId,
        params: GetQualificationCriteriaAndMethodAction.Params
    ): Result<Reply<GetQualificationCriteriaAndMethodAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetQualificationCriteriaAndMethod.build(id = id, params = params),
        target = AccessCommands.GetQualificationCriteriaAndMethod.target
    )

    override suspend fun checkPersonsStructure(
        id: CommandId,
        params: CheckPersonesStructureAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckPersonesStructure.build(id = id, params = params)
    )

    override suspend fun validateRequirementResponses(
        id: CommandId,
        params: ValidateRequirementResponsesAction.Params
    ): Result<Reply<ValidateRequirementResponsesAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ValidateRequirementResponses.build(id = id, params = params),
        target = AccessCommands.ValidateRequirementResponses.target
    )

    override suspend fun findCriteria(
        id: CommandId,
        params: FindCriteriaAction.Params
    ): Result<Reply<FindCriteriaAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.FindCriteria.build(id = id, params = params),
        target = AccessCommands.FindCriteria.target
    )

    override suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckTenderState.build(id = id, params = params)
    )

    override suspend fun findAuctions(
        id: CommandId,
        params: FindAuctionsAction.Params
    ): Result<Reply<FindAuctionsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.FindAuctions.build(id = id, params = params),
        target = AccessCommands.FindAuctions.target
    )

    override suspend fun checkExistenceFa(
        id: CommandId,
        params: CheckExistenceFaAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckExistenceFa.build(id = id, params = params)
    )

    override suspend fun checkExistenceSignAuction(
        id: CommandId,
        params: CheckExistenceSignAuctionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckExistenceSignAuction.build(id = id, params = params)
    )

    override suspend fun outsourcingPn(
        id: CommandId,
        params: OutsourcingPnAction.Params
    ): Result<Reply<OutsourcingPnAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.OutsourcingPn.build(id = id, params = params),
        target = AccessCommands.OutsourcingPn.target
    )

    override suspend fun validateRelatedTenderClassification(
        id: CommandId,
        params: ValidateRelatedTenderClassificationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ValidateRelatedTenderClassification.build(id = id, params = params)
    )

    override suspend fun createRelationToOtherProcess(
        id: CommandId,
        params: CreateRelationToOtherProcessAction.Params
    ): Result<Reply<CreateRelationToOtherProcessAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CreateRelationToOtherProcess.build(id = id, params = params),
        target = AccessCommands.CreateRelationToOtherProcess.target
    )

    override suspend fun createRelationToContractProcessStage(
        id: CommandId,
        params: CreateRelationToContractProcessStageAction.Params
    ): Result<Reply<CreateRelationToContractProcessStageAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CreateRelationToContractProcessStage.build(id = id, params = params),
        target = AccessCommands.CreateRelationToContractProcessStage.target
    )

    override suspend fun checkRelationDelegate(
        id: CommandId,
        params: CheckRelationAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckRelation.build(id = id, params = params)
    )

    override suspend fun calculateAPValueAction(
        id: CommandId,
        params: CalculateAPValueAction.Params
    ): Result<Reply<CalculateAPValueAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CalculateAPValue.build(id = id, params = params),
        target = AccessCommands.CalculateAPValue.target
    )

    override suspend fun checkEqualityCurrencies(
        id: CommandId,
        params: CheckEqualityCurrenciesAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckEqualityCurrencies.build(id = id, params = params)
    )

    override suspend fun getMainProcurementCategory(
        id: CommandId,
        params: GetMainProcurementCategoryAction.Params
    ): Result<Reply<GetMainProcurementCategoryAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetMainProcurementCategory.build(id = id, params = params),
        target = AccessCommands.GetMainProcurementCategory.target
    )

    override suspend fun checkLotsState(
        id: CommandId,
        params: CheckLotsStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckLotsState.build(id = id, params = params)
    )

    override suspend fun getLotsValue(
        id: CommandId,
        params: GetLotsValueAction.Params
    ): Result<Reply<GetLotsValueAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetLotsValue.build(id = id, params = params),
        target = AccessCommands.GetLotsValue.target
    )

    override suspend fun validateLotsDataForDivision(
        id: CommandId,
        params: ValidateLotsDataForDivisionAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ValidateLotsDataForDivision.build(id = id, params = params)
    )

    override suspend fun divideLot(
        id: CommandId,
        params: DivideLotAction.Params
    ): Result<Reply<DivideLotAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.DivideLot.build(id = id, params = params),
        target = AccessCommands.DivideLot.target
    )

    override suspend fun addClientsToPartiesInAP(
        id: CommandId,
        params: AddClientsToPartiesInAPAction.Params
    ): Result<Reply<AddClientsToPartiesInAPAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.AddClientsToPartiesInAP.build(id = id, params = params),
        target = AccessCommands.AddClientsToPartiesInAP.target
    )

    override suspend fun getItemsByLotIdsAction(
        id: CommandId,
        params: GetItemsByLotIdsAction.Params
    ): Result<Reply<GetItemsByLotIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetItemsByLotIds.build(id = id, params = params),
        target = AccessCommands.GetItemsByLotIds.target
    )

    override suspend fun getBuyersOwners(
        id: CommandId,
        params: GetBuyersOwnersAction.Params
    ): Result<Reply<GetBuyersOwnersAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetBuyersOwners.build(id = id, params = params),
        target = AccessCommands.GetBuyersOwners.target
    )

    override suspend fun validateRfqData(
        id: CommandId,
        params: ValidateRfqDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ValidateRfqData.build(id = id, params = params)
    )

    override suspend fun createRfq(
        id: CommandId,
        params: CreateRfqAction.Params
    ): Result<Reply<CreateRfqAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CreateRfq.build(id = id, params = params),
        target = AccessCommands.CreateRfq.target
    )

    override suspend fun personesProcessing(
        id: CommandId,
        params: PersonesProcessingAction.Params
    ): Result<Reply<PersonesProcessingAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.PersonesProcessing.build(id = id, params = params),
        target = AccessCommands.PersonesProcessing.target
    )

    override suspend fun defineTenderClassification(
        id: CommandId,
        params: DefineTenderClassificationAction.Params
    ): Result<Reply<DefineTenderClassificationAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.DefineTenderClassification.build(id = id, params = params),
        target = AccessCommands.DefineTenderClassification.target
    )

    override suspend fun getDataForContract(
        id: CommandId,
        params: GetDataForContractAction.Params
    ): Result<Reply<GetDataForContractAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetDataForContract.build(id = id, params = params),
        target = AccessCommands.GetDataForContract.target
    )
}
