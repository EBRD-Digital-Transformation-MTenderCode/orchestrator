package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.procurement.orchestrator.infrastructure.client.web.dossier.action.CheckPeriodAction
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QualificationActionTest {

    @Nested
    inner class CreateSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateSubmissionAction.Params>("json/client/qualification/create_submission_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateSubmissionAction.Result>("json/client/qualification/create_submission_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckPeriodAction.Params>("json/client/qualification/check_period_params_full.json")
            }
        }
    }

    @Nested
    inner class ValidateSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/qualification/validate_submission_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/qualification/validate_submission_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/qualification/validate_submission_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/qualification/validate_submission_params_required_3.json")
            }

            @Test
            fun required_4() {
                testingBindingAndMapping<ValidateSubmissionAction.Params>("json/client/qualification/validate_submission_params_required_4.json")
            }
        }
    }

}
