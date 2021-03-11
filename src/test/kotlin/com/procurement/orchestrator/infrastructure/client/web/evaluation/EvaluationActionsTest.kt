package com.procurement.orchestrator.infrastructure.client.web.evaluation

import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.AddRequirementResponseAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAccessToAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckAwardsStateAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CheckRelatedTendererAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CloseAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.CreateUnsuccessfulAwardsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.DoConsiderationAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.FindAwardsForProtocolAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.GetAwardStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.StartAwardPeriodAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.UpdateAwardAction
import com.procurement.orchestrator.infrastructure.client.web.evaluation.action.ValidateAwardDataAction
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

    @Nested
    inner class StartAwardPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<StartAwardPeriodAction.Params>("json/client/evaluation/start_award_period_params.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<StartAwardPeriodAction.Result>("json/client/evaluation/start_award_period_result.json")
            }
        }
    }

    @Nested
    inner class ValidateAwardData {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateAwardDataAction.Params>("json/client/evaluation/validate_award_data_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateAwardDataAction.Params>("json/client/evaluation/validate_award_data_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidateAwardDataAction.Params>("json/client/evaluation/validate_award_data_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<ValidateAwardDataAction.Params>("json/client/evaluation/validate_award_data_params_required_3.json")
            }
        }
    }

    @Nested
    inner class CreateAward {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateAwardAction.Params>("json/client/evaluation/create_award_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateAwardAction.Params>("json/client/evaluation/create_award_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateAwardAction.Params>("json/client/evaluation/create_award_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<CreateAwardAction.Params>("json/client/evaluation/create_award_params_required_3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateAwardAction.Result>("json/client/evaluation/create_award_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateAwardAction.Result>("json/client/evaluation/create_award_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateAwardAction.Result>("json/client/evaluation/create_award_result_required_2.json")
            }

            @Test
            fun required_4() {
                testingBindingAndMapping<CreateAwardAction.Result>("json/client/evaluation/create_award_result_required_3.json")
            }
        }
    }

    @Nested
    inner class CheckAwardsState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAwardsStateAction.Params>("json/client/evaluation/check_awards_state_params.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CheckAwardsStateAction.Params>("json/client/evaluation/check_awards_state_params_required_1.json")
            }
        }
    }

    @Nested
    inner class GetAwardByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardByIdsAction.Params>("json/client/evaluation/get_award_by_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetAwardByIdsAction.Result>("json/client/evaluation/get_award_by_ids_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetAwardByIdsAction.Result>("json/client/evaluation/get_award_by_ids_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetAwardByIdsAction.Result>("json/client/evaluation/get_award_by_ids_result_required_2.json")
            }

            @Test
            fun required_4() {
                testingBindingAndMapping<GetAwardByIdsAction.Result>("json/client/evaluation/get_award_by_ids_result_required_3.json")
            }
        }
    }

    @Nested
    inner class UpdateAward {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<UpdateAwardAction.Params>("json/client/evaluation/update_award_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<UpdateAwardAction.Params>("json/client/evaluation/update_award_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<UpdateAwardAction.Params>("json/client/evaluation/update_award_params_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<UpdateAwardAction.Params>("json/client/evaluation/update_award_params_required_3.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<UpdateAwardAction.Result>("json/client/evaluation/update_award_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<UpdateAwardAction.Result>("json/client/evaluation/update_award_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<UpdateAwardAction.Result>("json/client/evaluation/update_award_result_required_2.json")
            }

            @Test
            fun required_4() {
                testingBindingAndMapping<UpdateAwardAction.Result>("json/client/evaluation/update_award_result_required_3.json")
            }
        }
    }

    @Nested
    inner class EvaluationFindAwardsForProtocol{

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAwardsForProtocolAction.Params>("json/client/evaluation/find_awards_for_protocol_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindAwardsForProtocolAction.Result>("json/client/evaluation/find_awards_for_protocol_result_full.json")
            }
        }
    }

    @Nested
    inner class DoConsideration{

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<DoConsiderationAction.Params>("json/client/evaluation/do_consideration_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<DoConsiderationAction.Result>("json/client/evaluation/do_consideration_result_full.json")
            }
        }
    }
}

