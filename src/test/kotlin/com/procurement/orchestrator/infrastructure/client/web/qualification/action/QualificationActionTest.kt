package com.procurement.orchestrator.infrastructure.client.web.qualification.action

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

}
