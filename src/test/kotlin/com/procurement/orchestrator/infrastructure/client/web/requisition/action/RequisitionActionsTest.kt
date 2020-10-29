package com.procurement.orchestrator.infrastructure.client.web.requisition.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RequisitionActionsTest {

    @Nested
    inner class ValidatePcrData {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidatePcrDataAction.Params>("json/client/requisition/validate_pcr_data_params_full.json")
            }
        }
    }


    @Nested
    inner class CreatePrc {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreatePcrAction.Params>("json/client/requisition/create_pcr_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreatePcrAction.Result>("json/client/requisition/create_pcr_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreatePcrAction.Result>("json/client/requisition/create_pcr_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreatePcrAction.Result>("json/client/requisition/create_pcr_result_required_2.json")
            }
        }
    }

    @Nested
    inner class CheckTenderState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckTenderStateAction.Params>("json/client/requisition/check_tender_state_params_full.json")
            }
        }
    }

    @Nested
    inner class CreateRelationToContractProcessStage {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToContractProcessStageAction.Params>(
                    "json/client/requisition/create_relation_to_contract_process_stage_params_full.json"
                )
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToContractProcessStageAction.Result>(
                    "json/client/requisition/create_relation_to_contract_process_stage_result_full.json"
                )
            }
        }
    }
    @Nested
    inner class GetTenderState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Params>("json/client/requisition/get_tender_state_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Result>("json/client/requisition/get_tender_state_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckLotsState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckLotsStateAction.Params>("json/client/requisition/check_lots_state_params_full.json")
            }
        }
    }

}
