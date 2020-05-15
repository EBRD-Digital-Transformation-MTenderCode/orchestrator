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
    inner class ValidateSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/dossier/validate_submission_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/dossier/validate_submission_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/dossier/validate_submission_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/dossier/validate_submission_params_required_3.json")
            }

            @Test
            fun required_4() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/dossier/validate_submission_params_required_4.json")
            }
        }
    }

    @Nested
    inner class CheckPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckPeriodAction.Params>("json/client/dossier/check_period_params_full.json")
            }
        }
    }
}
