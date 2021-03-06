package com.procurement.orchestrator.infrastructure.client.web.clarification.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClarificationActionsTest {

    @Nested
    inner class FindEnquiryIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindEnquiryIdsAction.Params>("json/client/clarification/find_enquiry_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindEnquiryIdsAction.Params>("json/client/clarification/find_enquiry_ids_params_1.json")
            }
        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindEnquiryIdsAction.Result>("json/client/clarification/find_enquiry_ids_result_full.json")
            }

        }
    }

    @Nested
    inner class GetEnquiryByIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<GetEnquiryByIdsAction.Params>("json/client/clarification/get_enquiry_by_ids_params_full.json")
            }

        }
        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<GetEnquiryByIdsAction.Result>("json/client/clarification/get_enquiry_by_ids_result_full.json")
            }

            @Test
            fun fully1() {
                testingBindingAndMapping<GetEnquiryByIdsAction.Result>("json/client/clarification/get_enquiry_by_ids_result_1.json")
            }

            @Test
            fun fully2() {
                testingBindingAndMapping<GetEnquiryByIdsAction.Result>("json/client/clarification/get_enquiry_by_ids_result_2.json")
            }

        }
    }

    @Nested
    inner class FindEnquiries {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindEnquiriesAction.Params>("json/client/clarification/find_enquiries_params_full.json")
            }

        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<FindEnquiriesAction.Result>("json/client/clarification/find_enquiries_result_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindEnquiriesAction.Result>("json/client/clarification/find_enquiries_result_required_1.json")
            }

            @Test
            fun required2() {
                testingBindingAndMapping<FindEnquiriesAction.Result>("json/client/clarification/find_enquiries_result_required_2.json")
            }

            @Test
            fun required3() {
                testingBindingAndMapping<FindEnquiriesAction.Result>("json/client/clarification/find_enquiries_result_required_3.json")
            }

        }
    }

    @Nested
    inner class CreateEnquiryPeriod {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateEnquiryPeriodAction.Params>("json/client/clarification/create_enquiry_period_params_full.json")
            }

        }

        @Nested
        inner class Result {
            @Test
            fun fully() {
                testingBindingAndMapping<CreateEnquiryPeriodAction.Result>("json/client/clarification/create_enquiry_period_result_full.json")
            }
        }
    }
}
