package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContractingActionsTest {

    @Nested
    inner class FindCANIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindCANIdsAction.Params>("json/client/contracting/find_can_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindCANIdsAction.Params>("json/client/contracting/find_can_ids_params_1.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindCANIdsAction.Result>("json/client/contracting/find_can_ids_result.json")
            }
        }
    }

    @Nested
    inner class FindSupplierReferencesOfActivePacs {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Params>("json/client/contracting/find_supplier_references_of_active_pacs_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_required_2.json")
            }
        }
    }

    @Nested
    inner class FindContractDocumentId {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindContractDocumentIdAction.Params>("json/client/contracting/find_contract_document_id_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindContractDocumentIdAction.Result>("json/client/contracting/find_contract_document_id_result_full.json")
            }
        }
    }

    @Nested
    inner class CreateFrameworkContract {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateFrameworkContractAction.Params>("json/client/contracting/do/create_framework_contract_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateFrameworkContractAction.Result>("json/client/contracting/do/create_framework_contract_result_full.json")
            }
        }
    }

    @Nested
    inner class CancelFrameworkContract {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CancelFrameworkContractAction.Params>("json/client/contracting/cancel/cancel_framework_contract_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CancelFrameworkContractAction.Result>("json/client/contracting/cancel/cancel_framework_contract_result_full.json")
            }
        }
    }

    @Nested
    inner class DoPacs {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<DoPacsAction.Params>("json/client/contracting/do_pacs_params_fully.json")
            }

            @Test
            fun requires_1() {
                testingBindingAndMapping<DoPacsAction.Params>("json/client/contracting/do_pacs_params_required_1.json")
            }

            @Test
            fun requires_2() {
                testingBindingAndMapping<DoPacsAction.Params>("json/client/contracting/do_pacs_params_required_2.json")
            }

            @Test
            fun requires_3() {
                testingBindingAndMapping<DoPacsAction.Params>("json/client/contracting/do_pacs_params_required_3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<DoPacsAction.Result>("json/client/contracting/do_pacs_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<DoPacsAction.Result>("json/client/contracting/do_pacs_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<DoPacsAction.Result>("json/client/contracting/do_pacs_result_required_2.json")
            }
        }
    }

    @Nested
    inner class AddSupplierReferencesInFC {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<AddSupplierReferencesInFCAction.Params>("json/client/contracting/add_supplier_references_in_fc_params_fully.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<AddSupplierReferencesInFCAction.Result>("json/client/contracting/add_supplier_references_in_fc_result_fully.json")
            }
        }
    }

    @Nested
    inner class IssuingFrameworkContract {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<AddGeneratedDocumentToContractAction.Params>("json/client/contracting/add_generated_document_to_contract_params_fully.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<AddGeneratedDocumentToContractAction.Result>("json/client/contracting/add_generated_document_to_contract_result_fully.json")
            }
        }
    }

    @Nested
    inner class SetStateForContracts {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForContractsAction.Params>("json/client/contracting/set_state_for_contracts_params_fully.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<SetStateForContractsAction.Params>("json/client/contracting/set_state_for_contracts_params_required.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForContractsAction.Result>("json/client/contracting/set_state_for_contracts_result_fully.json")
            }
        }
    }

    @Nested
    inner class CheckContractState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckContractStateAction.Params>("json/client/contracting/check_contract_state_params_fully.json")
            }
        }
    }

    @Nested
    inner class CheckExistenceSupplierReferencesInFC {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckExistenceSupplierReferencesInFCAction.Params>("json/client/contracting/check_existence_supplier_references_in_fc_params_fully.json")
            }
        }
    }

    @Nested
    inner class GetContractState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetContractStateAction.Params>("json/client/contracting/get_contract_state_params_fully.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetContractStateAction.Result>("json/client/contracting/get_contract_state_result_fully.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetContractStateAction.Result>("json/client/contracting/get_contract_state_result_required_1.json")
            }
        }
    }

    @Nested
    inner class GetAwardIdByPac {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardIdByPacAction.Params>("json/client/contracting/get_award_id_by_pac_params_fully.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardIdByPacAction.Result>("json/client/contracting/get_award_id_by_pac_result_fully.json")
            }
        }
    }

    @Nested
    inner class CreateConfirmationRequests {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateConfirmationRequestsAction.Params>("json/client/contracting/create_confirmation_requests_params_fully.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateConfirmationRequestsAction.Params>("json/client/contracting/create_confirmation_requests_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateConfirmationRequestsAction.Params>("json/client/contracting/create_confirmation_requests_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateConfirmationRequestsAction.Result>("json/client/contracting/create_confirmation_requests_result_fully.json")
            }
        }
    }

    @Nested
    inner class ValidateConfirmationResponseData {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateConfirmationResponseDataAction.Params>("json/client/contracting/validate_confirmation_response_data_params_fully.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateConfirmationResponseDataAction.Params>("json/client/contracting/validate_confirmation_response_data_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidateConfirmationResponseDataAction.Params>("json/client/contracting/validate_confirmation_response_data_params_required_2.json")
            }
        }
    }

    @Nested
    inner class GetOrganizationIdAndSourceOfRequestGroup {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationIdAndSourceOfRequestGroupAction.Params>("json/client/contracting/get_request_group_by_confirmation_response_params_fully.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationIdAndSourceOfRequestGroupAction.Result>("json/client/contracting/get_request_group_by_confirmation_response_result_fully.json")
            }
        }
    }

    @Nested
    inner class CheckAccessToRequestOfConfirmation {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToRequestOfConfirmationAction.Params>("json/client/contracting/check_access_to_request_of_confirmation_fully.json")
            }
        }
    }

    @Nested
    inner class CreateConfirmationResponse {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Params>("json/client/contracting/create_confirmation_response_request_fully.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Params>("json/client/contracting/create_confirmation_response_request_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Params>("json/client/contracting/create_confirmation_response_request_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Result>("json/client/contracting/create_confirmation_response_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Result>("json/client/contracting/create_confirmation_response_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateConfirmationResponseAction.Result>("json/client/contracting/create_confirmation_response_result_required_2.json")
            }
        }

        @Nested
        inner class CheckExistenceOfConfirmationResponses {

            @Nested
            inner class Params {
                @Test
                fun fully() {
                    testingBindingAndMapping<CheckExistenceOfConfirmationResponsesAction.Params>("json/client/contracting/check_existence_of_confirmation_responses_params_fully.json")
                }
            }
        }
    }

    @Nested
    inner class CheckAccessToContract {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToContractAction.Params>("json/client/contracting/check_access_to_contract.json")
            }
        }
    }

    @Nested
    inner class GetSupplierIdsByContract {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSupplierIdsByContractAction.Params>("json/client/contracting/get_suppliers_ids_by_contract_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSupplierIdsByContractAction.Result>("json/client/contracting/get_suppliers_ids_by_contract_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckRelatedContractsState {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckRelatedContractsStateAction.Params>("json/client/contracting/check_related_contracts_state_params_full.json")
            }
        }
    }

    @Nested
    inner class GetRelatedAwardIdByCans {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSupplierIdsByContractAction.Params>("json/client/contracting/get_suppliers_ids_by_contract_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSupplierIdsByContractAction.Result>("json/client/contracting/get_suppliers_ids_by_contract_result_full.json")
            }
            @Test
            fun required() {
                testingBindingAndMapping<GetSupplierIdsByContractAction.Result>("json/client/contracting/get_suppliers_ids_by_contract_result_full.json")
            }
        }
    }

    @Nested
    inner class CreateContract {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateContractAction.Params>("json/client/contracting/create_contract_params_fully.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateContractAction.Params>("json/client/contracting/create_contract_params_required1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CreateContractAction.Params>("json/client/contracting/create_contract_params_required2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<CreateContractAction.Params>("json/client/contracting/create_contract_params_required3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateContractAction.Result>("json/client/contracting/create_contract_result_fully.json")
            }
            @Test
            fun required1() {
                testingBindingAndMapping<CreateContractAction.Result>("json/client/contracting/create_contract_result_required1.json")
            }
            @Test
            fun required2() {
                testingBindingAndMapping<CreateContractAction.Result>("json/client/contracting/create_contract_result_required2.json")
            }
            @Test
            fun required3() {
                testingBindingAndMapping<CreateContractAction.Result>("json/client/contracting/create_contract_result_required3.json")
            }
        }
    }

}
