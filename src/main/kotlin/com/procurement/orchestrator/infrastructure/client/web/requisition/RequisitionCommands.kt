package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreateRelationToContractProcessStageAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckLotsStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetCurrencyAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object CreatePcr : CreatePcrAction()

    object CheckTenderState: CheckTenderStateAction()

    object CreateRelationToContractProcessStage : CreateRelationToContractProcessStageAction()

    object GetTenderState : GetTenderStateAction()

    object CheckLotsState : CheckLotsStateAction()
    object CheckTenderState : CheckTenderStateAction()

    object GetCurrency : GetCurrencyAction()
}
