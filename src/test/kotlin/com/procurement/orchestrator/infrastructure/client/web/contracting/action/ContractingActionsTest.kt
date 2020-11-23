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
}
