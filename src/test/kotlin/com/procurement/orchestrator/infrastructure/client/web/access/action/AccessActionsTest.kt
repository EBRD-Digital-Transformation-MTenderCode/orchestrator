package com.procurement.orchestrator.infrastructure.client.web.access.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AccessActionsTest {

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToTenderAction.Params>("json/client/access/check_access_to_tender_params_full.json")
            }
        }
    }

    @Nested
    inner class VerifyRequirementResponse {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<VerifyRequirementResponseAction.Params>("json/client/access/verify_requirement_response_params_required_2.json")
            }
        }
    }

    @Nested
    inner class GetLotStateByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Params>("json/client/access/get_lot_state_by_ids_action_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotStateByIdsAction.Result>("json/client/access/get_lot_state_by_ids_action_result_full.json")
            }
        }
    }

    @Nested
    inner class FindLotIdsActionTest {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindLotIdsAction.Params>("json/client/access/find_lot_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindLotIdsAction.Result>("json/client/access/find_lot_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class ResponderProcessing {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<ResponderProcessingAction.Params>("json/client/access/responder_processing_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<ResponderProcessingAction.Result>("json/client/access/responder_processing_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForTender {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForTenderAction.Params>("json/client/access/set_state_for_tender_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForTenderAction.Result>("json/client/access/set_state_for_tender_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForLots {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Params>("json/client/access/set_state_for_lots_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForLotsAction.Result>("json/client/access/set_state_for_lots_result_full.json")
            }
        }
    }

    @Nested
    inner class GetTenderState {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Params>("json/client/access/get_tender_state_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderStateAction.Result>("json/client/access/get_tender_state_result_full.json")
            }
        }
    }

    @Nested
    inner class GetRelatedTenderCurrency {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderCurrencyAction.Params>("json/client/access/get_tender_currency_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetTenderCurrencyAction.Result>("json/client/access/get_tender_currency_result_full.json")
            }
        }
    }

    @Nested
    inner class GetOrganizations {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationsAction.Params>("json/client/access/get_organizations_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/access/get_organizations_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/access/get_organizations_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/access/get_organizations_result_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/access/get_organizations_result_required_3.json")
            }
        }
    }

    @Nested
    inner class GetQualificationCriteriaAndMethod {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetQualificationCriteriaAndMethodAction.Params>("json/client/access/get_qualification_criteria_and_method_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetQualificationCriteriaAndMethodAction.Result>("json/client/access/get_qualification_criteria_and_method_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetQualificationCriteriaAndMethodAction.Result>("json/client/access/get_qualification_criteria_and_method_result_required_1.json")
            }
        }
    }

    @Nested
    inner class ValidateRequirementResponses {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/access/validate_requirement_responses_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/access/validate_requirement_responses_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Params>("json/client/access/validate_requirement_responses_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRequirementResponsesAction.Result>("json/client/access/validate_requirement_responses_result_full.json")
            }
        }
    }

    @Nested
    inner class FindCriteria {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<FindCriteriaAction.Params>("json/client/access/find_criteria_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<FindCriteriaAction.Result>("json/client/access/find_criteria_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<FindCriteriaAction.Result>("json/client/access/find_criteria_result_required_1.json")
            }
        }
    }

    @Nested
    inner class CreateCriteriaForProcuringEntity {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateCriteriaForProcuringEntityAction.Params>("json/client/access/create_criteria_for_procuring_entity_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateCriteriaForProcuringEntityAction.Params>("json/client/access/create_criteria_for_procuring_entity_params_required_1.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateCriteriaForProcuringEntityAction.Result>("json/client/access/create_criteria_for_procuring_entity_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateCriteriaForProcuringEntityAction.Result>("json/client/access/create_criteria_for_procuring_entity_result_required_1.json")
            }
        }
    }

    @Nested
    inner class CheckTenderState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckTenderStateAction.Params>("json/client/access/check_tender_state_params_full.json")
            }
        }
    }

    @Nested
    inner class FindAuctions {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAuctionsAction.Params>("json/client/access/find_auctions_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAuctionsAction.Result>("json/client/access/find_auctions_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckExistenceFa {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckExistenceFaAction.Params>("json/client/access/check_existence_fa_full.json")
            }
        }
    }

    @Nested
    inner class CheckExistenceSignAuction {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckExistenceSignAuctionAction.Params>("json/client/access/check_existence_sign_auction_full.json")
            }
        }
    }

    @Nested
    inner class OutsourcingPn {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<OutsourcingPnAction.Params>("json/client/access/outsourcing_pn_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<OutsourcingPnAction.Result>("json/client/access/outsourcing_pn_result_full.json")
            }
        }
    }

    @Nested
    inner class ValidateRelatedTenderClassification {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRelatedTenderClassificationAction.Params>("json/client/access/validate_related_tender_classification_full.json")
            }
        }
    }

    @Nested
    inner class CreateRelationToOtherProcess {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToOtherProcessAction.Params>("json/client/access/create_relation_to_other_process_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToOtherProcessAction.Result>("json/client/access/create_relation_to_other_process_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateRelationToOtherProcessAction.Result>("json/client/access/create_relation_to_other_process_result_required_1.json")
            }
        }
    }

    @Nested
    inner class CreateRelationToContractProcessStage {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToContractProcessStageAction.Params>("json/client/access/create_relation_to_contract_process_stage_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRelationToContractProcessStageAction.Result>("json/client/access/create_relation_to_contract_process_stage_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckRelation {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckRelationAction.Params>("json/client/access/check_relation_params_full.json")
            }
        }
    }

    @Nested
    inner class CalculateAPValue {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CalculateAPValueAction.Params>("json/client/access/calculate_ap_value_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CalculateAPValueAction.Result>("json/client/access/calculate_ap_value_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckEqualityCurrencies {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckEqualityCurrenciesAction.Params>("json/client/access/check_equality_currencies_params_full.json")
            }
        }
    }

    @Nested
    inner class GetMainProcurementCategory {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetMainProcurementCategoryAction.Params>("json/client/access/get_main_procurement_category_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetMainProcurementCategoryAction.Result>("json/client/access/get_main_procurement_category_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckLotsState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckLotsStateAction.Params>("json/client/access/check_lots_state_params_full.json")
            }
        }
    }

    @Nested
    inner class GetLotsValue {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotsValueAction.Params>("json/client/access/get_lots_value_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetLotsValueAction.Result>("json/client/access/get_lots_value_result_full.json")
            }
        }
    }

    @Nested
    inner class ValidateLotsDataForDivision {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateLotsDataForDivisionAction.Params>("json/client/access/validate_lots_data_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ValidateLotsDataForDivisionAction.Params>("json/client/access/validate_lots_data_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<ValidateLotsDataForDivisionAction.Params>("json/client/access/validate_lots_data_params_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<ValidateLotsDataForDivisionAction.Params>("json/client/access/validate_lots_data_params_required_3.json")
            }
        }
    }

    @Nested
    inner class DivideLot {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<DivideLotAction.Params>("json/client/access/divide_lot_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<DivideLotAction.Params>("json/client/access/divide_lot_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<DivideLotAction.Params>("json/client/access/divide_lot_params_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<DivideLotAction.Params>("json/client/access/divide_lot_params_required_3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<DivideLotAction.Result>("json/client/access/divide_lot_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<DivideLotAction.Result>("json/client/access/divide_lot_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<DivideLotAction.Result>("json/client/access/divide_lot_result_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<DivideLotAction.Result>("json/client/access/divide_lot_result_required_3.json")
            }
        }
    }

    @Nested
    inner class AddClientsToPartiesInAP {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<AddClientsToPartiesInAPAction.Params>("json/client/access/add_clients_to_parties_in_ap_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<AddClientsToPartiesInAPAction.Result>("json/client/access/add_clients_to_parties_in_ap_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<AddClientsToPartiesInAPAction.Result>("json/client/access/add_clients_to_parties_in_ap_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<AddClientsToPartiesInAPAction.Result>("json/client/access/add_clients_to_parties_in_ap_result_required_2.json")
            }
        }
    }

    @Nested
    inner class GetItemsByLotIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetItemsByLotIdsAction.Params>("json/client/access/get_items_by_lot_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetItemsByLotIdsAction.Result>("json/client/access/get_items_by_lot_ids_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetItemsByLotIdsAction.Result>("json/client/access/get_items_by_lot_ids_result_required_1.json")
            }
        }
    }

    @Nested
    inner class GetBuyersOwners {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<GetBuyersOwnersAction.Params>("json/client/access/ge_buyers_owners_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetBuyersOwnersAction.Result>("json/client/access/get_buyers_owners_result_full.json")
            }
        }
    }

    @Nested
    inner class ValidateRfqData {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRfqDataAction.Params>("json/client/access/validate_rfq_data_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ValidateRfqDataAction.Params>("json/client/access/validate_rfq_data_params_required_1.json")
            }
        }
    }

    @Nested
    inner class CreateRfq {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRfqAction.Params>("json/client/access/create_rfq_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateRfqAction.Params>("json/client/access/create_rfq_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CreateRfqAction.Params>("json/client/access/create_rfq_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRfqAction.Result>("json/client/access/create_rfq_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateRfqAction.Result>("json/client/access/create_rfq_result_required_1.json")
            }
        }
    }

    @Nested
    inner class PersonesProcessing {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/access/persones_processing_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/access/persones_processing_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/access/persones_processing_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/access/persones_processing_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/access/persones_processing_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/access/persones_processing_result_required_2.json")
            }
        }
    }

    @Nested
    inner class DefineTenderClassification {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<DefineTenderClassificationAction.Params>("json/client/access/define_tender_classification_params_full.json")
            }
        }

        @Nested
        inner class Resut {

            @Test
            fun fully() {
                testingBindingAndMapping<DefineTenderClassificationAction.Result>("json/client/access/define_tender_classification_result_full.json")
            }
        }
    }

    @Nested
    inner class GetDataForContract {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<GetDataForContractAction.Params>("json/client/access/get_data_for_contract_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetDataForContractAction.Result>("json/client/access/get_data_for_contract_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetDataForContractAction.Result>("json/client/access/get_data_for_contract_result_required1.json")
            }
        }
    }
}
