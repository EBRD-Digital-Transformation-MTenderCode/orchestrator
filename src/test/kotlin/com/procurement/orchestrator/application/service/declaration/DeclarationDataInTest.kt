package com.procurement.orchestrator.application.service.declaration

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeclarationDataInTest {

    @Nested
    inner class Payload {
        @Test
        fun fully() {
            testingBindingAndMapping<DeclarationDataIn.Payload>("json/application/service/declaration/payload_declaration_data_in_full.json")
        }

        @Test
        fun required1() {
            testingBindingAndMapping<DeclarationDataIn.Payload>("json/application/service/declaration/payload_declaration_data_in_required_1.json")
        }

        @Test
        fun required2() {
            testingBindingAndMapping<DeclarationDataIn.Payload>("json/application/service/declaration/payload_declaration_data_in_required_2.json")
        }
    }
}
