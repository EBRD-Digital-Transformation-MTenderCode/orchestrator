package com.procurement.orchestrator.infrastructure.client.web.submission.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubmissionActionTest {

    @Nested
    inner class DoInvitations {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<DoInvitationsAction.Params>("json/client/qualification/start_qualification_period_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<DoInvitationsAction.Result>("json/client/qualification/start_qualification_period_result_full.json")
            }
        }
    }
}
