package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object CreateContract : CreateContractAction()
}
