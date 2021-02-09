package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object FindSupplierReferencesOfActivePacs : FindSupplierReferencesOfActivePacsAction()

    object CreateFrameworkContract : CreateFrameworkContractAction()

    object CancelFrameworkContract : CancelFrameworkContractAction()

    object DoPacs: DoPacsAction()

    object AddSupplierReferencesInFC: AddSupplierReferencesInFCAction()

    object SetStateForContracts: SetStateForContractsAction()

    object CheckContractState: CheckContractStateAction()
}
