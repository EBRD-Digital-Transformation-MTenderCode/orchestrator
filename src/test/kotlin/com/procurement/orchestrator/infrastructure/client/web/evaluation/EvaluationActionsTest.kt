package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateRequirementResponseAction
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
    inner class CreateRequirementResponse {

        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<CreateRequirementResponseAction.Params>("json/client/evaluation/create_requirement_response_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateRequirementResponseAction.Params>("json/client/evaluation/create_requirement_response_params_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<CreateRequirementResponseAction.Params>("json/client/evaluation/create_requirement_response_params_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<CreateRequirementResponseAction.Params>("json/client/evaluation/create_requirement_response_params_required_3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateRequirementResponseAction.Result>("json/client/evaluation/create_requirement_response_result_full.json")
            }
        }
    }
}
