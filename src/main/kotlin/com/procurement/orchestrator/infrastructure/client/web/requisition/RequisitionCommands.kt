package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreateRelationToContractProcessStageAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidateRequirementResponsesAction

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object CreatePcr : CreatePcrAction()

    object CheckTenderState: CheckTenderStateAction()

    object CreateRelationToContractProcessStage : CreateRelationToContractProcessStageAction()

    object GetTenderState : GetTenderStateAction()

    object ValidateRequirementResponses: ValidateRequirementResponsesAction()
}
