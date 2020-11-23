package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object DoContract : DoContractAction()

    object CancelFrameworkContract : CancelFrameworkContractAction()
}
