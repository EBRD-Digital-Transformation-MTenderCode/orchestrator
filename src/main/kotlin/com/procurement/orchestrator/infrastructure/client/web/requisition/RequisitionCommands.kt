package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object GetTenderState : GetTenderStateAction()
}
