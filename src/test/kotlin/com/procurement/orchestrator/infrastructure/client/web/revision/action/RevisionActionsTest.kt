package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RevisionActionsTest {

    @Nested
    inner class CheckAccessToAmendment {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToAmendmentAction.Params>("json/client/revision/check_access_to_amendment_params_full.json")
            }
        }
    }

    @Nested
    inner class CreateAmendment {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateAmendmentAction.Params>("json/client/revision/create_amendment_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateAmendmentAction.Params>("json/client/revision/create_amendment_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CreateAmendmentAction.Params>("json/client/revision/create_amendment_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateAmendmentAction.Result>("json/client/revision/create_amendment_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateAmendmentAction.Result>("json/client/revision/create_amendment_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CreateAmendmentAction.Result>("json/client/revision/create_amendment_result_required_2.json")
            }
        }
    }

    @Nested
    inner class DataValidation {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<DataValidationAction.Params>("json/client/revision/data_validation_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<DataValidationAction.Params>("json/client/revision/data_validation_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<DataValidationAction.Params>("json/client/revision/data_validation_params_required_2.json")
            }
        }
    }

    @Nested
    inner class FindAmendmentIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAmendmentIdsAction.Params>("json/client/revision/find_amendment_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindAmendmentIdsAction.Params>("json/client/revision/find_amendment_ids_params_required_1.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAmendmentIdsAction.Result>("json/client/revision/find_amendment_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class GetMainPartOfAmendmentByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAmendmentByIdsAction.Params>("json/client/revision/get_amendment_by_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAmendmentByIdsAction.Result>("json/client/revision/get_amendment_by_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForAmendment {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForAmendmentAction.Params>("json/client/revision/set_state_for_amendment_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForAmendmentAction.Result>("json/client/revision/set_state_for_amendment_result_full.json")
            }
        }
    }
}
