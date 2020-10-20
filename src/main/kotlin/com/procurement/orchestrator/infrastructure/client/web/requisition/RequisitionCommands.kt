package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object CreatePcr : CreatePcrAction()
}
