package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.ValidateConfirmationResponseDataAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object FindSupplierReferencesOfActivePacs : FindSupplierReferencesOfActivePacsAction()

    object CreateFrameworkContract : CreateFrameworkContractAction()

    object CancelFrameworkContract : CancelFrameworkContractAction()

    object DoPacs: DoPacsAction()

    object AddSupplierReferencesInFC: AddSupplierReferencesInFCAction()

    object AddGeneratedDocumentToContract: AddGeneratedDocumentToContractAction()

    object SetStateForContracts: SetStateForContractsAction()

    object CheckContractState: CheckContractStateAction()

    object CheckExistenceSupplierReferencesInFC: CheckExistenceSupplierReferencesInFCAction()

    object GetContractState : GetContractStateAction()

    object CreateConfirmationRequests : CreateConfirmationRequestsAction()

    object ValidateConfirmationResponseData: ValidateConfirmationResponseDataAction()
}
