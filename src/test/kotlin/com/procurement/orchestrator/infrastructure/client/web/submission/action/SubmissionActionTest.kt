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
                testingBindingAndMapping<DoInvitationsAction.Params>("json/client/submission/do_invitations_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<DoInvitationsAction.Result>("json/client/submission/do_invitations_result_full.json")
            }
        }
    }

    @Nested
    inner class ValidateTenderPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateTenderPeriodAction.Params>("json/client/dossier/validate_tender_period_full.json")
            }
        }
    }
}
