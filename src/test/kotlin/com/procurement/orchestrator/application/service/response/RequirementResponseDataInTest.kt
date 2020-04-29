package com.procurement.orchestrator.application.service.response

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RequirementResponseDataInTest {

    @Nested
    inner class Payload {
        @Test
        fun fully() {
            testingBindingAndMapping<RequirementResponseDataIn.Payload>("json/application/service/response/payload_requirement_response_data_in_full.json")
        }

        @Test
        fun required1() {
            testingBindingAndMapping<RequirementResponseDataIn.Payload>("json/application/service/response/payload_requirement_response_data_in_required_1.json")
        }

        @Test
        fun required2() {
            testingBindingAndMapping<RequirementResponseDataIn.Payload>("json/application/service/response/payload_requirement_response_data_in_required_2.json")
        }
    }
}
