package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.AddRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CloseAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EvaluationActionsTest {

    @Nested
    inner class CheckAccessToTender {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToAwardAction.Params>("json/client/evaluation/check_access_to_award_params_full.json")
            }
        }
    }

    @Nested
    inner class GetAwardIdsActionTest {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardStateByIdsAction.Params>("json/client/evaluation/get_award_state_by_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardStateByIdsAction.Result>("json/client/evaluation/get_award_state_by_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class CheckRelatedTenderer {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckRelatedTendererAction.Params>("json/client/evaluation/check_related_tenderer_params_full.json")
            }
        }
    }

    @Nested
    inner class AddRequirementResponse {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<AddRequirementResponseAction.Params>("json/client/evaluation/add_requirement_response_params_full.json")
            }
        }
    }

    @Nested
    inner class CreateCreateUnsuccessfulAwards {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateUnsuccessfulAwardsAction.Params>("json/client/evaluation/create_unsuccessful_awards_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateUnsuccessfulAwardsAction.Result>("json/client/evaluation/create_unsuccessful_awards_result.json")
            }
        }
    }

    @Nested
    inner class CloseAwardPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CloseAwardPeriodAction.Params>("json/client/evaluation/create_close_award_period_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CloseAwardPeriodAction.Result>("json/client/evaluation/create_close_award_period_result.json")
            }
        }
    }
}

