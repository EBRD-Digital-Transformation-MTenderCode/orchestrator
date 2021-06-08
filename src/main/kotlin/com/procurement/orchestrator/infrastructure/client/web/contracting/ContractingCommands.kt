package com.procurement.orchestrator.infrastructure.client.web.contracting

import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddGeneratedDocumentToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.AddSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CancelFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckAccessToRequestOfConfirmationAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceOfConfirmationResponsesAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckExistenceSupplierReferencesInFCAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CheckRelatedContractsStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationRequestsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateConfirmationResponseAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.CreateFrameworkContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.DoPacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindCANIdsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindContractDocumentIdAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.FindSupplierReferencesOfActivePacsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetAwardIdByPacAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetContractStateAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetOrganizationIdAndSourceOfRequestGroupAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.GetSupplierIdsByContractAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.SetStateForContractsAction
import com.procurement.orchestrator.infrastructure.client.web.contracting.action.ValidateConfirmationResponseDataAction

object ContractingCommands {

    object FindCANIds : FindCANIdsAction()

    object FindSupplierReferencesOfActivePacs : FindSupplierReferencesOfActivePacsAction()

    object FindContractDocumentId : FindContractDocumentIdAction()

    object CreateFrameworkContract : CreateFrameworkContractAction()

    object CancelFrameworkContract : CancelFrameworkContractAction()

    object DoPacs: DoPacsAction()

    object AddSupplierReferencesInFC: AddSupplierReferencesInFCAction()

    object AddGeneratedDocumentToContract: AddGeneratedDocumentToContractAction()

    object SetStateForContracts: SetStateForContractsAction()

    object CheckContractState: CheckContractStateAction()

    object CheckExistenceSupplierReferencesInFC: CheckExistenceSupplierReferencesInFCAction()

    object GetContractState : GetContractStateAction()

    object GetAwardIdByPac : GetAwardIdByPacAction()

    object CreateConfirmationRequests : CreateConfirmationRequestsAction()

    object ValidateConfirmationResponseData: ValidateConfirmationResponseDataAction()

    object GetOrganizationIdAndSourceOfRequestGroup: GetOrganizationIdAndSourceOfRequestGroupAction()

    object CheckAccessToRequestOfConfirmation: CheckAccessToRequestOfConfirmationAction()

    object CreateConfirmationResponse: CreateConfirmationResponseAction()

    object CheckAccessToContract: CheckAccessToContractAction()

    object CheckExistenceOfConfirmationResponses: CheckExistenceOfConfirmationResponsesAction()

    object GetSuppliersIdsByContract: GetSupplierIdsByContractAction()

    object CheckRelatedContractState: CheckRelatedContractsStateAction()
}
