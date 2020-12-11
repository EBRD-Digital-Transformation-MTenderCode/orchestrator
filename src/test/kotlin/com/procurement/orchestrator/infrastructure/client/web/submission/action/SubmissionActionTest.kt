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
}
