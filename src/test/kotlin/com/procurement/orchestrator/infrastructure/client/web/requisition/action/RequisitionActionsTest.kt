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
            @Test
            fun required_1() {
                testingBindingAndMapping<ValidatePcrDataAction.Params>("json/client/requisition/validate_pcr_data_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidatePcrDataAction.Params>("json/client/requisition/validate_pcr_data_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<ValidatePcrDataAction.Params>("json/client/requisition/validate_pcr_data_params_required_3.json")
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

            @Test
            fun required_3() {
                testingBindingAndMapping<CreatePcrAction.Result>("json/client/requisition/create_pcr_result_required_3.json")
            }
        }
    }

    @Nested
    inner class SetUnsuccessfulStateForLots {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetUnsuccessfulStateForLotsAction.Params>("json/client/requisition/set_unsuccessful_state_for_lots_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetUnsuccessfulStateForLotsAction.Result>("json/client/requisition/set_unsuccessful_state_for_lots_result_full.json")
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

    @Nested
    inner class CheckItemsDataForRfq {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckItemsDataForRfqAction.Params>("json/client/requisition/check_items_data_for_rfq_params_full.json")
            }
        }
    }

    @Nested
    inner class FindProcurementMethodModalities {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindProcurementMethodModalitiesAction.Params>("json/client/requisition/find_procurement_method_modalities_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindProcurementMethodModalitiesAction.Result>("json/client/requisition/find_procurement_method_modalities_result_full.json")
            }
        }
    }

    @Nested
    inner class GetCurrency {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetCurrencyAction.Params>("json/client/requisition/get_currency_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetCurrencyAction.Result>("json/client/requisition/get_currency_result_full.json")
            }
        }
    }

    @Nested
    inner class FindItemsByLotIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindItemsByLotIdsAction.Params>("json/client/requisition/find_items_by_lot_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindItemsByLotIdsAction.Result>("json/client/requisition/find_items_by_lot_ids_result_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<FindItemsByLotIdsAction.Result>("json/client/requisition/find_items_by_lot_ids_result_required.json")
            }
        }
    }

    @Nested
    inner class ValidateRequirementResponses {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/requisition/validate_requirement_responses_params_full.json")
            }
            @Test
            fun required() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/requisition/validate_requirement_responses_params_required.json")
            }
            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/requisition/validate_requirement_responses_params_required_1.json")
            }
        }
    }

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToTenderAction.Params>("json/client/requisition/check_access_to_tender_params_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForLots {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Params>("json/client/requisition/set_state_for_lots_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Result>("json/client/requisition/set_state_for_lots_result_full.json")
            }
        }
    }

    @Nested
    inner class FindCriteriaAndTargetsForPacs {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindCriteriaAndTargetsForPacsAction.Params>("json/client/requisition/find_criteria_and_targets_for_pacs_params_full.json")
            }
        }

        @Nested
        inner class Result{
            @Test
            fun fully() {
                testingBindingAndMapping<FindCriteriaAndTargetsForPacsAction.Result>("json/client/requisition/find_criteria_and_targets_for_pacs_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindCriteriaAndTargetsForPacsAction.Result>("json/client/requisition/find_criteria_and_targets_for_pacs_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<FindCriteriaAndTargetsForPacsAction.Result>("json/client/requisition/find_criteria_and_targets_for_pacs_required_2.json")
            }
        }
    }

    @Nested
    inner class GetOcidFromRelatedProcess{
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOcidFromRelatedProcessAction.Params>("json/client/requisition/get_ocid_from_related_process_params_full.json")
            }
        }

        @Nested
        inner class Result{
            @Test
            fun fully() {
                testingBindingAndMapping<GetOcidFromRelatedProcessAction.Result>("json/client/requisition/get_ocid_from_related_process_result_full.json")
            }
        }
    }

}
