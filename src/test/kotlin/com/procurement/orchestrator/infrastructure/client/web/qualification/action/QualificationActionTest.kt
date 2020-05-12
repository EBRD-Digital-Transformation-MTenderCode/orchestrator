package com.procurement.orchestrator.infrastructure.client.web.qualification.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QualificationActionTest {

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
    inner class CheckAccessToSubmission {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToSubmissionAction.Params>("json/client/qualification/check_access_to_submission_full.json")
            }
        }
    }
}
