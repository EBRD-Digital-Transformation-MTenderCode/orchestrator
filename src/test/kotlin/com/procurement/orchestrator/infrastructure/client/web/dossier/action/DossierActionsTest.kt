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
    inner class CheckAccessToSubmission {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToSubmissionAction.Params>("json/client/dossier/check_access_to_submission_full.json")
            }
        }
    }

    @Nested
    inner class GetSubmissionStateByIds {
        @Nested
        inner class Params {
            @Test
            fun test() {
                testingBindingAndMapping<GetSubmissionStateByIdsAction.Params>("json/client/dossier/get_submission_state_by_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun test() {
                testingBindingAndMapping<GetSubmissionStateByIdsAction.Result>("json/client/dossier/get_submission_state_by_ids_result_full.json")
            }
        }
    }

    @Nested
    inner class GetOrganizations {
        @Nested
        inner class Params {
            @Test
            fun test() {
                testingBindingAndMapping<GetOrganizationsAction.Params>("json/client/dossier/get_organizations_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/dossier/get_organizations_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/dossier/get_organizations_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/dossier/get_organizations_result_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<GetOrganizationsAction.Result>("json/client/dossier/get_organizations_result_required_3.json")
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

    @Nested
    inner class CreateSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateSubmissionAction.Params>("json/client/dossier/create_submission_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<CreateSubmissionAction.Result>("json/client/dossier/create_submission_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<CreateSubmissionAction.Result>("json/client/dossier/create_submission_result_required_1.json")
            }
        }
    }


    @Nested
    inner class SetStateForSubmission {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForSubmissionAction.Params>("json/client/dossier/set_state_for_submission_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<SetStateForSubmissionAction.Result>("json/client/dossier/set_state_for_submission_result_full.json")
            }
        }
    }
}
