package com.procurement.orchestrator.infrastructure.client.web.dossier.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DossierActionsTest {

    @Nested
    inner class ValidateRequirementResponse {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateRequirementResponseAction.Params>("json/client/dossier/validate_requirement_response_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<ValidateRequirementResponseAction.Params>("json/client/dossier/validate_requirement_response_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<ValidateRequirementResponseAction.Params>("json/client/dossier/validate_requirement_response_params_required_2.json")
            }
        }
    }

    @Nested
    inner class SetStateForSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForSubmissionAction.Params>("json/client/dossier/set_state_for_submission_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForSubmissionAction.Result>("json/client/dossier/set_state_for_submission_result_full.json")
            }
        }
    }
}
