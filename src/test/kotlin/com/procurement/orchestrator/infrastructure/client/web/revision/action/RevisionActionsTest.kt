package com.procurement.orchestrator.infrastructure.client.web.revision.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RevisionActionsTest {

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
    inner class GetAmendmentIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAmendmentIdsAction.Params>("json/client/revision/get_amendment_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetAmendmentIdsAction.Params>("json/client/revision/get_amendment_ids_params_required_1.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAmendmentIdsAction.Result>("json/client/revision/get_amendment_ids_result_full.json")
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
}
