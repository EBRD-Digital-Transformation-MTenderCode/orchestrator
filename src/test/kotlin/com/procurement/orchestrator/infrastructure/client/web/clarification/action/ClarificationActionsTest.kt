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
}
