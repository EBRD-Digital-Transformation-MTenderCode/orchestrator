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
        inner class Result{
            @Test
            fun fully(){
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
        inner class Result{
            @Test
            fun fully(){
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_full.json")
            }

            @Test
            fun required1(){
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_required_1.json")
            }

            @Test
            fun required2(){
                testingBindingAndMapping<FindSupplierReferencesOfActivePacsAction.Result>("json/client/contracting/find_supplier_references_of_active_pacs_result_required_2.json")
            }
        }
    }

    @Nested
    inner class CreateContract {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateFrameworkContractAction.Params>("json/client/contracting/do/create_framework_contract_params_full.json")
            }
        }

        @Nested
        inner class Result{
            @Test
            fun fully(){
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
        inner class Result{
            @Test
            fun fully(){
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
        inner class Result{
            @Test
            fun fully(){
                testingBindingAndMapping<DoPacsAction.Result>("json/client/contracting/do_pacs_result_full.json")
            }

            @Test
            fun required1(){
                testingBindingAndMapping<DoPacsAction.Result>("json/client/contracting/do_pacs_result_required_1.json")
            }

            @Test
            fun required2(){
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
        inner class Result{
            @Test
            fun fully(){
                testingBindingAndMapping<AddSupplierReferencesInFCAction.Result>("json/client/contracting/add_supplier_references_in_fc_result_fully.json")
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
        inner class Result{
            @Test
            fun fully(){
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

}
