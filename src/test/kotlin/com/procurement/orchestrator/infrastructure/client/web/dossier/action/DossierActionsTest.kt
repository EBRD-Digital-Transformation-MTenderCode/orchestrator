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

    @Nested
    inner class VerifySubmissionPeriodEnd {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<VerifySubmissionPeriodEndAction.Params>("json/client/dossier/verify_submission_period_end_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<VerifySubmissionPeriodEndAction.Result>("json/client/dossier/verify_submission_period_end_result_full.json")
            }
        }
    }

    @Nested
    inner class FindSubmissions {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindSubmissionsAction.Params>("json/client/dossier/find_submissions_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindSubmissionsAction.Result>("json/client/dossier/find_submissions_result_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<FindSubmissionsAction.Result>("json/client/dossier/find_submissions_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<FindSubmissionsAction.Result>("json/client/dossier/find_submissions_result_required_2.json")
            }

            @Test
            fun required_3() {
                testingBindingAndMapping<FindSubmissionsAction.Result>("json/client/dossier/find_submissions_result_required_3.json")
            }
        }
    }

    @Nested
    inner class GetSubmissionCandidateReferencesByQualificationIds {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSubmissionCandidateReferencesByQualificationIdsAction.Params>("json/client/dossier/get_submission_candidate_references_by_qualification_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>("json/client/dossier/get_submission_candidate_references_by_qualification_result_full.json")
            }

            @Test
            fun fully1() {
                testingBindingAndMapping<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>("json/client/dossier/get_submission_candidate_references_by_qualification_result_1.json")
            }

            @Test
            fun fully2() {
                testingBindingAndMapping<GetSubmissionCandidateReferencesByQualificationIdsAction.Result>("json/client/dossier/get_submission_candidate_references_by_qualification_result_2.json")
            }
        }
    }

    @Nested
    inner class FinalizeSubmissions {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FinalizeSubmissionsAction.Params>("json/client/dossier/finalize_submissions_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FinalizeSubmissionsAction.Result>("json/client/dossier/finalize_submissions_result_full.json")
            }
        }
    }

    @Nested
    inner class GetSubmissionsForTendering {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSubmissionsForTenderingAction.Params>("json/client/dossier/get_submission_for_tendering_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetSubmissionsForTenderingAction.Result>("json/client/dossier/get_submission_for_tendering_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<GetSubmissionsForTenderingAction.Result>("json/client/dossier/get_submission_for_tendering_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<GetSubmissionsForTenderingAction.Result>("json/client/dossier/get_submission_for_tendering_result_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<GetSubmissionsForTenderingAction.Result>("json/client/dossier/get_submission_for_tendering_result_required_3.json")
            }
        }
    }

    @Nested
    inner class CheckPresenceCandidateInOneSubmission {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckPresenceCandidateInOneSubmissionAction.Params>("json/client/dossier/check_presence_candidate_in_one_submission_params_full.json")
            }
        }
    }

    @Nested
    inner class DossierPersonesProcessing {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/dossier/persones_processing_params_full.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/dossier/persones_processing_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<PersonesProcessingAction.Params>("json/client/dossier/persones_processing_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/dossier/persones_processing_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/dossier/persones_processing_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<PersonesProcessingAction.Result>("json/client/dossier/persones_processing_result_required_2.json")
            }
        }
    }

    @Nested
    inner class GetInvitedCandidatesOwners {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetInvitedCandidatesOwnersAction.Params>("json/client/dossier/get_invited_candidates_owners_params_fully.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<GetInvitedCandidatesOwnersAction.Result>("json/client/dossier/get_invited_candidates_owners_result_full.json")
            }
        }
    }
}
