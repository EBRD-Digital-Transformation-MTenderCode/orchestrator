package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckAccessToTenderAction
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

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object CreatePcr : CreatePcrAction()

    object SetUnsuccessfulStateForLots : SetUnsuccessfulStateForLotsAction()

    object CheckTenderState: CheckTenderStateAction()

    object CreateRelationToContractProcessStage : CreateRelationToContractProcessStageAction()

    object GetTenderState : GetTenderStateAction()

    object CheckLotsState : CheckLotsStateAction()

    object FindProcurementMethodModalities: FindProcurementMethodModalitiesAction()

    object GetCurrency : GetCurrencyAction()

    object FindItemsByLotIds: FindItemsByLotIdsAction()

    object ValidateRequirementResponses: ValidateRequirementResponsesAction()

    object CheckAccessToTender : CheckAccessToTenderAction()

    object SetStateForLots: SetStateForLotsAction()

    object FindCriteriaAndTargetsForPacs: FindCriteriaAndTargetsForPacsAction()

    object GetOcidFromRelatedProcess: GetOcidFromRelatedProcessAction()
}
