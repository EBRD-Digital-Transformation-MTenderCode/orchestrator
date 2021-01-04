package com.procurement.orchestrator.infrastructure.client.web.access

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

object AccessCommands {

    object CheckAccessToTender : CheckAccessToTenderAction()

    @Deprecated(message = "Using VerifyRequirementResponse")
    object CheckPersonesStructure : CheckPersonesStructureAction()

    object VerifyRequirementResponse : VerifyRequirementResponseAction()

    object FindLotByIds : FindLotIdsAction()

    object GetLotStateByIds : GetLotStateByIdsAction()

    object ResponderProcessing : ResponderProcessingAction()

    object SetStateForTender : SetStateForTenderAction()

    object GetTenderState : GetTenderStateAction()

    object GetTenderCurrency : GetTenderCurrencyAction()

    object SetStateForLots : SetStateForLotsAction()

    object GetOrganization : GetOrganizationAction()

    object CreateCriteriaForProcuringEntity : CreateCriteriaForProcuringEntityAction()

    object GetQualificationCriteriaAndMethod : GetQualificationCriteriaAndMethodAction()

    object ValidateRequirementResponses : ValidateRequirementResponsesAction()

    object FindCriteria : FindCriteriaAction()

    object CheckTenderState : CheckTenderStateAction()

    object FindAuctions : FindAuctionsAction()

    object CheckExistenceFa : CheckExistenceFaAction()

    object CheckExistenceSignAuction : CheckExistenceSignAuctionAction()

    object OutsourcingPn : OutsourcingPnAction()

    object ValidateRelatedTenderClassification : ValidateRelatedTenderClassificationAction()

    object CreateRelationToOtherProcess : CreateRelationToOtherProcessAction()

    object CheckRelation : CheckRelationAction()

    object CalculateAPValue : CalculateAPValueAction()

    object CheckEqualityCurrencies: CheckEqualityCurrenciesAction()

    object GetMainProcurementCategory: GetMainProcurementCategoryAction()

    object CheckLotsState: CheckLotsStateAction()

    object GetLotsValue: GetLotsValueAction()

    object ValidateLotsData : ValidateLotsDataAction()

    object DivideLot: DivideLotAction()
}
