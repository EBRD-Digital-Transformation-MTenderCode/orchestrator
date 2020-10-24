package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.FindProcurementMethodModalitiesAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction

object RequisitionCommands {

    object ValidatePcrData : ValidatePcrDataAction()

    object CheckTenderState: CheckTenderStateAction()

    object FindProcurementMethodModalities: FindProcurementMethodModalitiesAction()
}
