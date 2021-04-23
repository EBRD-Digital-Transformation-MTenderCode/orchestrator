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
            fun fully() {
                testingBindingAndMapping<DoInvitationsAction.Params>("json/client/submission/do_invitations_params_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<DoInvitationsAction.Params>("json/client/submission/do_invitations_params_required.json")
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
    inner class CheckAbsenceActiveInvitations {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAbsenceActiveInvitationsAction.Params>("json/client/submission/check_absence_active_invitations_params_full.json")
            }
        }
    }

    @Nested
    inner class CheckAccessToBid {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckAccessToBidAction.Params>("json/client/submission/check_access_to_bid_params_full.json")
            }
        }
    }

    @Nested
    inner class ValidateTenderPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<ValidateTenderPeriodAction.Params>("json/client/submission/validate_tender_period_full.json")
            }
        }
    }

    @Nested
    inner class CheckBidState {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckBidStateAction.Params>("json/client/submission/check_bid_state_params_full.json")
            }
        }
    }

    @Nested
    inner class PublishInvitations {
        @Nested
        inner class Params {
            @Test
            fun full() {
                testingBindingAndMapping<PublishInvitationsAction.Params>("json/client/submission/publish_invitations_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun full() {
                testingBindingAndMapping<PublishInvitationsAction.Result>("json/client/submission/publish_invitations_result_full.json")
            }
        }
    }

    @Nested
    inner class SetTenderPeriod {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<SetTenderPeriodAction.Params>("json/client/submission/set_tender_period_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<SetTenderPeriodAction.Result>("json/client/submission/set_tender_period_result_full.json")
            }
        }
    }

    @Nested
    inner class SetStateForBids {

        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<SetStateForBidsAction.Params>("json/client/submission/set_state_for_bids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun required() {
                testingBindingAndMapping<SetStateForBidsAction.Result>("json/client/submission/set_state_for_bids_result_full.json")
            }
        }

    }

    @Nested
    inner class CheckPeriod {
        @Nested
        inner class Params {
            @Test
            fun required() {
                testingBindingAndMapping<CheckPeriodAction.Params>("json/client/submission/check_period_params_full.json")
            }
        }
    }

    @Nested
    inner class ValidateBidData {
        @Nested
        inner class Params {
            @Test
            fun full() {
                testingBindingAndMapping<ValidateBidDataAction.Params>("json/client/submission/validate_bid_data_params_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<ValidateBidDataAction.Params>("json/client/submission/validate_bid_data_params_required.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<ValidateBidDataAction.Params>("json/client/submission/validate_bid_data_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<ValidateBidDataAction.Params>("json/client/submission/validate_bid_data_params_required_2.json")
            }
        }
    }

    @Nested
    inner class CreateBid {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateBidAction.Params>("json/client/submission/create_bid_params_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<CreateBidAction.Params>("json/client/submission/create_bid_params_required.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateBidAction.Params>("json/client/submission/create_bid_params_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateBidAction.Params>("json/client/submission/create_bid_params_required_2.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateBidAction.Result>("json/client/submission/create_bid_result_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<CreateBidAction.Result>("json/client/submission/create_bid_result_required.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<CreateBidAction.Result>("json/client/submission/create_bid_result_required_1.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<CreateBidAction.Result>("json/client/submission/create_bid_result_required_2.json")
            }
        }
    }

    @Nested
    inner class CreateInvitations {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateInvitationsAction.Params>("json/client/submission/create_invitations_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateInvitationsAction.Result>("json/client/submission/create_invitations_result_full.json")
            }
        }
    }

    @Nested
    inner class FinalizeBidsByAwards {
        @Nested
        inner class Params {

            @Test
            fun fully() {
                testingBindingAndMapping<FinalizeBidsByAwardsAction.Params>("json/client/submission/finalize_bids_by_awards_params_full.json")
            }
        }

        @Nested
        inner class Result {

            @Test
            fun fully() {
                testingBindingAndMapping<FinalizeBidsByAwardsAction.Result>("json/client/submission/finalize_bids_by_awards_result_full.json")
            }
        }
    }

    @Nested
    inner class GetBidsForPacs {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetBidsForPacsAction.Params>("json/client/submission/get_bids_for_pacs_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetBidsForPacsAction.Result>("json/client/submission/get_bids_for_pacs_result_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<GetBidsForPacsAction.Result>("json/client/submission/get_bids_for_pacs_result_required_1.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetBidsForPacsAction.Result>("json/client/submission/get_bids_for_pacs_result_required_2.json")
            }
        }
    }

    @Nested
    inner class GetOrganizationsByReferencesFromPacs {
        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationsByReferencesFromPacsAction.Params>("json/client/submission/get_organizations_by_references_from_pacs_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetOrganizationsByReferencesFromPacsAction.Result>("json/client/submission/get_organizations_by_references_from_pacs_result_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<GetOrganizationsByReferencesFromPacsAction.Result>("json/client/submission/get_organizations_by_references_from_pacs_result_required_1.json")
            }

            @Test
            fun required_1() {
                testingBindingAndMapping<GetOrganizationsByReferencesFromPacsAction.Result>("json/client/submission/get_organizations_by_references_from_pacs_result_required_2.json")
            }

            @Test
            fun required_2() {
                testingBindingAndMapping<GetOrganizationsByReferencesFromPacsAction.Result>("json/client/submission/get_organizations_by_references_from_pacs_result_required_3.json")
            }
        }
    }

    @Nested
    inner class FindDocumentsByBidIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindDocumentsByBidIdsAction.Params>("json/client/submission/find_documents_by_bid_ids_params_full.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindDocumentsByBidIdsAction.Result>("json/client/submission/find_documents_by_bid_ids_result_full.json")
            }

            @Test
            fun required() {
                testingBindingAndMapping<FindDocumentsByBidIdsAction.Result>("json/client/submission/find_documents_by_bid_ids_result_required_1.json")
            }
        }
    }

    @Nested
    inner class CheckExistenceOfInvitation {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CheckExistenceOfInvitationAction.Params>("json/client/submission/check_existence_of_invitation_params_full.json")
            }
        }
    }
}
