package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreatePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object CreateFrameworkContract : CreateFrameworkContractAction()

    object CancelFrameworkContract : CancelFrameworkContractAction()

    object CreatePacs: CreatePacsAction()
}
