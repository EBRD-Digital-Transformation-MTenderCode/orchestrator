package com.procurement.orchestrator.infrastructure.client.web.contracting.action

import com.procurement.orchestrator.json.testingBindingAndMapping
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContractingActionsTest {

    @Nested
    inner class FindCANIds {

        @Nested
        inner class Params {
            @Test
            fun fully() {
                testingBindingAndMapping<FindCANIdsAction.Params>("json/client/contracting/find_can_ids_params_full.json")
            }

            @Test
            fun required1() {
                testingBindingAndMapping<FindCANIdsAction.Params>("json/client/contracting/find_can_ids_params_1.json")
            }

           /* @Test
            fun required2() {
                testingBindingAndMapping<ValidateRequirementResponseAction.Params>("json/client/dossier/validate_requirement_response_params_required_2.json")
            }*/
        }
    }
}
