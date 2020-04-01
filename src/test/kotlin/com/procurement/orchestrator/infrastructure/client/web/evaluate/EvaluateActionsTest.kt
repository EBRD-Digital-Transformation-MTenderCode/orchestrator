package com.procurement.orchestrator.infrastructure.client.web.evaluate

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EvaluateActionsTest {

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToAwardAction.Params>("json/client/evaluate/check_access_to_award_params_full.json")
            }
        }
    }

    @Nested
    inner class GetAwardIdsActionTest {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardStateByIdsAction.Params>("json/client/evaluate/get_award_state_by_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardStateByIdsAction.Result>("json/client/evaluate/get_award_state_by_ids_result_full.json")
            }
        }
    }
}
